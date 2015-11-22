package se.ugli.jocote.activemq;

import org.junit.Test;
import se.ugli.jocote.JocoteException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ActiveMqUrlTest {

    @Test(expected = JocoteException.class)
    public void shouldHandleEmptyConnectionComponents() {
        new ActiveMqUrl("jms:activemq:queue@");
    }

    @Test(expected = JocoteException.class)
    public void shouldHandleEmptyHostConnectionComponents() {
        new ActiveMqUrl("jms:activemq:queue@:TEST");
    }

    @Test(expected = JocoteException.class)
    public void shouldHandleAccept() {
        new ActiveMqUrl("jmz:activemq:queue@APA");
    }

    @Test
    public void shouldHandleOneConnectionComponentsUrl() {
        ActiveMqUrl url = new ActiveMqUrl("jms:activemq:queue@APA");
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo("61616"));
        assertThat(url.queue, equalTo("APA"));
    }

    @Test
    public void shouldHandleTwoConnectionComponentsUrl() {
        ActiveMqUrl url = new ActiveMqUrl("jms:activemq:queue@kth.se:APA");
        assertThat(url.host, equalTo("kth.se"));
        assertThat(url.port, equalTo("61616"));
        assertThat(url.queue, equalTo("APA"));
    }

    @Test
    public void shouldHandleThreeConnectionComponentsUrl() {
        ActiveMqUrl url = new ActiveMqUrl("jms:activemq:queue@kth.se:666:APA");
        assertThat(url.host, equalTo("kth.se"));
        assertThat(url.port, equalTo("666"));
        assertThat(url.queue, equalTo("APA"));
    }


}
