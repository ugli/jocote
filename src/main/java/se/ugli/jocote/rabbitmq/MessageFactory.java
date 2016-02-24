package se.ugli.jocote.rabbitmq;

import java.util.Map;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Message;

class MessageFactory {

    static Message create(final GetResponse response) {
        final BasicProperties props = response.getProps();
        final Map<String, Object> headers = props.getHeaders();
        return Message.builder().id(props.getMessageId()).body(response.getBody()).properties(headers).headers(headers).build();
    }

}
