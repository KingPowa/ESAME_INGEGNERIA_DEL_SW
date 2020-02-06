package is.db.daoClass;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import is.db.exception.ElementoInesistente;
import is.db.exception.ElementoInvalido;
import is.db.exception.TabellaEsistente;
import is.entity.Docente;
import is.entity.Elaborato;
import is.db.DBManager;

public class ElaboratoDAO implements DAOClass<Elaborato>{
	
	//If an error occurs, ElementoInvalido exception is thrown
	public Elaborato create(Elaborato element) throws ElementoInvalido{
		try {
			boolean autoG = element.getID() == 0;
			PreparedStatement pstm = translateToSQL(element, autoG);
		
		//If element with same id exists, do not execute update
			try {
				this.read(element.getID());
			} catch (ElementoInesistente e) {
			
				pstm.executeUpdate();
				if(autoG) {
					ResultSet result = pstm.getGeneratedKeys();
					// Assumption -> If elaborato with same ID does not exist, override
					// it with auto-generated ID
					if(result.next()) {
						element.setID(result.getInt("ID"));
					}
				}
				System.out.println("Elaborato creato. Elaborato: " + element + "\n");
				return element;
		
			}
			
			System.out.println("Elaborato di id " + element.getID() + " esistente.\n");
			return element;
		} catch(SQLException s) {
			throw new ElementoInvalido("Errore di creazione dal database.\n");
		}
	}
	
	//If an error occurs:
	// 1 - Related to Database -> Elemento Invalido
	// 2 - Related to Existence -> Elemento Inesistente
	public Elaborato read(int id) throws ElementoInvalido, ElementoInesistente {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM ELABORATO WHERE ID = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, id);
		
			ResultSet result = pstm.executeQuery();
		
			if(result.next()) {
				return this.translateFromSQL(result);
			} else {
				throw new ElementoInesistente("Non esiste un elaborato di id " + id);
			}
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	public ArrayList<Elaborato> readAll() throws ElementoInvalido {
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM ELABORATO;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			ArrayList<Elaborato> resultElaborati = new ArrayList<Elaborato>();
		
			ResultSet result = pstm.executeQuery();
		
			while (result.next()){
				resultElaborati.add(translateFromSQL(result));
			}
		
			return resultElaborati;
		} catch (SQLException e) {
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
	}
	
	public ArrayList<Elaborato> readAllAssegnati() throws ElementoInvalido {
		ArrayList<Elaborato> listaElaborati = readAll();
		ArrayList<Elaborato> listaAssegnati = new ArrayList<Elaborato>();
		
		for(Elaborato e : listaElaborati) {
			if(e.isAssegnato()) listaAssegnati.add(e);
		}
		
		return listaAssegnati;
	}
	
	//For filtering based on a docente, useful to DocenteDAO and control class
	public ArrayList<Elaborato> readAll(Docente docente) throws ElementoInvalido {
		//To avoid circular Dependency, it is necessary to change this method
		try {
			Connection conn = DBManager.getConnection();
		
			String query = "SELECT * FROM ELABORATO WHERE DOCENTE = ?;";
		
			PreparedStatement pstm = conn.prepareStatement(query);
		
			ArrayList<Elaborato> resultElaborati = new ArrayList<Elaborato>();
		
			pstm.setInt(1, docente.getMatDocente());
			
			ResultSet result = pstm.executeQuery();
		
			while (result.next()){
				//All is solved thanks to this stub
				int id = result.getInt("ID");
				String insegnamento = result.getString("INSEGNAMENTO");
				
				boolean assegnato = DBManager.assI.isAssegnato(id);
				
				resultElaborati.add(new Elaborato(id, insegnamento, docente, assegnato));
			}
		
			return resultElaborati;
		} catch (SQLException e) {
			e.printStackTrace();
			throw new ElementoInvalido("Errore di reperimento dal database.\n");
		}
		
		/*
		 * ArrayList<Elaborato> listaElaborati = this.readAll(); ArrayList<Elaborato>
		 * filteredLista = new ArrayList<Elaborato>();
		 * 
		 * try { DBManager.docI.read(docente.getMatDocente()); } catch
		 * (ElementoInesistente e) { e.printStackTrace(); return filteredLista; }
		 * 
		 * for(Elaborato e : listaElaborati) { if(e.getDocente().getMatDocente() ==
		 * docente.getMatDocente()) { filteredLista.add(e); } }
		 * 
		 * return filteredLista;
		 */
	}
	
	public ArrayList<Elaborato> readAllAssegnati(Docente docente) throws ElementoInvalido {
		ArrayList<Elaborato> listaElaborati = this.readAll(docente);
		ArrayList<Elaborato> filteredLista = new ArrayList<Elaborato>();
		
		for(Elaborato e : listaElaborati) {
			if(e.isAssegnato()) filteredLista.add(e);
		}
		
		return filteredLista;
	}
	
	public ArrayList<Elaborato> readAllDisponibili() throws ElementoInvalido {
		ArrayList<Elaborato> listaElaborati = readAll();
		ArrayList<Elaborato> listaDisponibili = new ArrayList<Elaborato>();
		
		//Very inefficient, would be better to use GetHashCode override on Elaborato Class
		for(Elaborato e : listaElaborati) {
			if(!e.isAssegnato()) {
				listaDisponibili.add(e);
			}
		}
		
		return listaDisponibili;
	}
	
	//ElementoInvalido exceptions related to read method are managed in here
	public boolean update(Elaborato element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "UPDATE ELABORATO SET DOCENTE=?, INSEGNAMENTO=? WHERE ID = ?";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Docente is valid
			//it is necessary to throw ElementoInesistente error since element is referring to 
			//a Docente not present in the database
			try {
				
				DBManager.docI.read(element.getDocente().getMatDocente());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Docente " + element.getDocente() + " inesistente.\n");
				
			}
			
			//verify if Elaborato exists
			try {
				
				this.read(element.getID());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. L'elaborato non esiste.");
				return false;
				
			}
			
			pstm.setInt(1, element.getDocente().getMatDocente());
			pstm.setString(2, element.getInsegnamento());
			pstm.setInt(3, element.getID());
			
			pstm.executeUpdate();
			System.out.println("Elaborato " + element + " aggiornato con successo.");
			return true;
			
		} catch (SQLException | ElementoInvalido e ) {
			// TODO Auto-generated catch block
			System.out.println("Errore di aggiornamento dal database.\n");
			return false;
		}
	}

	public boolean delete(Elaborato element) throws ElementoInesistente{
		
		try {
			Connection conn = DBManager.getConnection();
			
			String query = "DELETE FROM ELABORATO WHERE ID = ?";
			
			PreparedStatement pstm = conn.prepareStatement(query);
			
			//verify if Docente is valid
			try {
				
				DBManager.docI.read(element.getDocente().getMatDocente());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				throw new ElementoInesistente("Docente " + element.getDocente() + " inesistente.\n");
				
			}
			
			//verify if Elaborato exists
			try {
				
				this.read(element.getID());
				
			} catch(ElementoInesistente e) {
				
				e.printStackTrace();
				System.out.println("Errore nella query. L'elaborato non esiste.");
				return false;
			
			}
			
			
			pstm.setInt(1, element.getID());
			
			//Before eliminating Elaborato, it is necessary to eliminate every Assegnazione existing
			
			//done automatically by ON DELETE CASCADE
			/*
			 * try { Assegnazione assegnato = element.getAssegnato();
			 * DBManager.assI.delete(assegnato);
			 * System.out.println("Rimossa Assegnazione relativa all'elaborato."); }
			 * catch(NessunaAssegnazione a) {
			 * 
			 * }
			 */
			
			//Dealing with deleting Request is a bit different. We need to find every request
			//Referencing to a specific Elaborato. Therefore, we use a method provided by RichiestaDAO()
			
			//done automatically by ON DELETE NULL
			/*
			 * ArrayList<Richiesta> richiesteElab = DBManager.ricI.readAll(element);
			 * for(Richiesta r : richiesteElab) { try { //This reorder r
			 * r.rimuoviElaborato(element.getID()); //This remove elaborato from Richiesta
			 * and upload again on Database DBManager.ricI.update(r); }
			 * catch(ElaboratoAssente a) { continue; } }
			 */
			
			pstm.executeUpdate();
			System.out.println("Elaborato " + element + " eliminato con successo.");
			return true;
		} catch (SQLException | ElementoInvalido e) {
			// TODO Auto-generated catch block
			System.out.println("Errore di creazione dal database.\n");
			return false;
		}
	
	}
	
	public boolean createTable() {

		//Aggiunta ON DELETE CASCADE per eliminare la necessità di eliminare il docente
		String TabellaElaborato = "CREATE TABLE ELABORATO("
				+ "ID INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY, "
				+ "DOCENTE INT(10) NOT NULL, "
				+ "INSEGNAMENTO VARCHAR(60) NOT NULL, "
				+ "FOREIGN KEY (DOCENTE) REFERENCES DOCENTE(ID) ON DELETE CASCADE);";
		
		try {
			
			if(DBManager.checkTableExistence("ELABORATO")) {
				Connection conn = DBManager.getConnection();
				Statement stmt = conn.createStatement();
				stmt.execute(TabellaElaborato);
				//DBManager.closeConnection();
				System.out.println("Tabella ELABORATO creata.\n");
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
			return false;
		} catch(TabellaEsistente t) {
			System.out.println("Tabella ELABORATO già presente.");
			return true;
		}
		return true;
	}
	
	//Translate from result. Throw SQLException/ElementoInvalido if error occurs in database
	//Quit instantly if Docente is non existent (it's impossible but it would represent an inconsistency
	//error in the database!)
	private Elaborato translateFromSQL(ResultSet result) throws SQLException, ElementoInvalido {
		int id = result.getInt("ID");
		int idDocente = result.getInt("DOCENTE");
		String insegnamento = result.getString("INSEGNAMENTO");
		Docente docente = null;
		
		try {
			
			docente = DBManager.docI.read(idDocente);
			
		} catch(ElementoInesistente e) {
			
			e.printStackTrace();
			System.out.println("Errore nel database corrente. Riferimento ad un docente inesistente.");
			System.exit(0); //Such an error is forbidden by the system specification.
			
		}
		
		boolean assegnato = DBManager.assI.isAssegnato(id);
		
		return new Elaborato(id, insegnamento, docente, assegnato);
	}
	
	private PreparedStatement translateToSQL(Elaborato element, boolean auto) throws SQLException {
		Connection conn = DBManager.getConnection();
		PreparedStatement pstm = null;
		if(auto) {
			String query = "INSERT INTO ELABORATO(DOCENTE, INSEGNAMENTO) VALUES(?, ?);";
			pstm = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		
			pstm.setInt(1, element.getDocente().getMatDocente());
			pstm.setString(2, element.getInsegnamento());
		}
		else {
			String query = "INSERT INTO ELABORATO(ID, DOCENTE, INSEGNAMENTO) VALUES(?, ?, ?);";
			pstm = conn.prepareStatement(query);
		
			pstm.setInt(1, element.getID());
			pstm.setInt(2, element.getDocente().getMatDocente());
			pstm.setString(3, element.getInsegnamento());
		}
		return pstm;
	}
}
