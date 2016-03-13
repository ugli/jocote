package se.ugli.jocote;

import static java.lang.Integer.parseInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DriverTest {

    @SuppressWarnings("rawtypes")
    @Parameterized.Parameters(name = "{0}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] { { "ActiveMQ", "activemq:/APA" }, { "RAM", "ram:/APA" }, { "RabbitMQ", "rabbitmq:/APA" } });
    }

    private Connection connection;
    private final String url;

    public DriverTest(final String testName, final String url) {
        this.url = url;
    }

    @Before
    public void clearQueue() {
        connection = DriverManager.getConnection(url);
        final Iterator<byte[]> iterator = connection.iterator();
        while (iterator.next().isPresent())
            ;
        connection.close();
        connection = DriverManager.getConnection(url);
    }

    @After
    public void close() {
        connection.close();
        connection = null;
    }

    @Test
    public void shouldHandleGetAndPutBytes() {
        assertThat(connection.get().isPresent(), equalTo(false));
        connection.put("hej".getBytes());
        final Optional<byte[]> bytes = connection.get();
        assertThat(new String(bytes.get()), equalTo("hej"));
    }

    @Test
    public void shouldGetHeaderValue() {
        connection.put(Message.builder().body("hej".getBytes()).header("CorrelationID", "B").build());
        connection.get((msg) -> {
            assertThat(msg.headerNames().contains("CorrelationID"), equalTo(true));
            assertThat(msg.header("CorrelationID").toString(), equalTo("B"));
            return Optional.of(msg.body());
        });
    }

    @Test
    public void shouldGetPropertyValue() {
        connection.put(Message.builder().body("hej".getBytes()).property("environment", "TEST").build());
        connection.get((msg) -> {
            assertThat(msg.propertyNames().contains("environment"), equalTo(true));
            assertThat(msg.property("environment").toString(), equalTo("TEST"));
            return Optional.of(msg.body());
        });
    }

    @Test
    public void shouldAcknowledgeMessage() {
        connection.put("hej".getBytes());
        connection.getWithSession(session -> {
            session.acknowledgeMessage();
            return Optional.of(session.message());
        });
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldLeaveMessage() {
        connection.put("hej".getBytes());
        connection.getWithSession(session -> {
            session.leaveMessage();
            return Optional.of(session.message());
        });
        assertThat(new String(connection.get().get()), equalTo("hej"));
    }

    @Test
    public void shouldThrowThenNotLeavingOrAcknowledgeMessage() {
        try {
            connection.put("hej".getBytes());
            connection.getWithSession(session -> Optional.of(session.message()));
        }
        catch (final JocoteException e) {
            assertThat(e.getMessage(), equalTo("You have to acknowledge or leave message"));
        }
    }

    @Test
    public void shouldConsumeIterator() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        final Iterator<byte[]> iterator = connection.iterator();
        int sum = 0;
        int count = 0;
        Optional<byte[]> next = iterator.next();
        while (next.isPresent()) {
            next = iterator.next();
            if (next.isPresent()) {
                count++;
                sum += parseInt(new String(next.get()));
            }
        }
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(5050));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldConsumeStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        assertThat(connection.stream().mapToInt(b -> parseInt(new String(b))).sum(), equalTo(5050));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldAcknoledgeIterator() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        final SessionIterator<byte[]> iterator = connection.sessionIterator();
        int sum = 0;
        int count = 0;
        Optional<byte[]> next = iterator.next();
        while (next.isPresent()) {
            next = iterator.next();
            if (next.isPresent()) {
                count++;
                sum += parseInt(new String(next.get()));
            }
        }
        iterator.acknowledgeMessages();
        iterator.close();
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(5050));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldAcknoledgeStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        try (SessionStream stream = connection.sessionStream()) {
            assertThat(stream.mapToInt(b -> parseInt(new String(b))).sum(), equalTo(5050));
            stream.acknowledgeMessages();
        }
    }

    @Test
    public void shouldLeaveIterator() {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i).getBytes());
        final SessionIterator<byte[]> iterator = connection.sessionIterator();
        int sum = 0;
        int count = 0;
        Optional<byte[]> next = iterator.next();
        while (next.isPresent()) {
            count++;
            sum += parseInt(new String(next.get()));
            next = iterator.next();
        }
        iterator.leaveMessages();
        iterator.close();
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(4950));
        assertThat(new String(connection.get().get()), equalTo("0"));
    }

    @Test
    public void shouldLeaveStream() {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i).getBytes());
        try (SessionStream stream = connection.sessionStream()) {
            assertThat(stream.mapToInt(b -> parseInt(new String(b))).sum(), equalTo(4950));
            stream.leaveMessages();
        }
        assertThat(new String(connection.get().get()), equalTo("0"));
    }

    @Test
    public void shouldThrowThenNotLeavingOrAcknowledgeMessageIterator() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        final SessionIterator<byte[]> iterator = connection.sessionIterator();
        int sum = 0;
        int count = 0;
        Optional<byte[]> next = iterator.next();
        while (next.isPresent()) {
            next = iterator.next();
            if (next.isPresent()) {
                count++;
                sum += parseInt(new String(next.get()));
            }
        }
        assertThat(count, equalTo(100));
        assertThat(sum, equalTo(5050));
        try {
            iterator.close();
        }
        catch (final JocoteException e) {
            assertThat(e.getMessage(), equalTo("You have to acknowledge or leave messages before closing"));
        }
    }

    @Test
    public void shouldThrowThenNotLeavingOrAcknowledgeMessageStream() {
        for (int i = 0; i <= 100; i++)
            connection.put(String.valueOf(i).getBytes());
        try (SessionStream stream = connection.sessionStream()) {
            assertThat(stream.mapToInt(b -> parseInt(new String(b))).sum(), equalTo(5050));
        }
        catch (final JocoteException e) {
            assertThat(e.getMessage(), equalTo("You have to acknowledge or leave messages before closing"));
        }
    }

    private class IntWrap {
        int i = 0;

        @Override
        public String toString() {
            return "IntWrap [i=" + i + "]";
        }

    }

    @Test
    public void shouldGetValuesWhenSubscribe() throws InterruptedException {
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i).getBytes());
        final IntWrap sum = new IntWrap();
        final IntWrap count = new IntWrap();
        final Subscription subscription = DriverManager.subscribe(url, (msg) -> {
            final byte[] next = msg.body();
            count.i++;
            sum.i += parseInt(new String(next));
        });
        Thread.sleep(10);
        subscription.close();
        assertThat(count.i, equalTo(100));
        assertThat(sum.i, equalTo(4950));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

    @Test
    public void shouldGetValuesAfterSubscribe() throws InterruptedException {
        final IntWrap sum = new IntWrap();
        final IntWrap count = new IntWrap();
        final Subscription subscription = DriverManager.subscribe(url, (msg) -> {
            final byte[] next = msg.body();
            count.i++;
            sum.i += parseInt(new String(next));
        });
        for (int i = 0; i < 100; i++)
            connection.put(String.valueOf(i).getBytes());
        Thread.sleep(10);
        subscription.close();
        assertThat(count.i, equalTo(100));
        assertThat(sum.i, equalTo(4950));
        assertThat(connection.get().isPresent(), equalTo(false));
    }

}