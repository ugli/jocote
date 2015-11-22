package se.ugli.jocote.ibm.mq;

import org.junit.Test;
import se.ugli.jocote.Connection;

import java.io.IOException;

public class IbmMqDriverTest {

    @Test
    public void test() throws IOException {
        final IbmMqDriver driver = new IbmMqDriver();
        final Connection connection = driver.getConnection("jms:ibmmq:queue@I05.DTST.PUBLISH.EVENT.BQ?hostName=mvsprod");
        final Object object = connection.get();
        System.out.println(object);
        connection.close();
    }

}
