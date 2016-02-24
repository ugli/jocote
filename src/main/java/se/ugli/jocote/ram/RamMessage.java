package se.ugli.jocote.ram;

import java.util.Map;
import java.util.Set;

import se.ugli.jocote.Message;

public class RamMessage implements Message {

    final MessageId id;
    final byte[] body;
    final Map<String, Object> headers;
    final Map<String, Object> properties;

    public RamMessage(final byte[] body, final Map<String, Object> headers, final Map<String, Object> properties) {
        this.id = new MessageId();
        this.body = body;
        this.headers = headers;
        this.properties = properties;

    }

    @Override
    public String id() {
        return id.value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T header(final String headerName) {
        return (T) headers.get(headerName);
    }

    @Override
    public Set<String> headerNames() {
        return headers.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T property(final String propertyName) {
        return (T) properties.get(propertyName);
    }

    @Override
    public Set<String> propertyNames() {
        return properties.keySet();
    }

    @Override
    public byte[] body() {
        return body;
    }

}
