package se.ugli.jocote.rabbitmq;

import java.util.Optional;
import java.util.function.Function;

import com.rabbitmq.client.Channel;

import se.ugli.jocote.Iterator;
import se.ugli.jocote.Message;

public class RabbitMqIterator<T> implements Iterator<T> {

    private final Channel channel;
    private final Function<Message, Optional<T>> msgFunc;
    private final String queue;

    public RabbitMqIterator(final Channel channel, final String queue, final Function<Message, Optional<T>> msgFunc) {
        this.channel = channel;
        this.queue = queue;
        this.msgFunc = msgFunc;
    }

    @Override
    public Optional<T> next() {
        return BasicGet.apply(channel, queue).get(msgFunc);
    }

}
