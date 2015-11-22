package se.ugli.jocote.jms;

enum AcknowledgeMode {

    AUTO_ACKNOWLEDGE(1), CLIENT_ACKNOWLEDGE(2), DUPS_OK_ACKNOWLEDGE(3), SESSION_TRANSACTED(0);

    final int mode;

    AcknowledgeMode(final int mode) {
        this.mode = mode;
    }

}
