package se.ugli.jocote.jms;

import static se.ugli.jocote.jms.JmsHeaders.CorrelationID;
import static se.ugli.jocote.jms.JmsHeaders.CorrelationIDAsBytes;
import static se.ugli.jocote.jms.JmsHeaders.DeliveryMode;
import static se.ugli.jocote.jms.JmsHeaders.Destination;
import static se.ugli.jocote.jms.JmsHeaders.Expiration;
import static se.ugli.jocote.jms.JmsHeaders.MessageID;
import static se.ugli.jocote.jms.JmsHeaders.Priority;
import static se.ugli.jocote.jms.JmsHeaders.Redelivered;
import static se.ugli.jocote.jms.JmsHeaders.ReplyTo;
import static se.ugli.jocote.jms.JmsHeaders.Timestamp;
import static se.ugli.jocote.jms.JmsHeaders.Type;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import se.ugli.jocote.JocoteException;

class JmsMessage implements se.ugli.jocote.Message {

    private static Map<String, Object> createHeaders(final Message message) {
        final Map<String, Object> result = new HashMap<String, Object>();
        if (message != null)
            try {
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
            }
            catch (final JMSException e) {
                throw new JocoteException(e);
            }
        return result;
    }

    private static void putHeadedIfPresent(final Map<String, Object> result, final String name, final Object value) {
        if (value != null)
            result.put(name, value);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> createProperties(final Message message) {
        try {
            final HashMap<String, Object> result = new HashMap<String, Object>();
            if (message != null) {
                final Enumeration<String> propertyNames = message.getPropertyNames();
                while (propertyNames.hasMoreElements()) {
                    final String propertyName = propertyNames.nextElement();
                    result.put(propertyName, message.getObjectProperty(propertyName));
                }
            }
            return result;
        }
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    private final Map<String, Object> headers;
    private final Map<String, Object> properties;
    private final byte[] body;

    JmsMessage(final Message message) {
        body = MessageFactory.getBytes(message);
        headers = createHeaders(message);
        properties = createProperties(message);
    }

    public JmsMessage(final byte[] message) {
        body = message;
        headers = Collections.emptyMap();
        properties = Collections.emptyMap();
    }

    @Override
    public String id() {
        return (String) headers.get(MessageID);
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
