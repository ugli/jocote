package se.ugli.jocote.support;

import java.util.Optional;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.Message;

public class DefaultConsumer implements Consumer<byte[]> {

    @Override
    public Optional<byte[]> receive(final Message cxt) {
        return Optional.of(cxt.getBody());
    }

}
