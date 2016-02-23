package se.ugli.jocote.activemq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static se.ugli.jocote.DriverManager.getConnection;

import java.util.Optional;

import org.junit.Test;

import se.ugli.jocote.Connection;

public class ActiveMqDriverTest {

    @Test
    public void test() {
        try (Connection connection = getConnection("activemq:/TEST")) {
            connection.put("hello world");
            final Optional<String> msgOpt = connection.get(String.class);
            assertThat(msgOpt.isPresent(), equalTo(true));
            assertThat(msgOpt.get(), equalTo("hello world"));
        }
    }

}
