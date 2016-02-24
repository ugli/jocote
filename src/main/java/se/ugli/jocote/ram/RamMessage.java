package se.ugli.jocote.ram;

import java.util.Map;
import java.util.Set;

import se.ugli.jocote.Message;

class RamMessage implements Message {

    final MessageId id;
    final byte[] body;
    final Map<String, Object> headers;
    final Map<String, Object> properties;

    RamMessage(final byte[] body, final Map<String, Object> headers, final Map<String, Object> properties) {
        this.id = new MessageId();
        this.body = body;
        this.headers = headers;
        this.properties = properties;

    }

    @Override
    public String getMessageId() {
        return id.value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHeader(final String headerName) {
        return (T) headers.get(headerName);
    }

    @Override
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(final String propertyName) {
        return (T) properties.get(propertyName);
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

    @Override
    public byte[] getBody() {
        return body;
    }

}
