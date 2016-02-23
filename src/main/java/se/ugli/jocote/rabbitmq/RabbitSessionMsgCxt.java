package se.ugli.jocote.rabbitmq;

import java.io.IOException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.SessionMessageContext;

class RabbitSessionMsgCxt extends RabbitMqCxt implements SessionMessageContext {

    private boolean closable;
    private final Channel channel;
    private final Envelope envelope;

    public RabbitSessionMsgCxt(final Channel channel, final GetResponse response) {
        super(response);
        this.channel = channel;
        envelope = response.getEnvelope();
    }

    @Override
    public void acknowledgeMessage() {
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
    public void leaveMessage() {
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
