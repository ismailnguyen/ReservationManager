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

	public ReservationManagerConsole() throws NumberFormatException, InvalidActionException, IOException, ClassNotFoundException {
		System.out.println("Welcome to the Reservation Manager");
		
		File dir = new File("./");
		File[] theaterFile = dir.listFiles(new FilenameFilter() {
			public boolean accept (File dir, String filename) {
				return filename.endsWith(".bak");
			}
		});
		
		theater = new Theater(theaterFile.length > 0 ? 
				theaterFile[0].getAbsolutePath()
				: Const.THEATER_FILE);
		
		File clientFile = new File(Const.CLIENTS_FILE);
		clients = clientFile.exists() ?
						Serializer.<LinkedList<Client>>loadFromFile(Const.CLIENTS_FILE)
						: new LinkedList<Client>();
						
		int i = 0;
		for(Client c : clients)
			c.setCurrentId(i++);
		
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
		    		 
		    	 case "SR":
		    		 showReservation();
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
		     }
	     }
	}
	
	private void showHelp() {
		System.out.println("h: Print this help");
		
		System.out.println("st: Show Theater");
		System.out.println("mr: Make a Reservation");
		System.out.println("cr: Cancel a Reservation");
		System.out.println("sr: Show Reservation");
		
		System.out.println("lc: List of clients");
		System.out.println("ac: Add  new client");
		System.out.println("rc: Remove a client");
		
		System.out.println("q: Quit");
	}

	private void showTheater() {
		System.out.println(theater + "\n");
	}
	
	private void showReservation() throws InvalidActionException {
		System.out.println("Client list : ");
		if(!listDetailledClients())
			System.exit(0);
		
		System.out.println("Please enter the id of the wanted client or -1 to cancel :");
		
		Client selectedClient = selectClient();
		
		if(selectedClient != null)
		{
			System.out.println(selectedClient.toString() + " has reserved seats number :");
			System.out.println(selectedClient.getExplictedCost());;			
		}
	}
	
	//Methode pour faire une reservation
	private void makeReservation() throws NumberFormatException, InvalidActionException, IOException {
		if(theater.getNbFreeSeats() > 0)
			updateReservation(true);
		else
			System.out.println("House full.");
	}

	//Methode pour annuler une reservation
	private void cancelReservation() throws NumberFormatException, InvalidActionException, IOException {
		if(theater.getNbBookedSeats() > 0)
			updateReservation(false);
		else
			System.out.println("No reservation have been made yet.");
	}
	
	private void updateReservation(boolean isBooking) throws NumberFormatException, InvalidActionException, IOException {
		System.out.println("Client list : ");
		if(!listDetailledClients())
			System.exit(0);
		
		System.out.println("Please enter the id of the wanted client or -1 to cancel :");
		
		Client selectedClient = selectClient();
		
		if(selectedClient != null)
		{
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
			{
				//On reserve la place en convertissant la chaine en nombre et en la soustrayant pour qu'elle corresponde a nos attentes
				if(theater.makeReservation(row.charAt(0) - 65, Integer.parseInt(col)))
					//Si la résérvation dans la salle est faite, on ajoute la place à la liste des résérvations du client
					selectedClient.addSeat(theater.getSeat(row.charAt(0) - 65, Integer.parseInt(col)));
			}
			else
			{
				//Si le client a déja reservé cette place, on lui enlève
				if(selectedClient.removeSeat(theater.getSeat(row.charAt(0) - 65, Integer.parseInt(col))))
					//On annule la place en convertissant la chaine en nombre et en la soustrayant pour qu'elle corresponde a nos attentes
					theater.cancelReservation(row.charAt(0) - 65, Integer.parseInt(col));
			}

			//ex : on veut la place A2 : 'A' = 65 en ASCII et '2' = 50 donc 'A'-65 = 0 et '2'-48 = 2
		
			Serializer.saveToFile(Const.CLIENTS_FILE, clients);
			
			//On affiche la nouvelle salle
			showTheater();
		}
	}
	
	private void listClients() {
		System.out.print("Client list : [");
		
		for(Client c : clients)
			if(c != clients.getLast())
				System.out.print(c.toString() + ", ");
			else
				System.out.print(c.toString());
		
		System.out.println("]");
	}
	
	private boolean listDetailledClients() {
		if(clients.size() > 0)
		{
			for(Client c : clients)
				System.out.println(c.getFullString());
			
			return true;
		}
		else
			System.out.println("No clients");
		
		return false;
	}
	
	public void addClient() throws IOException {
		String firstname = new String();
		String lastname = new String();
		String address = new String();
		
		System.out.print("Lastname : ");
		lastname = scan.next();
		
		System.out.print("Firstname : ");
		firstname = scan.next();
		
		System.out.print("Address : ");
		address = scan.next();
		
		int nextId = clients.size() > 0 ?
						clients.getLast().getId() + 1
						: 0;
						
		System.out.println("Choose client type :");
		System.out.println("1 - Client");
		System.out.println("2 - VIP");
		System.out.println("3 - Group");
		
		switch(scan.nextInt())
		{
			case 1 :
				clients.add(new Client(lastname,
						firstname,
						address));
				break;
			case 2 :
				clients.add(new ClientVIP(lastname,
						firstname,
						address));
				break;
			case 3 :
				clients.add(new ClientGroup(lastname,
						firstname,
						address));
				break;
		}
				
		clients.getLast().setCurrentId(nextId);

		Serializer.saveToFile(Const.CLIENTS_FILE, clients);
	}
	
	public Client selectClient() throws InvalidActionException {
		Client selectedClient = null;
		
		try {
			int id = scan.nextInt();
			
			for(Client c : clients)
				if(c.getId() == id) {
					selectedClient = c;
					break;
				}
			
			if(selectedClient == null && id != -1)
				throw new InvalidActionException("Invalid selection");
			
			return id != -1 ?
						selectedClient
						: null;
		}
		catch(RuntimeException e) {
			scan.nextLine();
			throw new InvalidActionException("This is not a valid number !");
		}
	}
	
	public void removeClient() throws InvalidActionException, IOException {
		if(!listDetailledClients())
			System.exit(0);
		
		System.out.println("Please enter the id of the client to be removed or -1 to cancel :");
	
		Client selectedClient = selectClient();
		
		if(selectedClient != null)
		{
			clients.remove((Client) selectedClient);
		
			System.out.println(selectedClient.toString() + " was removed with success.");
			
			Serializer.saveToFile(Const.CLIENTS_FILE, clients);
		}
	}
	
	public static void main(String[] args) throws IOException, NumberFormatException, InvalidActionException, ClassNotFoundException {
		try {
			//new ReservationManagerConsole();
			new ReservationManagerGUI();
		} 
		catch (NumberFormatException 
				//| InvalidActionException 
				| FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}
}


