public enum SeatType
{
	SCENE("S", -1.0),
	OBSTACLE("X", -1.0),
	FIRST_CATEGORY("a", 125.0), 
	SECOND_CATEGORY("b", 80.0),
	THIRD_CATEGORY("c",50.0), 
	FOURTH_CATEGORY("d",20.0),
	FIFTH_CATEGORY("e",10.0);

	private String symbole;
	private double price;

	SeatType (String symbole, double price)
	{
		this.symbole = symbole;
		this.price = price;
	}

	public static SeatType getSeatTypeFromSymbole(String symbole)
	{
		switch(symbole.toUpperCase())
		{
		case "A":
			return SeatType.FIRST_CATEGORY;
		
		case "B":
			return SeatType.SECOND_CATEGORY;
		
		case "C":
			return SeatType.THIRD_CATEGORY;
		
		case "D":
			return SeatType.FOURTH_CATEGORY;
		
		case "E":
			return SeatType.FIFTH_CATEGORY;
		
		case "S":
			return SeatType.SCENE;
		
		case "X":
			return SeatType.OBSTACLE;
		
			default:
				return null;
		}
	}
	
	public static boolean isBooked(String symbole) {
		return Character.isUpperCase(symbole.charAt(0));
	}
	
	public String getSymbole()
	{
		return symbole;
	}

	public double getPrice()
	{
		return price;
	}
	
	@Override
	public String toString()
	{
		return symbole;
	}
}
