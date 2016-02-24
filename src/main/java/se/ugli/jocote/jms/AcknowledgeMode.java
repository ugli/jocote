package se.ugli.jocote.jms;

import javax.jms.Session;

enum AcknowledgeMode {

    AUTO_ACKNOWLEDGE(Session.AUTO_ACKNOWLEDGE),
    CLIENT_ACKNOWLEDGE(Session.CLIENT_ACKNOWLEDGE),
    DUPS_OK_ACKNOWLEDGE(Session.DUPS_OK_ACKNOWLEDGE),
    SESSION_TRANSACTED(Session.SESSION_TRANSACTED);

    final int mode;

    AcknowledgeMode(final int mode) {
        this.mode = mode;
    }

}
