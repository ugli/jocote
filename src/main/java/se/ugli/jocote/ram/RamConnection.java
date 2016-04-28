package se.ugli.jocote.ram;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import se.ugli.jocote.Connection;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageStream;
import se.ugli.jocote.Session;
import se.ugli.jocote.SessionStream;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.Streams;

class RamConnection implements Connection {

    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();
    private final List<Consumer<Message>> subscribers = new ArrayList<>();

    @Override
    public void close() {
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public Optional<Message> get() {
        return Optional.ofNullable(queue.poll());
    }

    @Override
    public <T> Optional<T> get(final Function<Session, Optional<T>> sessionFunc) {
        final Message message = queue.poll();
        if (message != null) {
            final RamSession cxt = new RamSession(message, queue);
            final Optional<T> result = sessionFunc.apply(cxt);
            if (cxt.isClosable())
                return result;
            throw new JocoteException("You have to acknowledge or leave message");
        }
        return Optional.empty();
    }

    @Override
    public MessageStream messageStream() {
        return Streams.messageStream(new RamIterator(queue));
    }

    @Override
    public SessionStream sessionStream() {
        return Streams.sessionStream(new RamSessionIterator(queue));
    }

    @Override
    public SessionStream sessionStream(final int batchSize) {
        return Streams.sessionStream(new RamSessionIterator(queue), batchSize);
    }

    @Override
    public MessageStream messageStream(final int batchSize) {
        return Streams.messageStream(new RamIterator(queue), batchSize);
    }

    @Override
    public void put(final byte[] message) {
        put(Message.builder().id(randomUUID().toString()).body(message).build());
    }

    @Override
    public void put(final Message message) {
        final Message cloneWithId = cloneWithId(message, randomUUID().toString());
        if (subscribers.isEmpty())
            queue.offer(cloneWithId);
        else
            randomSubscriber().accept(cloneWithId);
    }

    @Override
    public int put(final Stream<Message> messageStream) {
        List<Message> messages = messageStream.collect(toList());
        messages.forEach(this::put);
        return messages.size();
    }


    private Message cloneWithId(final Message msg, final String id) {
        return Message.builder().id(id).body(msg.body()).headers(msg.headers()).properties(msg.properties()).build();
    }

    private Consumer<Message> randomSubscriber() {
        final byte[] seed = String.valueOf(System.nanoTime()).getBytes();
        final SecureRandom secureRandom = new SecureRandom(seed);
        final int index = secureRandom.nextInt(subscribers.size());
        return subscribers.get(index);
    }

    Subscription addSubscription(final Consumer<Message> consumer) {
        subscribers.add(consumer);
        while (!queue.isEmpty())
            randomSubscriber().accept(queue.poll());
        return () -> subscribers.remove(consumer);
    }

}