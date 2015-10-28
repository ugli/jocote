package se.ugli.jocote;

import java.util.Map;

public interface MessageContext {

    Map<String, Object> getHeaders();

    Map<String, Object> getProperties();

}
