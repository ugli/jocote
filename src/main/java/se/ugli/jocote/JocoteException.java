package se.ugli.jocote;

public class JocoteException extends RuntimeException {

    private static final long serialVersionUID = -7517619168104995161L;

    public JocoteException(final Exception e) {
        super(e);
    }

    public JocoteException(final String message) {
        super(message);
    }

}
