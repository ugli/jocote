package se.ugli.jocote.jms;

import java.util.Optional;

import javax.jms.Message;

import se.ugli.jocote.Consumer;

class ConsumerHelper {

    static <T> Optional<T> sendReceive(final Consumer<T> consumer, final Message message) {
        return consumer.receive(new JmsMessage(message));
    }

}
