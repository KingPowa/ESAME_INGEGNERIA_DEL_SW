package is.exception;

import is.entity.Richiesta;

public class PendingRequest extends Exception {

	private Richiesta richiesta;
	private static final long serialVersionUID = 1L;

	public PendingRequest(String info, Richiesta richiesta) {
		super(info);
		this.richiesta = richiesta;
	}
	
	public Richiesta getRequest() {
		return richiesta;
	}
}