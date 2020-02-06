package is.entity;

import java.sql.Date;

public class Assegnazione {
	
	private Elaborato elaborato;
	private Date date;
	private Studente studente;
	
	
	public Assegnazione(Elaborato elaborato, Studente studente) {
		this.elaborato = elaborato;
		this.date = new Date(System.currentTimeMillis());
		this.studente = studente;
	}
	
	
	public Assegnazione(Elaborato elaborato, Studente studente, Date date) {
		this.elaborato = elaborato;
		this.date = date;
		this.studente = studente;
	}


	public Elaborato getElaborato() {
		return elaborato;
	}
	public void setElaborato(Elaborato elaborato) {
		this.elaborato = elaborato;
	}
	public Date getDate() {
		return date;
	}
	public Studente getStudente() {
		return studente;
	}
	public void setStudente(Studente studente) {
		this.studente = studente;
	}


	@Override
	public String toString() {
		return "[Elaborati: " + elaborato + ", Data: " + date + ", Studente: " + studente + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((elaborato == null) ? 0 : elaborato.hashCode());
		result = prime * result + ((studente == null) ? 0 : studente.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		Assegnazione other = (Assegnazione) obj;
		if(this.elaborato.equals(other.getElaborato()) && this.studente.equals(other.getStudente())) {
			return true;
		}
		return false;
	}
	
	
}
