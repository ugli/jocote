package se.ugli.jocote.rabbitmq;

import java.util.Optional;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.MessageContext;

class DefaultConsumer implements Consumer<byte[]> {

    @Override
    public Optional<byte[]> receive(final byte[] msg, final MessageContext cxt) {
        return Optional.ofNullable(msg);
    }

}
