package se.ugli.jocote.activemq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.ugli.jocote.Connection;
import se.ugli.jocote.DriverManager;

public class ActiveMqDriverTest {

    @Test
    public void test() {
        final Connection connection = DriverManager.getConnection("activemq:/TEST");
        connection.put("hello world");
        assertThat(connection.get().toString(), equalTo("hello world"));
        connection.close();
    }

}
