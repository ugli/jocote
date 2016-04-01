package se.ugli.jocote.jms;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Session;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.Message;

class JmsMessageFactory {

    public static javax.jms.Message create(final Session session, final Message jocoteMsg) {
        try {
            final BytesMessage jmsMsg = session.createBytesMessage();
            jmsMsg.writeBytes(jocoteMsg.body());
            addHeaders(jmsMsg, jocoteMsg);
            addProperties(jmsMsg, jocoteMsg);
            return jmsMsg;
        }
        catch (final JMSException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new JocoteException(e);
        }
    }

    private static void addHeaders(final javax.jms.Message jmsMsg, final Message jocoteMsg)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        for (final String headerName : jocoteMsg.headers().keySet()) {
            final Object headerValue = jocoteMsg.headers().get(headerName);
            final String methodName = "setJMS" + headerName;
            final Class<?>[] parameterTypes = new Class[] { headerValue.getClass() };
            final Method method = jmsMsg.getClass().getMethod(methodName, parameterTypes);
            method.invoke(jmsMsg, headerValue);
        }
    }

    private static void addProperties(final javax.jms.Message jmsMsg, final Message jocoteMsg) throws JMSException {
        for (final String propertyName : jocoteMsg.properties().keySet())
            jmsMsg.setObjectProperty(propertyName, jocoteMsg.properties().get(propertyName));
    }

}