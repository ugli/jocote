package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.JocoteUrl;
import se.ugli.jocote.SessionConsumer;
import se.ugli.jocote.SessionIterator;

public class RabbitMqConnection implements Connection {

    private final com.rabbitmq.client.Connection connection;
    private final Channel channel;
    private String queue;

    public RabbitMqConnection(final JocoteUrl url) {
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
            queue = url.queue;
            channel.queueDeclare(queue, false, false, false, null); // TODO params
        }
        catch (final TimeoutException | IOException e) {
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
        try {
            connection.close();
        }
        catch (final IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Object> get() {
        return get(new DefaultConsumer());
    }

    @Override
    public <T> Optional<T> get(final Class<T> type) {
        return get(new TypeConsumer<T>(type));
    }

    @Override
    public <T> Optional<T> get(final Consumer<T> consumer) {
        return BasicGet.apply(channel, queue).get(consumer);
    }

    @Override
    public <T> Optional<T> get(final SessionConsumer<T> consumer) {
        Channel newChannel = null;
        try {
            newChannel = connection.createChannel();
            newChannel.queueDeclare(queue, false, false, false, null); // TODO params
            final GetResponse basicGet = newChannel.basicGet(queue, false);
            if (basicGet != null) {
                final RabbitSessionMsgCxt cxt = new RabbitSessionMsgCxt(newChannel, basicGet);
                final Optional<T> result = consumer.receive(basicGet.getBody(), cxt);
                if (cxt.isClosable())
                    return result;
                throw new JocoteException("You have to acknowledge or leave message");
            }
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
        finally {
            if (newChannel != null)
                try {
                    newChannel.close();
                }
                catch (IOException | TimeoutException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public Iterator<Object> iterator() {
        return iterator(new DefaultConsumer());
    }

    @Override
    public <T> Iterator<T> iterator(final Class<T> type) {
        return iterator(new TypeConsumer<T>(type));
    }

    @Override
    public <T> Iterator<T> iterator(final Consumer<T> consumer) {
        return new RabbitMqIterator<T>(channel, queue, consumer);
    }

    @Override
    public SessionIterator<Object> sessionIterator() {
        return sessionIterator(new DefaultConsumer());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Class<T> type) {
        return sessionIterator(new TypeConsumer<T>(type));
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Consumer<T> consumer) {
        return new RabbitMqSessionIterator<T>(connection, queue, consumer);
    }

    @Override
    public void put(final Object message) {
        put(message, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    @Override
    public void put(final Object message, final Map<String, Object> headers, final Map<String, Object> properties) {
        try {
            final BasicProperties.Builder builder = new BasicProperties.Builder();
            final Map<String, Object> newHeaders = new HashMap<>();
            if (headers != null)
                newHeaders.putAll(headers);
            if (properties != null)
                newHeaders.putAll(properties);
            builder.headers(newHeaders);
            channel.basicPublish("", queue, builder.build(), message(message));
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    private byte[] message(final Object message) {
        if (message == null)
            return null;
        else if (message instanceof String)
            return ((String) message).getBytes();
        else if (message instanceof byte[])
            return (byte[]) message;
        throw new JocoteException("Unsupported message type: " + message.getClass().getName());
    }

}
