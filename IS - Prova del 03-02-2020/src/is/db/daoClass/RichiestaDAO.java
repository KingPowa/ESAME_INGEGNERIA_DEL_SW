package is.db.daoClass;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import is.db.DBManager;
import is.db.exception.ElementoInesistente;
import is.db.exception.ElementoInvalido;
import is.db.exception.TabellaEsistente;

import is.entity.Elaborato;
import is.entity.Richiesta;
import is.entity.Studente;

public class RichiestaDAO implements DAOClass<Richiesta> {
	
	//If an error occurs, ElementoInvalido exception is thrown
	public Richiesta create(Richiesta element) throws ElementoInvalido{
		try {
			PreparedStatement pstm = translateToSQL(element);
		
			try {
				this.read(element.getStudente().getMatricola());
			} catch (ElementoInesistente e) {
			
				pstm.executeUpdate();
				System.out.println("Richiesta creata. Richiesta: " + element + "\n");
				return element;
		
			}
			
			System.out.println("Richiesta dello studente id " + element.getStudente().getMatricola() + " esistente.\n");
			return element;
		} catch(SQLException s) {
			throw new ElementoInvalido("Errore di creazione dal database.\n");
		}
	}
	
	//If an error occurs:
	// 1 - Related to Database -> Elemento Invalido
	// 2 - Related to Existence -> Elemento Inesistente
	public Richiesta read(int id) throws ElementoInvalido, ElementoInesistente {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM RICHIESTA WHERE STUDENTE = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, id);
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return this.translateFromSQL(result);
			} else {
				throw new ElementoInesistente("Non esiste una richiesta per lo studente di id " + id);
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ArrayList because no duplicates Elaborati
	public ArrayList<Richiesta> readAll() throws ElementoInvalido {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM RICHIESTA;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			ArrayList<Richiesta> resultRichieste = new ArrayList<Richiesta>();
		
			ResultSet result = pstm.executeQuery();
		
			while (result.next()){
				resultRichieste.add(translateFromSQL(result));
			}
		
			return resultRichieste;
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ElementoInvalido exceptions related to read method are managed in here
	public boolean update(Richiesta element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "UPDATE RICHIESTA SET STUDENTE=?, E1=?, E2=?, E3=?, EF=? WHERE STUDENTE = ?;";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Studente is valid
			//it is necessary to throw ElementoInesistente error since element is referring to 
			//a Studente not present in the database
			try {
				
				DBManager.studI.read(element.getStudente().getMatricola());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Studente " + element.getStudente().getMatricola() + " inesistente.\n");
				
			}
			
			//verify if Elaborati are valid
			//it is necessary to throw ElementoInesistente error since element is referring to 
			//a Elaborato not present in the database
			Elaborato currentElab = null;
			try {
				
				for(Elaborato e : element.getElaborati()) {
					if(e != null) {
						currentElab = e;
						DBManager.elabI.read(e.getID());
					}
				}	
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Elaborato " + currentElab.getID() + " inesistente.\n");
				
			}
			
			pstm.setInt(1, element.getStudente().getMatricola());
			
			ArrayList<Elaborato> elaborati = element.getElaborati();
			
			int counter = 1;
			for(Elaborato e : elaborati) {
				if (e != null && counter < 4) {
					pstm.setInt(counter + 1, e.getID());
				}
				else if(counter < 4){
					pstm.setNull(counter + 1, java.sql.Types.INTEGER);
				}
				counter++;
			}
			while(counter < 4) {
				pstm.setNull(counter + 1, java.sql.Types.INTEGER);
				counter++;
			}
			pstm.setInt(6, element.getStudente().getMatricola());
			pstm.setInt(5, element.getElabRif());
			
			pstm.executeUpdate();
			System.out.println("Richiesta " + element + " aggiornata con successo.");
			return true;
			
		} catch (SQLException | ElementoInvalido e ) {
			e.printStackTrace();
			System.out.println("Errore di aggiornamento dal database.\n");
			return false;
		}
	}

	public boolean delete(Richiesta element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "DELETE FROM RICHIESTA WHERE STUDENTE = ?;";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Elaborato is valid
			Elaborato currentElab = null;
			try {
				
				for(Elaborato e : element.getElaborati()) {
					if(e != null) {
						currentElab = e;
						DBManager.elabI.read(e.getID());
					}
				}	
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Elaborato " + currentElab.getID() + " inesistente.\n");
				
			}
			
			//verify if Studente is valid
			try {
				
				DBManager.studI.read(element.getStudente().getMatricola());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Studente " + element.getStudente().getMatricola() + " inesistente.\n");
				
			}
			
			//verify if Richiesta exists
			try {
				
				this.read(element.getStudente().getMatricola());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. La richiesta non esiste.");
				return false;
				
			}
			
			
			pstm.setInt(1, element.getStudente().getMatricola());
			
			pstm.executeUpdate();
			System.out.println("Richiesta " + element + " eliminata con successo.");
			return true;
		} catch (SQLException | ElementoInvalido e) {
			// TODO Auto-generated catch block
			System.out.println("Errore di creazione dal database.\n");
			return false;
		}
	
	}
	
	public boolean createTable() {

		String TabellaElaborato = "CREATE TABLE RICHIESTA("
				+ "STUDENTE INT(10) NOT NULL PRIMARY KEY, "
				+ "E1 INT(10), "
				+ "E2 INT(10), "
				+ "E3 INT(10), "
				+ "EF INT(1) NOT NULL, "
				+ "FOREIGN KEY (STUDENTE) REFERENCES STUDENTE(ID) ON DELETE CASCADE, "
				+ "FOREIGN KEY (E1) REFERENCES ELABORATO(ID) ON DELETE SET NULL, "
				+ "FOREIGN KEY (E2) REFERENCES ELABORATO(ID) ON DELETE SET NULL, "
				+ "FOREIGN KEY (E1) REFERENCES ELABORATO(ID) ON DELETE SET NULL, "
				+ "CHECK (EF<=2));";
		
		try {
			
			if(DBManager.checkTableExistence("RICHIESTA")) {
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				stmt.execute(TabellaElaborato);
				//DBManager.closeConnection();
				System.out.println("Tabella RICHIESTA creata.\n");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} catch(TabellaEsistente t) {
			System.out.println("Tabella RICHIESTA già presente.");
			return true;
		}
		return true;
	}
	
	//Translate from result. Throw SQLException/ElementoInvalido if error occurs in database
	//Quit instantly if Elaborato/Studente is non existent (it's impossible but it would represent an inconsistency
	//error in the database!)
	private Richiesta translateFromSQL(ResultSet result) throws SQLException, ElementoInvalido {
		int idStudente = result.getInt("STUDENTE");
		
		ArrayList<Elaborato> elaborati = new ArrayList<Elaborato>();
		
		//Gestione concorrenza
		//Se si trovano posizione nulle prima di posizioni minori di EF ef decrementa
		int elabRif = result.getInt("EF");
		int newElabRif = elabRif;
		
		int counter = 1;
		for(counter = 1; counter < 4; counter++) {
			String current = String.format("E%d", counter);
			int elabID = result.getInt(current);
			if(result.wasNull()) {
				if(counter <= elabRif) newElabRif--;
				continue;
			}
			else {
				
				Elaborato elaborato = null;
				
				try {
					
					elaborato = DBManager.elabI.read(elabID);
					
				} catch(ElementoInesistente e) {
					
					e.printStackTrace();
					System.out.println("Errore nel database corrente. Riferimento ad un elaborato inesistente.");
					System.exit(0); //Such an error is forbidden by the system specification.
					
				}
				
				elaborati.add(elaborato);
			}
		}
		
		Studente studente = null;
		
		try {
			
			studente = DBManager.studI.read(idStudente);
			
		} catch(ElementoInesistente e) {
			
			e.printStackTrace();
			System.out.println("Errore nel database corrente. Riferimento ad uno studente inesistente.");
			System.exit(0); //Such an error is forbidden by the system specification.
			
		}
		
		return new Richiesta(elaborati, studente, newElabRif);
	}
	
	private PreparedStatement translateToSQL(Richiesta element) throws SQLException {
		Connection conn = DBManager.getConnection();
		String query = "INSERT INTO RICHIESTA(STUDENTE, E1, E2, E3, EF) VALUES(?, ?, ?, ?, ?);";
		PreparedStatement pstm = conn.prepareStatement(query);
		
		pstm.setInt(1, element.getStudente().getMatricola());
		
		ArrayList<Elaborato> elaborati = element.getElaborati();
		
		int counter = 1;
		for(Elaborato e : elaborati) {
			if (e != null && counter < 4) {
				pstm.setInt(counter + 1, e.getID());
			}
			else if(counter < 4){
				pstm.setNull(counter + 1, java.sql.Types.INTEGER);
			}
			counter++;
		}
		while(counter < 4) {
			pstm.setNull(counter + 1, java.sql.Types.INTEGER);
			counter++;
		}
		
		pstm.setInt(5, element.getElabRif());
		return pstm;
	}
	
	public ArrayList<Richiesta> readAll(Elaborato elab) throws ElementoInvalido{
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM RICHIESTA WHERE E1 = ? OR E2 = ? OR E3 = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			ArrayList<Richiesta> resultRichieste = new ArrayList<Richiesta>();
		
			pstm.setInt(1, elab.getID());
			pstm.setInt(2, elab.getID());
			pstm.setInt(3, elab.getID());
			
			ResultSet result = pstm.executeQuery();
		
			while (result.next()){
				resultRichieste.add(translateFromSQL(result));
			}
		
			return resultRichieste;
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
}
