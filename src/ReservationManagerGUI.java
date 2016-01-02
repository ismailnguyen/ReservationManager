import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileNotFoundException;
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
	Scanner scan;
	Theater theater = new Theater(Const.THEATER_FILE);//Construit le theatre
	LinkedList<Client> clients = new LinkedList<Client>();//Liste client
	
	JFrame frame = new JFrame(Const.APPLICATION_NAME);// Creer une nouvelle fenetre appelee  Reservation Manager GUI
	JButton newClient = new JButton(Const.NEW_CLIENT);//Creation bouton nouveau client
	JButton delClient = new JButton(Const.REMOVE_CLIENT);//Ceation boutton 
	JButton showReservation = new JButton(Const.SHOW_RESERVATION);//Creation boutton 
	JButton makeReservation = new JButton(Const.MAKE_RESERVATION);//Creation boutton 
	JButton cancelReservation = new JButton(Const.CANCEL_RESERVATION);

	JComboBox<Client> comboClients = new JComboBox<Client>(); // Empty combobox

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ReservationManagerGUI() throws FileNotFoundException {
		//On charge la liste de clients
		try {
			clients = Serializer.<LinkedList> loadFromFile(Const.CLIENTS_FILE);
			for (Client c : clients) {
				c.setCurrentId(c.getId() + 1);
			}
		}
		catch (ClassNotFoundException e1) {
			JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
		}
		catch (IOException e1) {
			clients = new LinkedList<Client>();
		}

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
		newClient.setActionCommand("newC");
		newClient.addActionListener(this);

		delClient.setActionCommand("delC");
		delClient.addActionListener(this);

		showReservation.setActionCommand("showRes");
		showReservation.addActionListener(this);

		makeReservation.setActionCommand("makeRes");
		makeReservation.addActionListener(this);

		cancelReservation.setActionCommand("cancelRes");
		cancelReservation.addActionListener(this);

		frame.setLayout(new BorderLayout());
		frame.add(theater, BorderLayout.CENTER); // Add it to the frame
		refreshCombo();//On rafrait la liste de clients
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
				case "newC":
					addClient();
					break;
				
				//delete Client
				case "delC":
					deleteClient();
					break;
				
				//Show Reservation
				case "showRes":
					//Get selected place
					selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex());
					if (selectedClient != null)
						JOptionPane.showMessageDialog(null, selectedClient.getSeats(), "Seats", JOptionPane.INFORMATION_MESSAGE);
					else
						JOptionPane.showMessageDialog(null, "Invalid Client", "Error", JOptionPane.ERROR_MESSAGE);

					break;
				
				//make Reservation
				case "makeRes":
					makeReservation();
					break;
				
					//cancel Reservation
				case "cancelRes":
					cancelReservation();
					break;
			}
			
			theater.updateUI();
		}
		catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public void addClient() {
		/* DATA FIELD */
		Client c = null;
		JComboBox<String> clientTypeChoice = new JComboBox<>();
		clientTypeChoice.addItem("Client");
		clientTypeChoice.addItem("VIP");
		clientTypeChoice.addItem("Group");
		JTextField lastnameField = new JTextField(5);
		JTextField firstnameField = new JTextField(5);
		JTextField addressField = new JTextField(5);
		/* BUILDING OF THE FORM */
		JPanel myPanel = new JPanel(new GridLayout(4, 2));
		myPanel.add(new JLabel("Type:"));
		myPanel.add(clientTypeChoice);
		myPanel.add(new JLabel("Lastname:"));
		myPanel.add(lastnameField);
		myPanel.add(new JLabel("Firstname:"));
		myPanel.add(firstnameField);
		myPanel.add(new JLabel("Address:"));
		myPanel.add(addressField);
		/* CALLING THE DIALOG */
		JOptionPane.showConfirmDialog(null, myPanel, "Create new Client", JOptionPane.OK_CANCEL_OPTION);
		
		switch (clientTypeChoice.getItemAt(clientTypeChoice.getSelectedIndex())) {
			case "Client":
				c = new Client(lastnameField.getText(), firstnameField.getText(), addressField.getText());
			break;
			case "VIP":
				c = new ClientVIP(lastnameField.getText(), firstnameField.getText(), addressField.getText());
			break;
			case "Group":
				c = new ClientGroup(lastnameField.getText(), firstnameField.getText(), addressField.getText());
			break;
		}
		
		clients.add(c);
		theater.updateUI();
		refreshCombo();
		
		try {
			Serializer.saveToFile(Const.CLIENTS_FILE, clients);//On sauvegarde la liste de client
		}
		catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	public void deleteClient() throws IOException {
		//On prend le client selecionne dans la liste
		Client selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex()); // Get the car that is currently selected
		if (selectedClient != null) {
			// OK/CANCEL DIALOG
			int val = JOptionPane.showConfirmDialog(null, "Effacer ?", "Confirm", JOptionPane.OK_CANCEL_OPTION);
			switch (val) {
				case JOptionPane.OK_OPTION: /* Go ! */
					//On retire le client de la liste
					clients.remove(selectedClient);
					theater.updateUI(); // Refresh User Interface
					refreshCombo();
					//On retire les sieges que ce client avait
					for (Seat s : selectedClient.getSeats()) {
						try {
							theater.cancelReservation(s.getCol(), s.getRow());
						}
						catch (InvalidActionException e1) {
							JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
						}
					}

				break;
			}

			//On sauvegarde cette liste
			try {
				Serializer.saveToFile(Const.CLIENTS_FILE, clients);
			}
			catch (IOException ex) {
				JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		else {
			JOptionPane.showMessageDialog(null, "Invalid Client !", "Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	public void makeReservation() throws IOException {
		Client selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex()); // Get the car that is currently selected
		if (selectedClient != null) {
			try {
				theater.makeReservation(theater.getSelectedCol(), theater.getSelectedRow());
				if ((theater.getSelectedCol() < theater.getNbCol()) && (theater.getSelectedRow() < theater.getNbRow())) {
					Seat s = new Seat(theater.getSelectedRow(), theater.getSelectedCol(), theater.getType(theater.getSelectedRow(), theater.getSelectedCol()), theater.isBooked(theater.getSelectedRow(), theater.getSelectedCol()));//On recupere la place reservee par le client 
					selectedClient.addSeat(s);//On ajoute cette place a la liste de places reservee par le client
					try {
						Serializer.saveToFile(Const.CLIENTS_FILE, clients);//On sauvegarde la liste de client pour prendre en compte la nouvelle place prise
					}
					catch (IOException ex) {
						JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			catch (InvalidActionException e1) {
				JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
			}

			theater.updateUI(); // Refresh User Interface
		}
		else {
			JOptionPane.showMessageDialog(null, "Invalid Client !", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void cancelReservation() throws IOException {
		Client selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex()); // Get the car that is currently selecte
		if (selectedClient != null) {
			try {//On recupere les erreurs s'il y en a

				if ((theater.getSelectedCol() < theater.getNbCol()) && (theater.getSelectedRow() < theater.getNbRow())) {
					Seat s = new Seat(theater.getSelectedRow(), theater.getSelectedCol(), theater.getType(theater.getSelectedRow(), theater.getSelectedCol()), theater.isBooked(theater.getSelectedRow(), theater.getSelectedCol()));//On recupere la place reservee par le client 
					//On regarde si le client selectionner veut supprimer une place qui lui appartient
					if (selectedClient.getSeats().toString().contains(s.toString())) {
						//Si la place lui appartient -> OK
						theater.cancelReservation(theater.getSelectedCol(), theater.getSelectedRow());
						selectedClient.removeSeat(s);//On ajoute cette place a la liste de places reservee par le client
					}
					else {
						//Sinon -> Message erreur
						JOptionPane.showMessageDialog(null, "Invalid Client !", "Error", JOptionPane.ERROR_MESSAGE);
					}

					//On sauvegarde la liste de client pour prendre en compte la nouvelle place prise
					try {
						Serializer.saveToFile(Const.CLIENTS_FILE, clients);
					}
					catch (IOException ex) {
						JOptionPane.showMessageDialog(null, ex, "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			catch (NumberFormatException e1) {
				JOptionPane.showMessageDialog(null, e1, "Error", JOptionPane.ERROR_MESSAGE);
			}
			catch (InvalidActionException e2) {
				JOptionPane.showMessageDialog(null, e2, "Error", JOptionPane.ERROR_MESSAGE);
			}
			theater.updateUI(); // Refresh User Interface
		}
		else {//Si client selectionner = null
			JOptionPane.showMessageDialog(null, "No Client !", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void refreshCombo() {
		// REFRESH
		comboClients.removeAllItems(); // Remove previous items
		for (Client c : clients) {
			comboClients.addItem(c); // Add each car to the combobox
		}
		comboClients.setActionCommand("combo");
		comboClients.addActionListener(this); // Notify when a selection has been made
		comboClients.updateUI(); // Refresh the combonent graphics
		// GET SELECTED ITEM
		//Client selectedClient = comboClients.getItemAt(comboClients.getSelectedIndex()); // Get the car that is currently selected
	}
}
