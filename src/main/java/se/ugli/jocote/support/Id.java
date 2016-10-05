package se.ugli.jocote.support;

import static java.util.UUID.randomUUID;

public class Id {

    public static String newId() {
        return randomUUID().toString().replace("-", "");
    }

}
