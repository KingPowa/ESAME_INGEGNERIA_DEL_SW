package is.entity;

import java.util.ArrayList;

import is.exception.ElaboratoAssente;

public class Richiesta {

	
	//Impose order in arrayList
	private ArrayList<Elaborato> elaborati;
	private Studente studente;
	private int elaboratiRifiutati = 0;
	
	
	public Richiesta(ArrayList<Elaborato> elaborati, Studente studente) {
		this.elaborati = elaborati;
		this.studente = studente;
	}
	
	public Richiesta(ArrayList<Elaborato> elaborati, Studente studente, int elabRif) {
		this.elaborati = elaborati;
		this.studente = studente;
		this.elaboratiRifiutati = elabRif;
	}
	
	public void setElabRif(int elabRif) {
		this.elaboratiRifiutati = elabRif;
	}
	
	public int getElabRif() {
		return this.elaboratiRifiutati;
	}
	
	public ArrayList<Elaborato> getElaborati() {
		return elaborati;
	}
	public void setElaborati(ArrayList<Elaborato> elaborati) {
		this.elaborati = elaborati;
	}
	public Studente getStudente() {
		return studente;
	}
	public void setStudente(Studente studente) {
		this.studente = studente;
	}
	
	public void aggiungiElaborato(Elaborato elaborato) {
		if(!elaborati.contains(elaborato) && elaborati.size() < 3) {
			elaborati.add(elaborato);
		}
		else {
			if(!elaborati.contains(elaborato)) {
				System.out.println("Elaborato già presente");
			}
			else {
				System.out.println("Array Pieno");
			}
		}
	}
	
	public Elaborato rimuoviElaborato(int id) throws ElaboratoAssente{
		for(Elaborato e : elaborati) {
			if(e.getID() == id) {
				Elaborato result = e;
				elaborati.remove(e);
				return result;
			}
		}
		throw new ElaboratoAssente("Elaborato di id " + id + "non presente.\n");
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((studente == null) ? 0 : studente.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		Richiesta other = (Richiesta) obj;
		if(this.studente.equals(other.getStudente())) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[Elaborati: " + elaborati + ", studente: " + studente + "]";
	}
	
	/*
	 * public void orderElaborati() { elaborati.sort(new ElaboratoComparator());
	 * while (elaborati.size() < 3) { elaborati.add(null); } if(elaborati.size() >
	 * 3) { while(elaborati.size() != 3) { elaborati.remove( elaborati.size() - 1 );
	 * } } }
	 */
}
