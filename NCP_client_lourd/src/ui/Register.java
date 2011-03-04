package ui;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextField;
import java.awt.Rectangle;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.border.BevelBorder;

import core.Client;

public class Register {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="6,5"
	private JPanel jContentPane = null;
	private JTextField textPseudo = null;
	private JLabel jLabelPseudo = null;
	private JPasswordField jPasswordMDP = null;
	private JPasswordField jPasswordMDP2 = null;
	private JLabel jLabelMDP = null;
	private JTextField jTextMail = null;
	private JTextField jTextMail2 = null;
	private JLabel jLabelMail = null;
	private JPanel jPanelPseudo = null;
	private JButton jButtonValider = null;
	private JButton jButtonAnnuler = null;
	private JPanel jPanelMDP = null;
	private JPanel jPanelMail = null;
	private JLabel jLabelConfirmationMDP = null;
	private JLabel jLabelConfirmationMail = null;
	protected Client client;
	protected Fenetre fenetre;
	protected String adresseMail = null;  //  @jve:decl-index=0:
	protected String adresseMailConfirmation = null;
	protected char[] mdp = null;
	protected char[] mdpConfirmation = null;
	protected String mdp2 = null;
	protected String mdpConfirmation2 = null;  //  @jve:decl-index=0:
	protected String pseudoEnregistrement = null;


	public Register(Client client) {
		super();
		this.client = client;
		getJFrame();
	}


	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setTitle("S'enregistrer");
			jFrame.setLocationRelativeTo(null);	
			jFrame.setSize(new Dimension(388, 312));
			jFrame.setContentPane(getJContentPane());
			jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			jFrame.setResizable(false);
			jFrame.setVisible(true);
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jLabelMail = new JLabel();
			jLabelMail.setText("Mail:");
			jLabelMail.setBounds(new Rectangle(15, 8, 26, 16));
			jLabelMDP = new JLabel();
			jLabelMDP.setText("Mot de passe:");
			jLabelMDP.setBounds(new Rectangle(15, 8, 79, 16));
			jLabelPseudo = new JLabel();
			jLabelPseudo.setText("Pseudo:");
			jLabelPseudo.setBounds(new Rectangle(15, 8, 46, 16));
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJPanelPseudo(), null);
			jContentPane.add(getJButtonValider(), null);
			jContentPane.add(getJButtonAnnuler(), null);
			jContentPane.add(getJPanelMDP(), null);
			jContentPane.add(getJPanelMail(), null);
			jContentPane.setBackground(new Color(205, 219, 242));
		}
		return jContentPane;
	}

	/**
	 * This method initializes textPseudo	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getTextPseudo() {
		if (textPseudo == null) {
			textPseudo = new JTextField();
			textPseudo.setText(this.client.pseudo);
			textPseudo.setBounds(new Rectangle(9, 33, 117, 20));
		}
		return textPseudo;
	}

	/**
	 * This method initializes jPasswordMDP	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordMDP() {
		if (jPasswordMDP == null) {
			jPasswordMDP = new JPasswordField();
			jPasswordMDP.setBounds(new Rectangle(20, 35, 117, 20));
		}
		return jPasswordMDP;
	}

	/**
	 * This method initializes jPasswordMDP2	
	 * 	
	 * @return javax.swing.JPasswordField	
	 */
	private JPasswordField getJPasswordMDP2() {
		if (jPasswordMDP2 == null) {
			jPasswordMDP2 = new JPasswordField();
			jPasswordMDP2.setBounds(new Rectangle(20, 100, 117, 20));
		}
		return jPasswordMDP2;
	}

	/**
	 * This method initializes jTextMail	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextMail() {
		if (jTextMail == null) {
			jTextMail = new JTextField();
			jTextMail.setBounds(new Rectangle(20, 35, 117, 20));
		}
		return jTextMail;
	}

	/**
	 * This method initializes jTextMail2	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextMail2() {
		if (jTextMail2 == null) {
			jTextMail2 = new JTextField();
			jTextMail2.setBounds(new Rectangle(20, 100, 117, 20));
		}
		return jTextMail2;
	}

	/**
	 * This method initializes jPanelPseudo	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelPseudo() {
		if (jPanelPseudo == null) {
			jPanelPseudo = new JPanel();
			jPanelPseudo.setBackground(new Color(253, 241, 230));
			jPanelPseudo.setLayout(null);
			jPanelPseudo.setBounds(new Rectangle(119, 12, 144, 68));
			jPanelPseudo.add(jLabelPseudo, null);
			jPanelPseudo.add(getTextPseudo(), null);
			jPanelPseudo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
		return jPanelPseudo;
	}



	/**
	 * This method initializes jButtonValider	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonValider() {
		if (jButtonValider == null) {
			jButtonValider = new JButton();
			jButtonValider.setBounds(new Rectangle(45, 240, 102, 26));
			jButtonValider.setText("Valider");
			jButtonValider.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					valider();
				}
			});
		}
		return jButtonValider;
	}

	/**
	 * This method initializes jButtonAnnuler	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButtonAnnuler() {
		if (jButtonAnnuler == null) {
			jButtonAnnuler = new JButton();
			jButtonAnnuler.setBounds(new Rectangle(220, 240, 102, 26));
			jButtonAnnuler.setText("Annuler");
			jButtonAnnuler.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jFrame.dispose();
				}
			});
		}
		return jButtonAnnuler;
	}

	/**
	 * This method initializes jPanelMDP	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelMDP() {
		if (jPanelMDP == null) {
			jLabelConfirmationMDP = new JLabel();
			jLabelConfirmationMDP.setBounds(new Rectangle(15, 75, 102, 16));
			jLabelConfirmationMDP.setText("Confirmation:");
			jPanelMDP = new JPanel();
			jPanelMDP.setBackground(new Color(253, 241, 230));
			jPanelMDP.setLayout(null);
			jPanelMDP.setBounds(new Rectangle(15, 90, 155, 133));
			jPanelMDP.add(jLabelMDP, null);
			jPanelMDP.add(getJPasswordMDP(), null);
			jPanelMDP.add(getJPasswordMDP2(), null);
			jPanelMDP.add(jLabelConfirmationMDP, null);
			jPanelMDP.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
		return jPanelMDP;
	}

	/**
	 * This method initializes jPanelMail	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanelMail() {
		if (jPanelMail == null) {
			jLabelConfirmationMail = new JLabel();
			jLabelConfirmationMail.setBounds(new Rectangle(15, 75, 102, 16));
			jLabelConfirmationMail.setText("Confirmation:");
			jPanelMail = new JPanel();
			jPanelMail.setBackground(new Color(253, 241, 230));
			jPanelMail.setLayout(null);
			jPanelMail.setBounds(new Rectangle(201, 90, 155, 133));
			jPanelMail.add(getJTextMail(), null);
			jPanelMail.add(getJTextMail2(), null);
			jPanelMail.add(jLabelMail, null);
			jPanelMail.add(jLabelConfirmationMail, null);
			jPanelMail.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		}
		return jPanelMail;
	}

	public boolean verificationMail(String adresseMail,String adresseMailConfirmation){
		boolean verifMail=false;
		if((adresseMail.equalsIgnoreCase(adresseMailConfirmation))){
			verifMail = true;
		}
		else{
			verifMail = false;
		}
		return verifMail;
	}

	public boolean verificationMdp(String mdp,String mdp2){
		boolean verifMdp =false;
		if(mdp.equals(mdp2)){
			verifMdp = true;
		}
		else{
			verifMdp = false;
		}
		return verifMdp;
	}

	public void valider(){
		adresseMail = jTextMail.getText();
		adresseMailConfirmation = jTextMail2.getText();
		pseudoEnregistrement = textPseudo.getText();
		mdp=jPasswordMDP.getPassword();
		mdp2=new String(mdp);
		mdpConfirmation=jPasswordMDP2.getPassword();
		mdpConfirmation2=new String(mdpConfirmation);
		String textInformation="";
		
		if (client.verifCheckConnexion){
			if((!adresseMail.isEmpty()) && (!mdp2.isEmpty()) && (!pseudoEnregistrement.isEmpty())){
				if(verificationMail(adresseMail,adresseMailConfirmation) && (verificationMdp(mdp2, mdpConfirmation2))){
					this.client.infosEnregistrement(pseudoEnregistrement,adresseMail,mdp2);
					jFrame.dispose();
				}
				else {

					if (!verificationMail(adresseMail,adresseMailConfirmation)){
						textInformation = textInformation+"Les adresses mail ne correspondent pas\n";
					}
					if(!verificationMdp(mdp2, mdpConfirmation2)){
						textInformation = textInformation+"Les mots de passe ne correspondent pas\n";
					}
					JOptionPane.showMessageDialog(null,
							textInformation,
							"Erreurs d'enregistrement",
							JOptionPane.INFORMATION_MESSAGE);				
				}
				textInformation = "";
			}
			else{
				JOptionPane.showMessageDialog(null,
						"Certains champs sont vides!",
						"Erreurs d'enregistrement",
						JOptionPane.INFORMATION_MESSAGE);		
			}
		}
		else{
			JOptionPane.showMessageDialog(null,
					"Impossible d'effectuer un enregistrement tant que vous n'êtes pas connecté!",
					"Enregistrement Impossible",
					JOptionPane.INFORMATION_MESSAGE);	
		}
	}
}
