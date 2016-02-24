package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Iterator;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;

class RabbitMqIterator<T> implements Iterator<T> {

    private final Channel channel;
    private final Function<Message, Optional<T>> msgFunc;
    private final String queue;

    RabbitMqIterator(final Channel channel, final String queue, final Function<Message, Optional<T>> msgFunc) {
        this.channel = channel;
        this.queue = queue;
        this.msgFunc = msgFunc;
    }

    @Override
    public Optional<T> next() {
        try {
            final GetResponse basicGet = channel.basicGet(queue, true);
            if (basicGet != null)
                return msgFunc.apply(MessageFactory.create(basicGet));
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

}
