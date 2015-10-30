package se.ugli.jocote;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import se.ugli.jocote.activemq.ActiveMqDriver;
import se.ugli.jocote.ram.RamDriver;

@RunWith(Parameterized.class)
public class DriverTest {

    @BeforeClass
    public static void registerDriver() {
        DriverManager.register(new ActiveMqDriver());
        DriverManager.register(new RamDriver());
    }

    @SuppressWarnings("rawtypes")
    @Parameterized.Parameters(name = "{0}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] { { "ActiveMQ", "jms:activemq:queue@localhost:61616:APA" }, { "RAM", "ram@APA" } });
    }

    private Connection connection;

    public DriverTest(final String testName, final String url) {
        connection = DriverManager.getConnection(url);
    }

    @After
    public void close() throws IOException {
        for (final Iterator<String> i = connection.iterator(); i.hasNext();)
            i.next();
        connection.close();
        connection = null;
    }

    @Test
    public void shouldHandleGetAndPutString() {
        assertThat(connection.get(), nullValue());
        connection.put("hej");
        final String message = connection.get();
        assertThat(message, equalTo("hej"));
    }

    @Test
    public void shouldHandleGetAndPutBytes() {
        assertThat(connection.<Object> get(), nullValue());
        connection.put("hej".getBytes());
        final byte[] bytes = connection.get();
        assertThat(new String(bytes), equalTo("hej"));
    }

    @Test
    public void shouldGetHeaderValue() {
        final HashMap<String, Object> header = new HashMap<String, Object>();
        header.put("CorrelationID", "B");
        connection.put("hej", header, null);
        connection.get(new Consumer<String>() {

            @Override
            public String receive(final Object message, final MessageContext cxt) {
                assertThat(cxt.getHeaderNames().contains("CorrelationID"), equalTo(true));
                assertThat(cxt.getHeader("CorrelationID").toString(), equalTo("B"));
                return (String) message;
            }
        });
    }

    @Test
    public void shouldGetPropertyValue() {
        final HashMap<String, Object> properties = new HashMap<String, Object>();
        properties.put("environment", "TEST");
        connection.put("hej", null, properties);
        connection.get(new Consumer<String>() {

            @Override
            public String receive(final Object message, final MessageContext cxt) {
                assertThat(cxt.getPropertyNames().contains("environment"), equalTo(true));
                assertThat(cxt.getProperty("environment").toString(), equalTo("TEST"));
                return (String) message;
            }
        });
    }

    @Test
    public void shouldAcknowledgeMessage() {
        connection.put("hej");
        connection.get(new SessionConsumer<String>() {

            @Override
            public String receive(final Object message, final SessionMessageContext cxt) {
                cxt.acknowledgeMessage();
                return (String) message;
            }
        });
        assertThat(connection.get(), nullValue());
    }

    @Test
    public void shouldLeaveMessage() {
        connection.put("hej");
        connection.get(new SessionConsumer<String>() {

            @Override
            public String receive(final Object message, final SessionMessageContext cxt) {
                cxt.leaveMessage();
                return (String) message;
            }
        });
        assertThat(connection.<String> get(), equalTo("hej"));
    }

    @Test(expected = JocoteException.class)
    public void shouldThrowThenNotLeavingOrAcknowledgeMessage() {
        connection.put("hej");
        connection.get(new SessionConsumer<String>() {

            @Override
            public String receive(final Object message, final SessionMessageContext cxt) {
                return (String) message;
            }
        });
    }

    @Test
    public void shouldConsumeIterator() throws IOException {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i));
        final Iterator<String> iterator = connection.iterator();
        int sum = 0;
        int count = 0;
        while (iterator.hasNext()) {
            final String next = iterator.next();
            count++;
            sum += Integer.parseInt(next);
        }
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(4950));
        assertThat(connection.get(), nullValue());
    }

    @Test
    public void shouldAcknolageIterator() throws IOException {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i));
        final SessionIterator<String> iterator = connection.sessionIterator();
        int sum = 0;
        int count = 0;
        while (iterator.hasNext()) {
            final String next = iterator.next();
            count++;
            sum += Integer.parseInt(next);
        }
        iterator.acknowledgeMessages();
        iterator.close();
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(4950));
        assertThat(connection.get(), nullValue());
    }

    @Test
    public void shouldLeaveIterator() throws IOException {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i));
        final SessionIterator<String> iterator = connection.sessionIterator();
        int sum = 0;
        int count = 0;
        while (iterator.hasNext()) {
            final String next = iterator.next();
            count++;
            sum += Integer.parseInt(next);
        }
        iterator.leaveMessages();
        iterator.close();
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(4950));
        assertThat(connection.<String> get(), equalTo("0"));
    }

    @Test(expected = JocoteException.class)
    public void shouldThrowThenNotLeavingOrAcknowlageMessageIterator() throws IOException {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i));
        final SessionIterator<String> iterator = connection.sessionIterator();
        int sum = 0;
        int count = 0;
        while (iterator.hasNext()) {
            final String next = iterator.next();
            count++;
            sum += Integer.parseInt(next);
        }
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(4950));
        iterator.close();
    }

}
