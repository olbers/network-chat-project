package core;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JButton;




public class DonneesConnexion {

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
	protected String port=null;
	protected String pseudo=null;


	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	public DonneesConnexion() {
		jFrame = new JFrame();
		jFrame.setSize(new Dimension(294, 242));
		jFrame.setTitle("Paramètres connexion");
		jFrame.setContentPane(getJContentPane());
		jFrame.setLocationRelativeTo(null);
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		// Le processus se termine correctement lorsque l'on ferme la fenêtre
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
			Port = new JLabel();
			Port.setBounds(new Rectangle(56, 107, 31, 19));
			Port.setText("Port: ");
			AdresseIP = new JLabel();
			AdresseIP.setBounds(new Rectangle(17, 59, 71, 20));
			AdresseIP.setText("Adresse IP: ");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(36, 16, 56, 19));
			jLabel.setText("Pseudo: ");
			jPanel = new JPanel();
			jPanel.setLayout(null);
			jPanel.setBounds(new Rectangle(0, 0, 278, 203));
			jPanel.add(jLabel, null);
			jPanel.add(AdresseIP, null);
			jPanel.add(Port, null);
			jPanel.add(getJTextField(), null);
			jPanel.add(getJTextField1(), null);
			jPanel.add(getJTextField2(), null);
			jPanel.add(getBoutonValider(), null);
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
			jTextFieldPseudo.setBounds(new Rectangle(110, 13, 136, 26));
			
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
			jTextFieldIP.setBounds(new Rectangle(110, 58, 136, 26));
			
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
			jTextFieldPort.setBounds(new Rectangle(110, 105, 136, 26));
			
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
			boutonValider.setBounds(new Rectangle(90, 157, 101, 26));
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
		
		if((!jTextFieldPseudo.getText().isEmpty()) && (!jTextFieldIP.getText().isEmpty()) && (!jTextFieldPort.getText().isEmpty())){
			
			if((jTextFieldPseudo.getText().length()<=10) && (jTextFieldIP.getText().length()<=15) && (jTextFieldPort.getText().length()<=5)){
				
				System.out.println("Contraintes OK");
				new Client(jTextFieldIP.getText(),Integer.parseInt(jTextFieldPort.getText()),jTextFieldPseudo.getText());
				jFrame.dispose();
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
}
