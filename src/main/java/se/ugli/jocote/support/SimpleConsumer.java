package se.ugli.jocote.support;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.MessageContext;

public class SimpleConsumer<T> implements Consumer<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T receive(final Object message, final MessageContext cxt) {
        return (T) message;
    }

}
