package se.ugli.jocote.rabbitmq;

import static se.ugli.jocote.support.Id.newId;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.support.JocoteProperties;

class RabbitMqSessionIterator extends RabbitMqBase implements SessionIterator {

    private final String queue;
    private final Channel channel;
    private final String sessionid;
    private final Integer sessionIteratorChannelCloseDelayMs;

    private boolean closable;
    private GetResponse lastMessage;

    RabbitMqSessionIterator(final Connection connection, final String queue, final boolean durable,
            final boolean exclusive, final boolean autoDelete, final Integer sessionIteratorChannelCloseDelayMs,
            final Map<String, Object> arguments) {
        try {
            if (sessionIteratorChannelCloseDelayMs != null)
                this.sessionIteratorChannelCloseDelayMs = sessionIteratorChannelCloseDelayMs;
            else
                this.sessionIteratorChannelCloseDelayMs = JocoteProperties.rabbitmqSessionIteratorChannelCloseDelayMs()
                        .orElse(null);
            this.queue = queue;
            channel = connection.createChannel();
            channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
            sessionid = newId();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public Optional<Message> next() {
        try {
            final GetResponse basicGet = channel.basicGet(queue, false);
            if (basicGet != null)
                lastMessage = basicGet;
            return Optional.ofNullable(MessageFactory.create(basicGet));
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void close() {
        if (!closable) {
            close(channel);
            throw new JocoteException("You have to acknowledge or leave messages before closing");
        }
        close(channel, sessionIteratorChannelCloseDelayMs);
    }

    @Override
    public void ack() {
        try {
            if (lastMessage != null) {
                final long deliveryTag = lastMessage.getEnvelope().getDeliveryTag();
                final boolean multiple = true;
                channel.basicAck(deliveryTag, multiple);
            }
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
        finally {
            closable = true;
        }
    }

    @Override
    public void nack() {
        try {
            if (lastMessage != null) {
                final long deliveryTag = lastMessage.getEnvelope().getDeliveryTag();
                final boolean multiple = true;
                final boolean requeue = true;
                channel.basicNack(deliveryTag, multiple, requeue);
            }
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
        finally {
            closable = true;
        }
    }

    @Override
    public String sessionid() {
        return sessionid;
    }

}
