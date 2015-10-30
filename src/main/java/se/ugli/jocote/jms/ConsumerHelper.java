package se.ugli.jocote.jms;

import javax.jms.Message;

import se.ugli.jocote.Consumer;

class ConsumerHelper {

    static <T> T sendReceive(final Consumer<T> consumer, final Message message) {
        return consumer.receive(MessageFactory.createObjectMessage(message), new JmsMessageContext(message));
    }

}
