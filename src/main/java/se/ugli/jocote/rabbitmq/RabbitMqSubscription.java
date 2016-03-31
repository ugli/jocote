package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.JocoteUrl;

class RabbitMqSubscription implements Subscription, com.rabbitmq.client.Consumer {

    private final static Logger logger = LoggerFactory.getLogger(RabbitMqSubscription.class);
    private final com.rabbitmq.client.Connection connection;
    private final Channel channel;
    private final Consumer<Message> consumer;

    RabbitMqSubscription(final JocoteUrl url, final Consumer<Message> consumer) {
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
        catch (final RuntimeException | TimeoutException | IOException e) {
            logger.warn("Couldn't close channel: " + e.getMessage());
        }
        try {
            connection.close();
        }
        catch (final RuntimeException | IOException e) {
            logger.warn("Couldn't close connection: " + e.getMessage());
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
        final Map<String, Object> headers = properties.getHeaders();
        final Message message = Message.builder().id(properties.getMessageId()).body(body).properties(headers).headers(headers).build();
        consumer.accept(message);
    }

    @Override
    public void handleShutdownSignal(final String consumerTag, final ShutdownSignalException sig) {
    }

    @Override
    public void handleRecoverOk(final String consumerTag) {
    }

}
