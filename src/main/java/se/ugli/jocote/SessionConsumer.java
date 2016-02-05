package se.ugli.jocote;

@FunctionalInterface
public interface SessionConsumer<T> {

    T receive(Object message, SessionMessageContext cxt);

}
