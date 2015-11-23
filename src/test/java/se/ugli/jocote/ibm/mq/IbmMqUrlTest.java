package se.ugli.jocote.ibm.mq;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import se.ugli.jocote.support.JocoteUrl;

public class IbmMqUrlTest {

    @Test
    public void shouldGiveQueueWithoutParams() {
        final JocoteUrl url = JocoteUrl.apply("ibmmq:/AbC");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.queue, equalTo("AbC"));
    }

    @Test
    public void shouldGiveQueueWithParams() {
        final JocoteUrl url = JocoteUrl.apply("ibmmq:/AbC?TransportType=1");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.queue, equalTo("AbC"));
    }

    @Test
    public void shouldHaveOneParam() {
        final JocoteUrl url = JocoteUrl.apply("ibmmq:/AbC?TransportType=1");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.params.get("TransportType"), equalTo("1"));
    }

    @Test
    public void shouldHaveTowParams() {
        final JocoteUrl url = JocoteUrl.apply("ibmmq:/AbC?TransportType=1&QueueManager=Test");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.params.get("TransportType"), equalTo("1"));
        assertThat(url.params.get("QueueManager"), equalTo("Test"));
    }

    @Test
    public void shouldHandleUserPasswordHostPort() {
        final JocoteUrl url = JocoteUrl
                .apply("ibmmq://fredde:pas1@localhost:1414/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.username, equalTo("fredde"));
        assertThat(url.password, equalTo("pas1"));
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo(1414));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

    @Test
    public void shouldHandleUserPassword() {
        final JocoteUrl url = JocoteUrl.apply("ibmmq://fredde:pas1@/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.username, equalTo("fredde"));
        assertThat(url.password, equalTo("pas1"));
        assertThat(url.host, equalTo(null));
        assertThat(url.port, equalTo(null));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

    @Test
    public void shouldHandleUserHostPort() {
        final JocoteUrl url = JocoteUrl
                .apply("ibmmq://fredde@localhost:1414/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.username, equalTo("fredde"));
        assertThat(url.password, equalTo(null));
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo(1414));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

    @Test
    public void shouldHandleHostPort() {
        final JocoteUrl url = JocoteUrl
                .apply("ibmmq://localhost:1414/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.username, equalTo(null));
        assertThat(url.password, equalTo(null));
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo(1414));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

    @Test
    public void shouldHandleHost() {
        final JocoteUrl url = JocoteUrl.apply("ibmmq://localhost/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN");
        assertThat(url.scheme, equalTo("ibmmq"));
        assertThat(url.username, equalTo(null));
        assertThat(url.password, equalTo(null));
        assertThat(url.host, equalTo("localhost"));
        assertThat(url.port, equalTo(null));
        assertThat(url.queue, equalTo("J80.DTST2.RECEIVEQ.BQ"));
        assertThat(url.params.get("QueueManager"), equalTo("QM.FTMQ"));
        assertThat(url.params.get("Channel"), equalTo("J80.DTST2.SVRCONN"));
    }

}
