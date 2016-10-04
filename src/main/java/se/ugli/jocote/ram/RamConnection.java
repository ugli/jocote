package se.ugli.jocote.ram;

import se.ugli.jocote.*;
import se.ugli.jocote.support.JocoteUrl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import static java.util.UUID.randomUUID;

class RamConnection implements Connection {

    private final JocoteUrl url;
    private final Queue<Message> queue = new ConcurrentLinkedQueue<>();
    private final List<Consumer<Message>> subscribers = new ArrayList<>();

    RamConnection(JocoteUrl url) {
        this.url = url;
    }

    @Override
    public void close() {
    }

    @Override
    public void clear() {
        queue.clear();
    }


    @Override
    public MessageIterator messageIterator() {
        return new RamIterator(queue);
    }

    @Override
    public SessionIterator sessionIterator() {
        return new RamSessionIterator(queue);
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

    @Override
    public String toString() {
        return url.toString();
    }

}