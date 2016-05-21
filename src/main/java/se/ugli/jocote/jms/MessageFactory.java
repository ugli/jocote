package se.ugli.jocote.jms;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static se.ugli.jocote.jms.JmsConnection.JMS_MESSAGE_TYPE;

class MessageFactory {

    private static final String CorrelationID = "CorrelationID";
    private static final String CorrelationIDAsBytes = "CorrelationIDAsBytes";
    private static final String DeliveryMode = "DeliveryMode";
    private static final String Destination = "Destination";
    private static final String Expiration = "Expiration";
    private static final String MessageID = "MessageID";
    private static final String Priority = "Priority";
    private static final String Redelivered = "Redelivered";
    private static final String ReplyTo = "ReplyTo";
    private static final String Timestamp = "Timestamp";
    private static final String Type = "Type";

    static Message create(final javax.jms.Message msg) {
        try {
            if (msg == null)
                return null;
            final String id = msg.getJMSMessageID();
            final byte[] body = body(msg);
            final Map<String, Object> headers = headers(msg);
            final Map<String, Object> properties = properties(msg);
            return Message.builder().id(id).body(body).headers(headers).properties(properties).build();
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
        final Map<String, Object> result = new HashMap<>();
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
        final HashMap<String, Object> result = new HashMap<>();
        final Enumeration<String> propertyNames = message.getPropertyNames();
        while (propertyNames.hasMoreElements()) {
            final String propertyName = propertyNames.nextElement();
            result.put(propertyName, message.getObjectProperty(propertyName));
        }
        result.put(JMS_MESSAGE_TYPE, getJmsMessageType(message));
        return result;
    }

    private static Object getJmsMessageType(final javax.jms.Message message) {
        if (message instanceof TextMessage)
            return TextMessage.class.getName();
        else if (message instanceof BytesMessage)
            return BytesMessage.class.getName();
        return null;
    }

}
