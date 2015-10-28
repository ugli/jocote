package se.ugli.jocote;

public interface Consumer<T> {

    T receive(Object message, MessageContext cxt);

}
