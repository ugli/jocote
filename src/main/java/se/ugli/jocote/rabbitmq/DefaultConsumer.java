package se.ugli.jocote.rabbitmq;

import java.util.Optional;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.MessageContext;

class DefaultConsumer implements Consumer<Object> {

    @Override
    public Optional<Object> receive(final Object msg, final MessageContext cxt) {
        return Optional.ofNullable(msg);
    }

}
