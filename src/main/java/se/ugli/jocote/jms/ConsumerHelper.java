package se.ugli.jocote.jms;

import javax.jms.Message;

import se.ugli.jocote.Consumer;
import se.ugli.jocote.SessionConsumer;

class ConsumerHelper {

    static <T> T sendReceive(final Consumer<T> consumer, final Message message) {
        return consumer.receive(MessageFactory.createObjectMessage(message), new JmsMessageContext(message));
    }

    static <T> T sendReceive(final SessionConsumer<T> consumer, final Message message) {
        return consumer.receive(MessageFactory.createObjectMessage(message), new JmsSessionMessageContext(message));
    }

}
