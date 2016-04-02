package se.ugli.jocote.rabbitmq;

import static java.util.stream.Collectors.toMap;
import static se.ugli.jocote.Message.builder;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.AppId;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.ContentEncoding;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.ContentType;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.CorrelationId;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.DeliveryMode;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.Expiration;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.Priority;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.ReplyTo;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.Timestamp;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.Type;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.UserId;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.GetResponse;
import com.rabbitmq.client.LongString;

import se.ugli.jocote.Message;

class MessageFactory {

    static Message create(final GetResponse response) {
        final BasicProperties props = response.getProps();
        final byte[] body = response.getBody();
        return builder().id(props.getMessageId()).body(body).properties(properties(props)).headers(headers(props)).build();
    }

    static Message create(final String consumerTag, final Envelope envelope, final BasicProperties props, final byte[] body) {
        return builder().id(props.getMessageId()).body(body).properties(properties(props)).headers(headers(props)).build();
    }

    private static Map<String, Object> headers(final BasicProperties props) {
        return props.getHeaders().entrySet().stream().collect(toMap(Entry::getKey, MessageFactory::headerValue));
    }

    private static Object headerValue(final Entry<String, Object> entry) {
        final Object value = entry.getValue();
        if (value instanceof LongString)
            return value.toString();
        else if (value instanceof Date)
            return toLocalDateTime((Date) value);
        // TODO java.util.List, java.util.Map, Object[]
        return value;
    }

    public static Map<String, Object> properties(final BasicProperties properties) {
        final HashMap<String, Object> result = new HashMap<>();
        result.put(AppId.name(), properties.getAppId());
        result.put(ContentEncoding.name(), properties.getContentEncoding());
        result.put(ContentType.name(), properties.getContentType());
        result.put(CorrelationId.name(), properties.getCorrelationId());
        result.put(DeliveryMode.name(), properties.getDeliveryMode());
        result.put(Expiration.name(), properties.getExpiration());
        result.put(Priority.name(), properties.getPriority());
        result.put(ReplyTo.name(), properties.getReplyTo());
        result.put(Timestamp.name(), toLocalDateTime(properties.getTimestamp()));
        result.put(Type.name(), properties.getType());
        result.put(UserId.name(), properties.getUserId());
        return result;
    }

    private static LocalDateTime toLocalDateTime(final Date date) {
        if (date == null)
            return null;
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

}
