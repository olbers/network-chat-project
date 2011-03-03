package ui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;

import core.Client;
import javax.swing.JPasswordField;



public class ParametresConnexion implements MouseListener{

	protected JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="378,9"
	protected JPanel jContentPane = null;
	protected JPanel jPanel = null;
	protected JLabel jLabel = null;
	protected JLabel AdresseIP = null;
	protected JLabel Port = null;
	protected JTextField jTextFieldPseudo = null;
	protected JTextField jTextFieldIP = null;
	protected JTextField jTextFieldPort = null;
	protected JButton boutonValider = null;
	protected String adresseIP=null;
	protected char[] pass=null;
	protected int port=0;
	protected String pseudo=null;
	protected Fenetre fenetre;
	protected Client client;
	private JLabel jLabelMDP = null;
	private JPasswordField jPasswordField = null;
	private JButton jButtonAide = null;
	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	public ParametresConnexion(Fenetre fenetre,Client client) {
		this.fenetre=fenetre;
		this.client=client;
		jFrame = new JFrame();
		jFrame.setSize(new Dimension(294, 270));
		jFrame.setTitle("Paramètres connexion");
		jFrame.setContentPane(getJContentPane());
		jFrame.setLocationRelativeTo(null);
		jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);		// Le processus se termine correctement lorsque l'on ferme la fenêtre
		jFrame.setResizable(false);
		jFrame.setVisible(true);

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
			jContentPane.add(getJPanel(), null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	protected JPanel getJPanel() {
		if (jPanel == null) {
			jLabelMDP = new JLabel();
			jLabelMDP.setBounds(new Rectangle(18, 166, 94, 20));
			jLabelMDP.setText("Mot de passe: ");
			Port = new JLabel();
			Port.setBounds(new Rectangle(70, 67, 31, 19));
			Port.setText("Port: ");
			AdresseIP = new JLabel();
			AdresseIP.setBounds(new Rectangle(33, 26, 71, 20));
			AdresseIP.setText("Adresse IP: ");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(50, 123, 56, 19));
			jLabel.setText("Pseudo: ");
			jPanel = new JPanel();
			jPanel.setBackground(new Color(205, 219, 242));
			jPanel.setLayout(null);
			jPanel.setBounds(new Rectangle(0, -1, 294, 242));
			jPanel.add(jLabel, null);
			jPanel.add(AdresseIP, null);
			jPanel.add(Port, null);
			jPanel.add(getJTextField(), null);
			jPanel.add(getJTextField1(), null);
			jPanel.add(getJTextField2(), null);
			jPanel.add(getBoutonValider(), null);
			jPanel.add(jLabelMDP, null);
			jPanel.add(getJPasswordField(), null);
			jPanel.add(getJButtonAide(), null);
		}
		return jPanel;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getJTextField() {
		if (jTextFieldPseudo == null) {
			jTextFieldPseudo = new JTextField();
			jTextFieldPseudo.setBounds(new Rectangle(120, 121, 136, 26));
			jTextFieldPseudo.setBackground(new Color(253, 241, 230));

			jTextFieldPseudo.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == KeyEvent.VK_ENTER){
						valider();
					}
				}
			});
		}
		return jTextFieldPseudo;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getJTextField1() {
		if (jTextFieldIP == null) {
			jTextFieldIP = new JTextField();
			jTextFieldIP.setBounds(new Rectangle(120, 24, 136, 26));
			jTextFieldIP.setBackground(new Color(253, 241, 230));

			jTextFieldIP.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == KeyEvent.VK_ENTER){
						valider();
					}
				}
			});
		}
		return jTextFieldIP;
	}

	/**
	 * This method initializes jTextField2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	protected JTextField getJTextField2() {
		if (jTextFieldPort == null) {
			jTextFieldPort = new JTextField();
			jTextFieldPort.setBounds(new Rectangle(120, 64, 136, 26));
			jTextFieldPort.setBackground(new Color(253, 241, 230));

			jTextFieldPort.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyTyped(java.awt.event.KeyEvent e) {
					if(e.getKeyChar() == KeyEvent.VK_ENTER){
						valider();
					}
				}
			});
		}
		return jTextFieldPort;
	}

	/**
	 * This method initializes boutonValider	
	 * 	
	 * @return javax.swing.JButton	
	 */
	protected JButton getBoutonValider() {
		if (boutonValider == null) {
			boutonValider = new JButton();
			boutonValider.setBounds(new Rectangle(29, 203, 101, 26));
			boutonValider.setText("Valider");


			boutonValider.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					valider();
				}
			});
		}
		return boutonValider;
	}

	public void valider(){

		adresseIP=jTextFieldIP.getText();
		port=Integer.parseInt(jTextFieldPort.getText());
		pseudo=jTextFieldPseudo.getText();
		pass=jPasswordField.getPassword();


		if((!jTextFieldPseudo.getText().isEmpty()) && (!jTextFieldIP.getText().isEmpty()) && (!jTextFieldPort.getText().isEmpty())){

			if((jTextFieldPseudo.getText().length()<=15) && (jTextFieldIP.getText().length()<=15) && (jTextFieldPort.getText().length()<=5)){

				System.out.println("Contraintes OK");
				this.jFrame.dispose();
				this.client.parametresConnexion(adresseIP, port, pseudo,pass);
				
			}
			else{
				String textContrainte="Certains champs ne sont pas conformes! \n\n" +
				"Vérifiez que votre pseudo est de 15 caractères maximum. \n" +
				"Vérifiez que l'adresse IP est correcte.\n" +
				"Vérifiez que le port du serveur est bien compris entre 1 et 49151.";
				JOptionPane.showMessageDialog(null,
						textContrainte,
						"Informations de saisie",
						JOptionPane.INFORMATION_MESSAGE);	
			}
		}
		else{
			String textContrainte="Des champs sont vides!\n\n" +
			"Vous devez remplir tous les champs.";
			JOptionPane.showMessageDialog(null,
					textContrainte,
					"Informations de saisie",
					JOptionPane.INFORMATION_MESSAGE);		
		}
	}


	/**
	 * This method initializes jPasswordField	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordField() {
		if (jPasswordField == null) {
			jPasswordField = new JPasswordField();
			jPasswordField.setBounds(new Rectangle(120, 162,  136, 26));
			jPasswordField.setBackground(new Color(192, 192, 192));
			jPasswordField.addMouseListener(this);
			jPasswordField.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					valider();
				}
			});
		}
		return jPasswordField;
	}

	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		jPasswordField.setBackground(new Color(253, 241, 230));
	}

	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	/**
	 * This method initializes jButtonAide	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAide() {
		if (jButtonAide == null) {
			jButtonAide = new JButton();
			jButtonAide.setBounds(new Rectangle(166, 203, 101, 26));
			jButtonAide.setText("Aide");
			jButtonAide.addActionListener (new ActionListener(){
				public void actionPerformed(ActionEvent arg0) {
					String textAbout="Pour vous connecter, entrez l'adresse IP du serveur (ex: 109.238.0.52)\n" +
							"Entrez le port (1999 par défaut)\n" +
							"Saisissez votre pseudo\n\n" +
							"Si vous êtes déjà enregistré, veuillez saisir votre mot de passe associé au pseudo.";
					JOptionPane.showMessageDialog(null,
							textAbout,
							"Aide à la connexion\n",
							JOptionPane.INFORMATION_MESSAGE);				
				}		
			});
		}
		return jButtonAide;
	}
}
