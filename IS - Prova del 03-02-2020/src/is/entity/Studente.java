package is.entity;

public class Studente {

	private int matricola;
	private int cfu;
	private boolean assegnato;
	private String nome;
	private String cognome;
	
	public Studente(int matricola, int cfu, String nome, String cognome, boolean assegnato) {
		this.matricola = matricola;
		this.cfu = cfu;
		this.assegnato = assegnato;
		this.cognome = cognome;
		this.nome = nome;
	}

	public Studente(int matricola, int cfu, String nome, String cognome) {
		this.matricola = matricola;
		this.cfu = cfu;
		this.assegnato = false;
		this.cognome = cognome;
		this.nome = nome;
	}

	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public int getMatricola() {
		return matricola;
	}
	
	public void setMatricola(int matricola) {
		this.matricola = matricola;
	}
	
	public int getCfu() {
		return cfu;
	}
	
	public void setCfu(int cfu) {
		this.cfu = cfu;
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
	
	public int incrementaCFU(int incremento) {
		cfu += incremento;
		return getCfu();
	}
	
	@Override
	public String toString() {
		return String.format("[Matricola: " + matricola + "]");
	}

	@Override
	public int hashCode() {
		return matricola;
	}

	@Override
	public boolean equals(Object obj) {
		Studente other = (Studente) obj;
		if(this.matricola == other.getMatricola()) {
			return true;
		}
		return false;
	}

	public void setAssegnato(boolean ass) {
		this.assegnato = ass;	
	}
	
}
