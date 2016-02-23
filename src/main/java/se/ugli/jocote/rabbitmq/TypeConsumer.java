package se.ugli.jocote.rabbitmq;

import java.util.Optional;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.JocoteException;
import se.ugli.jocote.MessageContext;

class TypeConsumer<T> implements Consumer<T> {

    private final Class<T> type;

    public TypeConsumer(final Class<T> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Optional<T> receive(final Object msg, final MessageContext cxt) {
        if (msg == null)
            return Optional.empty();
        final byte[] bytes = (byte[]) msg;
        if (type == byte[].class)
            return Optional.of((T) bytes);
        else if (type == String.class)
            return Optional.of((T) new String(bytes));
        throw new JocoteException("Unsupported message type: " + type.getName());
    }

}
