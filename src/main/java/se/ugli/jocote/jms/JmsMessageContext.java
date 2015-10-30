package se.ugli.jocote.jms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.MessageContext;

class JmsMessageContext implements MessageContext {

    @SuppressWarnings("unused")
    private static Map<String, Object> createHeadersReflection(final Message message) {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        if (message != null)
            for (final Method messageMethod : message.getClass().getMethods()) {
                final String methodName = messageMethod.getName();
                if (methodName.startsWith("getJMS") && messageMethod.getModifiers() == 1) {
                    final String headerName = methodName.substring(6);
                    try {
                        final Object headerValue = messageMethod.invoke(message);
                        if (headerValue != null)
                            result.put(headerName, headerValue);
                    }
                    catch (final IllegalAccessException e) {
                        throw new JocoteException(e);
                    }
                    catch (final InvocationTargetException e) {
                        throw new JocoteException(e);
                    }
                }
            }
        return result;
    }

    private static Map<String, Object> createHeaders(final Message message) {
        final HashMap<String, Object> result = new HashMap<String, Object>();
        if (message != null)
            try {
                putHeadedIfPresent(message, result, "CorrelationID", message.getJMSCorrelationID());
                putHeadedIfPresent(message, result, "CorrelationIDAsBytes", message.getJMSCorrelationIDAsBytes());
                putHeadedIfPresent(message, result, "DeliveryMode", message.getJMSDeliveryMode());
                putHeadedIfPresent(message, result, "Destination", message.getJMSDestination());
                putHeadedIfPresent(message, result, "Expiration", message.getJMSExpiration());
                putHeadedIfPresent(message, result, "MessageID", message.getJMSMessageID());
                putHeadedIfPresent(message, result, "Priority", message.getJMSPriority());
                putHeadedIfPresent(message, result, "Redelivered", message.getJMSRedelivered());
                putHeadedIfPresent(message, result, "ReplyTo", message.getJMSReplyTo());
                putHeadedIfPresent(message, result, "Timestamp", message.getJMSTimestamp());
                putHeadedIfPresent(message, result, "Type", message.getJMSType());
            }
            catch (final JMSException e) {
                throw new JocoteException(e);
            }
        return result;
    }

    private static void putHeadedIfPresent(final Message message, final HashMap<String, Object> result, final String name,
            final Object value) throws JMSException {
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

    protected final Message message;

    JmsMessageContext(final Message message) {
        this.message = message;
        headers = createHeaders(message);
        properties = createProperties(message);
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
