public class Seat
{
	private int row;
	private int col;
	private SeatType type;
	private boolean isBooked;

	public Seat(int row, int line, SeatType type, boolean isBooked) {
		this.row = row;
		this.col = line;
		this.type = type;
		this.isBooked = isBooked;
	}
	
	public void setRow(int row) {
		this.row = row;
	}
	
	public int getRow() {
		return row;
	}
	
	public void setCol(int col) {
		this.col = col;
	}
	
	public int getCol() {
		return col;
	}
	
	public void setBooked(boolean isBooked) {
		this.isBooked = isBooked;
	}
	
	public boolean isBooked() {
		return isBooked;
	}
	
	public void setType(SeatType type) {
		this.type = type;
	}
	
	public SeatType getType() {
		return type;
	}

	public String toString() {
		char rowLetter = (char) ('A' + row);
		return "" + rowLetter + col;
	}
}
