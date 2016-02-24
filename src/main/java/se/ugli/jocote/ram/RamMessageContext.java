package se.ugli.jocote.ram;

import java.util.Map;
import java.util.Set;

import se.ugli.jocote.MessageContext;

class RamMessageContext implements MessageContext {

    private final Map<String, Object> headers;
    private final Map<String, Object> properties;
    private final MessageId messageId;
    private final byte[] body;

    RamMessageContext(final byte[] body, final MessageId messageId, final Map<String, Object> headers,
            final Map<String, Object> properties) {
        this.body = body;
        this.messageId = messageId;
        this.headers = headers;
        this.properties = properties;
    }

    RamMessageContext(final RamMessage message) {
        this.messageId = message.id;
        this.headers = message.headers;
        this.properties = message.properties;
        this.body = message.body;
    }

    @Override
    public String getMessageId() {
        return messageId.value;
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
