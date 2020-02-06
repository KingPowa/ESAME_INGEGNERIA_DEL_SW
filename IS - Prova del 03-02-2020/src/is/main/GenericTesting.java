package is.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import is.control.GestioneElaborati;
import is.db.exception.DAOException;
import is.entity.*;
import is.exception.FailException;
import is.exception.RichiestaRespinta;
import is.db.DBManager;

public class GenericTesting {

	public static void main(String[] args) {
		
		GestioneElaborati gestoreElab = new GestioneElaborati();
		
		try {
			Connection conn = DBManager.getConnection();
			
		} catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Database related problem.\n");
		}
		
		//Create Table
		boolean allTable = DBManager.docI.createTable() && DBManager.studI.createTable() && DBManager.elabI.createTable() && DBManager.assI.createTable() && DBManager.ricI.createTable();
		if(!allTable) {
			System.out.println("Non è stato possibile creare le tabelle.\n");
		}
		
		//Creazione Docente/Studente/Elaborato
		try {
			Docente docente1 = DBManager.docI.create(new Docente(1, "Roberto", "Natella"));
			Studente studente1 = DBManager.studI.create(new Studente(0, 10, "Tecla", "Perenze")); 
			Studente studente2 = DBManager.studI.create(new Studente(1, 10, "Tecla", "Perenze"));
			Elaborato elaborato1 = DBManager.elabI.create(new Elaborato(1, "IS", docente1)); 
			Elaborato elaborato2 = DBManager.elabI.create(new Elaborato(2, "IA", docente1)); 
			Elaborato elaborato3 = DBManager.elabI.create(new Elaborato(3, "OW", docente1));
			ArrayList<Elaborato> list = new ArrayList<Elaborato>(); 
			list.add(elaborato1);
			list.add(null); 
			list.add(elaborato2); 
			/*
			Assegnazione ass1 = DBManager.assI.create(new Assegnazione(elaborato1, studente1)); 
			Richiesta ric1 = DBManager.ricI.create(new Richiesta(list, studente1));
			Richiesta richiesta = DBManager.ricI.read(1);
			DBManager.ricI.delete(richiesta);
			*/
		} catch(DAOException e) {
			e.printStackTrace();
		}
		/*
		try {	
			Docente docente1 = DBManager.docI.read(1);
			Elaborato elaborato1 = gestoreElab.aggiuntaElaborato("EL", docente1);
			gestoreElab.rimuoviElaborato(elaborato1, docente1);
			
			gestoreElab.mostraElencoElaborati(docente1);
			gestoreElab.mostraElencoElaboratiAssegnati(docente1);
			gestoreElab.mostraElencoElaboratiDisponibili();
		} catch (FailException | DAOException e) {
			e.printStackTrace();
		}
		System.out.println("done");
		*/
		
		try {
			Docente docente1 = DBManager.docI.read(1);
			Studente studente1 = DBManager.studI.read(1);
			ArrayList<Elaborato> elaborati = new ArrayList<Elaborato>();
			Elaborato elaborato1 = DBManager.elabI.read(1);
			Elaborato elaborato2 = DBManager.elabI.read(2); 
			Elaborato elaborato3 = DBManager.elabI.read(3);
			Assegnazione assegnazione = gestoreElab.richiestaAssegnazione(studente1);
		} catch (FailException | DAOException e) {
			e.printStackTrace();
		} catch (RichiestaRespinta rr) {
			System.out.println(rr.getMessage());
		}
	}

}
