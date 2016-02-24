package se.ugli.jocote.rabbitmq;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.Message;

class RabbitMessage implements Message {

    private final BasicProperties properties;
    private final byte[] body;

    RabbitMessage(final GetResponse response) {
        this(response.getBody(), response.getProps());
    }

    public RabbitMessage(final byte[] body, final BasicProperties properties) {
        this.body = body;
        this.properties = properties;
    }

    @Override
    public String id() {
        if (properties != null)
            return properties.getMessageId();
        return null;
    }

    private Map<String, Object> headers() {
        if (properties != null && properties.getHeaders() != null)
            return properties.getHeaders();
        return Collections.emptyMap();
    }

    @Override
    public Set<String> headerNames() {
        return headers().keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T header(final String headerName) {
        return (T) headers().get(headerName);
    }

    @Override
    public Set<String> propertyNames() {
        return headers().keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T property(final String propertyName) {
        return (T) headers().get(propertyName);
    }

    @Override
    public byte[] body() {
        return body;
    }

}
