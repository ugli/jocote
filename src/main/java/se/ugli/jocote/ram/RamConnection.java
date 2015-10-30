package se.ugli.jocote.ram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.SessionConsumer;
import se.ugli.jocote.SessionIterable;
import se.ugli.jocote.SessionIterator;
import se.ugli.jocote.Subscription;
import se.ugli.jocote.support.SimpleConsumer;

class RamConnection implements Connection {

    private final Queue<Message> queue = new ConcurrentLinkedQueue<Message>();
    private final List<Consumer<Object>> subscribers = new ArrayList<Consumer<Object>>();

    @Override
    public void close() {
        if (subscribers.isEmpty())
            queue.clear();
    }

    @Override
    public <T> T get() {
        return get(new SimpleConsumer<T>());
    }

    @Override
    public <T> T get(final Consumer<T> consumer) {
        final Message message = queue.poll();
        if (message != null)
            return consumer.receive(message.body, new RamMessageContext(message));
        return null;
    }

    @Override
    public <T> T get(final SessionConsumer<T> consumer) {
        final Message message = queue.poll();
        if (message != null)
            return consumer.receive(message.body, new RamSessionMessageContext(message, queue));
        return null;
    }

    @Override
    public <T> Iterable<T> iterable() {
        return iterable(Integer.MAX_VALUE, new SimpleConsumer<T>());
    }

    @Override
    public <T> Iterable<T> iterable(final Consumer<T> consumer) {
        return iterable(Integer.MAX_VALUE, consumer);
    }

    @Override
    public <T> Iterable<T> iterable(final int limit) {
        return iterable(limit, new SimpleConsumer<T>());
    }

    @Override
    public <T> Iterable<T> iterable(final int limit, final Consumer<T> consumer) {
        final List<T> result = new LinkedList<T>();
        T t = get(consumer);
        while (t != null && limit != result.size()) {
            result.add(t);
            t = get(consumer);
        }
        return result;
    }

    @Override
    public void put(final Object message) {
        put(message, new HashMap<String, Object>(), new HashMap<String, Object>());
    }

    @Override
    public void put(final Object body, final Map<String, Object> headers, final Map<String, Object> properties) {
        if (subscribers.isEmpty())
            queue.offer(new Message(body, headers, properties));
        else
            for (final Consumer<Object> consumer : subscribers)
                consumer.receive(body, new RamMessageContext(headers, properties));
    }

    @Override
    public <T> SessionIterable<T> sessionIterable() {
        return sessionIterable(Integer.MAX_VALUE, new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterable<T> sessionIterable(final Consumer<T> consumer) {
        return sessionIterable(Integer.MAX_VALUE, consumer);
    }

    @Override
    public <T> SessionIterable<T> sessionIterable(final int limit) {
        return sessionIterable(limit, new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterable<T> sessionIterable(final int limit, final Consumer<T> consumer) {
        return new RamSessionIterable<T>(limit, consumer, queue);
    }

    @Override
    public <T> SessionIterator<T> sessionIterator() {
        return sessionIterator(new SimpleConsumer<T>());
    }

    @Override
    public <T> SessionIterator<T> sessionIterator(final Consumer<T> consumer) {
        return new RamSessionIterator(queue, consumer);
    }

    Subscription addSubscrition(final Consumer<Object> consumer) {
        for (final Message message : queue)
            consumer.receive(message.body, new RamMessageContext(message));
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