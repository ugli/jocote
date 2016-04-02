package se.ugli.jocote.support;

import java.util.stream.Stream;

import se.ugli.jocote.Message;
import se.ugli.jocote.SessionStream;

class SessionStreamImpl extends MessageStreamImpl implements SessionStream {

    private final SessionIterator iterator;

    SessionStreamImpl(final Stream<Message> stream, final SessionIterator iterator) {
        super(stream, iterator);
        this.iterator = iterator;
    }

    @Override
    public void close() {
        super.close();
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
