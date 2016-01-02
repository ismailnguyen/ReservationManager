public class ClientGroup extends Client {

	public ClientGroup(String lastname, String firstname, String address) {
		super(lastname, firstname, address);
	}
	
	@Override
	public String toString() {
		return super.toString() + " \"Group\"";
	}

	@Override
	public double getReservationCost() {
		double result = 0;
		
		for(Seat s : seats)
			result += s.getType().getPrice();
		
		return result - (result * getPromotionByQuantity(seats.size()));
	}
	
	@Override
	public String getExplictedCost() {
		if(seats.size() == 0)
			return "No reservation made for this client.\n";
		
		StringBuffer buffer = new StringBuffer();
		
		for(Seat s : seats)
			buffer.append(s.toString()
					+ " (" + s.getType().getPrice()
					+ "e =>\n");
		
		if(getPromotionByQuantity(seats.size()) != 0)
			buffer.append(" -"
							+ getPromotionByQuantity(seats.size()) * 100
							+ "%\n");
		
		buffer.append("Total : " + this.getReservationCost() + "e\n");
		
		return buffer.toString();
	}
	
	private double getPromotionByQuantity(int nbSeats) {
		return nbSeats >= 5 && nbSeats <= 10 ?
				0.1
				: nbSeats > 10 ?
						0.2
						: 0;
	}
}
