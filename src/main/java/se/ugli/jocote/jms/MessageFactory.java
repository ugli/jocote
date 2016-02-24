package se.ugli.jocote.jms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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

    public static Message createJmsMessage(final Session session, final se.ugli.jocote.Message jocoteMsg) {
        try {
            final BytesMessage jmsMsg = session.createBytesMessage();
            addHeadersAndProperties(jmsMsg, jocoteMsg);
            jmsMsg.writeBytes(jocoteMsg.body());
            return jmsMsg;
        }
        catch (final JMSException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JocoteException(e);
        }
    }

    private static void addHeaders(final Message jmsMsg, final se.ugli.jocote.Message jocoteMsg)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (final String headerName : jocoteMsg.headerNames()) {
            final Object headerValue = jocoteMsg.header(headerName);
            final String methodName = "setJMS" + headerName;
            final Class<?>[] parameterTypes = new Class[] { headerValue.getClass() };
            final Method method = jmsMsg.getClass().getMethod(methodName, parameterTypes);
            method.invoke(jmsMsg, headerValue);
        }
    }

    private static void addHeadersAndProperties(final Message jmsMsg, final se.ugli.jocote.Message jocoteMsg)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, JMSException {
        addHeaders(jmsMsg, jocoteMsg);
        addProperties(jmsMsg, jocoteMsg);
    }

    private static void addProperties(final Message jmsMsg, final se.ugli.jocote.Message jocoteMsg) throws JMSException {
        for (final String propertyName : jocoteMsg.propertyNames())
            jmsMsg.setObjectProperty(propertyName, jocoteMsg.property(propertyName));

    }

}
