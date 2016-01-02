import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class ReservationManagerGUI extends WindowAdapter implements MouseListener, ActionListener {
	
	private Scanner scan = new Scanner(System.in);
	private Theater theater;
	private LinkedList<Client> clients;
	
	JFrame frame = new JFrame(Const.APPLICATION_NAME);// Creer une nouvelle fenetre appelee  Reservation Manager GUI
	JButton newClient = new JButton(Const.NEW_CLIENT);//Creation bouton nouveau client
	JButton delClient = new JButton(Const.REMOVE_CLIENT);//Ceation boutton 
	JButton showReservation = new JButton(Const.SHOW_RESERVATION);//Creation boutton 
	JButton makeReservation = new JButton(Const.MAKE_RESERVATION);//Creation boutton 
	JButton cancelReservation = new JButton(Const.CANCEL_RESERVATION);
	JComboBox<Client> comboClients = new JComboBox<Client>(); // Empty combobox

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ReservationManagerGUI() throws NumberFormatException, InvalidActionException, IOException, ClassNotFoundException {
		File dir = new File("./");
		File[] theaterFile = dir.listFiles(new FilenameFilter() {
			public boolean accept (File dir, String filename) {
				return filename.endsWith(".bak");
			}
		});
		
		theater = new Theater(theaterFile.length > 0 ? 
				theaterFile[0].getAbsolutePath()
				: Const.THEATER_FILE);
		
		File clientFile = new File(Const.CLIENTS_FILE);
		clients = clientFile.exists() ?
						Serializer.<LinkedList<Client>>loadFromFile(Const.CLIENTS_FILE)
						: new LinkedList<Client>();

		frame.setMinimumSize(/*new Dimension(640, 480)*/theater.getPreferredSize());// Dimension minimum de la fenetre : 640 pixels sur 480 pixels
		
		//Affiche une boite fde dialogue lorsqu'on appui sur la croix
		frame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowEvent) {
				int val = JOptionPane.showConfirmDialog(null, "Do you want to quit ?", "Confirm", JOptionPane.YES_NO_OPTION);
				switch (val) {
					case JOptionPane.YES_OPTION:
						try {
							theater.save();
						}
						catch (IOException e) {
							e.printStackTrace();
						}
						frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					break;
					default:
						frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

					break;
				}
			}
		});

		frame.pack(); // on demande d'attribuer une taille minimale a la fenetre (juste assez pour voir tous les composants)
		GridLayout buttonLayout = new GridLayout(2, 3);//Layout pour disposer les bouton
		JPanel buttonPanel = new JPanel();//Panneaux pour mettre les bouton a part

		//On attribu une action a chaque boutton
		newClient.setActionCommand("AC");
		newClient.addActionListener(this);

		delClient.setActionCommand("RC");
		delClient.addActionListener(this);

		showReservation.setActionCommand("SR");
		showReservation.addActionListener(this);

		makeReservation.setActionCommand("MR");
		makeReservation.addActionListener(this);

		cancelReservation.setActionCommand("CR");
		cancelReservation.addActionListener(this);

		frame.setLayout(new BorderLayout());
		frame.add(theater, BorderLayout.CENTER); // Add it to the frame
		refreshCombo();//On rafraichit la liste de clients
		
		//On ajoute tout les bouton et la liste au panneau buttonPanel
		buttonPanel.add(comboClients);
		buttonPanel.add(newClient);
		buttonPanel.add(delClient);
		buttonPanel.add(showReservation);
		buttonPanel.add(makeReservation);
		buttonPanel.add(cancelReservation);
		//On dispose les boutons  selon buttonLayout
		buttonPanel.setLayout(buttonLayout);
		//On met les bouton en haut
		frame.add(buttonPanel, BorderLayout.NORTH);
		
		//On ecoute les actions lies a la souris
		theater.addMouseListener(this);
		frame.setLocationRelativeTo(null);//Affiche la fentre au centre de l'ecran
		frame.setVisible(true);// On affiche le contenu de la fenetre
	}

	public void mouseClicked(MouseEvent ev) {
		int col = 0;
		int row = 0;
		
		Point p = ev.getPoint();//Pour recuperer l'endroit ou on a clique sur la fenetre
		col = p.x / theater.getRectSize();//On convertit l'endroit ou on a cliquer en colonne 
		row = p.y / theater.getRectSize();//On fait la meme mais en ligne
		
		//On met informe la classe theater des lignes et colonnes selectionnee
		theater.setSelectedCol(col);
		theater.setSelectedRow(row);
		
		//On rafraichit le theatre
		theater.updateUI();
	}
	
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Client selectedClient = null;

		try {
			//si le parametre est :
			switch (e.getActionCommand()) {
				//new Client	
				case "AC":
					addClient();
					break;
				
				//delete Client
				case "RC":
					removeClient();
					break;
				
				//Show Reservation
				case "SR":
					//Get selected place
					selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex());
					if (selectedClient != null)
						JOptionPane.showMessageDialog(null, selectedClient.getSeats(), "Seats", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(null, "Invalid Client", "Error", JOptionPane.ERROR_MESSAGE);

					break;
				
				//make Reservation
				case "MR":
					makeReservation();
					break;
				
				//cancel Reservation
				case "CR":
					cancelReservation();
					break;
			}
			
			theater.updateUI();
		}
		catch(IOException 
				| InvalidActionException ex) {
			JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void addClient() throws InvalidActionException, IOException {
		/* DATA FIELD */
		JComboBox<String> clientTypeChoice = new JComboBox<>();
		clientTypeChoice.addItem("Client");
		clientTypeChoice.addItem("VIP");
		clientTypeChoice.addItem("Group");
		JTextField lastnameField = new JTextField(5);
		JTextField firstnameField = new JTextField(5);
		JTextField addressField = new JTextField(5);
		
		/* BUILDING OF THE FORM */
		JPanel myPanel = new JPanel(new GridLayout(4, 2));
		myPanel.add(new JLabel("Type : "));
		myPanel.add(clientTypeChoice);
		myPanel.add(new JLabel("Lastname : "));
		myPanel.add(lastnameField);
		myPanel.add(new JLabel("Firstname : "));
		myPanel.add(firstnameField);
		myPanel.add(new JLabel("Address : "));
		myPanel.add(addressField);
		
		/* CALLING THE DIALOG */
		JOptionPane.showConfirmDialog(null, myPanel, "Create new Client", JOptionPane.OK_CANCEL_OPTION);
		
		int nextId = clients.size() > 0 ?
				clients.getLast().getId() + 1
				: 0;
		
		switch(clientTypeChoice.getItemAt(clientTypeChoice.getSelectedIndex()))
		{
			case "Client" :
				clients.add(new Client(lastnameField.getText(),
										firstnameField.getText(),
										addressField.getText()));
				break;
			case "VIP" :
				clients.add(new ClientVIP(lastnameField.getText(),
											firstnameField.getText(),
											addressField.getText()));
				break;
			case "Group" :
				clients.add(new ClientGroup(lastnameField.getText(),
												firstnameField.getText(),
												addressField.getText()));
				break;
		}
		
		clients.getLast().setCurrentId(nextId);

		theater.updateUI();
		refreshCombo();
		
		Serializer.saveToFile(Const.CLIENTS_FILE, clients);
	}

	public void removeClient() throws InvalidActionException, IOException {
		//On prend le client selecionne dans la liste
		Client selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex()); // Get the car that is currently selected
		if (selectedClient != null) {
			// OK/CANCEL DIALOG
			int val = JOptionPane.showConfirmDialog(null, "Effacer ?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
			switch (val) {
				case JOptionPane.OK_OPTION: /* Go ! */
					//On retire le client de la liste
					clients.remove((Client) selectedClient);
					theater.updateUI(); // Refresh User Interface
					refreshCombo();
					
					//On retire les sieges que ce client avait
					for (Seat s : selectedClient.getSeats()) {
						theater.cancelReservation(s.getCol(), s.getRow());
					}

				break;
			}

			Serializer.saveToFile(Const.CLIENTS_FILE, clients);
		}
		else
			JOptionPane.showMessageDialog(null, "Invalid Client !", "Error", JOptionPane.ERROR_MESSAGE);
	}

	//Methode pour faire une reservation
	private void makeReservation() throws NumberFormatException, InvalidActionException, IOException {
		if(theater.getNbFreeSeats() > 0)
			updateReservation(true);
		else
			JOptionPane.showMessageDialog(null, "House full !", "Error", JOptionPane.ERROR_MESSAGE);
	}

	//Methode pour annuler une reservation
	private void cancelReservation() throws NumberFormatException, InvalidActionException, IOException {
		if(theater.getNbBookedSeats() > 0)
			updateReservation(false);
		else
			JOptionPane.showMessageDialog(null, "No reservation have been made yet.", "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	private void updateReservation(boolean isBooking) throws NumberFormatException, InvalidActionException, IOException {
		Client selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex()); // Get the car that is currently selecte
		if (selectedClient != null) {
			if ((theater.getSelectedCol() < theater.getNbCol())
					&& (theater.getSelectedRow() < theater.getNbRow())) {		
				if(isBooking)
				{
					//On reserve la place en convertissant la chaine en nombre et en la soustrayant pour qu'elle corresponde a nos attentes
					if(theater.makeReservation(theater.getSelectedCol(), theater.getSelectedRow()))
						//Si la résérvation dans la salle est faite, on ajoute la place à la liste des résérvations du client
						selectedClient.addSeat(theater.getSeat(theater.getSelectedCol(), theater.getSelectedRow()));
				}
				else
				{
					//Si le client a déja reservé cette place, on lui enlève
					if(selectedClient.removeSeat(theater.getSeat(theater.getSelectedCol(), theater.getSelectedRow())))
						//On annule la place en convertissant la chaine en nombre et en la soustrayant pour qu'elle corresponde a nos attentes
						theater.cancelReservation(theater.getSelectedCol(), theater.getSelectedRow());
					else
						JOptionPane.showMessageDialog(null, "This seat is not yours !", "Error", JOptionPane.ERROR_MESSAGE);
				}

				Serializer.saveToFile(Const.CLIENTS_FILE, clients);
			}
			
			theater.updateUI();
		}
		else
			JOptionPane.showMessageDialog(null, "Invalid Client !", "Error", JOptionPane.ERROR_MESSAGE);
	}

	public void refreshCombo() {
		// REFRESH
		comboClients.removeAllItems(); // Remove previous items
		
		for (Client c : clients)
			comboClients.addItem(c); // Add each car to the combobox
			
		comboClients.setActionCommand("combo");
		comboClients.addActionListener(this); // Notify when a selection has been made
		comboClients.updateUI(); // Refresh the combonent graphics
		// GET SELECTED ITEM
		//Client selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex()); // Get the car that is currently selected
	}
}
