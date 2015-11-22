import java.io.Serializable;
import java.util.UUID;

public class Client implements Serializable {
	private int currentID;
	private String lastname;
	private String firstname;
	private String address;
	
	public Client(String lastname, String firstname, String address) {
		this.firstname = firstname;
		this.lastname = lastname;
		this.address = address;
	}
	
	public void setCurrentId(int id)
	{
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
		return "Client n°" 
				+ this.currentID
				+ " : " + this.firstname
				+ " " + this.lastname
				+ " (" + this.address + ")";
	}
}
