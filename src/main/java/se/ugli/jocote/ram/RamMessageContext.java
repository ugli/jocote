package se.ugli.jocote.ram;

import java.util.Map;
import java.util.Set;

import se.ugli.jocote.MessageContext;

class RamMessageContext implements MessageContext {

    private final Map<String, Object> headers;
    private final Map<String, Object> properties;

    public RamMessageContext(final Map<String, Object> headers, final Map<String, Object> properties) {
        this.headers = headers;
        this.properties = properties;
    }

    public RamMessageContext(final Message message) {
        this.headers = message.headers;
        this.properties = message.properties;
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
}
