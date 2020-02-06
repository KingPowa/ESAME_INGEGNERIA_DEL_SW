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
import is.entity.Studente;

public class StudenteDAO implements DAOClass<Studente> {
	
	//If an error occurs, ElementoInvalido exception is thrown
	public Studente create(Studente element) throws ElementoInvalido{
		try {
			boolean autoG = element.getMatricola() == 0;
			PreparedStatement pstm = translateToSQL(element, autoG);
		
		//If element with same id exists, do not execute update
			try {
				this.read(element.getMatricola());
			} catch (ElementoInesistente e) {
				
				pstm.executeUpdate();
				if(autoG) {
					ResultSet result = pstm.getGeneratedKeys();
					// Assumption -> If docente with same ID does not exist, override
					// it with auto-generated ID
					if(result.next()) {
						element.setMatricola(result.getInt("ID"));
					}
				}
				System.out.println("Studente creato. Studente: " + element + "\n");
				return element;
		
			}
			
			System.out.println("Studente di id " + element.getMatricola() + " esistente.\n");
			return element;
		} catch(SQLException s) {
			s.printStackTrace();
			throw new ElementoInvalido("Errore di creazione dal database.\n");
		}
	}
	
	//If an error occurs:
	// 1 - Related to Database -> Elemento Invalido
	// 2 - Related to Existence -> Elemento Inesistente
	public Studente read(int id) throws ElementoInvalido, ElementoInesistente {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM STUDENTE WHERE ID = ?";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, id);
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return this.translateFromSQL(result);
			} else {
				throw new ElementoInesistente("Non esiste uno studente di id " + id);
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ArrayList because no duplicate Studenti
	public ArrayList<Studente> readAll() throws ElementoInvalido {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM STUDENTE";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			ArrayList<Studente> resultStudenti = new ArrayList<Studente>();
		
			ResultSet result = pstm.executeQuery();
		
			while (result.next()){
				resultStudenti.add(translateFromSQL(result));
			}
		
			return resultStudenti;
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ElementoInvalido exceptions related to read method are managed in here
	//It could not throw elemento inesistente since no reference to invalid elements is made
	public boolean update(Studente element){
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "UPDATE STUDENTE SET ID=?, NOME=?, COGNOME=?, CFU=? WHERE ID = ?";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Docente exists
			try {
				
				this.read(element.getMatricola());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. Lo studente non esiste.");
				return false;
				
			}
			
			pstm.setInt(1, element.getMatricola());
			pstm.setString(2, element.getNome());
			pstm.setString(3, element.getCognome());
			pstm.setInt(4, element.getCfu());
			pstm.setInt(5, element.getMatricola());
			
			pstm.executeUpdate();
			System.out.println("Studente " + element + " aggiornato con successo.");
			return true;
			
		} catch (SQLException | ElementoInvalido e ) {
			// TODO Auto-generated catch block
			System.out.println("Errore di aggiornamento dal database.\n");
			return false;
		}
	}

	public boolean delete(Studente element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "DELETE FROM STUDENTE WHERE ID = ?";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Studente exists
			try {
				
				this.read(element.getMatricola());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. Lo studente non esiste.");
				return false;
			
			}
			
			
			pstm.setInt(1, element.getMatricola());
			
			//Before Eliminating studente, it's necessary to eliminate every elaborate related to
			//the specific studente
			
			//managed by ON DELETE CASCADE
			/*
			 * try { Assegnazione assegnato = element.getAssegnato();
			 * DBManager.assI.delete(assegnato);
			 * System.out.println("Rimossa Assegnazione relativa allo studente."); }
			 * catch(NessunaAssegnazione a) {
			 * 
			 * }
			 */
			
			//It is also necessary to remove any Pending request
			
			//Managed by ON DELETE CASCADE
			/*
			 * try {
			 * 
			 * Richiesta richiesta = DBManager.ricI.read(element.getMatricola());
			 * DBManager.ricI.delete(richiesta);
			 * 
			 * } catch (ElementoInesistente e) {
			 * 
			 * }
			 */
			
			pstm.executeUpdate();
			System.out.println("Studente " + element + " eliminato con successo.\n");
			return true;
		} catch (SQLException | ElementoInvalido e) {
			// TODO Auto-generated catch block
			System.out.println("Errore di creazione dal database.\n");
			return false;
		}
	
	}
	
	public boolean createTable() {

		String TabellaElaborato = "CREATE TABLE STUDENTE("
				+ "ID INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ "NOME VARCHAR(60) NOT NULL, "
				+ "COGNOME VARCHAR(60) NOT NULL, "
				+ "CFU INT(3) NOT NULL, "
				+ "CHECK (CFU>0));";
		
		try {
			
			if(DBManager.checkTableExistence("STUDENTE")) {
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				stmt.execute(TabellaElaborato);
				//DBManager.closeConnection();
				System.out.println("Tabella STUDENTE creata.\n");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} catch(TabellaEsistente t) {
			System.out.println("Tabella STUDENTE già presente.");
			return true;
		}
		return true;
	}
	
	//Translate from result. Throw SQLException/ElementoInvalido if error occurs in database
	private Studente translateFromSQL(ResultSet result) throws SQLException, ElementoInvalido {
		int id = result.getInt("ID");
		String nome = result.getString("NOME");
		String cognome = result.getString("COGNOME");
		int cfu = result.getInt("CFU");
		
		Studente studente = new Studente(id, cfu, nome, cognome);
		
		boolean assegnato = DBManager.assI.isAssegnato(studente);
			
		studente.setAssegnato(assegnato);

		return studente;
	}
	
	private PreparedStatement translateToSQL(Studente element, boolean auto) throws SQLException {
		Connection conn = DBManager.getConnection();
		PreparedStatement pstm = null;
		if(auto) {
			String query = "INSERT INTO STUDENTE(NOME, COGNOME, CFU) VALUES(?, ?, ?);";
			pstm = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
			pstm.setString(1, element.getNome());
			pstm.setString(2, element.getCognome());
			pstm.setInt(3, element.getCfu());
		}
		else {
			String query = "INSERT INTO STUDENTE(ID, NOME, COGNOME, CFU) VALUES(?, ?, ?, ?);";
			pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, element.getMatricola());
			pstm.setString(2, element.getNome());
			pstm.setString(3, element.getCognome());
			pstm.setInt(4, element.getCfu());
		}
		
		return pstm;
	}

}
