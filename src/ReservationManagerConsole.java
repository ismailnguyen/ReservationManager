import java.io.FileNotFoundException;
import java.util.Scanner;

public class ReservationManagerConsole {
	
	private Scanner scan = new Scanner(System.in);
	private Theater theater;

	public ReservationManagerConsole() throws NumberFormatException, InvalidActionException, FileNotFoundException {
		System.out.println("Welcome to the Reservation Manager");
		
		theater = new Theater("theater1.csv");
		
		loop:while(true){     
		     System.out.println("What do you want to do (h for help)");	    
	
		     switch(scan.nextLine()) {
			     case "h":
			     case "H":
				     showHelp();
				     break;
	
			     case "q":
		    	 case "Q":
		    		 System.out.println("Bye Bye");
				     System.exit(0);
				     break loop;

		    	 case "st":
		    	 case "ST":
		    		 showTheater();
		    		 break;
				     
		    	 case "mr":
		    	 case "MR":
		    		 makeReservation();
		    		 break;

		    	 case "cr":
		    	 case "CR":
		    		 cancelReservation();
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
		System.out.println("q: Quit");
	}

	private void showTheater() {
		System.out.println(theater); 
	}
	
	//Methode pour faire une reservation
	private void makeReservation() throws NumberFormatException, InvalidActionException {
		updateReservation(true);
	}

	//Methode pour annuler une reservation
	private void cancelReservation() throws NumberFormatException, InvalidActionException {
		updateReservation(false);
	}
	
	private void updateReservation(boolean isBooking) throws NumberFormatException, InvalidActionException {
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
	
	public static void main(String[] args) throws NumberFormatException, InvalidActionException, FileNotFoundException {
		try {
			new ReservationManagerConsole();
		} 
		catch (NumberFormatException 
				| InvalidActionException e) {
			System.out.println(e.getMessage());
			new ReservationManagerConsole();
		}
	}
}


