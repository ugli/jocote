package se.ugli.jocote;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class DriverTest {

    @SuppressWarnings("rawtypes")
    @Parameterized.Parameters(name = "{0}")
    public static Collection primeNumbers() {
        return Arrays.asList(new Object[][] { { "ActiveMQ", "activemq:/APA" }, { "RAM", "ram:/APA" } });
    }

    private final String url;

    public DriverTest(final String testName, final String url) {
        this.url = url;
    }

    @Before
    public void clearQueue() {
        try (Connection connection = DriverManager.getConnection(url)) {
            final Iterator<Object> iterator = connection.iterator();
            while (iterator.next().isPresent())
                ;
        }
    }

    @Test
    public void shouldHandleGetAndPutString() {
        try (Connection connection = DriverManager.getConnection(url)) {
            assertThat(connection.get().isPresent(), equalTo(false));
            connection.put("hej");
            final Optional<String> message = connection.get();
            assertThat(message.get(), equalTo("hej"));
        }
    }

    @Test
    public void shouldHandleGetAndPutBytes() {
        try (Connection connection = DriverManager.getConnection(url)) {
            assertThat(connection.get().isPresent(), equalTo(false));
            connection.put("hej".getBytes());
            final Optional<byte[]> bytes = connection.get();
            assertThat(new String(bytes.get()), equalTo("hej"));
        }
    }

    @Test
    public void shouldGetHeaderValue() {
        try (Connection connection = DriverManager.getConnection(url)) {
            final HashMap<String, Object> header = new HashMap<String, Object>();
            header.put("CorrelationID", "B");
            connection.put("hej", header, null);
            connection.get((Consumer<String>) (msg, cxt) -> {
                assertThat(cxt.getHeaderNames().contains("CorrelationID"), equalTo(true));
                assertThat(cxt.getHeader("CorrelationID").toString(), equalTo("B"));
                return Optional.ofNullable((String) msg);
            });
        }
    }

    @Test
    public void shouldGetPropertyValue() {
        try (Connection connection = DriverManager.getConnection(url)) {
            final HashMap<String, Object> properties = new HashMap<String, Object>();
            properties.put("environment", "TEST");
            connection.put("hej", null, properties);
            connection.get((Consumer<String>) (msg, cxt) -> {
                assertThat(cxt.getPropertyNames().contains("environment"), equalTo(true));
                assertThat(cxt.getProperty("environment").toString(), equalTo("TEST"));
                return Optional.ofNullable((String) msg);
            });
        }
    }

    @Test
    public void shouldAcknowledgeMessage() {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.put("hej");
            connection.get((SessionConsumer<String>) (msg, cxt) -> {
                cxt.acknowledgeMessage();
                return Optional.ofNullable((String) msg);
            });
            assertThat(connection.get().isPresent(), equalTo(false));
        }
    }

    @Test
    public void shouldLeaveMessage() {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.put("hej");
            connection.get((SessionConsumer<String>) (msg, cxt) -> {
                cxt.leaveMessage();
                return Optional.ofNullable((String) msg);
            });
            assertThat(connection.<String> get().get(), equalTo("hej"));
        }
    }

    @Test(expected = JocoteException.class)
    public void shouldThrowThenNotLeavingOrAcknowledgeMessage() {
        try (Connection connection = DriverManager.getConnection(url)) {
            connection.put("hej");
            connection.get((SessionConsumer<String>) (msg, cxt) -> Optional.ofNullable((String) msg));
        }
    }

    @Test
    public void shouldConsumeIterator() {
        try (Connection connection = DriverManager.getConnection(url)) {
            for (int i = 0; i <= 100; i++)
                connection.put(String.valueOf(i));
            final Iterator<String> iterator = connection.iterator();
            int sum = 0;
            int count = 0;
            Optional<String> next = iterator.next();
            while (next.isPresent()) {
                next = iterator.next();
                if (next.isPresent()) {
                    count++;
                    sum += Integer.parseInt(next.get());
                }
            }
            assertThat(count, equalTo(100));
            assertThat(sum, equalTo(5050));
            assertThat(connection.get().isPresent(), equalTo(false));
        }
    }

    @Test
    public void shouldAcknoledgeIterator() {
        try (Connection connection = DriverManager.getConnection(url)) {
            for (int i = 0; i <= 100; i++)
                connection.put(String.valueOf(i));
            final SessionIterator<String> iterator = connection.sessionIterator();
            int sum = 0;
            int count = 0;
            Optional<String> next = iterator.next();
            while (next.isPresent()) {
                next = iterator.next();
                if (next.isPresent()) {
                    count++;
                    sum += Integer.parseInt(next.get());
                }
            }
            iterator.acknowledgeMessages();
            iterator.close();
            assertThat(count, equalTo(100));
            assertThat(sum, equalTo(5050));
            assertThat(connection.get().isPresent(), equalTo(false));
        }
    }

    @Test
    public void shouldLeaveIterator() {
        try (Connection connection = DriverManager.getConnection(url)) {
            for (int i = 0; i < 100; i++)
                connection.put(String.valueOf(i));
            final SessionIterator<String> iterator = connection.sessionIterator();
            int sum = 0;
            int count = 0;
            Optional<String> next = iterator.next();
            while (next.isPresent()) {
                count++;
                sum += Integer.parseInt(next.get());
                next = iterator.next();
            }
            iterator.leaveMessages();
            iterator.close();
            assertThat(count, equalTo(100));
            assertThat(sum, equalTo(4950));
            assertThat(connection.<String> get().get(), equalTo("0"));
        }
    }

    @Test(expected = JocoteException.class)
    public void shouldThrowThenNotLeavingOrAcknowledgeMessageIterator() {
        try (Connection connection = DriverManager.getConnection(url)) {
            for (int i = 0; i <= 100; i++)
                connection.put(String.valueOf(i));
            final SessionIterator<String> iterator = connection.sessionIterator();
            int sum = 0;
            int count = 0;
            Optional<String> next = iterator.next();
            while (next.isPresent()) {
                next = iterator.next();
                if (next.isPresent()) {
                    count++;
                    sum += Integer.parseInt(next.get());
                }
            }
            assertThat(count, equalTo(100));
            assertThat(sum, equalTo(5050));
            iterator.close();
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
        try (Connection connection = DriverManager.getConnection(url)) {
            for (int i = 0; i < 100; i++)
                connection.put(String.valueOf(i));
            final IntWrap sum = new IntWrap();
            final IntWrap count = new IntWrap();
            final Subscription<String> subscription = DriverManager.subscribe(url, (msg, cxt) -> {
                final String next = (String) msg;
                count.i++;
                sum.i += Integer.parseInt(next);
                return Optional.ofNullable(next);
            });
            Thread.sleep(10);
            subscription.close();
            assertThat(count.i, equalTo(100));
            assertThat(sum.i, equalTo(4950));
            assertThat(connection.get().isPresent(), equalTo(false));
        }
    }

    @Test
    public void shouldGetValuesAfterSubscribe() throws InterruptedException {
        try (Connection connection = DriverManager.getConnection(url)) {
            final IntWrap sum = new IntWrap();
            final IntWrap count = new IntWrap();
            final Subscription<String> subscription = DriverManager.subscribe(url, (msg, cxt) -> {
                final String next = (String) msg;
                count.i++;
                sum.i += Integer.parseInt(next);
                return Optional.ofNullable(next);
            });
            for (int i = 0; i < 100; i++)
                connection.put(String.valueOf(i));
            Thread.sleep(10);
            subscription.close();
            assertThat(count.i, equalTo(100));
            assertThat(sum.i, equalTo(4950));
            assertThat(connection.get().isPresent(), equalTo(false));
        }
    }

}
