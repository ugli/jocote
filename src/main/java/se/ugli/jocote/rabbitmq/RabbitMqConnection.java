package se.ugli.jocote.rabbitmq;

import static java.util.stream.Collectors.toList;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.stream.Stream;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Connection;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageStream;
import se.ugli.jocote.Session;
import se.ugli.jocote.SessionStream;
import se.ugli.jocote.support.JocoteUrl;
import se.ugli.jocote.support.MessageIterator;
import se.ugli.jocote.support.SessionIterator;
import se.ugli.jocote.support.Streams;

public class RabbitMqConnection extends RabbitMqBase implements Connection {

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
            durable = durable(url);
            exclusive = exclusive(url);
            autoDelete = autoDelete(url);
            arguments = arguments(url);
            channel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
        }
        catch (final TimeoutException | IOException e) {
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
        if (durable != null)
            return "true".equals(durable);
        return true;
    }

    public Channel getChannel() {
        return channel;
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
    public Optional<Message> get() {
        try {
            final GetResponse basicGet = channel.basicGet(queue, true);
            if (basicGet != null)
                return Optional.of(MessageFactory.create(basicGet));
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public <T> Optional<T> get(final Function<Session, Optional<T>> sessionFunc) {
        Channel newChannel = null;
        try {
            newChannel = connection.createChannel();
            newChannel.queueDeclare(queue, durable, exclusive, autoDelete, arguments);
            final GetResponse basicGet = newChannel.basicGet(queue, false);
            if (basicGet != null) {
                final RabbitMqSession cxt = new RabbitMqSession(newChannel, basicGet);
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
            close(newChannel);
        }
    }

    private MessageIterator iterator() {
        return new RabbitMqIterator(channel, queue);
    }

    @Override
    public MessageStream messageStream() {
        return Streams.messageStream(iterator());
    }

    @Override
    public MessageStream messageStream(final int batchSize) {
        return Streams.messageStream(iterator(), batchSize);
    }

    @Override
    public SessionStream sessionStream() {
        return Streams.sessionStream(sessionIterator());
    }

    @Override
    public SessionStream sessionStream(final int batchSize) {
        return Streams.sessionStream(sessionIterator(), batchSize);
    }

    public SessionIterator sessionIterator() {
        return new RabbitMqSessionIterator(connection, queue, durable, exclusive, autoDelete, arguments);
    }

    @Override
    public void put(final byte[] message) {
        put(Message.builder().body(message).build());
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

    @Override
    public int put(final Stream<Message> messageStream) {
        List<Message> messages = messageStream.collect(toList());
        messages.forEach(this::put);
        return messages.size();
    }

}
