package se.ugli.jocote.jms;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.MessageContext;

import javax.jms.JMSException;
import javax.jms.Message;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static se.ugli.jocote.jms.JmsHeaders.*;

class JmsMessageContext implements MessageContext {

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
            } catch (final JMSException e) {
                throw new JocoteException(e);
            }
        return result;
    }

    private static void putHeadedIfPresent(final Map<String, Object> result, final String name,
                                           final Object value) {
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
        } catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    private final Map<String, Object> headers;
    private final Map<String, Object> properties;

    JmsMessageContext(final Message message) {
        headers = createHeaders(message);
        properties = createProperties(message);
    }

    @Override
    public String getMessageId() {
        return (String) headers.get(MessageID);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getHeader(final String headerName) {
        return (T) headers.get(headerName);
    }

    @Override
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getProperty(final String propertyName) {
        return (T) properties.get(propertyName);
    }

    @Override
    public Set<String> getPropertyNames() {
        return properties.keySet();
    }

}
