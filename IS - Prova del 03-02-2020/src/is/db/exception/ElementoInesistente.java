package is.db.exception;

public class ElementoInesistente extends DAOException {

	private static final long serialVersionUID = 1L;

	public ElementoInesistente(String info) {
		super(info);
	}
}
