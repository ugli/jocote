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
                        throw new JocoteException(e);
                    }
                    catch (final InvocationTargetException e) {
                        throw new JocoteException(e);
                    }
                }
            }
        return result;
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
