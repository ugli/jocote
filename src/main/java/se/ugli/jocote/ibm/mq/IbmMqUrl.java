package se.ugli.jocote.ibm.mq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import se.ugli.jocote.JocoteException;

class IbmMqUrl {

    //jms:ibmmq://fredde:pas1@localhost:1414/J80.DTST2.RECEIVEQ.BQ?QueueManager=QM.FTMQ&Channel=J80.DTST2.SVRCONN
    private static final String URL_PREFIX = "jms:ibmmq://";

    static boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    final String username;
    final String password;
    final String host;
    final String port;
    final String queue;
    final Map<String, String> params;

    IbmMqUrl(final String url) {
        if (!acceptsURL(url))
            throw new JocoteException("Bad url: " + url);
        try {
            final String withoutPrefix = url.replace(URL_PREFIX, "");
            {
                final String connection = withoutPrefix.substring(0, withoutPrefix.indexOf('/'));
                if (connection.contains("@")) {
                    username = connection.substring(0, connection.indexOf(':'));
                    password = connection.substring(connection.indexOf(':') + 1, connection.indexOf('@'));
                    final String hostPort = connection.substring(connection.indexOf('@') + 1);
                    if (hostPort.isEmpty()) {
                        host = null;
                        port = null;
                    }
                    else if (hostPort.contains(":")) {
                        host = hostPort.substring(0, hostPort.indexOf(':'));
                        port = hostPort.substring(hostPort.indexOf(':') + 1);
                    }
                    else {
                        host = hostPort;
                        port = null;
                    }
                }
                else if (!connection.isEmpty()) {
                    if (connection.contains(":")) {
                        host = connection.substring(0, connection.indexOf(':'));
                        port = connection.substring(connection.indexOf(':') + 1);
                    }
                    else {
                        host = connection;
                        port = null;
                    }
                    username = null;
                    password = null;
                }
                else {
                    username = null;
                    password = null;
                    host = null;
                    port = null;
                }
            }
            final int paramStartIndex = withoutPrefix.indexOf('?');
            if (paramStartIndex == -1) {
                params = Collections.emptyMap();
                queue = withoutPrefix.substring(withoutPrefix.indexOf('/') + 1, withoutPrefix.length());
            }
            else {
                queue = withoutPrefix.substring(withoutPrefix.indexOf('/') + 1, paramStartIndex);
                params = createParams(withoutPrefix.substring(paramStartIndex + 1));
            }
        }
        catch (final RuntimeException e) {
            throw new JocoteException("Bad url: " + url);
        }

    }

    private Map<String, String> createParams(final String paramStr) {
        final StringTokenizer st = new StringTokenizer(paramStr, "&");
        final HashMap<String, String> result = new HashMap<String, String>();
        while (st.hasMoreTokens()) {
            final String token = st.nextToken();
            final int eqIndex = token.indexOf('=');
            if (eqIndex > 0)
                result.put(token.substring(0, eqIndex), token.substring(eqIndex + 1, token.length()));
        }
        return result;
    }

}
