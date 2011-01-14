package core;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
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
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;






public class Client extends JFrame implements Runnable {

	protected JFrame jFrame = null; 
	protected JPanel jContentPane = null;
	protected JTextField saisie = null;
	protected JButton BoutonEnvoyer = null;
	protected JList listeClients = null;
	protected Socket priseClient;  //  @jve:decl-index=0:
	protected PrintWriter versServeur;  
	protected BufferedReader depuisServeur;  //  @jve:decl-index=0:
	protected BufferedReader clavier;
	protected String texteEnvoi=null; 
	protected String texteConnexion=null;  //  @jve:decl-index=0:
	protected String texteDeconnexion=null;  
	protected String texteRecu=null;  //  @jve:decl-index=0:
	protected JScrollPane jScrollPane = null;
	protected JTextPane jTextPane1 = null;
	
	
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
	
	
	/* Constructeurs */
	public Client(String serveur, int port , String pseudo) {	
		
		JFrame fenetre = new JFrame();								// Nouvelle fenêtre déclarée.
		this.setTitle("ClientChatV1.0");							// Définition du titre de la fenêtre
		this.setSize(new Dimension(600, 400));						// Définit une taille de la fenêtre. (600 par 400).
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

		this.pseudo=pseudo;
		
		if (this.connexion(serveurIP, port)){
			clavier = new BufferedReader(new InputStreamReader(System.in));
		
			new Thread(this).start();
		
			this.connexion();
			connexionOK();
		}
		else{
			erreurCo=true;
			System.err.println("Connexion impossible au serveur: vérifiez les paramètres de connexion!");
			erreur();
			
		}

		
	}	
	
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

	public void envoieMessage(String message) {
		this.versServeur.println(pseudo+": "+message);
		this.versServeur.flush();
	}
	
	public void envoiEvennement(String message){
		this.versServeur.println(message);
		this.versServeur.flush();
	}

	public String receptionMessage() {

		try {
			texteRecu = this.depuisServeur.readLine();
			jTextPane1.setText(jTextPane1.getText()+"\n"+texteRecu);
		} catch (IOException e) {
			System.out.println("Erreur de lecture");
		}
		return texteRecu;
	}

	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	protected JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setSize(new Dimension(600, 400));
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
			jContentPane.add(getJTextPane1(), null);
			jContentPane.add(getSaisie(), null);
			jContentPane.add(getBoutonEnvoyer(), null);
			jContentPane.add(getListeClients(), null);
			jContentPane.add(getJScrollPane(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes saisie	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getSaisie() {
		if (saisie == null) {
			saisie = new JTextField();
			saisie.setBounds(new Rectangle(30, 300, 350, 35));
			saisie.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == KeyEvent.VK_ENTER){
						texteEnvoi= saisie.getText();
						saisie.setText("");		// On vide le champ de texte.
						//echo();
						envoieMessage(texteEnvoi);
					}
				}
			});
		}
		return saisie;
	}

	/**
	 * This method initializes BoutonEnvoyer	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getBoutonEnvoyer() {
		if (BoutonEnvoyer == null) {
			BoutonEnvoyer = new JButton();
			BoutonEnvoyer.setBounds(new Rectangle(420, 300, 110, 35));
			BoutonEnvoyer.setText("Envoyer");
			BoutonEnvoyer.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					//System.out.println(texteEnvoi);
					texteEnvoi= saisie.getText();
					saisie.setText("");		// On vide le champ de texte.
					//echo();
					envoieMessage(texteEnvoi);
				}
			});
		}
		return BoutonEnvoyer;
	}

	/**
	 * This method initializes listeClients	
	 * 	
	 * @return javax.swing.JList	
	 */
	protected JList getListeClients() {
		if (listeClients == null) {
			listeClients = new JList();
			listeClients.setBounds(new Rectangle(400, 20, 150, 250));
		}
		return listeClients;
	}
	
	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	protected JScrollPane getJScrollPane() {
		if (jScrollPane == null) {
			jScrollPane = new JScrollPane();
			jScrollPane.setBounds(new Rectangle(30, 20, 350, 250));
			jScrollPane.setViewportView(getJTextPane1());
		}
		return jScrollPane;
	}

	/**
	 * This method initializes jTextPane1	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	protected JTextPane getJTextPane1() {
		if (jTextPane1 == null) {
			jTextPane1 = new JTextPane();
			jTextPane1.setBounds(new Rectangle(30, 20, 350, 250));
		}
		return jTextPane1;
	}
	
	public void connexion(){
		
			texteConnexion="Le client "+pseudo+" vient de se connecter.";
			envoiEvennement(texteConnexion);
	}
	
	public void deconnexion(){
		
		texteDeconnexion="Le client "+pseudo+" vient de se déconnecter.";
		envoiEvennement(texteDeconnexion);
	}
	public void erreur(){
		jTextPane1.setText(jTextPane1.getText()+"\n"+"Connexion impossible au serveur: vérifiez les paramètres de connexion!");
	}
	public void connexionOK(){
		jTextPane1.setText(jTextPane1.getText()+"\n"+"Vous êtes connecté au serveur!");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			receptionMessage();
			depuisServeur=null;
			try {
				depuisServeur = new BufferedReader(new InputStreamReader(priseClient.getInputStream()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {

		new DonneesConnexion();
	}
}
