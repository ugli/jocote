package se.ugli.jocote.ram;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.HashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import se.ugli.jocote.Connection;
import se.ugli.jocote.Consumer;
import se.ugli.jocote.DriverManager;
import se.ugli.jocote.MessageContext;

public class RamConnectionTest {

    @BeforeClass
    public static void registerDriver() {
        DriverManager.register(new RamDriver());
    }

    private Connection connection;

    @Before
    public void connect() {
        connection = DriverManager.getConnection("ram@APA");
    }

    @After
    public void close() throws IOException {
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

}
