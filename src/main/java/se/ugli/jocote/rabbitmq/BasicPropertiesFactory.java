package se.ugli.jocote.rabbitmq;

import static java.util.stream.Collectors.toMap;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.AppId;
import static se.ugli.jocote.rabbitmq.RabbitMqProperties.ClusterId;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;

class BasicPropertiesFactory {

    private final boolean durable;

    public BasicPropertiesFactory(final boolean durable) {
        this.durable = durable;
    }

    BasicProperties create(final Message message) {
        final Builder builder = new Builder();

        builder.appId(toStr(message.properties().get(AppId.name())));

        builder.clusterId(toStr(message.properties().get(ClusterId.name())));

        builder.contentEncoding(toStr(message.properties().get(ContentEncoding.name())));

        final String contentType = toStr(message.properties().get(ContentType.name()));
        if (contentType != null)
            builder.contentType(contentType);
        else
            builder.contentType("text/plain");

        builder.correlationId(toStr(message.properties().get(CorrelationId.name())));

        final Integer deliveryMode = toInt(message.properties().get(DeliveryMode.name()));
        if (deliveryMode != null)
            builder.deliveryMode(deliveryMode);
        else if (durable)
            builder.deliveryMode(2);
        else
            builder.deliveryMode(1);

        builder.expiration(toStr(message.properties().get(Expiration.name())));

        builder.headers(headers(message));

        builder.messageId(message.id());

        final Integer priority = toInt(message.properties().get(Priority.name()));
        if (priority != null)
            builder.priority(priority);
        else
            builder.priority(0);

        builder.replyTo(toStr(message.properties().get(ReplyTo.name())));

        builder.timestamp(toDate(message.properties().get(Timestamp.name())));

        builder.type(toStr(message.properties().get(Type.name())));

        builder.userId(toStr(message.properties().get(UserId.name())));

        return builder.build();
    }

    private Map<String, Object> headers(final Message message) {
        return message.headers().entrySet().stream().collect(toMap(Entry::getKey, BasicPropertiesFactory::headerValue));
    }

    private static Object headerValue(final Entry<String, Object> entry) {
        final Object value = entry.getValue();
        if (value instanceof Temporal || value instanceof Calendar)
            return toDate(value);
        // TODO java.util.List, java.util.Map, Object[]
        return value;
    }

    private static Integer toInt(final Object value) {
        if (value == null || value instanceof Integer)
            return (Integer) value;
        return Integer.parseInt(value.toString());
    }

    private static String toStr(final Object value) {
        if (value == null || value instanceof String)
            return (String) value;
        return value.toString();
    }

    private static Date toDate(final Object value) {
        if (value == null)
            return null;
        else if (value instanceof Date)
            return (Date) value;
        else if (value instanceof LocalDate)
            return Date.from(((LocalDate) value).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        else if (value instanceof LocalDateTime)
            return Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
        else if (value instanceof Calendar)
            return Date.from(((Calendar) value).toInstant());
        throw new JocoteException("Couldn't convert to java.util.Date: " + value);
    }

}
