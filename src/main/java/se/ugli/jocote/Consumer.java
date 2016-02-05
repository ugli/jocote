package se.ugli.jocote;

@FunctionalInterface
public interface Consumer<T> {

    T receive(Object message, MessageContext cxt);

}
