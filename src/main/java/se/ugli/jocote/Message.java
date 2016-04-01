package se.ugli.jocote;

import static java.util.Collections.unmodifiableMap;

import java.util.HashMap;
import java.util.Map;

public interface Message {

    byte[] body();

    String id();

    Map<String, Object> headers();

    Map<String, Object> properties();

    public static MessageBuilder builder() {
        return new MessageBuilder();
    }

    public static class MessageBuilder {

        private byte[] body;
        private String id;
        private final Map<String, Object> headers = new HashMap<>();
        private final Map<String, Object> properties = new HashMap<>();

        public MessageBuilder id(final String id) {
            this.id = id;
            return this;
        }

        public MessageBuilder body(final byte[] body) {
            this.body = body;
            return this;
        }

        public MessageBuilder header(final Object name, final Object value) {
            headers.put(name.toString(), value);
            return this;
        }

        public MessageBuilder headers(final Map<String, Object> headers) {
            if (headers != null)
                this.headers.putAll(headers);
            return this;
        }

        public MessageBuilder property(final Object name, final Object value) {
            properties.put(name.toString(), value);
            return this;
        }

        public MessageBuilder properties(final Map<String, Object> properties) {
            if (properties != null)
                this.properties.putAll(properties);
            return this;
        }

        public Message build() {
            return new MessageImpl(id, body, unmodifiableMap(headers), unmodifiableMap(properties));
        }
    }

    static class MessageImpl implements Message {

        private final String id;
        private final byte[] body;
        private final Map<String, Object> headers;
        private final Map<String, Object> properties;

        MessageImpl(final String id, final byte[] body, final Map<String, Object> headers, final Map<String, Object> properties) {
            this.id = id;
            this.body = body;
            this.headers = headers;
            this.properties = properties;
        }

        @Override
        public String id() {
            return id;
        }

        @Override
        public Map<String, Object> headers() {
            return headers;
        }

        @Override
        public Map<String, Object> properties() {
            return properties;
        }

        @Override
        public byte[] body() {
            return body;
        }

        @Override
        public String toString() {
            return "MessageImpl [id=" + id + ", headers=" + headers + ", properties=" + properties + ", body=" + new String(body) + "]";
        }

    }

}
