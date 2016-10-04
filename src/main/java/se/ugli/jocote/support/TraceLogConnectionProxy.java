package se.ugli.jocote.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;

import java.util.Optional;

public class TraceLogConnectionProxy implements Connection {

    private final Connection connection;
    private Logger logger = LoggerFactory.getLogger(getClass());

    public TraceLogConnectionProxy(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void close() {
        if (logger.isTraceEnabled())
            logger.trace("[{}] close", connection);
        connection.close();
    }

    @Override
    public void clear() {
        if (logger.isTraceEnabled())
            logger.trace("[{}] clear", connection);
        connection.clear();
    }

    @Override
    public MessageIterator messageIterator() {
        return new TraceLogMessageIterator(connection.messageIterator());
    }

    private class TraceLogMessageIterator implements MessageIterator {

        private final MessageIterator iterator;

        TraceLogMessageIterator(MessageIterator iterator) {
            this.iterator = iterator;
        }

        @Override
        public Optional<Message> next() {
            Optional<Message> next = iterator.next();
            if (logger.isTraceEnabled())
                logger.trace("[{}] MessageIterator next: {}", connection, next);
            return next;
        }
    }

    @Override
    public SessionIterator sessionIterator() {
        return new TraceLogSessionIterator(connection.sessionIterator());
    }

    private class TraceLogSessionIterator implements SessionIterator {

        private final SessionIterator iterator;

        TraceLogSessionIterator(SessionIterator iterator) {
            this.iterator =iterator;
        }

        @Override
        public void close() {
            if (logger.isTraceEnabled())
                logger.trace("[{}] SessionIterator close", connection);
            iterator.close();
        }

        @Override
        public void ack() {
            if (logger.isTraceEnabled())
                logger.trace("[{}] SessionIterator ack", connection);
            iterator.ack();
        }

        @Override
        public void nack() {
            if (logger.isTraceEnabled())
                logger.trace("[{}] SessionIterator ack", connection);
            iterator.nack();
        }

        @Override
        public Optional<Message> next() {
            Optional<Message> next = iterator.next();
            if (logger.isTraceEnabled())
                logger.trace("[{}] SessionIterator next: {}", connection, next);
            return next;
        }
    }

    @Override
    public void put(Message message) {
        if (logger.isTraceEnabled())
            logger.trace("[{}] put: {}", connection, message);
        connection.put(message);
    }
}
