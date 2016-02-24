package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionIterator;

public class RabbitMqSessionIterator<T> implements SessionIterator<T> {

    private final String queue;
    private final Consumer<T> consumer;
    private Channel channel;
    private boolean closable;
    private GetResponse lastMessage;

    public RabbitMqSessionIterator(final Connection connection, final String queue, final Consumer<T> consumer) {
        try {
            this.queue = queue;
            this.consumer = consumer;
            this.channel = connection.createChannel();
            this.channel.queueDeclare(queue, false, false, false, null); // TODO params
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
                return consumer.receive(new RabbitMessage(basicGet));
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
        catch (final TimeoutException | IOException e) {
            e.printStackTrace();
        }
        if (!closable && lastMessage != null)
            throw new JocoteException("You have to acknowledge or leave messages before closing");
    }

    @Override
    public void acknowledgeMessages() {
        try {
            closable = true;
            final long deliveryTag = lastMessage.getEnvelope().getDeliveryTag();
            final boolean multiple = true;
            channel.basicAck(deliveryTag, multiple);
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void leaveMessages() {
        try {
            final long deliveryTag = lastMessage.getEnvelope().getDeliveryTag();
            final boolean multiple = true;
            final boolean requeue = true;
            channel.basicNack(deliveryTag, multiple, requeue);
            closable = true;
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

}
