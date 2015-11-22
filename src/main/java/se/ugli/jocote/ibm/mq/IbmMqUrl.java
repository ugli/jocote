package se.ugli.jocote.ibm.mq;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

class IbmMqUrl {

    private static final String URL_PREFIX = "jms:ibmmq:queue@";

    static boolean acceptsURL(final String url) {
        return url.startsWith(URL_PREFIX);
    }

    final String queue;
    final Map<String, String> params;

    IbmMqUrl(final String url) {
        final String withoutPrefix = url.replace(URL_PREFIX, "");
        final int paramStartIndex = withoutPrefix.indexOf('?');
        if (paramStartIndex == -1) {
            queue = withoutPrefix;
            params = Collections.emptyMap();
        }
        else {
            queue = withoutPrefix.substring(0, paramStartIndex);
            params = createParams(withoutPrefix.substring(paramStartIndex + 1));
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
