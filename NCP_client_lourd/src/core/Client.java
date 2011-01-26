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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.JTabbedPane;


public class Client extends JFrame implements Runnable {

	protected JFrame jFrame = null; 
	protected JPanel jContentPane = null;
	protected JTextField saisieGeneral = null;
	protected JButton boutonEnvoyerGeneral = null;
	protected Socket priseClient;  
	protected PrintWriter versServeur;  
	protected BufferedReader depuisServeur;
	protected BufferedReader clavier;
	protected String texteEnvoiGeneral=null; 
	protected String texteConnexion=null;
	protected String texteDeconnexion=null;  
	protected String texteRecu=null; 
	protected JTextPane jTextPaneMP = null;  
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenu menu1 = new JMenu("Fichier");
	protected JMenu menu2 = new JMenu("Aide");
	protected JMenuItem item1 = new JMenuItem("Connexion");
	protected JMenuItem item2 = new JMenuItem("Quitter");
	protected JMenuItem item3 = new JMenuItem("A propos...");
	protected String serveurIP;
	protected int port;
	protected String pseudo=null;
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
	protected JTextPane listeClient = null;
	protected Thread ecoute;
	
	
	/* Constructeur */
	public Client(String serveur, int port , String pseudo) {	
		
		JFrame fenetre = new JFrame();								// Nouvelle fenêtre déclarée.
		this.setTitle("ClientChatV1.0");							// Définition du titre de la fenêtre
		this.setSize(new Dimension(700, 500));						// Définit une taille de la fenêtre. (700 par 500).
		this.setContentPane(getJContentPane());						// Panel de la fenêtre.
		this.setLocationRelativeTo(null);							// On positionne la fenètre au centre de l'écran					
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// Le processus se termine correctement lorsque l'on ferme la fenêtre
		this.setResizable(false);									// On rend la fenêtre non redimentionnable
		
		this.serveurIP=serveurIP;
		this.port=port;
		this.pseudo=pseudo;
		
		/* Création du menu */
		
		this.menuBar.add(menu1);
		this.menu1.add(item1);
		item1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,KeyEvent.CTRL_DOWN_MASK));
		item1.addActionListener (new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				new DonneesConnexion();
				dispose();
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
		
		this.setJMenuBar(menuBar);
		this.setVisible(true);
		

		if (this.connexion(serveurIP, port)){
			clavier = new BufferedReader(new InputStreamReader(System.in));
			//new Thread(this).start();
			Thread ecoute = new Thread(this);
			ecoute.start();
			this.connexion();
			connexionOK();
		}
		else{
			erreurCo=true;
			System.err.println("Connexion impossible au serveur: vérifiez les paramètres de connexion!");
			erreur();
		}
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
			jFrame.setSize(new Dimension(700, 500));
			jFrame.setTitle("ClientChatV1.0");
			jFrame.setContentPane(getJContentPane());
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
						envoieMessageGeneral(texteEnvoiGeneral);
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
					//System.out.println(texteEnvoi);
					texteEnvoiGeneral= saisieGeneral.getText();
					saisieGeneral.setText("");		// On vide le champ de texte.
					//echo();
					envoieMessageGeneral(texteEnvoiGeneral);
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
	
	/* Définitions des méthodes du client */
	
	public boolean connexion(String serveur, int port) {
		System.out.println("Connexion au serveur");
		try {
			this.priseClient = new Socket(serveur, port);
			this.versServeur = new PrintWriter(priseClient.getOutputStream());
			this.depuisServeur = new BufferedReader(new InputStreamReader(priseClient.getInputStream()));
			return true;
		} catch (UnknownHostException e) {
			System.out.println("Serveur " + serveur + ":" + port + " inconnu");
		} catch (IOException e) {
			System.out.println("Erreur d'I/O");
			e.printStackTrace();
		}
		return false;
	}

	public void envoieMessageGeneral(String message) {
		this.versServeur.println(pseudo+": "+message);
		this.versServeur.flush();
	}
	
	public void envoiEvennement(String message){
		this.versServeur.println(message);
		this.versServeur.flush();
	}

	public String receptionMessageGeneral() {
		try {
			texteRecu = this.depuisServeur.readLine();
			jTextPane.setText(jTextPane.getText()+"\n"+texteRecu);
		} catch (IOException e) {
			System.err.println("Erreur de lecture");
		}
		return texteRecu;
	}
	
	public void connexion(){
		
			texteConnexion="Le client "+pseudo+" vient de se connecter.";
			envoiEvennement(texteConnexion);
	}
	
	public void deconnexion(){
		
		if(ecoute != null){
		texteDeconnexion="Le client "+pseudo+" vient de se déconnecter.";
		envoiEvennement(texteDeconnexion);
		ecoute.stop();
		}
	}
	
	public void erreur(){
		String messageErreur=jTextPane.getText()+"\n"+"Connexion impossible au serveur: vérifiez les paramètres de connexion!";
		
		jTextPane.setText(messageErreur);
		jTextPane.setBackground(new Color(253, 241, 230));
	}
	
	public void connexionOK(){
		jTextPane.setText(jTextPane.getText()+"\n"+"Vous êtes connecté au serveur!");
	}
	
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			receptionMessageGeneral();
			depuisServeur=null;
			try {
				depuisServeur = new BufferedReader(new InputStreamReader(priseClient.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
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
			jScrollPane.setViewportView(getListeClient());	
		}
		return jScrollPane;
	}



	/**
	 * This method initializes listeClient	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	protected JTextPane getListeClient() {
		if (listeClient == null) {
			listeClient = new JTextPane();
			listeClient.setBackground(new Color(253, 241, 230));
		}
		return listeClient;
	}



	public static void main(String[] args) {

		new DonneesConnexion(); // Lancement de la fenêtre de connexion.
	}
}
