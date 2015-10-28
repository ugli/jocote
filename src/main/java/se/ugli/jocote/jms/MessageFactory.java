package se.ugli.jocote.jms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

class MessageFactory {

    public static Object createObjectMessage(final Message message) throws JMSException {
        if (message == null)
            return null;
        else if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            return textMessage.getText();
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

    public static Message createJmsMessage(final Session session, final Object message, final Map<String, Object> headers,
            final Map<String, Object> properties) throws JMSException, NoSuchMethodException, SecurityException, IllegalAccessException,
                    IllegalArgumentException, InvocationTargetException {
        if (message instanceof String) {
            final TextMessage textMessage = session.createTextMessage();
            addHeadersAndProperties(textMessage, headers, properties);
            textMessage.setText((String) message);
            return textMessage;
        }
        else if (message instanceof byte[]) {
            final BytesMessage bytesMessage = session.createBytesMessage();
            addHeadersAndProperties(bytesMessage, headers, properties);
            bytesMessage.writeBytes((byte[]) message);
            return bytesMessage;
        }
        else
            throw new UnsupportedOperationException(message.getClass().getName());
    }

    private static void addHeadersAndProperties(final Message message, final Map<String, Object> headers,
            final Map<String, Object> properties)
                    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, JMSException {
        addHeaders(message, headers);
        addProerties(message, properties);
    }

    private static void addProerties(final Message message, final Map<String, Object> properties) throws JMSException {
        if (properties != null)
            for (final Entry<String, Object> property : properties.entrySet())
                message.setObjectProperty(property.getKey(), property.getValue());

    }

    private static void addHeaders(final Message message, final Map<String, Object> headers)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if (headers != null)
            for (final Entry<String, Object> header : headers.entrySet()) {
                final Object headerValue = header.getValue();
                final String methodName = "setJMS" + header.getKey();
                final Class<?>[] parameterTypes = new Class[] { headerValue.getClass() };
                final Method method = message.getClass().getMethod(methodName, parameterTypes);
                method.invoke(message, headerValue);
            }
    }

}
