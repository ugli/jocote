package se.ugli.jocote.linkedqueue;

import java.util.Map;

import se.ugli.jocote.MessageContext;

class LinkedQueueMessageContext implements MessageContext {

    private final Map<String, Object> headers;
    private final Map<String, Object> properties;

    public LinkedQueueMessageContext(final Message message) {
        this.headers = message.headers;
        this.properties = message.properties;
    }

    public LinkedQueueMessageContext(final Map<String, Object> headers, final Map<String, Object> properties) {
        this.headers = headers;
        this.properties = properties;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

}
