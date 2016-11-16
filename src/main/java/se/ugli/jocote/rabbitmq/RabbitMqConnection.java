package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;

import se.ugli.jocote.Connection;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.support.JocoteUrl;

public class RabbitMqConnection extends RabbitMqBase implements Connection {

    private final JocoteUrl url;
    private final com.rabbitmq.client.Connection connection;
    private final Channel channel;
    private final String queue;
    private final boolean durable;
    private final boolean exclusive;
    private final boolean autoDelete;
    private final boolean automaticRecoveryEnabled;
    private final Map<String, Object> arguments;

    RabbitMqConnection(final JocoteUrl url) {
        try {
            this.url = url;
            automaticRecoveryEnabled = automaticRecoveryEnabled(url);
            connection = ClientConnectionFactory.create(url, automaticRecoveryEnabled);
            channel = connection.createChannel();
            queue = url.queue;
            durable = durable(url);
            exclusive = exclusive(url);
            autoDelete = autoDelete(url);
            arguments = arguments(url);
            channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    private Map<String, Object> arguments(final JocoteUrl url) {
        final HashMap<String, Object> result = new HashMap<>(url.params);
        result.remove("durable");
        result.remove("exclusive");
        result.remove("autoDelete");
        result.remove("automaticRecoveryEnabled");
        return result;
    }

    private boolean autoDelete(final JocoteUrl url) {
        return "true".equals(url.params.get("autoDelete"));
    }

    private boolean exclusive(final JocoteUrl url) {
        return "true".equals(url.params.get("exclusive"));
    }

    private boolean durable(final JocoteUrl url) {
        final String durable = url.params.get("durable");
        return durable == null || "true".equals(durable);
    }

    private boolean automaticRecoveryEnabled(final JocoteUrl url) {
        final String automaticRecoveryEnabled = url.params.get("automaticRecoveryEnabled");
        return automaticRecoveryEnabled == null || "true".equals(automaticRecoveryEnabled);

    }

    @Override
    public void close() {
        close(channel);
        close(connection);
    }

    @Override
    public long clear() {
        try {
            return channel.queuePurge(queue).getMessageCount();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public long messageCount() {
        try {
            return channel.messageCount(queue);
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public MessageIterator messageIterator() {
        return new RabbitMqIterator(channel, queue);
    }

    @Override
    public SessionIterator sessionIterator() {
        return new RabbitMqSessionIterator(connection, queue, durable, exclusive, autoDelete, arguments);
    }

    @Override
    public void put(final Message message) {
        try {
            final BasicProperties props = new BasicPropertiesFactory(durable).create(message);
            channel.basicPublish("", queue, props, message.body());
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    public com.rabbitmq.client.Connection rabbitConnection() {
        return connection;
    }

    public Channel rabbitChannel() {
        return channel;
    }

    @Override
    public String toString() {
        return url.toString();
    }

}
