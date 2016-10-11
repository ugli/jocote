package se.ugli.jocote.support;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;

public class DebugLogConnectionProxy implements Connection {

    private final Connection connection;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public DebugLogConnectionProxy(final Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() {
        if (logger.isDebugEnabled())
            logger.debug("[{}] close", connection);
        connection.close();
    }

    @Override
    public void clear() {
        if (logger.isDebugEnabled())
            logger.debug("[{}] clear", connection);
        connection.clear();
    }

    @Override
    public long messageCount() {
        if (logger.isDebugEnabled())
            logger.debug("[{}] messageCount", connection);
        return connection.messageCount();
    }

    @Override
    public MessageIterator messageIterator() {
        return new TraceLogMessageIterator(connection.messageIterator());
    }

    private class TraceLogMessageIterator implements MessageIterator {

        private final MessageIterator iterator;

        TraceLogMessageIterator(final MessageIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public Optional<Message> next() {
            final Optional<Message> next = iterator.next();
            if (logger.isDebugEnabled())
                logger.debug("[{}] MessageIterator next: {}", connection, next);
            return next;
        }
    }

    @Override
    public SessionIterator sessionIterator() {
        return new TraceLogSessionIterator(connection.sessionIterator());
    }

    private class TraceLogSessionIterator implements SessionIterator {

        private final SessionIterator iterator;

        TraceLogSessionIterator(final SessionIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public void close() {
            if (logger.isDebugEnabled())
                logger.debug("[{}][{}] SessionIterator close", connection, sessionid());
            iterator.close();
        }

        @Override
        public void ack() {
            if (logger.isDebugEnabled())
                logger.debug("[{}][{}] SessionIterator ack", connection, sessionid());
            iterator.ack();
        }

        @Override
        public void nack() {
            if (logger.isDebugEnabled())
                logger.debug("[{}][{}] SessionIterator ack", connection, sessionid());
            iterator.nack();
        }

        @Override
        public Optional<Message> next() {
            final Optional<Message> next = iterator.next();
            if (logger.isTraceEnabled())
                logger.debug("[{}][{}] SessionIterator next: {}", connection, sessionid());
            return next;
        }

        @Override
        public String sessionid() {
            return iterator.sessionid();
        }
    }

    @Override
    public void put(final Message message) {
        if (logger.isDebugEnabled())
            logger.debug("[{}] put: {}", connection, message);
        connection.put(message);
    }
}
