package se.ugli.jocote.log;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.support.GetNotSupportedConnection;
import se.ugli.jocote.support.JocoteUrl;

class LogConnection extends GetNotSupportedConnection {

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
