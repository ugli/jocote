package se.ugli.jocote.support;

import java.util.Optional;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.MessageContext;

public class SimpleConsumer<T> implements Consumer<T> {

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> receive(final Object message, final MessageContext cxt) {
        return Optional.ofNullable((T) message);
    }

}
