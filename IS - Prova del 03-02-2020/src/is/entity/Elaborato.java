package is.entity;

public class Elaborato {
	
	private int ID;
	private String insegnamento;
	private Docente docente;
	private boolean assegnato;
	
	public Elaborato(int id, String insegnamento, Docente docente, boolean assegnato){
		this.ID = id;
		this.insegnamento = insegnamento;
		this.docente = docente;
		this.assegnato = assegnato;
	}

	public Elaborato(int id, String insegnamento, Docente docente) {
		this.ID = id;
		this.insegnamento = insegnamento;
		this.docente = docente;
		this.assegnato = false;
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public String getInsegnamento() {
		return insegnamento;
	}

	public void setInsegnamento(String insegnamento) {
		this.insegnamento = insegnamento;
	}

	public Docente getDocente() {
		return docente;
	}

	public void setDocente(Docente docente) {
		this.docente = docente;
	}

	public void setAssegnato(boolean assegnato) {
		this.assegnato = assegnato;
	}
	
	public boolean isAssegnato() {
		return assegnato;
	}
	
	public void assegnaElaborato() {
		this.assegnato = true;
	}
	
	public void rimuoviElaborato() {
		this.assegnato = false;
	}

	@Override
	public String toString() {
		return String.format("[ID: " + ID + ", Insegnamento: " + insegnamento + ", Docente: " + docente + "]");
	}

	@Override
	public int hashCode() {
		return ID;
	}

	@Override
	public boolean equals(Object obj) {
		Elaborato other = (Elaborato) obj;
		if(this.ID == other.getID()) {
			return true;
		}
		return false;
	}	
	
}
