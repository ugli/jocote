package se.ugli.jocote.support;

import se.ugli.jocote.SessionAware;

public interface SessionIterator extends MessageIterator, SessionAware, AutoCloseable {

    @Override
    void close();

}
