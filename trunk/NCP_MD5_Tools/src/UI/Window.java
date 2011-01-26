package UI;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import core.MD5;
import javax.swing.JLabel;
/**
 * Class qui contient l'interface de l'application
 * @author Poirier Kévin
 * @version 1.0.0
 */

public class Window {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="346,98"
	private JPanel jContentPane = null;
	private JTextField jTextField = null;
	private JButton jButton = null;
	private JTextField jTextField1 = null;
	private final JFileChooser fc;
	private JButton jButton1 = null;
	private JLabel jLabel = null;
	private JLabel jLabel1 = null;
	
	public Window(){		
		super();
		JFrame fenetre = this.getJFrame();
		fenetre.setLocationRelativeTo(null);							
		fenetre.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);		
		fenetre.setResizable(false);									
		fenetre.setVisible(true);
		this.fc = new JFileChooser(System.getProperty("user.dir" ));
	}
	

	/**
	 * This method initializes jFrame	
	 * 	
	 * @return javax.swing.JFrame	
	 */
	private JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setSize(new Dimension(394, 210));
			jFrame.setTitle("NCP MD5 Tools");
			jFrame.setContentPane(getJContentPane());
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
			jLabel1 = new JLabel();
			jLabel1.setBounds(new Rectangle(5, 78, 60, 19));
			jLabel1.setText("MD5 :");
			jLabel = new JLabel();
			jLabel.setBounds(new Rectangle(5, 9, 91, 17));
			jLabel.setText("Fichier :");
			jContentPane = new JPanel();
			jContentPane.setLayout(null);
			jContentPane.add(getJTextField(), null);
			jContentPane.add(getJButton(), null);
			jContentPane.add(getJTextField1(), null);
			jContentPane.add(getJButton1(), null);
			jContentPane.add(jLabel, null);
			jContentPane.add(jLabel1, null);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jTextField	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setBounds(new Rectangle(5, 28, 237, 30));
		}
		return jTextField;
	}

	/**
	 * This method initializes jButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton() {
		if (jButton == null) {
			jButton = new JButton();
			jButton.setBounds(new Rectangle(252, 28, 117, 28));
			jButton.setText("Parcourir...");
			jButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					  int returnVal = fc.showOpenDialog(jFrame);
				        if (returnVal == JFileChooser.APPROVE_OPTION) {
				            File file = fc.getSelectedFile();
				            jTextField.setText(file.getPath());
				           // System.out.println("Opening: " + file.getName() + "." );
				            jTextField1.setText(new MD5().checkSum(file));
				        } else {
				            System.out.println("Open command cancelled by user." );
				        }
				}
			});
		}
		return jButton;
	}

	/**
	 * This method initializes jTextField1	
	 * 	
	 * @return javax.swing.JTextField	
	 */
	private JTextField getJTextField1() {
		if (jTextField1 == null) {
			jTextField1 = new JTextField();
			jTextField1.setBounds(new Rectangle(5, 96, 238, 28));
		}
		return jTextField1;
	}


	/**
	 * This method initializes jButton1	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJButton1() {
		if (jButton1 == null) {
			jButton1 = new JButton();
			jButton1.setBounds(new Rectangle(252, 96, 115, 26));
			jButton1.setText("Copier !!");
			jButton1.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Toolkit toolKit = Toolkit.getDefaultToolkit();
					Clipboard cb = toolKit.getSystemClipboard();
					cb.setContents(new StringSelection(jTextField1.getText()), null);
				}
			});
		}
		return jButton1;
	}

}
