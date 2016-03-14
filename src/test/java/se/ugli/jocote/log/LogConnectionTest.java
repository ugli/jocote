package se.ugli.jocote.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import se.ugli.jocote.JocoteException;
import se.ugli.jocote.support.JocoteUrl;

public class LogConnectionTest {

    @Test
    public void shouldConnect() {
        try (LogConnection connection = new LogConnection(JocoteUrl.apply("log:///INFO"))) {
            connection.put("hej".getBytes());
        }
    }

    @Test
    public void shouldThrow() {
        try (LogConnection connection = new LogConnection(JocoteUrl.apply("log:///APA"))) {
            fail();
        }
        catch (final JocoteException e) {
            assertThat(e.getMessage(), is("Valid queues: [ERROR, WARN, INFO, DEBUG, TRACE]"));
        }
    }

}
