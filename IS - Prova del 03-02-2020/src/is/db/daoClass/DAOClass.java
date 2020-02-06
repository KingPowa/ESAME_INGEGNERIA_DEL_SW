package is.db.daoClass;

import java.util.ArrayList;

import is.db.exception.ElementoInesistente;
import is.db.exception.ElementoInvalido;

//Public interface for DAOClasses
public interface DAOClass<T> {

	//Method for creating an element, database exception cause ElementoInvalido throw
	public T create(T element) throws ElementoInvalido;
	
	//Method for reading an element
	//1 - Non-Existence throws ElementoInesistente
	//2 - Database error throws ElementoInvalido
	public T read(int id) throws ElementoInvalido, ElementoInesistente;
	
	//Method for updating, referring to invalid elements throws ElementoInesistente
	//false if update has failed
	public boolean update(T element) throws ElementoInesistente;
	
	//Method for updating, referring to invalid elements throws ElementoInesistente
	public boolean delete(T element) throws ElementoInesistente;
	
	//Method for getting HashSet of element in database, database error throws ElementoInvalido
	public ArrayList<T> readAll() throws ElementoInvalido;
	
	//Used for creating the table associated to DAOclass, return true if table has been created
	//or already exists
	public boolean createTable();
	
}
