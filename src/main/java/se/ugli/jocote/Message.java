package se.ugli.jocote;

import static java.util.Collections.unmodifiableMap;
import static se.ugli.jocote.support.Id.newId;

import java.util.Arrays;
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

    public class MessageBuilder {

        private byte[] body;
        private String id = newId();
        private final Map<String, Object> headers = new HashMap<>();
        private final Map<String, Object> properties = new HashMap<>();

        public MessageBuilder id(final String id) {
            if (id != null)
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
        public boolean equals(final Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            final MessageImpl message = (MessageImpl) o;
            if (id != null ? !id.equals(message.id) : message.id != null)
                return false;
            if (!Arrays.equals(body, message.body))
                return false;
            if (headers != null ? !headers.equals(message.headers) : message.headers != null)
                return false;
            return properties != null ? properties.equals(message.properties) : message.properties == null;

        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + Arrays.hashCode(body);
            result = 31 * result + (headers != null ? headers.hashCode() : 0);
            result = 31 * result + (properties != null ? properties.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "MessageImpl [id=" + id + ", headers=" + headers + ", properties=" + properties + ", bodySize=" + body.length + "]";
        }

    }

}
