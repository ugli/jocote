package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.Optional;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.support.MessageIterator;

class RabbitMqIterator implements MessageIterator {

    private final Channel channel;
    private final String queue;
    private int index = 0;

    RabbitMqIterator(final Channel channel, final String queue) {
        this.channel = channel;
        this.queue = queue;
    }

    @Override
    public Optional<Message> next() {
        try {
            final GetResponse basicGet = channel.basicGet(queue, true);
            if (basicGet != null) {
                index++;
                return Optional.of(MessageFactory.create(basicGet));
            }
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public int index() {
        return index;
    }

}
