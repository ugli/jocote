package se.ugli.jocote.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;
import se.ugli.jocote.Session;

class RabbitMqSession implements Session {

    private boolean closable;
    private final Channel channel;
    private final Envelope envelope;
    private final Message message;

    RabbitMqSession(final Channel channel, final GetResponse response) {
        this.channel = channel;
        envelope = response.getEnvelope();
        message = MessageFactory.create(response);
    }

    @Override
    public Message message() {
        return message;
    }

    @Override
    public void ack() {
        try {
            closable = true;
            final long deliveryTag = envelope.getDeliveryTag();
            final boolean multiple = false;
            channel.basicAck(deliveryTag, multiple);
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    @Override
    public void nack() {
        try {
            final long deliveryTag = envelope.getDeliveryTag();
            final boolean multiple = false;
            final boolean requeue = true;
            channel.basicNack(deliveryTag, multiple, requeue);
            closable = true;
        }
        catch (final IOException e) {
            throw new JocoteException(e);
        }
    }

    boolean isClosable() {
        return closable;
    }

}
