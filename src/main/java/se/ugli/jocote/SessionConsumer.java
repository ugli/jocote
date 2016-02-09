package se.ugli.jocote;

import java.util.Optional;

@FunctionalInterface
public interface SessionConsumer<T> {

    Optional<T> receive(Object msg, SessionMessageContext cxt);

}
