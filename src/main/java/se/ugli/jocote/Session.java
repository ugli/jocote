package se.ugli.jocote;

import java.util.Optional;

public interface Session extends SessionAware {

    Optional<Message> message();

}
