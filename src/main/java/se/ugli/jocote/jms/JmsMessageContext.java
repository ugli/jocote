package se.ugli.jocote.jms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;

import se.ugli.jocote.MessageContext;

class JmsMessageContext implements MessageContext {

    private final Message message;
    private final Map<String, Object> headers;

    JmsMessageContext(final Message message) {
        this.message = message;
        headers = createHeaders(message);
    }

    public void acknowledge() {
        try {
            message.acknowledge();
        }
        catch (final JMSException e) {
            throw new JmsException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> getProperties() {
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
            throw new JmsException(e);
        }
    }

    private static Map<String, Object> createHeaders(final Message message) {
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
                        throw new JmsException(e);
                    }
                    catch (final InvocationTargetException e) {
                        throw new JmsException(e);
                    }
                }
            }
        return result;
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headers;
    }

}
