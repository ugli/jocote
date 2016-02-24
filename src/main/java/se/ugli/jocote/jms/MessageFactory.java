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

import se.ugli.jocote.JocoteException;

class MessageFactory {

    public static byte[] getBytes(final Message message) {
        try {
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
        catch (final JMSException e) {
            throw new JocoteException(e);
        }
    }

    public static Message createJmsMessage(final Session session, final byte[] message, final Map<String, Object> headers,
            final Map<String, Object> properties) {
        try {
            final BytesMessage bytesMessage = session.createBytesMessage();
            addHeadersAndProperties(bytesMessage, headers, properties);
            bytesMessage.writeBytes(message);
            return bytesMessage;
        }
        catch (final JMSException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JocoteException(e);
        }
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

    private static void addHeadersAndProperties(final Message message, final Map<String, Object> headers,
            final Map<String, Object> properties)
                    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, JMSException {
        addHeaders(message, headers);
        addProperties(message, properties);
    }

    private static void addProperties(final Message message, final Map<String, Object> properties) throws JMSException {
        if (properties != null)
            for (final Entry<String, Object> property : properties.entrySet())
                message.setObjectProperty(property.getKey(), property.getValue());

    }

}
