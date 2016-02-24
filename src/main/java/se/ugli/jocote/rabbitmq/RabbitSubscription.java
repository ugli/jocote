package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

public class RabbitSubscription implements Subscription, com.rabbitmq.client.Consumer {

    private final com.rabbitmq.client.Connection connection;
    private final Channel channel;
    private final Consumer<Message> consumer;

    public RabbitSubscription(final JocoteUrl url, final Consumer<Message> consumer) {
        try {
            final ConnectionFactory factory = new ConnectionFactory();
            if (url.host != null)
                factory.setHost(url.host);
            else
                factory.setHost("localhost");
            if (url.port != null)
                factory.setPort(url.port);
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.basicConsume(url.queue, true, this);
        }
        catch (final TimeoutException | IOException e) {
            throw new JocoteException(e);
        }
        this.consumer = consumer;
    }

    @Override
    public void close() {
        try {
            channel.close();
        }
        catch (final TimeoutException | IOException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleConsumeOk(final String consumerTag) {
    }

    @Override
    public void handleCancelOk(final String consumerTag) {
    }

    @Override
    public void handleCancel(final String consumerTag) {
    }

    @Override
    public void handleDelivery(final String consumerTag, final Envelope envelope, final BasicProperties properties, final byte[] body) {
        consumer.accept(new RabbitMessage(body, properties));
    }

    @Override
    public void handleShutdownSignal(final String consumerTag, final ShutdownSignalException sig) {
    }

    @Override
    public void handleRecoverOk(final String consumerTag) {
    }

}
