package se.ugli.jocote.jms;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;

class MessageFactory {

    public static final String CorrelationID = "CorrelationID";
    public static final String CorrelationIDAsBytes = "CorrelationIDAsBytes";
    public static final String DeliveryMode = "DeliveryMode";
    public static final String Destination = "Destination";
    public static final String Expiration = "Expiration";
    public static final String MessageID = "MessageID";
    public static final String Priority = "Priority";
    public static final String Redelivered = "Redelivered";
    public static final String ReplyTo = "ReplyTo";
    public static final String Timestamp = "Timestamp";
    public static final String Type = "Type";

    static Message create(final javax.jms.Message msg) {
        try {
            final String id = msg.getJMSMessageID();
            final byte[] body = body(msg);
            final Map<String, Object> headers = headers(msg);
            final Map<String, Object> properties = properties(msg);
            return se.ugli.jocote.Message.builder().id(id).body(body).headers(headers).properties(properties).build();
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    private static byte[] body(final javax.jms.Message message) throws JMSException {
        if (message == null)
            return null;
        else if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            return textMessage.getText().getBytes();
        }
        else if (message instanceof BytesMessage) {
            final BytesMessage bytesMessage = (BytesMessage) message;
            final byte[] bytes = new byte[(int) bytesMessage.getBodyLength()];
            bytesMessage.readBytes(bytes);
            return bytes;
        }
        else
            throw new UnsupportedOperationException(message.getClass().getName());
    }

    private static Map<String, Object> headers(final javax.jms.Message message) throws JMSException {
        final Map<String, Object> result = new HashMap<String, Object>();
        putHeadedIfPresent(result, CorrelationID, message.getJMSCorrelationID());
        putHeadedIfPresent(result, CorrelationIDAsBytes, message.getJMSCorrelationIDAsBytes());
        putHeadedIfPresent(result, DeliveryMode, message.getJMSDeliveryMode());
        putHeadedIfPresent(result, Destination, message.getJMSDestination());
        putHeadedIfPresent(result, Expiration, message.getJMSExpiration());
        putHeadedIfPresent(result, MessageID, message.getJMSMessageID());
        putHeadedIfPresent(result, Priority, message.getJMSPriority());
        putHeadedIfPresent(result, Redelivered, message.getJMSRedelivered());
        putHeadedIfPresent(result, ReplyTo, message.getJMSReplyTo());
        putHeadedIfPresent(result, Timestamp, message.getJMSTimestamp());
        putHeadedIfPresent(result, Type, message.getJMSType());
        return result;
    }

    private static void putHeadedIfPresent(final Map<String, Object> result, final String name, final Object value) {
        if (value != null)
            result.put(name, value);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> properties(final javax.jms.Message message) throws JMSException {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        final Enumeration<String> propertyNames = message.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            final String propertyName = propertyNames.nextElement();
            result.put(propertyName, message.getObjectProperty(propertyName));
        }
        return result;
    }

}
