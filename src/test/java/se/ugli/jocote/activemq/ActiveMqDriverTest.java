package se.ugli.jocote.activemq;

import org.junit.Test;
import se.ugli.jocote.Connection;
import se.ugli.jocote.Message;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static se.ugli.jocote.Jocote.connect;

public class ActiveMqDriverTest {

    @Test
    public void test() {
        try (Connection connection = connect("activemq:/TEST")) {
            connection.put("hello world".getBytes());
            final Optional<Message> msgOpt = connection.get();
            assertThat(msgOpt.isPresent(), equalTo(true));
            assertThat(new String(msgOpt.get().body()), equalTo("hello world"));
        }
    }

}
