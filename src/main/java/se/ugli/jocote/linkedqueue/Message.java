package se.ugli.jocote.linkedqueue;

import java.util.Map;

class Message {

    final Object body;
    final Map<String, Object> headers;
    final Map<String, Object> properties;

    Message(final Object body, final Map<String, Object> headers, final Map<String, Object> properties) {
        this.body = body;
        this.headers = headers;
        this.properties = properties;

    }

}
