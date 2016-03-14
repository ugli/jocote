package se.ugli.jocote.log;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionContext;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.SessionStream;
import se.ugli.jocote.support.JocoteUrl;

public class LogConnection implements Connection {

    private final Level level;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public LogConnection(final JocoteUrl url) {
        try {
            this.level = Level.valueOf(url.queue.toUpperCase());
        }
        catch (final IllegalArgumentException e) {
            throw new JocoteException("Valid queues: " + Arrays.asList(Level.values()));
        }
    }

    @Override
    public void close() {
    }

    @Override
    public Optional<byte[]> get() {
        throw new UnsupportedOperationException("You can't get log messages.");
    }

    @Override
    public <T> Optional<T> get(final Function<Message, Optional<T>> msgFunc) {
        throw new UnsupportedOperationException("You can't get log messages.");
    }

    @Override
    public <T> Optional<T> getWithSession(final Function<SessionContext, Optional<T>> sessionFunc) {
        throw new UnsupportedOperationException("You can't get log messages.");
    }

    @Override
    public Iterator<byte[]> iterator() {
        throw new UnsupportedOperationException("You can't iterate log messages.");
    }

    @Override
    public <T> Iterator<T> iterator(final Function<Message, Optional<T>> msgFunc) {
        throw new UnsupportedOperationException("You can't iterate log messages.");
    }

    @Override
    public Stream<byte[]> stream() {
        throw new UnsupportedOperationException("You can't stream log messages.");
    }

    @Override
    public Stream<byte[]> stream(final int batchSize) {
        throw new UnsupportedOperationException("You can't stream log messages.");
    }

    @Override
    public SessionIterator<byte[]> sessionIterator() {
        throw new UnsupportedOperationException("You can't iterate log messages.");
    }

    @Override
    public SessionStream sessionStream() {
        throw new UnsupportedOperationException("You can't stream log messages.");
    }

    @Override
    public SessionStream sessionStream(final int batchSize) {
        throw new UnsupportedOperationException("You can't stream log messages.");
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Function<Message, Optional<T>> msgFunc) {
        throw new UnsupportedOperationException("You can't iterate log messages.");
    }

    @Override
    public void put(final byte[] message) {
        put(Message.builder().body(message).build());
    }

    @Override
    public void put(final Message message) {
        if (level == Level.ERROR)
            logger.error("Message: {}", message);
        else if (level == Level.WARN)
            logger.warn("Message: {}", message);
        else if (level == Level.INFO)
            logger.info("Message: {}", message);
        else if (level == Level.DEBUG)
            logger.debug("Message: {}", message);
        else if (level == Level.TRACE)
            logger.trace("Message: {}", message);
    }

}
