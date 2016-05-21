package se.ugli.jocote.rabbitmq;

import com.rabbitmq.client.Channel;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.MessageIterator;

import java.io.IOException;
import java.util.Optional;

class RabbitMqIterator implements MessageIterator {

    private final Channel channel;
    private final String queue;

    RabbitMqIterator(final Channel channel, final String queue) {
        this.channel = channel;
        this.queue = queue;
    }

    @Override
    public Optional<Message> next() {
        try {
            return Optional.ofNullable(MessageFactory.create(channel.basicGet(queue, true)));
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

}
