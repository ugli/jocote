package se.ugli.jocote;

import java.util.stream.Stream;

public interface MessageStream extends Stream<Message> {

    int elementIndex();
}
