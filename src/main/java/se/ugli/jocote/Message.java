package se.ugli.jocote;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public interface Message {

    byte[] body();

    String id();

    Set<String> headerNames();

    <T> T header(String headerName);

    Set<String> propertyNames();

    <T> T property(String propertyName);

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

        public MessageBuilder header(final String name, final Object value) {
            headers.put(name, value);
            return this;
        }

        public MessageBuilder headers(final Map<String, Object> headers) {
            if (headers != null)
                this.headers.putAll(properties);
            return this;
        }

        public MessageBuilder property(final String name, final Object value) {
            properties.put(name, value);
            return this;
        }

        public MessageBuilder properties(final Map<String, Object> properties) {
            if (properties != null)
                this.properties.putAll(properties);
            return this;
        }

        public Message build() {
            return new MessageImpl(id, body, headers, properties);
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

}
