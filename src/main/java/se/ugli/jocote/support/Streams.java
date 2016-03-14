package se.ugli.jocote.support;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import se.ugli.jocote.Iterator;
import se.ugli.jocote.Message;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.SessionStream;

public final class Streams {

    private static final int MAX_BATCH = 100;

    public static Stream<Message> stream(final Iterator<Message> iterator) {
        return stream(iterator, MAX_BATCH);
    }

    public static Stream<Message> stream(final Iterator<Message> iterator, final int batchSize) {
        final SpliteratorImpl spliterator = new SpliteratorImpl(iterator, batchSize);
        return StreamSupport.stream(spliterator, false);
    }

    public static SessionStream sessionStream(final SessionIterator<Message> iterator) {
        return sessionStream(iterator, MAX_BATCH);
    }

    public static SessionStream sessionStream(final SessionIterator<Message> iterator, final int batchSize) {
        final Stream<Message> stream = stream(iterator, batchSize);
        return new SessionStreamImpl(stream, iterator);
    }

}
