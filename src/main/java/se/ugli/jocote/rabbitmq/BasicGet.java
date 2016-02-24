package se.ugli.jocote.rabbitmq;

import java.io.IOException;
import java.util.Optional;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;

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

    public <T> Optional<T> get(final Consumer<T> consumer) {
        try {
            final GetResponse basicGet = channel.basicGet(queue, true);
            if (basicGet != null)
                return consumer.receive(new RabbitMqCxt(basicGet));
            return Optional.empty();
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

}
