package is.db.daoClass;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;

import is.db.DBManager;
import is.db.exception.ElementoInesistente;
import is.db.exception.ElementoInvalido;
import is.db.exception.TabellaEsistente;
import is.entity.Assegnazione;
import is.entity.Elaborato;
import is.entity.Studente;

public class AssegnazioneDAO implements DAOClass<Assegnazione> {
	
	//If an error occurs, ElementoInvalido exception is thrown
	public Assegnazione create(Assegnazione element) throws ElementoInvalido{
		try {
			PreparedStatement pstm = translateToSQL(element);
		
			//Verify no Elaborato exists in Assegnazione
			Assegnazione doesElaborato = null;
			Assegnazione doesStudente = null;
			try {
				doesElaborato = this.read(element.getElaborato().getID());
			} catch (ElementoInesistente e) {
		
			}
			
			try {
				doesStudente = this.read(element.getStudente());
			} catch (ElementoInesistente e) {
				
			}
			
			if(doesStudente == null && doesElaborato == null) {
				pstm.executeUpdate();
				System.out.println("Assegnazione creata. Assegnazione: " + element + "\n");
				return element;
			}
			
			else {
				if (doesStudente != null && doesElaborato == null) {
					System.out.println("Assegnazione relativa allo studente " + element.getStudente() + " già esistente.\n");
					System.out.println("Ritorno assegnazione corretta.\n");
					return doesStudente;
				}
				else if (doesStudente == null && doesElaborato != null) {
					System.out.println("Assegnazione relativa all'elaborato " + element.getElaborato() + " già esistente.\n");
					System.out.println("Ritorno assegnazione corretta.\n");
					return doesElaborato;
				}
				else {
					if(doesElaborato.equals(doesStudente)) {
						System.out.println("Assegnazione già esistente.\n");
						return element;
					}
					else {
						System.out.println("Ambiguità rilevata. Elaborato e studente posseggono già una reference.\n");
						throw new ElementoInvalido("Ambiguità d'assegnazione.\n");
					}
				}
			}
			
		} catch(SQLException s) {
			throw new ElementoInvalido("Errore di creazione dal database.\n");
		}
	}
	
	//If an error occurs:
	// 1 - Related to Database -> Elemento Invalido
	// 2 - Related to Existence -> Elemento Inesistente
	public Assegnazione read(int id) throws ElementoInvalido, ElementoInesistente {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM ASSEGNAZIONE WHERE ELABORATO = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, id);
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return this.translateFromSQL(result);
			} else {
				throw new ElementoInesistente("Non esiste un assegnazione con elaborato di id " + id);
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	public Assegnazione read(Studente studente) throws ElementoInvalido, ElementoInesistente {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM ASSEGNAZIONE WHERE STUDENTE = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, studente.getMatricola());
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return this.translateFromSQL(result);
			} else {
				throw new ElementoInesistente("Non esiste un assegnazione con studente di id " + studente.getMatricola());
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//For reading a specific Assegnazione
	public Assegnazione read(int[] ids) throws ElementoInvalido, ElementoInesistente {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM ASSEGNAZIONE WHERE ELABORATO = ? and STUDENTE = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			if(ids.length >= 2) {
				pstm.setInt(1, ids[0]);//elaborato
				pstm.setInt(2, ids[1]);//studente
			}
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return this.translateFromSQL(result);
			} else {
				throw new ElementoInesistente("Non esiste un assegnazione con il pair di id passato.\n");
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ArrayList because no duplicates Elaborati
	public ArrayList<Assegnazione> readAll() throws ElementoInvalido {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM ASSEGNAZIONE;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			ArrayList<Assegnazione> resultAssegnati = new ArrayList<Assegnazione>();
		
			ResultSet result = pstm.executeQuery();
		
			while (result.next()){
				resultAssegnati.add(translateFromSQL(result));
			}
		
			return resultAssegnati;
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ElementoInvalido exceptions related to read method are managed in here
	public boolean update(Assegnazione element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "UPDATE ASSEGNAZIONE SET ELABORATO=?, STUDENTE=?, DATA=? WHERE ELABORATO = ? AND STUDENTE = ?;";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Elaborato is valid
			//it is necessary to throw ElementoInesistente error since element is referring to 
			//a Elaborato not present in the database
			try {
				
				DBManager.elabI.read(element.getElaborato().getID());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Elaborato " + element.getElaborato().getID() + " inesistente.\n");
				
			}
			
			//verify if Studente is valid
			//it is necessary to throw ElementoInesistente error since element is referring to 
			//a Studente not present in the database
			try {
				
				DBManager.studI.read(element.getStudente().getMatricola());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Studente " + element.getStudente().getMatricola() + " inesistente.\n");
				
			}
			
			//verify if Assegnazione exists
			try {
				
				int[] list = {element.getElaborato().getID(), element.getStudente().getMatricola()};
				this.read(list);
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. L'assegnazione non esiste.");
				return false;
				
			}
			
			pstm.setInt(1, element.getElaborato().getID());
			pstm.setInt(2, element.getStudente().getMatricola());
			pstm.setDate(3, element.getDate());
			
			pstm.executeUpdate();
			System.out.println("Assegnazione " + element + " aggiornata con successo.");
			return true;
			
		} catch (SQLException | ElementoInvalido e ) {
			// TODO Auto-generated catch block
			System.out.println("Errore di aggiornamento dal database.\n");
			return false;
		}
	}

	public boolean delete(Assegnazione element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "DELETE FROM ELABORATO WHERE ELABORATO = ? AND STUDENTE = ?;";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Elaborato is valid
			try {
				
				DBManager.elabI.read(element.getElaborato().getID());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Elaborato " + element.getElaborato().getID() + " inesistente.\n");
				
			}
			
			//verify if Studente is valid
			try {
				
				DBManager.studI.read(element.getStudente().getMatricola());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Studente " + element.getStudente().getMatricola() + " inesistente.\n");
				
			}
			
			//verify if Assegnazione exists
			try {
				
				int[] list = {element.getElaborato().getID(), element.getStudente().getMatricola()};
				this.read(list);
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. L'assegnazione non esiste.");
				return false;
				
			}
			
			
			pstm.setInt(1, element.getElaborato().getID());
			pstm.setInt(2, element.getStudente().getMatricola());
			
			pstm.executeUpdate();
			System.out.println("Assegnazione " + element + " eliminata con successo.");
			return true;
		} catch (SQLException | ElementoInvalido e) {
			// TODO Auto-generated catch block
			System.out.println("Errore di creazione dal database.\n");
			return false;
		}
	
	}
	
	public boolean createTable() {

		String TabellaElaborato = "CREATE TABLE ASSEGNAZIONE("
				+ "ELABORATO INT(10) NOT NULL PRIMARY KEY, "
				+ "STUDENTE INT(10) NOT NULL UNIQUE, "
				+ "DATA DATE NOT NULL, "
				+ "FOREIGN KEY (STUDENTE) REFERENCES STUDENTE(ID) ON DELETE CASCADE, "
				+ "FOREIGN KEY (ELABORATO) REFERENCES ELABORATO(ID) ON DELETE CASCADE);";
		
		try {
			
			if(DBManager.checkTableExistence("ASSEGNAZIONE")) {
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				stmt.execute(TabellaElaborato);
				//DBManager.closeConnection();
				System.out.println("Tabella ASSEGNAZIONE creata.\n");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} catch(TabellaEsistente t) {
			System.out.println("Tabella ASSEGNAZIONE già presente.");
			return true;
		}
		return true;
	}
	
	//Translate from result. Throw SQLException/ElementoInvalido if error occurs in database
	//Quit instantly if Studente/Elaborato is non existent (it's impossible but it would represent an inconsistency
	//error in the database!)
	private Assegnazione translateFromSQL(ResultSet result) throws SQLException, ElementoInvalido {
		int idStudente = result.getInt("STUDENTE");
		int idElaborato = result.getInt("ELABORATO");
		Date data = result.getDate("DATA");
		
		Studente studente = null;
		
		try {
			
			studente = DBManager.studI.read(idStudente);
			
		} catch(ElementoInesistente e) {
			
			e.printStackTrace();
			System.out.println("Errore nel database corrente. Riferimento ad uno studente inesistente.");
			System.exit(0); //Such an error is forbidden by the system specification.
			
		}
		
		Elaborato elaborato = null;
		
		try {
			
			elaborato = DBManager.elabI.read(idElaborato);
			
		} catch(ElementoInesistente e) {
			
			e.printStackTrace();
			System.out.println("Errore nel database corrente. Riferimento ad un elaborato inesistente.");
			System.exit(0); //Such an error is forbidden by the system specification.
			
		}
		
		return new Assegnazione(elaborato, studente, data);
	}
	
	private PreparedStatement translateToSQL(Assegnazione element) throws SQLException {
		Connection conn = DBManager.getConnection();
		String query = "INSERT INTO ASSEGNAZIONE(ELABORATO, STUDENTE, DATA) VALUES(?, ?, ?);";
		PreparedStatement pstm = conn.prepareStatement(query);
		
		pstm.setInt(1, element.getElaborato().getID());
		pstm.setInt(2, element.getStudente().getMatricola());
		pstm.setDate(3, element.getDate());
		
		return pstm;
	}
	
	public boolean isAssegnato(int id) throws ElementoInvalido{
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT STUDENTE FROM ASSEGNAZIONE WHERE ELABORATO = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, id);
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	public boolean isAssegnato(Studente studente) throws ElementoInvalido{
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT STUDENTE FROM ASSEGNAZIONE WHERE STUDENTE = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, studente.getMatricola());
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return true;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	public boolean isAssegnatoCurrentYear(int id) throws ElementoInvalido{
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT DATA FROM ASSEGNAZIONE WHERE ELABORATO = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, id);
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				Date data = result.getDate("DATA");
				Calendar cal = Calendar.getInstance();
				cal.setTime(data);
				int year = cal.get(Calendar.YEAR);
				if(year == Calendar.getInstance().get(Calendar.YEAR)) return true;
				return false;
			} else {
				return false;
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
}
