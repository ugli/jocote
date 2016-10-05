package se.ugli.jocote;

import java.util.Optional;

public interface MessageIterator {

    Optional<Message> next();

}
