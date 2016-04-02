package se.ugli.jocote.support;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import se.ugli.jocote.Message;
import se.ugli.jocote.MessageStream;
import se.ugli.jocote.SessionStream;

public final class Streams {

    private static final int MAX_BATCH = 100;

    public static MessageStream messageStream(final MessageIterator iterator) {
        return messageStream(iterator, MAX_BATCH);
    }

    public static MessageStream messageStream(final MessageIterator iterator, final int batchSize) {
        final SpliteratorImpl spliterator = new SpliteratorImpl(iterator, batchSize);
        final Stream<Message> stream = StreamSupport.stream(spliterator, false);
        return new MessageStreamImpl(stream, iterator);
    }

    public static SessionStream sessionStream(final SessionIterator iterator) {
        return sessionStream(iterator, MAX_BATCH);
    }

    public static SessionStream sessionStream(final SessionIterator iterator, final int batchSize) {
        final Stream<Message> stream = messageStream(iterator, batchSize);
        return new SessionStreamImpl(stream, iterator);
    }

}
