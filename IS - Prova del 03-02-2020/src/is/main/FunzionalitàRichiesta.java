package is.main;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import is.control.GestioneElaborati;
import is.db.DBManager;
import is.db.exception.DAOException;
import is.db.exception.ElementoInesistente;
import is.db.exception.ElementoInvalido;
import is.entity.Assegnazione;
import is.entity.Docente;
import is.entity.Elaborato;
import is.entity.Richiesta;
import is.entity.Studente;
import is.exception.FailException;
import is.exception.RichiestaRespinta;

public class FunzionalitàRichiesta {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

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
		
		try {
			Docente docente = DBManager.docI.create(new Docente(1, "F", "F"));
			//Grep again from DB
			
			//solo 1 non ha assegnazioni
			Studente studente1 = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Studente studente2 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente3 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente4 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente5 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente6 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente7 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente8 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente9 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente10 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente11 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			Studente studente12 = DBManager.studI.create(new Studente(0, 120, "Nome", "Cognome"));
			
			//solo 2 verrà assegnato, anche se 3 è non assegnato

			Elaborato elaborato1 = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			Elaborato elaborato2 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato3 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato4 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato5 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato6 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato7 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato8 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato9 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato10 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato11 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato12 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			
			//Assegnazioni
			Assegnazione assegnazione1 = DBManager.assI.create(new Assegnazione(elaborato2, studente2));
			Assegnazione assegnazione2 = DBManager.assI.create(new Assegnazione(elaborato3, studente3));
			Assegnazione assegnazione3 = DBManager.assI.create(new Assegnazione(elaborato4, studente4));
			Assegnazione assegnazione6 = DBManager.assI.create(new Assegnazione(elaborato7, studente7));
			Assegnazione assegnazione7 = DBManager.assI.create(new Assegnazione(elaborato8, studente8));
			Assegnazione assegnazione8 = DBManager.assI.create(new Assegnazione(elaborato9, studente9));
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(false);
			Assegnazione assegnazione = gestoreElab.richiestaAssegnazione(studente1);
			gestoreElab.mostraElencoElaborati(docente);
			gestoreElab.mostraElencoElaboratiAssegnati(docente);
		} catch(ElementoInvalido | FailException e) {
			e.printStackTrace();
			fail();
		} catch(RichiestaRespinta rr) {
			System.out.println("Richiesta Respinta.\n");
		}
	}

}
