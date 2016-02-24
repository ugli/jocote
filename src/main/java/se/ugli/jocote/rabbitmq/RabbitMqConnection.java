package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionContext;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.support.DefaultConsumer;
import se.ugli.jocote.support.JocoteUrl;

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
    public Optional<byte[]> get() {
        return get(new DefaultConsumer());
    }

    @Override
    public <T> Optional<T> get(final Function<Message, Optional<T>> msgFunc) {
        return BasicGet.apply(channel, queue).get(msgFunc);
    }

    @Override
    public <T> Optional<T> getWithSession(final Function<SessionContext, Optional<T>> sessionFunc) {
        Channel newChannel = null;
        try {
            newChannel = connection.createChannel();
            newChannel.queueDeclare(queue, false, false, false, null); // TODO params
            final GetResponse basicGet = newChannel.basicGet(queue, false);
            if (basicGet != null) {
                final RabbitSessionContext cxt = new RabbitSessionContext(newChannel, basicGet);
                final Optional<T> result = sessionFunc.apply(cxt);
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
    public Iterator<byte[]> iterator() {
        return iterator(new DefaultConsumer());
    }

    @Override
    public <T> Iterator<T> iterator(final Function<Message, Optional<T>> msgFunc) {
        return new RabbitMqIterator<T>(channel, queue, msgFunc);
    }

    @Override
    public SessionIterator<byte[]> sessionIterator() {
        return sessionIterator(new DefaultConsumer());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Function<Message, Optional<T>> msgFunc) {
        return new RabbitMqSessionIterator<T>(connection, queue, msgFunc);
    }

    @Override
    public void put(final byte[] message) {
        put(message, null, null);
    }

    @Override
    public void put(final byte[] message, final Map<String, Object> headers, final Map<String, Object> properties) {
        try {
            final BasicProperties.Builder builder = new BasicProperties.Builder();
            final Map<String, Object> newHeaders = new HashMap<>();
            if (headers != null)
                newHeaders.putAll(headers);
            if (properties != null)
                newHeaders.putAll(properties);
            builder.headers(newHeaders);
            channel.basicPublish("", queue, builder.build(), message);
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

}
