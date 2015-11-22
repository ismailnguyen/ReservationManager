import java.util.UUID;

public class Client {
	private int currentID;
	private String lastname;
	private String firstname;
	private String address;
	
	public Client(int id, String lastname, String firstname, String address) {
		this.currentID = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.address = address;
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
		return "Client n°" 
				+ this.currentID
				+ " : " + this.firstname
				+ " " + this.lastname
				+ " (" + this.address + ")";
	}
}
