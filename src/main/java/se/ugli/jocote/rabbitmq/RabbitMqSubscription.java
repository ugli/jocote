package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.function.Consumer;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

class RabbitMqSubscription extends RabbitMqBase implements Subscription, com.rabbitmq.client.Consumer {

    private final com.rabbitmq.client.Connection connection;
    private final Channel channel;
    private final Consumer<Message> consumer;

    RabbitMqSubscription(final JocoteUrl url, final Consumer<Message> consumer) {
        try {
            connection = ClientConnectionFactory.create(url, true);
            channel = connection.createChannel();
            channel.basicConsume(url.queue, true, this);
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
        this.consumer = consumer;
    }

    @Override
    public void handleDelivery(final String consumerTag, final Envelope envelope, final BasicProperties props,
            final byte[] body) {
        consumer.accept(MessageFactory.create(consumerTag, envelope, props, body));
    }

    @Override
    public void close() {
        close(channel);
        close(connection);
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
    public void handleShutdownSignal(final String consumerTag, final ShutdownSignalException sig) {
    }

    @Override
    public void handleRecoverOk(final String consumerTag) {
    }

}
