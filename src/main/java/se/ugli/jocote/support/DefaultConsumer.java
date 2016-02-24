package se.ugli.jocote.support;

import java.util.Optional;
import java.util.function.Function;

import se.ugli.jocote.Message;

public class DefaultConsumer implements Function<Message, Optional<byte[]>> {

    @Override
    public Optional<byte[]> apply(final Message cxt) {
        return Optional.of(cxt.body());
    }

}
