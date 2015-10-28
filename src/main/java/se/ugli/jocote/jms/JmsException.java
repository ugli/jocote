package se.ugli.jocote.jms;


public class JmsException extends RuntimeException {

	private static final long serialVersionUID = -7517619168104995161L;

	public JmsException(final Exception e) {
		super(e);
	}

}
