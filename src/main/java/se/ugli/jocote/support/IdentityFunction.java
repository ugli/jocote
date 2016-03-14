package se.ugli.jocote.support;

import java.util.Optional;
import java.util.function.Function;

import se.ugli.jocote.Message;

public class IdentityFunction implements Function<Message, Optional<Message>> {

    @Override
    public Optional<Message> apply(final Message message) {
        return Optional.of(message);
    }

}
