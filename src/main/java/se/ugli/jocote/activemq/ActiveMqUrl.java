package se.ugli.jocote.activemq;

class ActiveMqUrl {

    static final String defaultPort = "61616";
    static final String URL_PREFIX = "jms:activemq:queue@";

    static boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    final String host;
    final String port;
    final String queue;

    ActiveMqUrl(final String url) {
        final String withoutPrefix = url.replace(URL_PREFIX, "");
        final String[] abc = withoutPrefix.split(":");
        if (abc.length < 2)
            throw new IllegalArgumentException("Bad url: " + url);
        host = abc[0];
        if (abc.length == 2) {
            port = defaultPort;
            queue = abc[1];
        }
        else {
            port = abc[1];
            queue = abc[2];
        }

    }

}
