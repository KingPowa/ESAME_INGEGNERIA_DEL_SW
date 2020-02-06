package is.exception;

//Occurs when creation fails
public class FailException extends Exception {

	private static final long serialVersionUID = 1L;

	public FailException(String info) {
		super(info);
	}
}
