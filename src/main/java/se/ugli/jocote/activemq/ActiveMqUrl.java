package se.ugli.jocote.activemq;

import se.ugli.jocote.JocoteException;

class ActiveMqUrl {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "61616";
    private static final String URL_PREFIX = "jms:activemq:queue@";

    static boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    final String host;
    final String port;
    final String queue;

    ActiveMqUrl(final String url) {
        if (acceptsURL(url)) {
            final String withoutPrefix = url.replace(URL_PREFIX, "");
            final String[] connectionSpecs = withoutPrefix.split(":");
            for (String connectionSpec : connectionSpecs)
                if (connectionSpec.isEmpty())
                    throw new JocoteException("Bad url: " + url);
            if (connectionSpecs.length == 1) {
                host = DEFAULT_HOST;
                port = DEFAULT_PORT;
                queue = connectionSpecs[0];
            } else {
                host = connectionSpecs[0];
                if (connectionSpecs.length == 2) {
                    port = DEFAULT_PORT;
                    queue = connectionSpecs[1];
                } else {
                    if (connectionSpecs.length == 3) {
                        port = connectionSpecs[1];
                        queue = connectionSpecs[2];
                    } else {
                        throw new JocoteException("Bad url: " + url);
                    }
                }
            }
        } else
            throw new JocoteException("Bad url: " + url);
    }

}
