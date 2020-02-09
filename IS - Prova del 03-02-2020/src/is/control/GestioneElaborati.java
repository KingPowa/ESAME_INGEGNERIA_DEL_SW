package is.control;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import is.db.DBManager;
import is.db.exception.*;
import is.entity.*;
import is.exception.AssegnazioneAutomatica;
import is.exception.ElaboratoAssente;
import is.exception.FailException;
import is.exception.PendingRequest;
import is.exception.RichiestaRespinta;

public class GestioneElaborati {
	
	public void mostraElencoElaborati(Docente docente){
		try {
			ArrayList<Elaborato> elencoElab = DBManager.elabI.readAll(docente);
			System.out.println("Elenco Elaborati Docente id " + docente.getMatDocente() + ":\n");
			Utility.printList(elencoElab);
		} catch (ElementoInvalido e) {
			e.printStackTrace();
			System.out.println("Nessun Elaborato a causa di un errore.\n");
		}
	}
	
	public void mostraElencoElaboratiAssegnati(Docente docente){
		try {
			ArrayList<Elaborato> elencoElabAss = DBManager.elabI.readAllAssegnati(docente);
			System.out.println("Elenco Elaborati Assegnati Docente id " + docente.getMatDocente() + ":\n");
			Utility.printList(elencoElabAss);
		} catch (ElementoInvalido e) {
			e.printStackTrace();
			System.out.println("Nessun Elaborato a causa di un errore.\n");
		}
	}
	
	public void mostraElencoElaboratiDisponibili(){
		try {
			ArrayList<Elaborato> elencoElabD = DBManager.elabI.readAllDisponibili();
			System.out.println("Elenco Elaborati Disponibili:\n");
			Utility.printList(elencoElabD);
		} catch (ElementoInvalido e) {
			e.printStackTrace();
			System.out.println("Nessun elaborato a causa di un errore.\n");
		}
	}
	
	public Elaborato aggiuntaElaborato(String insegnamento, Docente docente) throws FailException{
		Elaborato elab = new Elaborato(0, insegnamento, docente); //autoassigned elab
		//A duplicate cannot exist
		try {
			DBManager.elabI.create(elab);
			docente.aggiungiElaborato(elab);
			System.out.println("Elaborato: " + elab + " aggiunto.\n");
			return elab;
		} catch (ElementoInvalido e) {
			throw new FailException(e.getMessage());
		}
	}
	
	public boolean rimuoviElaborato(Elaborato elaborato, Docente docente) {
		if(docente.getElaborati().contains(elaborato)) {
			try {
				DBManager.elabI.delete(elaborato);
				docente.rimuoviElaborato(elaborato.getID());
				System.out.println("Elaborato: " + elaborato + " rimosso.\n");
				return true;
			} catch (ElementoInesistente | ElaboratoAssente e) {
				System.out.println("Elaborato: " + elaborato + " non presente.\n");
				return false;
			}
		}
		else {
			System.out.println("Elaborato: " + elaborato + " non relativo al docente.\n");
			return false;
		}
	}
	
	//funzione per aggregare la convalida dello studente
	private void convalidaStudente(Studente studente) throws FailException, RichiestaRespinta, PendingRequest{
		try {
			studente = DBManager.studI.read(studente.getMatricola());
			if(studente.isAssegnato()) {
				System.out.println("Lo studente: " + studente + " è già stato assegnato. Richiesta Respinta");
				throw new RichiestaRespinta("RICHIESTA RESPINTA");
			}
		} catch(ElementoInesistente e) {
			System.out.println("Lo studente: " + studente + " non è registrato. Richiesta Respinta");
			throw new RichiestaRespinta("RICHIESTA RESPINTA");
		} catch(ElementoInvalido s) {
			System.out.println("Errore nel database. Richiesta Respinta.");
			throw new FailException(s.getMessage());
		}
		//Passo 2 - Verifico se lo studente ha una richiesta aperta, nel qual caso la servo
		Richiesta richiesta = null;
		try {
			richiesta = DBManager.ricI.read(studente.getMatricola());
			System.out.println("Lo studente: " + studente + " ha già effettuato una richiesta. Tentativo di servirla.");
			throw new PendingRequest("Richiesta già aperta", richiesta);
		} catch (ElementoInesistente | ElementoInvalido e) {

		}
	}
	
	public Assegnazione richiestaAssegnazione(Studente studente) throws FailException, RichiestaRespinta{
		// Passo 1 - Verifico se lo studente è stato asssegnato o è valido
		boolean richiestaEsistente = false;
		Richiesta richiesta = null;
		try {
			convalidaStudente(studente);
		} catch(PendingRequest pr) {
			richiesta = pr.getRequest();
			richiestaEsistente = true;
		}
		
		Scanner scanner = new Scanner(System.in);
		if(!richiestaEsistente) {
			//Passo 3 - Effettuo la scelta di un elaborato
			//Per farlo, memorizzo la scelta
			ArrayList<Elaborato> elencoScelti = new ArrayList<Elaborato>();
			try {
				ArrayList<Elaborato> elaboratiDisponibili = DBManager.elabI.readAllDisponibili();
				while (elencoScelti.size() < 3 && elaboratiDisponibili.size() > 0) {
					int counter = 1;
					System.out.println("...Elenco Elaborati disponibili...");
					for (Elaborato e : elaboratiDisponibili) {
						System.out.println(counter + " - " + e);
						counter++;
					}
					System.out.println("\nE' possibile scegliere altri " + (3 - elencoScelti.size()) + " elaborati.");
					System.out.println("Digita:\n• Un numero per selezionare l'elaborato corris.;");
					System.out.println("• Altro per terminare la selezione.");
					try {
						int choice = scanner.nextInt();
						Elaborato elabScelto = null;
						for(Elaborato e: elaboratiDisponibili) {
							if(e.getID() == choice) {
								elabScelto = e;
								break;
							}
						}
						
						if (elabScelto != null) {
							elencoScelti.add(elabScelto);
							elaboratiDisponibili.remove(elabScelto);
							System.out.println("\nScelto elaborato id." + choice + ".\n");
						} else {
							System.out.println("\nNumero invalido.\n");
						}
					} catch (InputMismatchException e) {
						scanner.nextLine();
						System.out.println("\nSi è scelto di terminare.\n");
						break;
					}
				}
				//Gestisce il caso in cui non esistono elaborati disponibili
				//E le scelte sono dunque nulle
				if (elencoScelti.size() == 0 && elaboratiDisponibili.size() == 0) {
					System.out.println("Non sono presenti elaborati disponibili.\n");
					throw new RichiestaRespinta("RICHIESTA RESPINTA");
				}
			} catch (ElementoInvalido e) {
				e.printStackTrace();
				throw new FailException(e.getMessage());
			}
			//Studente ed elaborato vengono passati alla funzione per la richiesta
			//Caso eccezionale: nessuna richiesta viene elaborata perchè il numero degli esami
			//Scelti è nullo
			if (elencoScelti.isEmpty()) return assegnazioneAutomatica(studente, null);
			try {
				richiesta = this.richiediAssegnazione(studente, elencoScelti);
			} catch (AssegnazioneAutomatica aa) {
				System.out.println(aa.getMessage());
				return assegnazioneAutomatica(studente, null);
			}
		}
		

		Docente docente = null;
		Assegnazione assegnazione = null;
		while(assegnazione == null) {
			try {
				//C'è un problema: se il professore, mentre legge la mail, si trova il suo elaborato
				//Eliminato dalla richiesta di riferimento, risulta necessario validare l'elaborato
				//di riferimento. Per fare ciò, nel momento in cui un elaborato viene recuperato dal db,
				//SE PRESENTA VALORI NULLI IN POSIZIONI MINORI O UGUALI DEL NUMERO DI ELAB RIFIUTATI
				//ALLORA ELAB RIF VIENE DIMINUITO (non vengono contati elaborati eliminati già rifiutati)
				int daRifiutare = richiesta.getElabRif();
				//Se daRifiutare è maggiore della dimensione significa che ogni elaborato è già stato rifiutato!
				if(daRifiutare >= richiesta.getElaborati().size()) return assegnazioneAutomatica(studente, richiesta);
				docente = richiesta.getElaborati().get(daRifiutare).getDocente();
				boolean accettato = false;
				System.out.println("\n\nDocente, è arrivata una richiesta di elaborato!");
				System.out.println("Per l'elaborato: " + richiesta.getElaborati().get(daRifiutare));
				System.out.println("\nDigita:\n• (s)ì per accettare;\n• Altro per rifiutare.\n");
				try{
					String choice = scanner.next();
					if(choice.startsWith("s") || choice.startsWith("S")) accettato = true;
				} catch (NoSuchElementException e) {
					System.out.println("Rifiutato!");
				}
				assegnazione = this.accettazioneRichiesta(docente, richiesta, accettato);
			} catch (PendingRequest pr) {
				//La nuova richiesta viene tornata dall'errore
				richiesta = pr.getRequest();
				//Notare che l'esistenza di una pending request fa si che il docente non sia nullo
				int daRifiutare = richiesta.getElabRif();
				//Se daRifiutare è maggiore della dimensione significa che ogni elaborato è già stato rifiutato!
				if(daRifiutare >= richiesta.getElaborati().size()) return assegnazioneAutomatica(studente, richiesta);
				docente = richiesta.getElaborati().get(daRifiutare).getDocente();
				//Grep the newDocente (which is surely valid)
				inviaMail(docente, richiesta);
			}
		}
		//Si esce se 
		// 1 - Avviene l'assegnazione
		// 2 - La richiesta viene respinta
		return assegnazione;
		//
	}
	
	//Precondition : elaborati is an array which contains only avaiable elaborati
	private Richiesta richiediAssegnazione(Studente studente, ArrayList<Elaborato> elaborati) throws FailException, RichiestaRespinta, AssegnazioneAutomatica{
		//Verify if CFU = 100
		if(studente.getCfu() >= 100) {
			//Create request. Elaborati array is ordered and any additional preference is eliminated
			Richiesta richiesta = new Richiesta(elaborati, studente);
			
			//Check if Docente's elaborati has been assigned more than 10
			//Not I do not need to call the database: the Docente is automatically valid
			//todo: rimuovi elaborati impraticabili
			
			ArrayList<Elaborato> elaboratiDisponibiliEmail = new ArrayList<Elaborato>();
			for(Elaborato e : elaborati) {
				Docente docente = e.getDocente();
				if(isElaboratiAssegnatiAnno(docente)) {
					elaboratiDisponibiliEmail.add(e);
				}
			}
			
			if(elaboratiDisponibiliEmail.isEmpty()) {
				System.out.println("Gli elaborati non rispettano i criteri. Assegnazione automatica.");
				//Creo una richiesta con il numero di rifiutati uguale al numero degli elaborati scelti
				throw new AssegnazioneAutomatica("Assegnazione automatica a seguito di elab. non disponibili.");
			}
			
			richiesta.setElaborati(elaboratiDisponibiliEmail);
			
			try {
				richiesta = DBManager.ricI.create(richiesta);
			} catch (ElementoInvalido e) {
				System.out.println("Impossibile creare richiesta a causa di errore di Database.");
				throw new FailException(e.getMessage());
			}
			inviaMail(elaboratiDisponibiliEmail.get(0).getDocente(), richiesta);
			System.out.println("Elaborata richiesta: " + richiesta);
			return richiesta;
		}
		else {
			System.out.println("Il numero di cfu è insufficiente (numero posseduto: " + studente.getCfu() + ").");
			throw new RichiestaRespinta("RICHIESTA RESPINTA");
		}
	}
	
	private Assegnazione accettazioneRichiesta(Docente docente, Richiesta richiesta, boolean accettato) throws FailException, RichiestaRespinta, PendingRequest{
		Studente studente = richiesta.getStudente();
		
		//Verify if request is valid
		try {
			richiesta = DBManager.ricI.read(studente.getMatricola());
		} catch (ElementoInvalido | ElementoInesistente e) {
			System.out.println("Richiesta inesistente! Assegnazione nulla.");
			e.printStackTrace();
			throw new FailException("Errore nel reperimento della richiesta.");
		}
		//to do
		if(richiesta == null) return null; //Impossible but for being sure
		
		//verify if elaborati array is empty
		//necessary in a real situation since richiesta could be modified by concurrence
		if(richiesta.getElaborati().isEmpty()) return assegnazioneAutomatica(studente, richiesta);
		
		//Verify elaborati are not disponibili
		ArrayList<Elaborato> effectiveElaburates = new ArrayList<Elaborato>();
		int daRiferirsi = richiesta.getElabRif();
		int counter = 0;
		for(Elaborato e : richiesta.getElaborati()) {
			if(elaboratoDisponibile(e)) {
				effectiveElaburates.add(e);
			}
			else {
				//significa che un elaborato presente nella richiesta è diventato non disponibile
				//Nel caso in cui questo sia stato in precedenza rifiutato bisogna diminuire il numero
				//della posizione a cui riferirsi
				if(counter < richiesta.getElabRif()) daRiferirsi--;
			}
			counter++;
		}
		
		//gli elaborati non disponibili sono automaticamente rimossi
		richiesta.setElabRif(daRiferirsi);
		richiesta.setElaborati(effectiveElaburates);
		//This can happen when an elaburate is removed or assigned for another student
		if(effectiveElaburates.isEmpty()) return assegnazioneAutomatica(studente, richiesta);
		
		//Verify if docente is referenced to the first elaburate of effectiveElaburates
		if(daRiferirsi > effectiveElaburates.size()) return assegnazioneAutomatica(studente, richiesta);
		if(!effectiveElaburates.get(daRiferirsi).getDocente().equals(docente)){
			//In this case, I want the system to send again the request to the right docente
			//Note request is still the same
			throw new PendingRequest("Richiesta non accettata reinviata al docente", richiesta);
		}
		
		//Finally, we need to verify if docente has accepted
		if(accettato) {
			System.out.println("Il docente " + docente + " ha accettato la richiesta.");
			Assegnazione assegnazione = new Assegnazione(effectiveElaburates.get(daRiferirsi), studente);
			try {
				if(!DBManager.ricI.delete(richiesta)) throw new FailException("Errore di eliminazione della richiesta.");
				assegnazione = DBManager.assI.create(assegnazione);
			} catch (ElementoInvalido | ElementoInesistente e) {
				System.out.println("C'è stato un errore nell'elaborazione della richiesta.");
				e.printStackTrace();
				throw new FailException("Errore nel reperimento della richiesta.");
			}
			studente.assegnaElaborato();
			richiesta.getElaborati().get(daRiferirsi).assegnaElaborato();
			return assegnazione;
		}
		
		else {
			System.out.println("Il docente " + docente + " non ha accettato la richiesta.");
			//We need to verify if there are more choices.
			//
			int newRif = richiesta.getElabRif()+1;
			if(richiesta.getElaborati().size() > newRif) {
				richiesta.setElaborati(effectiveElaburates);
				richiesta.setElabRif(newRif);
				try {
					if(!DBManager.ricI.update(richiesta)) throw new FailException("Errore di update della richiesta.");
				} catch (ElementoInesistente e) {
					System.out.println("Richiesta non esistente!");
					e.printStackTrace();
					throw new FailException("Errore nel reperimento della richiesta.");
				}
				throw new PendingRequest("Richiesta non accettata reinviata al docente", richiesta);
			}
			return assegnazioneAutomatica(studente, richiesta);
		}
		
	}
	
	private Assegnazione assegnazioneAutomatica(Studente studente, Richiesta richiesta) throws FailException, RichiestaRespinta{
		System.out.println("Richiesta erronea o non accettata per tutte le preferenze. "
				+ "\nVerrà assegnato il primo elaborato disponibile.");
		ArrayList<Elaborato> elaborati = new ArrayList<Elaborato>();
		try {
			elaborati = DBManager.elabI.readAllDisponibili();
		} catch (ElementoInvalido e) {
			System.out.println("Errore nel recupero degli elaborati disponibili!");
			throw new FailException("Errore nel reperimento degli elaborati.");
		}
		
		//Rimuovo tutti gli elaborati rifiutati
		if(richiesta != null) {
			for(Elaborato e: richiesta.getElaborati()) {
				if(elaborati.contains(e)) elaborati.remove(e);
			}
		}
		
		for(Elaborato e : elaborati) {
			Docente doc = e.getDocente();
			if(isElaboratiAssegnatiAnno(doc)) {
				Assegnazione assegnazione = new Assegnazione(e, studente);
				try {
					assegnazione = DBManager.assI.create(assegnazione);
					if(richiesta != null) {
						if(!DBManager.ricI.delete(richiesta)) throw new FailException("Errore di eliminazione della richiesta.");
					}
				} catch (ElementoInvalido | ElementoInesistente s) {
					System.out.println("C'è stato un errore nell'elaborazione della richiesta.");
					s.printStackTrace();
					throw new FailException("Errore nel reperimento della richiesta.");
				}
				studente.assegnaElaborato();
				e.assegnaElaborato();
				return assegnazione;
			}
		}
		
		System.out.println("Nessun elaborato disponibile. Richiesta respinta.");
		try {
			if(richiesta != null)
				if(!DBManager.ricI.delete(richiesta)) throw new FailException("Errore di eliminazione della richiesta.");
		} catch (ElementoInesistente s) {
			System.out.println("Richiesta inesistente.");
			s.printStackTrace();
			throw new FailException("Errore nel reperimento della richiesta.");
		}
		throw new RichiestaRespinta("RICHIESTA RESPINTA");
	}
	
	public Assegnazione stubSimulateAssegnazione(Studente studente, ArrayList<Integer> sceltaElaborati, ArrayList<Boolean> scelta) throws RichiestaRespinta, FailException{
		// Passo 1 - Verifico se lo studente è stato asssegnato o è valido
		boolean richiestaEsistente = false;
		Richiesta richiesta = null;
		try {
			convalidaStudente(studente);
		} catch(PendingRequest pr) {
			richiesta = pr.getRequest();
			richiestaEsistente = true;
		}

		if(!richiestaEsistente) {
			//Passo 3 - Effettuo la scelta di un elaborato
			//Per farlo, memorizzo la scelta
			ArrayList<Elaborato> elencoScelti = new ArrayList<Elaborato>();
			try {
				ArrayList<Elaborato> elaboratiDisponibili = DBManager.elabI.readAllDisponibili();
				for (int i = 0; i < sceltaElaborati.size() && elaboratiDisponibili.size() > 0; i++)  {
					int counter = 1;
					System.out.println("...Elenco Elaborati disponibili...");
					for (Elaborato e : elaboratiDisponibili) {
						System.out.println(counter + " - " + e);
						counter++;
					}
					System.out.println("\nE' possibile scegliere altri " + (3 - elencoScelti.size()) + " elaborati.");
					System.out.println("Digita:\n• Un numero per selezionare l'elaborato corris. (id);");
					System.out.println("• Altro per terminare la selezione.");
					int choice = sceltaElaborati.get(i);
					Elaborato elabScelto = null;
					for(Elaborato e: elaboratiDisponibili) {
						if(e.getID() == choice) {
							elabScelto = e;
							break;
						}
					}
					
					if (elabScelto != null) {
						elencoScelti.add(elabScelto);
						elaboratiDisponibili.remove(elabScelto);
						System.out.println("\nScelto elaborato id." + choice + ".\n");
					} else {
						System.out.println("\nNumero invalido.\n");
					}
				}
				//Gestisce il caso in cui non esistono elaborati disponibili
				//E le scelte sono dunque nulle
				if (elencoScelti.size() == 0 && elaboratiDisponibili.size() == 0) {
					System.out.println("Non sono presenti elaborati disponibili.\n");
					throw new RichiestaRespinta("RICHIESTA RESPINTA");
				}
			} catch (ElementoInvalido e) {
				e.printStackTrace();
				throw new FailException(e.getMessage());
			}
			//Studente ed elaborato vengono passati alla funzione per la richiesta
			//Caso eccezionale: nessuna richiesta viene elaborata perchè il numero degli esami
			//Scelti è nullo
			if (elencoScelti.isEmpty()) return assegnazioneAutomatica(studente, null);
			try {
				richiesta = this.richiediAssegnazione(studente, elencoScelti);
			} catch (AssegnazioneAutomatica aa) {
				System.out.println(aa.getMessage());
				return assegnazioneAutomatica(studente, null);
			}
		}


		Docente docente = null;
		Assegnazione assegnazione = null;
		int counter = 0;
		while(assegnazione == null) {
			try {
				//C'è un problema: se il professore, mentre legge la mail, si trova il suo elaborato
				//Eliminato dalla richiesta di riferimento, risulta necessario validare l'elaborato
				//di riferimento. Per fare ciò, nel momento in cui un elaborato viene recuperato dal db,
				//SE PRESENTA VALORI NULLI IN POSIZIONI MINORI O UGUALI DEL NUMERO DI ELAB RIFIUTATI
				//ALLORA ELAB RIF VIENE DIMINUITO (non vengono contati elaborati eliminati già rifiutati)
				int daRifiutare = richiesta.getElabRif();
				//Se daRifiutare è maggiore della dimensione significa che ogni elaborato è già stato rifiutato!
				if(daRifiutare >= richiesta.getElaborati().size()) return assegnazioneAutomatica(studente, richiesta);
				docente = richiesta.getElaborati().get(daRifiutare).getDocente();
				boolean accettato = false;
				if(scelta.size() > counter) accettato = scelta.get(counter);
				System.out.println("\n\nDocente, è arrivata una richiesta di elaborato!");
				System.out.println("Per l'elaborato: " + richiesta.getElaborati().get(daRifiutare));
				System.out.println("\nDigita:\n• (s)ì per accettare;\n• Altro per rifiutare.\n");
				
				if(!accettato) System.out.println("Rifiutato!");
				counter++;
				assegnazione = this.accettazioneRichiesta(docente, richiesta, accettato);
			} catch (PendingRequest pr) {
				//La nuova richiesta viene tornata dall'errore
				richiesta = pr.getRequest();
				//Notare che l'esistenza di una pending request fa si che il docente non sia nullo
				int daRifiutare = richiesta.getElabRif();
				//Se daRifiutare è maggiore della dimensione significa che ogni elaborato è già stato rifiutato!
				if(daRifiutare >= richiesta.getElaborati().size()) return assegnazioneAutomatica(studente, richiesta);
				docente = richiesta.getElaborati().get(daRifiutare).getDocente();
				//Grep the newDocente (which is surely valid)
				inviaMail(docente, richiesta);
			}
		}
		//Si esce se 
		// 1 - Avviene l'assegnazione
		// 2 - La richiesta viene respinta
		return assegnazione;
		//
	}
	
	//Metodo Fittizio
	private void inviaMail(Docente docente, Richiesta richiesta) {
		System.out.println("Email inviata al docente: " + docente + " per la richiesta: "+ richiesta);
	}
	
	private boolean elaboratoDisponibile(Elaborato elaborato) {
		//It would be better to save readAllDisponibili as a variable ready to use in every situation
		//as this is executed in a loop and at least one query is always nedded
		try {
			ArrayList<Elaborato> elaboratiDisponibili = DBManager.elabI.readAllDisponibili();
			return elaboratiDisponibili.contains(elaborato);
		} catch (ElementoInvalido e){
			e.printStackTrace();
			return false;
		}
	}
	
	private boolean isElaboratiAssegnatiAnno(Docente docente) {
		try {
			ArrayList<Elaborato> elaboratiAssegnati = DBManager.elabI.readAll(docente);

			int counter = 0;
			for(Elaborato e : elaboratiAssegnati) {
				if(DBManager.assI.isAssegnatoCurrentYear(e.getID())) counter++;
			}
			if(counter <= 10) return true;
			return false;
			
		} catch (ElementoInvalido e) {
			return false;
		}
	}
}
