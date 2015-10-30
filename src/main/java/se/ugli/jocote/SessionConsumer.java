package se.ugli.jocote;

public interface SessionConsumer<T> {

    T receive(Object message, SessionMessageContext cxt);

}
