package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Function;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;

public class BasicGet {

    private final Channel channel;
    private final String queue;

    private BasicGet(final Channel channel, final String queue) {
        this.channel = channel;
        this.queue = queue;
    }

    public static BasicGet apply(final Channel channel, final String queue) {
        return new BasicGet(channel, queue);
    }

    public <T> Optional<T> get(final Function<Message, Optional<T>> msgFunc) {
        try {
            final GetResponse basicGet = channel.basicGet(queue, true);
            if (basicGet != null)
                return msgFunc.apply(new RabbitMessage(basicGet));
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

}
