package se.ugli.jocote;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import se.ugli.jocote.support.Streams;

public interface Connection extends AutoCloseable {

    @Override
    void close();

    long clear();

    long messageCount();

    MessageIterator messageIterator();

    SessionIterator sessionIterator();

    CompletableFuture<Void> put(Message message);

    default Optional<Message> get() {
        return messageIterator().next();
    }

    default Stream<Message> messageStream() {
        return Streams.messageStream(messageIterator());
    }

    default SessionStream sessionStream() {
        return Streams.sessionStream(sessionIterator());
    }

    default CompletableFuture<Void> put(final byte[] message) {
        return put(Message.builder().body(message).build());
    }

    default CompletableFuture<Long> put(final Stream<Message> messageStream) {
        return CompletableFuture.supplyAsync(() -> {
            return messageStream.map(this::put).map(CompletableFuture::join).count();
        }, executor());
    }

    default Executor executor() {
        return Jocote.defaultExecutor;
    }

}
