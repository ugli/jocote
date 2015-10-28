package se.ugli.jocote.jms;

public enum AcknowledgeMode {

	AUTO_ACKNOWLEDGE(1), CLIENT_ACKNOWLEDGE(2), DUPS_OK_ACKNOWLEDGE(3), SESSION_TRANSACTED(0);

	public final int mode;

	private AcknowledgeMode(final int mode) {
		this.mode = mode;
	}

}
