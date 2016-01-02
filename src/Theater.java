import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class Theater extends JPanel {
	
	//GUI
	private int rectSize = 40;
	private int currentRow = -1;
	private int currentCol = -1;
	
	//Room settings
	private StringBuffer brand;
	
	//Seats
	private Seat[][] seats;
	
	//Filename
	private String filename;
	
	//Seat type
	private SeatType placeType;

	//Constructor 1
	public Theater(int nbRow, int nbCol) {
		seats = new Seat[nbRow][nbCol];
		
		//Fill double dimensional array
		for (int i = 0; i < nbRow; i++)
			for (int j = 0; j < nbCol; j++)
				//Fill each seat with first category type and available status
				seats[i][j] = new Seat(j, i, SeatType.FIRST_CATEGORY, false);
	}

	//Constructor 2 // Ex 7
	public Theater(String filename) throws FileNotFoundException {
		//Liste des contenus du fichier CSV
		List<String> values = new ArrayList<String>();
		
		//Chaine de caractere qui va contenir le symbole pour pour le convertir en une place (Seat)
		String symbole = new String();
		
		//Compteur pour ne pas prendre en compte certain caractere dans la chaine
		int a = 2;
		
		//on creer un objet file qui correspond a notre fichier csv
		File file = new File(filename);
		//On verifie que le fichier existe
		if (!file.exists())
			// Alors on affiche un message d'erreur
			throw new FileNotFoundException("This file doesn't exist !");
		//System.out.println("File " + filename + " loaded with success.");

		//On recupere le contenu du fichier dans un Scanner
		Scanner scan = new Scanner(new FileInputStream(file));
		//Ajoute ; en temps que delimiteur
		scan.useDelimiter(scan.delimiter() + "|;+");
		
		//On recupere le contenu du fichier que l'on met dans une chaine de caractere
		while (scan.hasNext()){
			String next = scan.next();
			if(!next.isEmpty())
				values.add(next);
		}

		//On declare un nouveau tableau
		seats = new Seat[Integer.parseInt(values.get(0))][Integer.parseInt(values.get(1))];
		
		//On rempli le tableau(ligne)
		for (int i = 0; i < Integer.parseInt(values.get(0)); i++)
			//colonne
			for (int j = 0; j < Integer.parseInt(values.get(1)); j++)
				//Si on n'a pas depasser la chaine
				if (a < values.size())
					seats[i][j] = new Seat(j,
											i,
											SeatType.getSeatTypeFromSymbole(values.get(a)),
											SeatType.isBooked(values.get(a++)));
	}
	
	public int getNbRow() {
		return seats.length;
	}
	
	public int getNbCol() {
		return seats[0].length;
	}
	
	public Seat getSeat(int row, int col) {
		return seats[col][row];
	}
	
	public SeatType getType(int row, int col){
		return seats[row][col].getType();
	}

	public boolean isBooked(int row, int col){
		return seats[row][col].isBooked();
	}
	
	//Renvoie le nombre de places libres restantes
	public int getNbFreeSeats() {
		int res = 0;
		
		for(int i=0; i<seats.length; i++)
			for(int j=0; j<seats[0].length; j++)
				if(!seats[i][j].isBooked()
					&& seats[i][j].getType() != SeatType.OBSTACLE
					&& seats[i][j].getType() != SeatType.SCENE)
					res++;
		
		return res;
	}
	
	//Renvoie le nombre de places réservées
		public int getNbBookedSeats() {
			int res = 0;
			
			for(int i=0; i<seats.length; i++)
				for(int j=0; j<seats[0].length; j++)
					if(seats[i][j].isBooked()
						&& seats[i][j].getType() != SeatType.OBSTACLE
						&& seats[i][j].getType() != SeatType.SCENE)
						res++;
			
			return res;
		}
	
	//Renvoie sous forme de chaine de caractere la disposition de la salle
	public String toString() {
		brand = new StringBuffer();
		
		//Double boucle
		for (int i = 0; i < seats.length; i++) {
			//Affiche un indicateur sous forme de lettre pour les lignes
			brand.append((char) ('A' + i));
			
			for (int j = 0; j < seats[0].length; j++) {
				//On recupere le type de la place
				placeType = seats[i][j].getType();
				
				//Si la place est prise
				brand.append(" " + (seats[i][j].isBooked() ? 
										//On l'affiche en majuscule
										placeType.getSymbole().toUpperCase()
										//sinon en minuscule
										: placeType.getSymbole()));
				
			}
			
			//retour à la ligne
			brand.append("\n");
		}

		brand.append("  ");//espace pour que ce soit plus lisible

		//On affiche un indicateur pour les colonnes en chiffre
		for (int k = 0; k < seats[0].length; k++)
			brand.append((int) (0 + k) + " ");

		return brand.toString();
	}
	
	//Fait une reservation
	public boolean makeReservation(int row, int col) throws InvalidActionException, IOException {
		return updateReservation(row, col, true);
	}

	//Pour annuler une reservation (comme pour reserver mais avec false au lieu de true dans setBooked())
	public boolean cancelReservation(int row, int col) throws InvalidActionException, IOException {
		return updateReservation(row, col, false);
	}

	private boolean updateReservation(int row, int col, boolean isBooking) throws InvalidActionException, IOException {
		//Si on veut prendre une place qui n'existe pas dans le tabeau
		if (row >= seats.length
				|| col >= seats[0].length)
			// Alors on affiche un message d'erreur
			throw new InvalidActionException("This place doesn't exist !");

		//Si l'utilisateur a choisi un obstacle ou la scene
		if(seats[row][col].getType() == SeatType.OBSTACLE
				|| seats[row][col].getType() == SeatType.SCENE)
			// Alors on affiche un message d'erreur
			throw new InvalidActionException("This isn't a seat !");
		
		//Si la place est deja prise (en majuscule)
		if ((isBooking && seats[row][col].isBooked())
				|| (!isBooking && !seats[row][col].isBooked()))
			throw new InvalidActionException(isBooking ?
												"This seat is already taken !"
												: "This seat is already free !");
		
		//Sinon (la place est libre)
		//on reserve la place
		seats[row][col].setBooked(isBooking);
		
		save();
		
		return true;
	}
	
	//Methode pour sauver l'etat de la salle de theatre dans un nouveau fichier//ex8
	public void save() throws IOException {
		FileWriter fw = new FileWriter(filename != null ?
											filename+".bak"
											: "theater.bak");
		try {
			fw.write(seats.length + ";" + seats[0].length + ";;;\n");
			
			for (int i = 0; i < seats.length; i++) {
				for (int j = 0; j < seats[0].length; j++)
					fw.write(seats[i][j].isBooked() ?
								seats[i][j].getType().getSymbole().toUpperCase() + ";"
								: seats[i][j].getType().getSymbole() + ";");
				
				fw.write("\n");
			}
		}
		catch (IOException ex) {
			System.err.println(ex.getMessage());
			System.exit(-1);
		}
		fw.close();
	}
	
	@Override
	public int getWidth() { // Width of the component
		return 640/*((nbCol+1) * 40) -23 //pour avoir exatement la taille de la fenetre au dimension du theatre*/;
	}

	@Override
	public int getHeight() { // Height of the component
		return 480/*(nbRow+1) * 40 //pour avoir exatement la taille de la fenetre au dimension du theatre*/;
	}

	@Override
	public Dimension getPreferredSize() { // Preferred size of the component
		return new Dimension(getWidth(), getHeight());
	}

	@Override
	public void paint(Graphics g) { // How the component is drawn
		ImageIcon seatImg = new ImageIcon("img/free_seat.png");
		ImageIcon reservedImg = new ImageIcon("img/reserved_seat.png");
		super.paint(g); // Firsly, draw the super-class (the original JPanel)
		// Custom drawing by method calls to the Graphics instance (see java.awt.Graphics)
		g.setColor(new Color(221, 221, 221)); // Set the color to Black
		g.fillRect(0, 0, getWidth(), getHeight()); // Fill a rectangle where top-left corner is at (0,0) and of the same size than the component
		
		for (int i = 0; i < seats.length; i++) {//Double boucle pour creer une image du theatre
			for (int j = 0; j < seats[0].length; j++) {
				//En fonction du type de la place on met une couleur differente
				switch (seats[i][j].getType()) {
					case OBSTACLE:
						g.setColor(Color.GRAY);
					break;
					case SCENE:
						g.setColor(Color.YELLOW);
					break;
					default:
						//Vert si place libre rouge sinon
						if (seats[i][j].isBooked() == false) {
							g.setColor(Color.GREEN);
						}
						else {
							g.setColor(Color.RED);
						}
					break;
				}
				//On creer des carre pour representer les places
				g.fillRect(/*150 +*/ (j * rectSize), /*4 + */(i * rectSize), rectSize, rectSize);
				//On met un siege dans les places et pas dans obstacle ou secene
				if ((seats[i][j].getType() != SeatType.OBSTACLE) && (seats[i][j].getType() != SeatType.SCENE)) {
					if (seats[i][j].isBooked() == false) {
						seatImg.paintIcon(this, g, /*150 + */(j * rectSize),/* 4 + */(i * rectSize));
					}
					else if (seats[i][j].isBooked() == true) {
						reservedImg.paintIcon(this, g,/* 150 + */(j * rectSize), /*4 + */(i * rectSize));
					}
					g.setColor(Color.BLACK);//Couleur noir pour les lettres
					/* Loading a new font (14pt, bold) */
					g.setFont(new Font("default", Font.BOLD, 14));
					/* Getting the category String */
					String category = seats[i][j].getType().toString().toUpperCase();
					/* Calculating the String dimension in pixels */
					Rectangle2D stringDim = g.getFontMetrics().getStringBounds(category, g);
					/* Drawing the string centered within the rectangle and centered from the string itself */
					g.drawString(category, /*150 +*/ j * rectSize + rectSize / 2 - (int) stringDim.getWidth() / 2,/* 4 +*/ i * rectSize + rectSize / 2 + (int) stringDim.getHeight() / 2);
				}
					
				//Contour de toutes les places et obsatcles 
				g.setColor(Color.BLACK);
				g.drawRect(/*150 + */(j * rectSize),/* 4 +*/ (i * rectSize), rectSize, rectSize);
				
			}
		}
		/*ImageIcon img = new ImageIcon("emosquare.png");
		img.paintIcon(this, g,400, 200);*/
		if((currentCol < seats.length) && (currentRow >= 0 )){
			if((currentCol < seats[0].length) && (currentRow >= 0)){
				g.setColor(Color.RED);
				g.drawRect(currentCol*rectSize, currentRow*rectSize, rectSize, rectSize);
			}
		}
	}
	
	public int getRectSize() {
		return rectSize;
	}
	
	public void setSelectedRow(int row){
		currentRow = row;
	}
	
	public void setSelectedCol(int col){
		currentCol = col;
	}
	
	public int getSelectedRow(){
		return currentRow;
	}
	
	public int getSelectedCol(){
		return currentCol;
	}
}
