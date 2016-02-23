package se.ugli.jocote.rabbitmq;

import java.util.Optional;

import com.rabbitmq.client.Channel;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Iterator;

public class RabbitMqIterator<T> implements Iterator<T> {

    private final Channel channel;
    private final Consumer<T> consumer;
    private final String queue;

    public RabbitMqIterator(final Channel channel, final String queue, final Consumer<T> consumer) {
        this.channel = channel;
        this.queue = queue;
        this.consumer = consumer;
    }

    @Override
    public Optional<T> next() {
        return BasicGet.apply(channel, queue).get(consumer);
    }

}
