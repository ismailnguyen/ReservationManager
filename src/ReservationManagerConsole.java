import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Scanner;

public class ReservationManagerConsole {
	
	private Scanner scan = new Scanner(System.in);
	private Theater theater;
	private LinkedList<Client> clients;

	public ReservationManagerConsole() throws NumberFormatException, InvalidActionException, IOException {
		System.out.println("Welcome to the Reservation Manager");
		
		File dir = new File("./");
		File[] file = dir.listFiles(new FilenameFilter() {
			public boolean accept (File dir, String filename) {
				return filename.endsWith(".bak");
			}
		});
		
		clients = new LinkedList<Client>();
		
		theater = new Theater(file.length > 0 ? 
								file[0].getAbsolutePath()
								: "theater1.csv");
		
		loop:while(true){     
		     System.out.println("What do you want to do (h for help)");	    
	
		     switch((scan.nextLine()).toUpperCase()) {
			     case "H":
				     showHelp();
				     break;

		    	 case "Q":
		    		 System.out.println("Bye Bye");
				     System.exit(0);
				     break loop;

		    	 case "ST":
		    		 showTheater();
		    		 break;

		    	 case "MR":
		    		 makeReservation();
		    		 break;

		    	 case "CR":
		    		 cancelReservation();
		    		 break;

		    	 case "LC":
		    		 listClients();
		    		 break;

		    	 case "AC":
		    		 addClient();
		    		 break;

		    	 case "RC":
		    		 removeClient();
		    		 break;
	
			     default:
				     System.out.println("You didn't choose correct option ...");
		     }
	     }
	}
	
	private void showHelp() {
		System.out.println("h: Print this help");
		
		System.out.println("st: Show Theater");
		System.out.println("mr: Make a Reservation");
		System.out.println("cr: Cancel a Reservation");
		
		System.out.println("lc: List of clients");
		System.out.println("ac: Add  new client");
		System.out.println("rc: Remove a client");
		
		System.out.println("q: Quit");
	}

	private void showTheater() {
		System.out.println(theater); 
	}
	
	//Methode pour faire une reservation
	private void makeReservation() throws NumberFormatException, InvalidActionException, IOException {
		updateReservation(true);
	}

	//Methode pour annuler une reservation
	private void cancelReservation() throws NumberFormatException, InvalidActionException, IOException {
		updateReservation(false);
	}
	
	private void updateReservation(boolean isBooking) throws NumberFormatException, InvalidActionException, IOException {
		//Chaine qui recupere la ligne que choisi l'user
		String row = new String();
		
		//Chaine qui recupere la colonne que choisi l'user
		String col = new String();

		//On affiche la repartition des sièges
		showTheater();

		//On demande a l'user qu'il choisisse une ligne (lettre)
		System.out.println("Please enter a row letter :");

		//On recupere la ligne && on le met en majuscule
		row = (scan.next()).toUpperCase();

		//On demande a l'user de choisir une  colonne (chiffre)
		System.out.println("Please enter line number : ");

		//On la recupere
		col = scan.next();
	
		if(isBooking)
			//On reserve la place en convertissant la chaine en nombre et en la soustrayant pour qu'elle corresponde a nos attentes
			theater.makeReservation(row.charAt(0) - 65, Integer.parseInt(col));
		else
			//On annule la place en convertissant la chaine en nombre et en la soustrayant pour qu'elle corresponde a nos attentes
			theater.cancelReservation(row.charAt(0) - 65, Integer.parseInt(col));

		//ex : on veut la place A2 : 'A' = 65 en ASCII et '2' = 50 donc 'A'-65 = 0 et '2'-48 = 2
	
		//On affiche la nouvelle salle
		showTheater();
	}
	
	private void listClients() {
		System.out.println("Client list : [");
		
		for(Client c : clients)
			System.out.println(c.getFullString());
		
		System.out.println("]");
	}
	
	public void addClient() {
		String firstname = new String();
		String lastname = new String();
		String address = new String();
		
		System.out.print("Lastname : ");
		lastname = scan.next();
		
		System.out.print("Firstname : ");
		firstname = scan.next();
		
		System.out.print("Address : ");
		address = scan.next();
		
		clients.add(new Client(lastname,
								firstname,
								address));
	}
	
	public Client selectClient() {
		return null;
	}
	
	public void removeClient() throws InvalidActionException {
		listClients();
		
		System.out.println("Please enter the id of the client to be removed or -1 to cancel the action.");
	
		int id;
		Client selectedClient = null;
		
		try {
			id = scan.nextInt();
			
			for(Client c : clients)
			{
				if(c.getId() == id) {
					selectedClient = c;
					break;
				}
			}
			
			if(selectedClient == null && id != -1)
				throw new InvalidActionException("Invalid selection");
		}
		catch(RuntimeException e) {
			scan.nextLine();
			throw new InvalidActionException("This is not a valid number !");
		}
	}
	
	public static void main(String[] args) throws IOException, NumberFormatException, InvalidActionException {
		try {
			new ReservationManagerConsole();
		} 
		catch (NumberFormatException 
				| InvalidActionException 
				| FileNotFoundException e) {
			System.err.println(e.getMessage());
			new ReservationManagerConsole();
		}
	}
}


