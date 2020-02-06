package is.db.exception;

public class TabellaEsistente extends DAOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public TabellaEsistente(String info) {
		super(info);
	}
}
