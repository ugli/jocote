package se.ugli.jocote.log;

import org.junit.Test;
import se.ugli.jocote.Connection;
import se.ugli.jocote.Jocote;
import se.ugli.jocote.JocoteException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class LogConnectionTest {

    @Test
    public void shouldConnect() {
        try (Connection connection = Jocote.connect("log:///INFO")) {
            connection.put("hej".getBytes());
        }
    }

    @Test
    public void shouldThrow() {
        try (Connection connection = Jocote.connect("log:///APA")) {
            fail();
        }
        catch (final JocoteException e) {
            assertThat(e.getMessage(), is("Valid queues: [ERROR, WARN, INFO, DEBUG, TRACE]"));
        }
    }

}
