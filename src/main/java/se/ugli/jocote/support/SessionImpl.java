package se.ugli.jocote.support;

import se.ugli.jocote.Message;
import se.ugli.jocote.Session;
import se.ugli.jocote.SessionIterator;

import java.util.Optional;

public class SessionImpl implements Session {

    private final SessionIterator iterator;
    private final Optional<Message> message;

    public SessionImpl(SessionIterator iterator) {
        this.iterator = iterator;
        message = iterator.next();
    }

    @Override
    public Optional<Message> message() {
        return message;
    }

    @Override
    public void close() {
        iterator.close();
    }

    @Override
    public void ack() {
        iterator.ack();
    }

    @Override
    public void nack() {
        iterator.nack();
    }

}
