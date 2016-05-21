package se.ugli.jocote.rabbitmq;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import se.ugli.jocote.*;
import se.ugli.jocote.support.JocoteUrl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RabbitMqConnection extends RabbitMqBase implements Connection {

    private final com.rabbitmq.client.Connection connection;
    private final Channel channel;
    private String queue;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;
    private Map<String, Object> arguments;

    RabbitMqConnection(final JocoteUrl url) {
        try {
            connection = ClientConnectionFactory.create(url);
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

    @Override
    public void close() {
        close(channel);
        close(connection);
    }

    @Override
    public void clear() {
        try {
            channel.queuePurge(queue);
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


}
