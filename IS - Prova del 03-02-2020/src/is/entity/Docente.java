package is.entity;

import java.util.ArrayList;
import is.exception.ElaboratoAssente;

public class Docente {

	private int matDocente;
	private String nome;
	private String cognome;
	private ArrayList<Elaborato> elaborati;
	
	public Docente(int matDocente, String nome, String cognome, ArrayList<Elaborato> elaborati) {
		super();
		this.matDocente = matDocente;
		this.nome = nome;
		this.cognome = cognome;
		this.elaborati = elaborati;
	}

	public Docente(int matDocente, String nome, String cognome) {
		super();
		this.matDocente = matDocente;
		this.nome = nome;
		this.cognome = cognome;
		this.elaborati = new ArrayList<Elaborato>();
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

	public int getMatDocente() {
		return matDocente;
	}
	
	public void setMatDocente(int matDocente) {
		this.matDocente = matDocente;
	}
	
	public ArrayList<Elaborato> getElaborati() {
		return elaborati;
	}
	
	public void setElaborati(ArrayList<Elaborato> elaborati) {
		this.elaborati = elaborati;
	}
	
	public void aggiungiElaborato(Elaborato elaborato) {
		if(!elaborati.contains(elaborato)) {
			elaborati.add(elaborato);
		}
		else {
			System.out.println("Elaborato già presente");
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
		return matDocente;
	}

	@Override
	public boolean equals(Object obj) {
		Docente other = (Docente) obj;
		if(this.matDocente == other.getMatDocente()) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "[MatricolaDocente: " + matDocente + "]";
	}
	
	
	
}
