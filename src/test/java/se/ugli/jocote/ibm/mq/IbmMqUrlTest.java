package se.ugli.jocote.ibm.mq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.ugli.jocote.JocoteException;

public class IbmMqUrlTest {

    @Test
    public void shouldGiveQueueWithoutParams() {
        assertThat(new IbmMqUrl("jms:ibmmq:///AbC").queue, equalTo("AbC"));
    }

    @Test
    public void shouldGiveQueueWithParams() {
        assertThat(new IbmMqUrl("jms:ibmmq:///AbC?TransportType=1").queue, equalTo("AbC"));
    }

    @Test
    public void shouldHaveOneParam() {
        assertThat(new IbmMqUrl("jms:ibmmq:///AbC?TransportType=1").params.get("TransportType"), equalTo("1"));
    }

    @Test
    public void shouldHaveTowParams() {
        final IbmMqUrl url = new IbmMqUrl("jms:ibmmq:///AbC?TransportType=1&QueueManager=Test");
        assertThat(url.params.get("TransportType"), equalTo("1"));
        assertThat(url.params.get("QueueManager"), equalTo("Test"));
    }

    @Test
    public void shouldHandleUserPasswordHostPort() {
        final IbmMqUrl url = new IbmMqUrl(
                "jms:ibmmq://fredde:pas1@localhost:1414/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.username, equalTo("fredde"));
        assertThat(url.password, equalTo("pas1"));
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo("1414"));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

    @Test
    public void shouldHandleUserPassword() {
        final IbmMqUrl url = new IbmMqUrl("jms:ibmmq://fredde:pas1@/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.username, equalTo("fredde"));
        assertThat(url.password, equalTo("pas1"));
        assertThat(url.host, equalTo(null));
        assertThat(url.port, equalTo(null));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

    @Test(expected = JocoteException.class)
    public void shouldHandleUserHostPort() {
        new IbmMqUrl("jms:ibmmq://fredde@localhost:1414/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
    }

    @Test
    public void shouldHandleHostPort() {
        final IbmMqUrl url = new IbmMqUrl(
                "jms:ibmmq://localhost:1414/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.username, equalTo(null));
        assertThat(url.password, equalTo(null));
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo("1414"));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

    @Test
    public void shouldHandleHost() {
        final IbmMqUrl url = new IbmMqUrl("jms:ibmmq://localhost/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.username, equalTo(null));
        assertThat(url.password, equalTo(null));
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo(null));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

}
