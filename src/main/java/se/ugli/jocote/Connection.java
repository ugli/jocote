package se.ugli.jocote;

import java.util.Optional;
import java.util.function.Function;

import se.ugli.jocote.support.MessageIterator;
import se.ugli.jocote.support.SessionIterator;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    Optional<Message> get();

    <T> Optional<T> get(Function<Session, Optional<T>> consumer);

    MessageStream messageStream();

    MessageStream messageStream(int limit);

    SessionStream sessionStream();

    SessionStream sessionStream(int limit);

    void put(byte[] message);

    void put(Message message);

    @Deprecated
    MessageIterator iterator();

    @Deprecated
    SessionIterator sessionIterator();

}
