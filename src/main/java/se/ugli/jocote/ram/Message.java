package se.ugli.jocote.ram;

import java.util.Map;

class Message {

    final MessageId id;
    final byte[] body;
    final Map<String, Object> headers;
    final Map<String, Object> properties;

    Message(final byte[] body, final Map<String, Object> headers, final Map<String, Object> properties) {
        this.id = new MessageId();
        this.body = body;
        this.headers = headers;
        this.properties = properties;

    }

}
