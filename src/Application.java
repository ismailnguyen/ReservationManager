import java.io.FileNotFoundException;
import java.io.IOException;

public class Application {
	public static void main(String[] args) throws IOException, NumberFormatException, InvalidActionException, ClassNotFoundException {
		try {
			//new ReservationManagerConsole();
			new ReservationManagerGUI();
		} 
		catch (NumberFormatException 
				| InvalidActionException 
				| FileNotFoundException e) {
			System.err.println(e.getMessage());
		}
	}
}
