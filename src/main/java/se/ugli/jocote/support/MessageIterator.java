package se.ugli.jocote.support;

import java.util.Optional;

import se.ugli.jocote.Message;

public interface MessageIterator {

    Optional<Message> next();

    int index();
}
