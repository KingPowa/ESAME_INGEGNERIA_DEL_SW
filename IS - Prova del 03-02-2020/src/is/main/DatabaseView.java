package is.main;

import java.util.ArrayList;

import is.db.DBManager;
import is.db.exception.ElementoInvalido;
import is.entity.*;

public class DatabaseView {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			ArrayList<Assegnazione> ass = DBManager.assI.readAll();
			ArrayList<Elaborato> elab = DBManager.elabI.readAll();
			ArrayList<Docente> doc = DBManager.docI.readAll();
			ArrayList<Studente> std = DBManager.studI.readAll();
			ArrayList<Richiesta> ric = DBManager.ricI.readAll();
			
			System.out.println("Elaborati:\n");
			Utility.printList(elab);
			System.out.println("Docenti:\n");
			Utility.printList(doc);
			System.out.println("Studenti:\n");
			Utility.printList(std);
			System.out.println("Assegnazioni:\n");
			Utility.printList(ass);
			System.out.println("Richieste:\n");
			Utility.printList(ric);
			
		} catch (ElementoInvalido e){
			
		}
	}

}
