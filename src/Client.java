import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Client implements Serializable {
	private int currentID;
	private int id;
	private String lastname;
	private String firstname;
	private String address;
	public LinkedList<Seat> seats;
	
	public Client(String lastname, String firstname, String address) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.address = address;
	}
	
	public void setCurrentId(int id) {
		this.currentID = id;
	}
	
	public int getId() {
		return this.currentID;
	}
	
	public String getLastname() {
		return this.lastname;
	}
	
	public String getFirstname() {
		return this.firstname;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String toString() {
		return this.firstname + " " + this.lastname;
	}
	
	public String getFullString() {
		return "Client n�" 
				+ this.currentID
				+ " : " + this.firstname
				+ " " + this.lastname
				+ " (" + this.address + ")";
	}
	
	public void addSeat(Seat s) {
		seats.add(s);
	}
	
	public void removeSeat(Seat s) {
		if (seats.contains((Seat) s))
			seats.remove((Seat) s);
	}
	
	public List<Seat> getSeats() {
		return seats;
	}
	
	public double getReservationCost() {
		double result = 0;
		
		for(Seat s : seats)
			result += s.getType().getPrice();
		
		return result;
	}
	
	public String getExplictedCost() {
		StringBuffer buffer = new StringBuffer();
		
		for(Seat s : seats)
			buffer.append(s.toString() + " ("+ s.getType().getPrice() +"e)\n");
		
		buffer.append("Total : " + this.getReservationCost() + "e\n");
		
		return buffer.toString();
	}
}
