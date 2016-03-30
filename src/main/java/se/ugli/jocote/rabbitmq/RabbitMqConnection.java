package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import se.ugli.jocote.SessionStream;
import se.ugli.jocote.support.DefaultConsumer;
import se.ugli.jocote.support.IdentityFunction;
import se.ugli.jocote.support.JocoteUrl;
import se.ugli.jocote.support.Streams;

public class RabbitMqConnection implements Connection {

    private final static Logger logger = LoggerFactory.getLogger(RabbitMqConnection.class);
    private final com.rabbitmq.client.Connection connection;
    private final Channel channel;
    private String queue;
    private boolean durable;
    private boolean exclusive;
    private boolean autoDelete;
    private Map<String, Object> arguments;

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
            durable = true;
            exclusive = false;
            autoDelete = false;
            channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
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
    public Optional<byte[]> get() {
        return get(new DefaultConsumer());
    }

    @Override
    public <T> Optional<T> get(final Function<Message, Optional<T>> msgFunc) {
        try {
            final GetResponse basicGet = channel.basicGet(queue, true);
            if (basicGet != null)
                return msgFunc.apply(MessageFactory.create(basicGet));
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public <T> Optional<T> getWithSession(final Function<SessionContext, Optional<T>> sessionFunc) {
        Channel newChannel = null;
        try {
            newChannel = connection.createChannel();
            newChannel.queueDeclare(queue, false, false, false, null); // TODO params
            final GetResponse basicGet = newChannel.basicGet(queue, false);
            if (basicGet != null) {
                final RabbitMqSessionContext cxt = new RabbitMqSessionContext(newChannel, basicGet);
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
    public Stream<Message> stream() {
        return Streams.stream(iterator(new IdentityFunction()));
    }

    @Override
    public Stream<Message> stream(final int batchSize) {
        return Streams.stream(iterator(new IdentityFunction()), batchSize);
    }

    @Override
    public SessionStream sessionStream() {
        return Streams.sessionStream(sessionIterator(new IdentityFunction()));
    }

    @Override
    public SessionStream sessionStream(final int batchSize) {
        return Streams.sessionStream(sessionIterator(new IdentityFunction()), batchSize);
    }

    @Override
    public SessionIterator<byte[]> sessionIterator() {
        return sessionIterator(new DefaultConsumer());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Function<Message, Optional<T>> msgFunc) {
        return new RabbitMqSessionIterator<>(connection, msgFunc, queue, durable, exclusive, autoDelete, arguments);
    }

    @Override
    public void put(final byte[] message) {
        put(Message.builder().body(message).build());
    }

    @Override
    public void put(final Message message) {
        try {
            final BasicProperties.Builder builder = new BasicProperties.Builder();
            final Map<String, Object> newHeaders = new HashMap<>();
            message.headerNames().forEach(name -> newHeaders.put(name, message.header(name)));
            message.propertyNames().forEach(name -> newHeaders.put(name, message.property(name)));
            builder.headers(newHeaders);
            channel.basicPublish("", queue, builder.build(), message.body());
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

}
