package ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;


import core.Client;
import javax.swing.JLabel;



public class Fenetre extends JFrame {

	public JFrame jFrame = null; 
	protected JPanel jContentPane = null;
	protected JTextField saisieGeneral = null;
	protected JButton boutonEnvoyerGeneral = null;
	protected JTextPane jTextPaneMP = null;  
	protected JMenuBar menuBar = new JMenuBar();
	protected JMenu menu1 = new JMenu("Fichier");
	protected JMenu menu2 = new JMenu("Aide");
	protected JMenuItem item1 = new JMenuItem("Connexion");
	protected JMenuItem item4 = new JMenuItem("Enregistrement");
	protected JMenuItem item2 = new JMenuItem("Quitter");
	protected JMenuItem item3 = new JMenuItem("A propos...");
	protected JTabbedPane jTabbedPane = null;
	protected JPanel jPanelGeneral = null;
	protected JPanel jPanelMP = null;
	protected JScrollPane jScrollPaneMP = null;
	protected JTextField saisieMP = null;
	protected JButton boutonEnvoyerMP = null;
	protected JScrollPane jScrollPane2 = null;
	protected JScrollPane jScrollPane = null;
	protected JList jListClients = null;
	protected Client client;
	protected JTextPane jTextPaneGeneral = null;
	public JLabel jLabelTotal = null;
	protected String icone="image/logoSimple.png";

	
	/* Constructeur */
	public Fenetre() {	
		super();
		this.client = new Client(this);
		getJFrame();
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
				client.deconnexion();
				System.exit(0);
			}		
		});

		this.menuBar.add(menu2);
		this.menu2.add(item3);
		item3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,KeyEvent.CTRL_DOWN_MASK));
		item3.addActionListener (new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				String textAbout="Nom: NCP_Client\n" +
				"Auteurs: HUCHEDÉ Cédric et POIRIER Kévin\n" +
				"Nous contacter: cedric@huchede.com \n" +
				"Version: 1.0\n" +
				"                                                     ------------------------------------------------------------- \n\n"+
				"Description:\n" +
				"\n" +
				"Ce programme est un client de messagerie instantanée permettant de dialoguer via un serveur web.\n" +
				"Le serveur par défaut est le suivant: IP: 109.238.0.52 PORT: 1999.\n\n";
				JOptionPane.showMessageDialog(null,
						textAbout,
						"A Propos...",
						JOptionPane.INFORMATION_MESSAGE);				
			}		
		});

		menu1.setMnemonic('F');
		menu2.setMnemonic('A');

		jFrame.setJMenuBar(menuBar);
		menuBar.setBackground(new Color(232,241,242));
		item1.setBackground(new Color(197,218,219));
		item2.setBackground(new Color(197,218,219));
		item3.setBackground(new Color(197,218,219));
		item4.setBackground(new Color(197,218,219));
	}

	/* Définition des composantes graphiques */

	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	public JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setTitle("NCP Client v1.0");								// Définition du titre de la fenêtre
			jFrame.setSize(new Dimension(700, 500));						// Définit une taille de la fenêtre. (700 par 500).
			jFrame.setContentPane(getJContentPane());						// Panel de la fenêtre.
			jFrame.setLocationRelativeTo(null);								// On positionne la fenètre au centre de l'écran					
			//jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// Le processus se termine correctement lorsque l'on ferme la fenêtre
			jFrame.addWindowListener(new MyWindowListener(this.client));
			jFrame.setResizable(false);										// On rend la fenêtre non redimentionnable
			
			//Image icone = Toolkit.getDefaultToolkit().getImage("./src/ui/logoSimple.png");
			//jFrame.setIconImage(icone);
			jFrame.setIconImage(new ImageIcon(this.getClass().getResource(icone)).getImage());
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
			jContentPane.setBackground(new Color(232,241,242));
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
			jLabelTotal = new JLabel();
			jLabelTotal.setBounds(new Rectangle(565, 332, 94, 19));
			jPanelGeneral = new JPanel();
			jPanelGeneral.setLayout(null);
			jPanelGeneral.setBackground(new Color(205, 219, 242));		// Coloration du contenu des onglets
			jPanelGeneral.add(getBoutonEnvoyerGeneral(), null);
			jPanelGeneral.add(getSaisieGeneral(), null);
			jPanelGeneral.add(getJScrollPane2(), null);
			jPanelGeneral.add(getJScrollPane(), null);

			jPanelGeneral.add(jLabelTotal, null);
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
			saisieMP.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == KeyEvent.VK_ENTER){

						/* Méthode pour envoyer un message privé */
						String texteEnvoiMP="";
						texteEnvoiMP= saisieMP.getText();
						saisieMP.setText("");		// On vide le champ de texte.
						client.envoiMessagePrive(texteEnvoiMP);
					}
				}
			});
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
			boutonEnvoyerMP.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					/* Méthode pour envoyer un message privé */
					String texteEnvoiMP="";
					texteEnvoiMP= saisieMP.getText();
					saisieMP.setText("");		// On vide le champ de texte.
					client.envoiMessagePrive(texteEnvoiMP);
				}
			});
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

						/* Méthode pour envoyer un message général */
						String texteEnvoiGeneral="";
						texteEnvoiGeneral= saisieGeneral.getText();
						saisieGeneral.setText("");		// On vide le champ de texte.
						client.envoiMessageGeneral(texteEnvoiGeneral);
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
					if(client.conecte == true){
						/* méthode pour envoyer un message général */
						String texteEnvoiGeneral="";
						texteEnvoiGeneral= saisieGeneral.getText();
						saisieGeneral.setText("");		// On vide le champ de texte.
						client.envoiMessageGeneral(texteEnvoiGeneral);
					}
					else{
						client.messageSystem("Vous n'êtes pas connecté à un serveur.");
					}
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
	public JTextPane getJTextPaneMP() {
		if (jTextPaneMP == null) {
			jTextPaneMP = new JTextPane();
			jTextPaneMP.setBackground(new Color(253, 241, 230));
			jTextPaneMP.setContentType("text/html");
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
			jScrollPane2.setViewportView(getJTextPaneGeneral());
		}
		return jScrollPane2;
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

	/**
	 * This method initializes jListClients	
	 * 	
	 * @return javax.swing.JList	
	 */
	public JList getJListClients() {
		if (jListClients == null) {
			jListClients = new JList();
			jListClients.setBackground(new Color(253, 241, 230));
		}
		return jListClients;
	}


	public void CreateConnexion(){
		new ParametresConnexion(this,client);
	}


	/**
	 * This method initializes jTextPaneGeneral	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	public JTextPane getJTextPaneGeneral() {
		if (jTextPaneGeneral == null) {
			jTextPaneGeneral = new JTextPane();
			jTextPaneGeneral.setContentType("text/html");
			jTextPaneGeneral.setBackground(new Color(253, 241, 230));
		}
		return jTextPaneGeneral;
	}
}
