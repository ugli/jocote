package se.ugli.jocote;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class JocoteUrl {

    public final String scheme;
    public final String host;
    public final Integer port;
    public final String username;
    public final String password;
    public final String queue;
    public final Map<String, String> params;

    private JocoteUrl(final String url) {
        final URI uri = URI.create(url);
        this.scheme = uri.getScheme();
        if (scheme == null)
            throw new JocoteException("Url must define scheme");
        this.host = uri.getHost();
        if (uri.getPort() != -1)
            this.port = uri.getPort();
        else
            this.port = null;
        if (host == null && uri.getAuthority() != null) {
            final Pair userInfo = new Pair(uri.getAuthority(), ":");
            this.username = userInfo.p1;
            if (userInfo.p2 != null)
                this.password = userInfo.p2.replace("@", "");
            else
                this.password = null;
        }
        else {
            final Pair userInfo = new Pair(uri.getUserInfo(), ":");
            this.username = userInfo.p1;
            this.password = userInfo.p2;
        }
        queue = createQueue(uri.getPath());
        if (queue == null)
            throw new JocoteException("Url must define queue");
        params = createQuery(uri.getQuery());
    }

    private String createQueue(final String path) {
        if (path != null) {
            if (path.startsWith("/"))
                return path.substring(1);
            return path;
        }
        return null;
    }

    private Map<String, String> createQuery(final String queryStr) {
        if (queryStr != null) {
            final Map<String, String> result = new LinkedHashMap<String, String>();
            final StringTokenizer st = new StringTokenizer(queryStr, "&");
            while (st.hasMoreTokens()) {
                final Pair pair = new Pair(st.nextToken(), "=");
                final String key = pair.p1;
                final String value = pair.p2;
                if (key != null & value != null)
                    result.put(key, value);
            }
            return Collections.unmodifiableMap(result);
        }
        return Collections.emptyMap();
    }

    public static JocoteUrl apply(final String url) {
        return new JocoteUrl(url);
    }

    private static class Pair {

        final String p1, p2;

        Pair(final String str, final String delim) {
            if (str != null) {
                final String[] split = str.split(delim);
                if (split.length == 2) {
                    p1 = split[0];
                    if (split[1].isEmpty())
                        p2 = null;
                    else
                        p2 = split[1];
                }
                else if (split.length == 1) {
                    p1 = split[0];
                    p2 = null;
                }
                else {
                    p1 = null;
                    p2 = null;
                }
            }
            else {
                p1 = null;
                p2 = null;
            }
        }
    }

    private String passwordToString() {
        if (password != null) {
            final StringBuilder sb = new StringBuilder();
            for (int i = 0; i < password.length(); i++)
                sb.append("*");
            return sb.toString();
        }
        return null;
    }

    @Override
    public String toString() {
        return "JocoteUrl [scheme=" + scheme + ", host=" + host + ", port=" + port + ", username=" + username + ", password="
                + passwordToString() + ", queue=" + queue + ", params=" + params + "]";
    }

}
