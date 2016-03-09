public class ClientVIP extends Client {

	public ClientVIP(String lastname, String firstname, String address) {
		super(lastname, firstname, address);
	}

	@Override
	public String toString() {
		return super.toString() + " \"VIP\"";
	}
	
	@Override
	public double getReservationCost() {
		double result = 0;
		
		for(Seat s : seats)
			result += getReducedPrice(s.getType());
		
		return result;
	}
	
	@Override
	public String getExplictedCost() {
		if(seats.size() == 0)
			return "No reservation made for this client.\n";
		
		StringBuffer buffer = new StringBuffer();
		
		for(Seat s : seats)
		{
			buffer.append(s.toString()
							+ " (" + s.getType().getPrice()
							+ "e -"
							+ getPromotionBySeatType(s.getType()) * 100
							+ "% => "
							+ getReducedPrice(s.getType())
							+ "e )\n");
		}
			
		buffer.append("Total : " + this.getReservationCost() + "e\n");
		
		return buffer.toString();
	}
	
	private double getPromotionBySeatType(SeatType type) {
		return type == SeatType.FIRST_CATEGORY ?
					0.3
					: type == SeatType.SECOND_CATEGORY ?
							0.2
							: type == SeatType.THIRD_CATEGORY ?
									0.1
									: 0;
	}
	
	private double getReducedPrice(SeatType type) {
		return type.getPrice() - (type.getPrice() * getPromotionBySeatType(type));
	}
}
