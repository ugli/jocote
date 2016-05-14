package se.ugli.jocote.support;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import se.ugli.jocote.Message;
import se.ugli.jocote.MessageStream;
import se.ugli.jocote.SessionStream;

public final class Streams {

    public static MessageStream messageStream(final MessageIterator iterator) {
        final SpliteratorImpl spliterator = new SpliteratorImpl(iterator);
        final Stream<Message> stream = StreamSupport.stream(spliterator, false);
        return new MessageStreamImpl(stream, iterator);
    }

    public static SessionStream sessionStream(final SessionIterator iterator) {
        final Stream<Message> stream = messageStream(iterator);
        return new SessionStreamImpl(stream, iterator);
    }

}
