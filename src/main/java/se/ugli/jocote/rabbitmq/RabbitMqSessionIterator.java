package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;

class RabbitMqSessionIterator<T> implements SessionIterator<T> {

    private final static Logger logger = LoggerFactory.getLogger(RabbitMqSessionIterator.class);
    private final String queue;
    private final Function<Message, Optional<T>> msgFunc;
    private Channel channel;
    private boolean closable;
    private GetResponse lastMessage;

    RabbitMqSessionIterator(final Connection connection, final Function<Message, Optional<T>> msgFunc, final String queue,
            final boolean durable, final boolean exclusive, final boolean autoDelete, final Map<String, Object> arguments) {
        try {
            this.queue = queue;
            this.msgFunc = msgFunc;
            this.channel = connection.createChannel();
            this.channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public Optional<T> next() {
        try {
            final GetResponse basicGet = channel.basicGet(queue, false);
            if (basicGet != null) {
                lastMessage = basicGet;
                return msgFunc.apply(MessageFactory.create(basicGet));
            }
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void close() {
        try {
            channel.close();
        }
        catch (final RuntimeException | TimeoutException | IOException e) {
            logger.warn("Couldn't close channel: " + e.getMessage());
        }
        if (!closable)
            throw new JocoteException("You have to acknowledge or leave messages before closing");
    }

    @Override
    public void acknowledgeMessages() {
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
    public void leaveMessages() {
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

}
