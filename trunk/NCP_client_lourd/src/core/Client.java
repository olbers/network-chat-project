package core;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextPane;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.JTabbedPane;
import javax.swing.JList;


public class Client extends JFrame implements Runnable {

	protected JFrame jFrame = null; 
	protected JPanel jContentPane = null;
	protected JTextField saisieGeneral = null;
	protected JButton boutonEnvoyerGeneral = null;
	protected Socket socketClient;  //  @jve:decl-index=0:
	protected PrintWriter versServeur;  
	protected BufferedReader depuisServeur;  //  @jve:decl-index=0:
	protected BufferedReader clavier;
	protected String texteEnvoiGeneral=null; 
	protected String texteConnexion=null;
	protected String commandeDeconnexion=null;  //  @jve:decl-index=0:
	protected String texteRecu=null;  //  @jve:decl-index=0:
	protected String texteMP=null;
	protected JTextPane jTextPaneMP = null;  
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenu menu1 = new JMenu("Fichier");
	protected JMenu menu2 = new JMenu("Aide");
	protected JMenuItem item1 = new JMenuItem("Connexion");
	protected JMenuItem item4 = new JMenuItem("Enregistrement");
	protected JMenuItem item2 = new JMenuItem("Quitter");
	protected JMenuItem item3 = new JMenuItem("A propos...");
	protected String adresseIP;  //  @jve:decl-index=0:
	protected int port;
	protected String pseudo=null;  //  @jve:decl-index=0:
	protected boolean erreurCo=false;
	protected JTabbedPane jTabbedPane = null;
	protected JPanel jPanelGeneral = null;
	protected JPanel jPanelMP = null;
	protected JScrollPane jScrollPaneMP = null;
	protected JTextField saisieMP = null;
	protected JButton boutonEnvoyerMP = null;
	protected JScrollPane jScrollPane2 = null;
	protected JTextPane jTextPane = null;
	protected JScrollPane jScrollPane = null;
	protected Thread ecoute;
	protected ArrayList messagesGeneral;
	protected ArrayList messagesPrives;
	protected ArrayList clientsConnectes;
	private JList jListClients = null;




	/* Constructeur */
	public Client() {	
		super();
		getJFrame();

		if (this.connexion(adresseIP, port)){
			clavier = new BufferedReader(new InputStreamReader(System.in));
			Thread ecoute = new Thread(this);
			ecoute.start();
			this.connexion(adresseIP, port);
		}
		else{
			erreurCo=true;
			System.err.println("Connexion impossible au serveur: vérifiez les paramètres de connexion!");
			erreurConnexion();
		}
	}


	public void menu(){
		this.menuBar.add(menu1);
		this.menu1.add(item1);
		item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_DOWN_MASK));
		item1.addActionListener (new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				CreateConnexion();
			}		
		});

		this.menu1.add(item4);
		item4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E,KeyEvent.CTRL_DOWN_MASK));
		item4.addActionListener (new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new Register();
			}		
		});

		this.menu1.add(item2);
		item2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q,KeyEvent.CTRL_DOWN_MASK));
		item2.addActionListener (new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				deconnexion();
				System.exit(0);
			}		
		});

		this.menuBar.add(menu2);
		this.menu2.add(item3);
		item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_DOWN_MASK));
		item3.addActionListener (new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String textAbout="Nom: ClientChat\n" +
				"Auteur: HUCHEDÉ Cédric\n" +
				"Version: 1.0\n" +
				"                                                     ------------------------------------------------------------- \n\n"+
				"Description:\n" +
				"\n" +
				"Ce programme est un client permettant de se connecter à un serveur.\nIl permet de communiquer par messagerie instantanée à d'autres clients connectés au même serveur.\n";
				JOptionPane.showMessageDialog(null,
						textAbout,
						"A Propos...",
						JOptionPane.INFORMATION_MESSAGE);				
			}		
		});

		menu1.setMnemonic('F');
		menu2.setMnemonic('A');

		jFrame.setJMenuBar(menuBar);
	}

	/* Définition des composantes graphiques */

	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	protected JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setTitle("ClientChatV1.0");								// Définition du titre de la fenêtre
			jFrame.setSize(new Dimension(700, 500));						// Définit une taille de la fenêtre. (700 par 500).
			jFrame.setContentPane(getJContentPane());						// Panel de la fenêtre.
			jFrame.setLocationRelativeTo(null);								// On positionne la fenètre au centre de l'écran					
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);			// Le processus se termine correctement lorsque l'on ferme la fenêtre
			jFrame.setResizable(false);										// On rend la fenêtre non redimentionnable
			menu();
			jFrame.setVisible(true);
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTabbedPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	protected JTabbedPane getJTabbedPane() {
		if (jTabbedPane == null) {
			jTabbedPane = new JTabbedPane();
			jTabbedPane.setBounds(new Rectangle(0, 0, 695, 462));
			jTabbedPane.setBackground(Color.getHSBColor(58,17, 95));		// Coloration des onglets
			jTabbedPane.addTab("Général",null, getJPanelGeneral(), null);
			jTabbedPane.addTab("Messages personnels", null, getJPanelMP(), null);
		}
		return jTabbedPane;
	}

	/* Onglet général */

	/**
	 * This method initializes jPanel	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getJPanelGeneral() {
		if (jPanelGeneral == null) {
			jPanelGeneral = new JPanel();
			jPanelGeneral.setLayout(null);
			jPanelGeneral.setBackground(new Color(205, 219, 242));		// Coloration du contenu des onglets
			jPanelGeneral.add(getBoutonEnvoyerGeneral(), null);
			jPanelGeneral.add(getSaisieGeneral(), null);
			jPanelGeneral.add(getJScrollPane2(), null);
			jPanelGeneral.add(getJScrollPane(), null);

		}
		return jPanelGeneral;
	}

	/* Onglet Messages personnels */

	/**
	 * This method initializes jPanel1	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getJPanelMP() {
		if (jPanelMP == null) {
			jPanelMP = new JPanel();
			jPanelMP.setLayout(null);
			jPanelMP.setBackground(new Color(205, 219, 242));		// Coloration du contenu des onglets
			jPanelMP.add(getJScrollPaneMP(), null);
			jPanelMP.add(getSaisieMP(), null);
			jPanelMP.add(getBoutonEnvoyerMP(), null);

		}
		return jPanelMP;
	}

	/**
	 * This method initializes jScrollPane1	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	protected JScrollPane getJScrollPaneMP() {
		if (jScrollPaneMP == null) {
			jScrollPaneMP = new JScrollPane();
			jScrollPaneMP.setBounds(new Rectangle(20, 20, 640, 308));
			jScrollPaneMP.setViewportView(getJTextPaneMP());
		}
		return jScrollPaneMP;
	}

	/**
	 * This method initializes saisie1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getSaisieMP() {
		if (saisieMP == null) {
			saisieMP = new JTextField();
			saisieMP.setBounds(new Rectangle(20, 370, 482, 35));
			saisieMP.setBackground(new Color(253, 241, 230));
		}
		return saisieMP;
	}

	/**
	 * This method initializes BoutonEnvoyer1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getBoutonEnvoyerMP() {
		if (boutonEnvoyerMP == null) {
			boutonEnvoyerMP = new JButton();
			boutonEnvoyerMP.setBounds(new Rectangle(545, 370, 95, 35));
			boutonEnvoyerMP.setText("Envoyer");
		}
		return boutonEnvoyerMP;
	}

	/**
	 * This method initializes saisie	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getSaisieGeneral() {
		if (saisieGeneral == null) {
			saisieGeneral = new JTextField();
			saisieGeneral.setBounds(new Rectangle(20, 370, 482, 35));
			saisieGeneral.setBackground(new Color(253, 241, 230));
			saisieGeneral.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == KeyEvent.VK_ENTER){
						texteEnvoiGeneral= saisieGeneral.getText();
						saisieGeneral.setText("");		// On vide le champ de texte.
						envoiMessageGeneral(texteEnvoiGeneral);
					}
				}
			});
		}
		return saisieGeneral;
	}

	/**
	 * This method initializes BoutonEnvoyer	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getBoutonEnvoyerGeneral() {
		if (boutonEnvoyerGeneral == null) {
			boutonEnvoyerGeneral = new JButton();
			boutonEnvoyerGeneral.setText("Envoyer");
			boutonEnvoyerGeneral.setBounds(new Rectangle(545, 370, 95, 35));
			boutonEnvoyerGeneral.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					texteEnvoiGeneral= saisieGeneral.getText();
					saisieGeneral.setText("");		// On vide le champ de texte.
					envoiMessageGeneral(texteEnvoiGeneral);
				}
			});
		}
		return boutonEnvoyerGeneral;
	}

	/**
	 * This method initializes jTextPane1	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	protected JTextPane getJTextPaneMP() {
		if (jTextPaneMP == null) {
			jTextPaneMP = new JTextPane();
			jTextPaneMP.setBackground(new Color(253, 241, 230));
		}
		return jTextPaneMP;
	}

	/**
	 * This method initializes jScrollPane2	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	protected JScrollPane getJScrollPane2() {
		if (jScrollPane2 == null) {
			jScrollPane2 = new JScrollPane();
			jScrollPane2.setBounds(new Rectangle(20, 21, 480, 308)); //540, 20, 123, 308
			jScrollPane2.setViewportView(getJTextPane());
		}
		return jScrollPane2;
	}



	/**
	 * This method initializes jTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	protected JTextPane getJTextPane() {
		if (jTextPane == null) {
			jTextPane = new JTextPane();
			jTextPane.setBackground(new Color(253, 241, 230));
		}
		return jTextPane;
	}



	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	protected JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(530, 20, 130, 308));
			jScrollPane.setViewportView(getJListClients());
		}
		return jScrollPane;
	}


	public void CreateConnexion(){
		new Connexion(this);
	}

	public boolean connexion(String serveurIP, int port) {
		System.out.println("Connexion au serveur...");
		try {
			this.socketClient = new Socket(serveurIP, port);
			this.versServeur = new PrintWriter(socketClient.getOutputStream());
			this.depuisServeur = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			return true;
		} catch (UnknownHostException e) {
			System.out.println("Serveur " + serveurIP + ":" + port + " inconnu");
		} catch (IOException e) {
			System.out.println("Erreur Input/Output");
			e.printStackTrace();
		}
		return false;
	}

	public void envoiMessageGeneral(String message) {
		this.versServeur.println(message);
		this.versServeur.flush();
	}

	public void envoiEvennement(String message){
		this.versServeur.println(message);
		this.versServeur.flush();
	}

	public String typeMessage(String message){
		if(message.substring(0,1).equalsIgnoreCase("%")){
			message=supprChar(message);
			messagePrive(message);
		}
		else if(message.substring(0,1).equalsIgnoreCase("*")){
			message=supprChar(message);
			messageGeneral(message);
		}
		else if(message.substring(0,1).equalsIgnoreCase("$")){
			// LISTE CONNECTES
		}
		else if(message.substring(0,1).equalsIgnoreCase("&")){
			if(message.equalsIgnoreCase("&verif")){
				// Renvoyer HASH MD5 de l'application au serveur
			}
			/* Incorporer ici les nouvelles commandes */
			else{
				System.err.println("Commande non supportée");
			}

		}
		else if(message.substring(0,1).equalsIgnoreCase("#")){
			messageSystem(message);
		}
		else{
			System.err.println("SYNTAX MESSAGE ERROR: The message sent by server is not allowed by the protocol.");
		}
		return message;
	}

	public void messagePrive(String message) {		// Revoir selon ARRAYLIST
		texteMP=texteMP+message;
		jTextPaneMP.setText(texteMP+message);
	}
	public void messageSystem(String message){	// Print message system en rouge.

		jTextPane.setEnabled(false);
		jTextPane.setForeground(Color.RED);
		jTextPane.setText(jTextPane.getText()+"\n"+message);//jTextPane.getText()+"\n"+message
	}


	public String supprChar(String message){
		String chaineModif="";
		for(int i=0;i<message.length()-1;i++){
			chaineModif=chaineModif+message.charAt(i+1);
		}
		return chaineModif;
	}

	public void messageGeneral(String message) {
		jTextPane.setText(jTextPane.getText()+"\n"+message); // Modifier avec ARRAYLIST
	}

	public void deconnexion(){

		if(ecoute != null){
			commandeDeconnexion="&deconnexion";
			envoiEvennement(commandeDeconnexion);

			try {
				ecoute.stop();
				this.depuisServeur.close();
				this.versServeur.close();
				this.socketClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void erreurConnexion(){
		String messageErreur=jTextPane.getText()+"\n"+"Connexion impossible au serveur: vérifiez les paramètres de connexion!";
		jTextPane.setText(messageErreur);
	}

	public void parametresConnexion(String adresseIP,int port,String pseudo){
		this.adresseIP=adresseIP;
		this.port=port;
		this.pseudo=pseudo;
	}

	public void run() {
		// TODO Auto-generated method stub
		while(true){
			String message=null;
			depuisServeur=null;
			try {
				if(this.depuisServeur.ready()){
					message = this.depuisServeur.readLine();
					if(message != null){
						typeMessage(message);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * This method initializes jListClients	
	 * 	
	 * @return javax.swing.JList	
	 */
	private JList getJListClients() {
		if (jListClients == null) {
			jListClients = new JList();
			jListClients.setBackground(new Color(253, 241, 230));
			//jListClients.setSelectedIndex(-1);
		}
		return jListClients;
	}


	public static void main(String[] args) {

		new Client(); // Lancement de la fenêtre de connexion.
	}
}
