package is.main;

import java.beans.Statement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import is.db.DBManager;
import is.db.exception.ElementoInesistente;
import is.db.exception.ElementoInvalido;
import is.entity.*;

public class InjectFixedValues {

	public static void main(String[] args) {
		//Funzione per immettere valori nel database
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
		
		Random random = new Random();
		System.out.println("Funzione per immettere valori nel database.");
		Scanner scan = new Scanner(System.in);
		String choice = "";
		while(true) {
			System.out.println("Uso:\n");
			System.out.println("Digita (s)tudente per inserire uno studente.");
			System.out.println("Digita (e)laborato per inserire un elaborato.");
			System.out.println("Digita (d)ocente per inserire un docente.");
			System.out.println("Digita (x)eliminate per eliminare le tabelle");
			System.out.println("Digita (q)uit per uscire.");
			choice = scan.next();
			if(choice.startsWith("q") == true) break;
			else if(choice.startsWith("s")) {
				try {
					DBManager.studI.create(new Studente(0, random.nextInt(200), "StudenteF", "StudenteF"));
				} catch (ElementoInvalido e) {}
			}
			else if(choice.startsWith("d")) {
				try {
					DBManager.docI.create(new Docente(0, "DocenteF", "DocenteF", new ArrayList<Elaborato>()));
				} catch (ElementoInvalido e) {}
			}
			else if(choice.startsWith("e")) {
				System.out.println("\nSpecifica un id docente per l'elaborato.");
				int choice1 = scan.nextInt();
				try {
					Docente docente = DBManager.docI.read(choice1);
					DBManager.elabI.create(new Elaborato(0, "Fittizio", docente));
				} catch (ElementoInvalido e) {
				} catch (ElementoInesistente e) { System.out.println("Docente non esistente"); }
			}
			else if(choice.startsWith("x")){
				Connection conn = null;
				try {
					conn = DBManager.getConnection();
					PreparedStatement stmt = conn.prepareStatement("DROP ALL OBJECTS");
					stmt.execute();
					System.out.println("Tabelle eliminate.\n");
				} catch (SQLException e ) { e.printStackTrace();}
				allTable = DBManager.docI.createTable() && DBManager.studI.createTable() && DBManager.elabI.createTable() && DBManager.assI.createTable() && DBManager.ricI.createTable();
				if(!allTable) {
					System.out.println("Non è stato possibile creare le tabelle.\n");
				}
			}
		}
	}
	
}
