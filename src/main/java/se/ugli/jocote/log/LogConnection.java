package se.ugli.jocote.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import se.ugli.jocote.*;
import se.ugli.jocote.support.JocoteUrl;

import java.util.Arrays;

class LogConnection implements Connection {

    private final JocoteUrl url;
    private final Level level;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    LogConnection(final JocoteUrl url) {
        this.url = url;
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
    public void clear() {
        throw new UnsupportedOperationException("You can't clear log messages.");
    }

    @Override
    public MessageIterator messageIterator() {
        throw new UnsupportedOperationException("You can't iterate log messages.");
    }

    @Override
    public SessionIterator sessionIterator() {
        throw new UnsupportedOperationException("You can't iterate log messages.");
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

    @Override
    public String toString() {
        return url.toString();
    }

}
