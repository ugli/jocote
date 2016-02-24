package se.ugli.jocote.rabbitmq;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.GetResponse;

import se.ugli.jocote.MessageContext;

class RabbitMqCxt implements MessageContext {

    private final BasicProperties properties;
    private final byte[] body;

    RabbitMqCxt(final GetResponse response) {
        this(response.getBody(), response.getProps());
    }

    public RabbitMqCxt(final byte[] body, final BasicProperties properties) {
        this.body = body;
        this.properties = properties;
    }

    @Override
    public String getMessageId() {
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
    public Set<String> getHeaderNames() {
        return headers().keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHeader(final String headerName) {
        return (T) headers().get(headerName);
    }

    @Override
    public Set<String> getPropertyNames() {
        return headers().keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(final String propertyName) {
        return (T) headers().get(propertyName);
    }

    @Override
    public byte[] getBody() {
        return body;
    }

}
