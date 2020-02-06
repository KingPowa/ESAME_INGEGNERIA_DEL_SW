package is.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import is.db.daoClass.AssegnazioneDAO;
import is.db.daoClass.DocenteDAO;
import is.db.daoClass.ElaboratoDAO;
import is.db.daoClass.RichiestaDAO;
import is.db.daoClass.StudenteDAO;
import is.db.exception.TabellaEsistente;

public class DBManager {

	public static ElaboratoDAO elabI = new ElaboratoDAO();
	public static AssegnazioneDAO assI = new AssegnazioneDAO();
	public static DocenteDAO docI = new DocenteDAO();
	public static StudenteDAO studI = new StudenteDAO();
	public static RichiestaDAO ricI = new RichiestaDAO();
	
	private static Connection conn = null;

	private DBManager() {}

	public static Connection getConnection() throws SQLException {

		if(conn == null) {
			conn = DriverManager.getConnection("jdbc:h2:./DatabaseLaurea", "sa", "");
		}

		return conn;
	}


	public static void closeConnection() throws SQLException {

		if(conn != null) {
			conn.close();
		}
	}

	//Method for discriminating Exception type
	public static boolean checkTableExistence(String table) throws SQLException, TabellaEsistente{
		DatabaseMetaData dbm = getConnection().getMetaData();
		ResultSet tables = dbm.getTables(null, null, table, null);
		if(!tables.next()) return true;
		throw new TabellaEsistente("Tabella " + table + " presente nel database!");
	}

}
