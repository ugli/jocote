package se.ugli.jocote;

import java.util.Optional;

@FunctionalInterface
public interface Consumer<T> {

    Optional<T> receive(byte[] msg, MessageContext cxt);

}
