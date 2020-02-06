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
import is.entity.Docente;
import is.entity.Elaborato;

public class DocenteDAO implements DAOClass<Docente> {

	
	//If an error occurs, ElementoInvalido exception is thrown
	public Docente create(Docente element) throws ElementoInvalido{
		try {
			boolean autoG = element.getMatDocente() == 0;
			PreparedStatement pstm = translateToSQL(element, autoG);
		
		//If element with same id exists, do not execute update
			try {
				this.read(element.getMatDocente());
			} catch (ElementoInesistente e) {
				pstm.executeUpdate();
				if (autoG) {
					ResultSet result = pstm.getGeneratedKeys();
					// Assumption -> If docente with same ID does not exist, override
					// it with auto-generated ID
					if (result.next()) {
						element.setMatDocente(result.getInt("ID"));
					} 
				}
				
				System.out.println("Docente creato. Docente: " + element + "\n");
				return element;
		
			}
			
			System.out.println("Docente di id " + element.getMatDocente() + " esistente.\n");
			return element;
		} catch(SQLException s) {
			s.printStackTrace();
			throw new ElementoInvalido("Errore di creazione dal database.\n");
		}
	}
	
	//If an error occurs:
	// 1 - Related to Database -> Elemento Invalido
	// 2 - Related to Existence -> Elemento Inesistente
	public Docente read(int id) throws ElementoInvalido, ElementoInesistente {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM DOCENTE WHERE ID = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, id);
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return this.translateFromSQL(result);
			} else {
				throw new ElementoInesistente("Non esiste un docente di id " + id);
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ArrayList because no duplicate Docenti
	public ArrayList<Docente> readAll() throws ElementoInvalido {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM DOCENTE;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			ArrayList<Docente> resultDocenti = new ArrayList<Docente>();
		
			ResultSet result = pstm.executeQuery();
		
			while (result.next()){
				resultDocenti.add(translateFromSQL(result));
			}
		
			return resultDocenti;
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	//ElementoInvalido exceptions related to read method are managed in here
	//It could not throw elemento inesistente since no reference to invalid elements is made
	public boolean update(Docente element){
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "UPDATE DOCENTE SET ID=?, NOME=?, COGNOME=? WHERE ID = ?;";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Docente exists
			try {
				
				this.read(element.getMatDocente());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. Il docente non esiste.");
				return false;
				
			}
			
			pstm.setInt(1, element.getMatDocente());
			pstm.setString(2, element.getNome());
			pstm.setString(3, element.getCognome());
			pstm.setInt(4, element.getMatDocente());
			
			pstm.executeUpdate();
			System.out.println("Docente " + element + " aggiornato con successo.");
			return true;
			
		} catch (SQLException | ElementoInvalido e ) {
			// TODO Auto-generated catch block
			System.out.println("Errore di aggiornamento dal database.\n");
			return false;
		}
	}

	public boolean delete(Docente element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "DELETE FROM DOCENTE WHERE ID = ?;";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Docente exists
			try {
				
				this.read(element.getMatDocente());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. Il docente non esiste.");
				return false;
			
			}
			
			
			pstm.setInt(1, element.getMatDocente());
			
			//Before Eliminating docente, it's necessary to eliminate every elaborate related to
			//the specific docente
			
			//Done by on deleteCascade
			/*
			 * if(element.getElaborati() != null) { for(Elaborato e :
			 * element.getElaborati()) { DBManager.elabI.delete(e); //Note it is impossible
			 * for delete to throw //since the condition is verified before. Still, it's
			 * safe to leave //the throw clause } }
			 */
			
			pstm.executeUpdate();
			System.out.println("Docente " + element + " eliminato con successo. Sono stati inoltre");
			System.out.println(" eliminati tutti i suoi elaborati.\n");
			return true;
		} catch (SQLException | ElementoInvalido e) {
			// TODO Auto-generated catch block
			System.out.println("Errore di creazione dal database.\n");
			return false;
		}
	
	}
	
	public boolean createTable() {

		String TabellaElaborato = "CREATE TABLE DOCENTE("
				+ "ID INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ "NOME VARCHAR(60) NOT NULL, "
				+ "COGNOME VARCHAR(60) NOT NULL);";
		
		try {
			
			if(DBManager.checkTableExistence("DOCENTE")) {
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				stmt.execute(TabellaElaborato);
				//DBManager.closeConnection();
				System.out.println("Tabella DOCENTE creata.\n");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} catch(TabellaEsistente t) {
			System.out.println("Tabella DOCENTE già presente.");
			return true;
		}
		return true;
	}
	
	//Translate from result. Throw SQLException/ElementoInvalido if error occurs in database
	private Docente translateFromSQL(ResultSet result) throws SQLException, ElementoInvalido {
		int id = result.getInt("ID");
		String nome = result.getString("NOME");
		String cognome = result.getString("COGNOME");
		
		Docente docente = new Docente(id, nome, cognome);
		
		ArrayList<Elaborato> ElaboratiDocente = new ArrayList<Elaborato>();
		
		ElaboratiDocente = DBManager.elabI.readAll(docente);
		
		/*
		 * if(ElaboratiDocente.isEmpty()) {
		 * System.out.println("Nessun elaborto assegnato al docente: " + docente +
		 * ".\n"); }
		 */
			
		docente.setElaborati(ElaboratiDocente);

		return docente;
	}
	
	private PreparedStatement translateToSQL(Docente element, boolean auto) throws SQLException {
		Connection conn = DBManager.getConnection();
		PreparedStatement pstm = null;
		if(auto) {
			String query = "INSERT INTO DOCENTE(NOME, COGNOME) VALUES(?, ?);";
			pstm = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
			//pstm.setInt(1, element.getMatDocente());
			pstm.setString(1, element.getNome());
			pstm.setString(2, element.getCognome());
		}
		else {
			String query = "INSERT INTO DOCENTE(ID, NOME, COGNOME) VALUES(?, ?, ?);";
			pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, element.getMatDocente());
			pstm.setString(2, element.getNome());
			pstm.setString(3, element.getCognome());
		}
		return pstm;
	}

}
