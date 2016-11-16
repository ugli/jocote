package se.ugli.jocote.support;

import se.ugli.jocote.Connection;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;

public abstract class GetNotSupportedConnection implements Connection {

    @Override
    public final long clear() {
        throw new UnsupportedOperationException("clear not supported by a" + getClass().getName());
    }

    @Override
    public final long messageCount() {
        throw new UnsupportedOperationException("messageCount not supported by a" + getClass().getName());
    }

    @Override
    public final MessageIterator messageIterator() {
        throw new UnsupportedOperationException("messageIterator not supported by a" + getClass().getName());
    }

    @Override
    public final SessionIterator sessionIterator() {
        throw new UnsupportedOperationException("sessionIterator not supported by a" + getClass().getName());
    }

}
