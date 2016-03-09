package se.ugli.jocote.ram;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Function;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Message.MessageBuilder;
import se.ugli.jocote.SessionContext;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.DefaultConsumer;

class RamConnection implements Connection {

    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();
    private final List<Consumer<Message>> subscribers = new ArrayList<>();

    @Override
    public void close() {
    }

    @Override
    public Optional<byte[]> get() {
        return get(new DefaultConsumer());
    }

    @Override
    public <T> Optional<T> get(final Function<Message, Optional<T>> msgFunc) {
        final Message message = queue.poll();
        if (message != null)
            return msgFunc.apply(message);
        return Optional.empty();
    }

    @Override
    public <T> Optional<T> getWithSession(final Function<SessionContext, Optional<T>> sessionFunc) {
        final Message message = queue.poll();
        if (message != null) {
            final RamSessionMessageContext cxt = new RamSessionMessageContext(message, queue);
            final Optional<T> result = sessionFunc.apply(cxt);
            if (cxt.isClosable())
                return result;
            throw new JocoteException("You have to acknowledge or leave message");
        }
        return Optional.empty();
    }

    @Override
    public Iterator<byte[]> iterator() {
        return iterator(new DefaultConsumer());
    }

    @Override
    public <T> Iterator<T> iterator(final Function<Message, Optional<T>> msgFunc) {
        return new RamIterator<T>(queue, msgFunc);
    }

    @Override
    public void put(final byte[] message) {
        put(Message.builder().id(UUID.randomUUID().toString()).body(message).build());
    }

    @Override
    public void put(final Message message) {
        if (subscribers.isEmpty())
            queue.offer(cloneWithId(message, UUID.randomUUID().toString()));
        else
            randomSubscriber().accept(message);
    }

    private Message cloneWithId(final Message msg, final String id) {
        final MessageBuilder builder = new MessageBuilder();
        builder.id(id).body(msg.body());
        msg.headerNames().forEach(h -> builder.header(h, msg.header(h)));
        msg.propertyNames().forEach(p -> builder.property(p, msg.property(p)));
        return builder.build();
    }

    private Consumer<Message> randomSubscriber() {
        final byte[] seed = String.valueOf(System.nanoTime()).getBytes();
        final SecureRandom secureRandom = new SecureRandom(seed);
        final int index = secureRandom.nextInt(subscribers.size());
        return subscribers.get(index);
    }

    @Override
    public SessionIterator<byte[]> sessionIterator() {
        return sessionIterator(new DefaultConsumer());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Function<Message, Optional<T>> msgFunc) {
        return new RamSessionIterator<T>(queue, msgFunc);
    }

    Subscription addSubscription(final Consumer<Message> consumer) {
        for (final Message message : queue)
            consumer.accept(message);
        subscribers.add(consumer);
        return new Subscription() {

            @Override
            public void close() {
                subscribers.remove(consumer);
                RamConnection.this.close();
            }
        };
    }

}