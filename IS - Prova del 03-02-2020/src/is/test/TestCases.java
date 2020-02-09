package is.test;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import is.db.DBManager;
import is.db.exception.*;
import is.entity.*;
import is.exception.FailException;
import is.exception.PendingRequest;
import is.exception.RichiestaRespinta;
import is.control.*;

public class TestCases {

	GestioneElaborati gElab = new GestioneElaborati();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		try {
			Connection conn = DBManager.getConnection();
			
		} catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Database related problem.\n");
		}
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		DBManager.closeConnection();
	}

	@Before
	public void setUp() throws Exception {
		//Create Table
		boolean allTable = DBManager.docI.createTable() && DBManager.studI.createTable() && DBManager.elabI.createTable() && DBManager.assI.createTable() && DBManager.ricI.createTable();
		if(!allTable) {
			System.out.println("Non è stato possibile creare le tabelle.\n");
		}

		DBManager.docI.create(new Docente(1, "DocenteF", "DocenteF", new ArrayList<Elaborato>()));
	}

	@After
	public void tearDown() throws Exception {
		Connection conn = null;
		try {
			conn = DBManager.getConnection();
			PreparedStatement stmt = conn.prepareStatement("DROP ALL OBJECTS");
			stmt.execute();
			System.out.println("Tabelle eliminate.\n");
		} catch (SQLException e ) { e.printStackTrace();}
	}

	@Test
	// E:0 - P:0 - S:A - A:NA - STD:NANR - CFU:>100
	public void test1StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			fail();
		} catch(ElementoInvalido | FailException e) {
			e.printStackTrace();
			fail();
		} catch(RichiestaRespinta rr) {
		}
	}
	
	@Test
	// E:1 - P:1 - S:A - A:A - STD:NANR - CFU:>100
	public void test2StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(true);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			studente = DBManager.studI.read(1);
			elaborato = DBManager.elabI.read(1);
			Assegnazione assegnazioneToTest = new Assegnazione(elaborato, studente);
			assertEquals(assegnazione, assegnazioneToTest);
		} catch(ElementoInvalido | ElementoInesistente | FailException | RichiestaRespinta e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	// E:1 - P:1 - S:R - A:A - STD:NANR - CFU:>100
	public void test3StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(false);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			fail();
		} catch(ElementoInvalido | ElementoInesistente | FailException e) {
			e.printStackTrace();
			fail();
		} catch (RichiestaRespinta rr){
			
		}
	}
	
	@Test
	// E:3 - P:1 - S:A - A:A - STD:NANR - CFU:>100
	public void test4StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato1 = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			Elaborato elaborato2 = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			Elaborato elaborato3 = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(true);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			studente = DBManager.studI.read(1);
			elaborato1 = DBManager.elabI.read(1);
			Assegnazione assegnazioneToTest = new Assegnazione(elaborato1, studente);
			assertEquals(assegnazione, assegnazioneToTest);
		} catch(ElementoInvalido | ElementoInesistente | FailException | RichiestaRespinta e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	// E:3 - P:1 - S:R - A:A - STD:NANR - CFU:>100
	public void test5StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato1 = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			Elaborato elaborato2 = DBManager.elabI.create(new Elaborato(2, "Insegnamento", docente));
			Elaborato elaborato3 = DBManager.elabI.create(new Elaborato(3, "Insegnamento", docente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(false);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			studente = DBManager.studI.read(1);
			elaborato2 = DBManager.elabI.read(2);
			Assegnazione assegnazioneToTest = new Assegnazione(elaborato2, studente);
			assertEquals(assegnazione, assegnazioneToTest);
		} catch(ElementoInvalido | ElementoInesistente | FailException | RichiestaRespinta e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	// E:3 - P:3 - S:A - A:ANA - STD:NANR - CFU:>100
	public void test6StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Docente docente = DBManager.docI.read(1);
			//Sarà il docente il cui elaborato verrà assegnato
			Docente docente2 = DBManager.docI.create(new Docente(0, "fit", "fit"));
			
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
			Elaborato elaborato2 = DBManager.elabI.create(new Elaborato(2, "Insegnamento", docente2));
			Elaborato elaborato3 = DBManager.elabI.create(new Elaborato(3, "Insegnamento", docente2));
			Elaborato elaborato1 = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			Elaborato elaborato4 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato5 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato6 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato7 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato8 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato9 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato10 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato11 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato12 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato13 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			Elaborato elaborato14 = DBManager.elabI.create(new Elaborato(0, "Insegnamento", docente));
			
			//Assegnazioni
			Assegnazione assegnazione1 = DBManager.assI.create(new Assegnazione(elaborato4, studente2));
			Assegnazione assegnazione2 = DBManager.assI.create(new Assegnazione(elaborato5, studente3));
			Assegnazione assegnazione3 = DBManager.assI.create(new Assegnazione(elaborato6, studente4));
			Assegnazione assegnazione4 = DBManager.assI.create(new Assegnazione(elaborato7, studente5));
			Assegnazione assegnazione5 = DBManager.assI.create(new Assegnazione(elaborato8, studente6));
			Assegnazione assegnazione6 = DBManager.assI.create(new Assegnazione(elaborato9, studente7));
			Assegnazione assegnazione7 = DBManager.assI.create(new Assegnazione(elaborato10, studente8));
			Assegnazione assegnazione8 = DBManager.assI.create(new Assegnazione(elaborato11, studente9));
			Assegnazione assegnazione9 = DBManager.assI.create(new Assegnazione(elaborato12, studente10));
			Assegnazione assegnazione10 = DBManager.assI.create(new Assegnazione(elaborato13, studente11));
			Assegnazione assegnazione11 = DBManager.assI.create(new Assegnazione(elaborato14, studente12));
			
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			scelte.add(2);
			scelte.add(3);
			accetta.add(true);
			accetta.add(true);
			accetta.add(true);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente1, scelte, accetta);
			studente2 = DBManager.studI.read(1);
			elaborato2 = DBManager.elabI.read(2);
			Assegnazione assegnazioneToTest = new Assegnazione(elaborato2, studente1);
			assertEquals(assegnazione, assegnazioneToTest);
		} catch(ElementoInvalido | ElementoInesistente | FailException | RichiestaRespinta e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	// E:3 - P:3 - S:RA - A:A - STD:NANR - CFU:>100
	public void test7StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato1 = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			Elaborato elaborato2 = DBManager.elabI.create(new Elaborato(2, "Insegnamento", docente));
			Elaborato elaborato3 = DBManager.elabI.create(new Elaborato(3, "Insegnamento", docente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			scelte.add(2);
			scelte.add(3);
			accetta.add(false);
			accetta.add(false);
			accetta.add(true);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			studente = DBManager.studI.read(1);
			elaborato3 = DBManager.elabI.read(3);
			Assegnazione assegnazioneToTest = new Assegnazione(elaborato3, studente);
			assertEquals(assegnazione, assegnazioneToTest);
		} catch(ElementoInvalido | ElementoInesistente | FailException | RichiestaRespinta e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	// E:1 - P:0 - S:0 - A:A - STD:NANR - CFU:>100
	public void test8StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			studente = DBManager.studI.read(1);
			elaborato = DBManager.elabI.read(1);
			Assegnazione assegnazioneToTest = new Assegnazione(elaborato, studente);
			assertEquals(assegnazione, assegnazioneToTest);
		} catch(ElementoInvalido | ElementoInesistente | FailException | RichiestaRespinta e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	// E:1 - P:1 - S:A - A:NA - STD:NANR - CFU:>100
	public void test9StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Docente docente = DBManager.docI.read(1);
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
			Assegnazione assegnazione4 = DBManager.assI.create(new Assegnazione(elaborato5, studente5));
			Assegnazione assegnazione5 = DBManager.assI.create(new Assegnazione(elaborato6, studente6));
			Assegnazione assegnazione6 = DBManager.assI.create(new Assegnazione(elaborato7, studente7));
			Assegnazione assegnazione7 = DBManager.assI.create(new Assegnazione(elaborato8, studente8));
			Assegnazione assegnazione8 = DBManager.assI.create(new Assegnazione(elaborato9, studente9));
			Assegnazione assegnazione9 = DBManager.assI.create(new Assegnazione(elaborato10, studente10));
			Assegnazione assegnazione10 = DBManager.assI.create(new Assegnazione(elaborato11, studente11));
			Assegnazione assegnazione11 = DBManager.assI.create(new Assegnazione(elaborato12, studente12));
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(false);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente1, scelte, accetta);
			fail();
		} catch(ElementoInvalido | ElementoInesistente | FailException e) {
			e.printStackTrace();
			fail();
		} catch(RichiestaRespinta rr) {
			
		}
	}
	
	@Test
	// E:1 - P:1 - S:A - A:A - STD:NAR - CFU:>100
	public void test10StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			ArrayList<Elaborato> elaborati = new ArrayList<Elaborato>();
			elaborati.add(elaborato);
			Richiesta richiesta = DBManager.ricI.create(new Richiesta(elaborati, studente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(true);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			studente = DBManager.studI.read(1);
			elaborato = DBManager.elabI.read(1);
			Assegnazione assegnazioneToTest = new Assegnazione(elaborato, studente);
			assertEquals(assegnazione, assegnazioneToTest);
		} catch(ElementoInvalido | ElementoInesistente | FailException | RichiestaRespinta e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	// E:1 - P:1 - S:A - A:A - STD:ANR - CFU:>100
	public void test11StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 120, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			Elaborato elaborato2 = DBManager.elabI.create(new Elaborato(2, "Insegnamento", docente));
			ArrayList<Elaborato> elaborati = new ArrayList<Elaborato>();
			elaborati.add(elaborato);
			Assegnazione assPresente = DBManager.assI.create(new Assegnazione(elaborato2, studente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(true);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			fail();
		} catch(ElementoInvalido | ElementoInesistente | FailException e) {
			e.printStackTrace();
			fail();
		} catch(RichiestaRespinta rr) {
			
		}
	}
	
	@Test
	// E:1 - P:1 - S:A - A:A - STD:NANR - CFU:>100
	public void test12StubSimulateAssegazione() {
		//Popolazione Database
		try {
			Studente studente = DBManager.studI.create(new Studente(1, 1, "Nome", "Cognome"));
			Docente docente = DBManager.docI.read(1);
			Elaborato elaborato = DBManager.elabI.create(new Elaborato(1, "Insegnamento", docente));
			//Grep again from DB
			
			ArrayList<Integer> scelte = new ArrayList<Integer>();
			ArrayList<Boolean> accetta = new ArrayList<Boolean>();
			scelte.add(1);
			accetta.add(true);
			Assegnazione assegnazione = gElab.stubSimulateAssegnazione(studente, scelte, accetta);
			fail();
		} catch(ElementoInvalido | ElementoInesistente | FailException e) {
			e.printStackTrace();
			fail();
		} catch(RichiestaRespinta rr) {
			
		}
	}
}

