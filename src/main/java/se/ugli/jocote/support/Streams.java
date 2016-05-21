package se.ugli.jocote.support;

import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.SessionStream;

import java.util.stream.Stream;

import static java.util.stream.StreamSupport.stream;

public final class Streams {

    public static Stream<Message> messageStream(final MessageIterator iterator) {
        return stream(new SpliteratorImpl(iterator), false);
    }

    public static SessionStream sessionStream(final SessionIterator iterator) {
        return new SessionStreamImpl(messageStream(iterator), iterator);
    }

}
