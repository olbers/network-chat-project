package test;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;


public class TestTab extends JFrame {

	private JTabbedPane onglet;
	//Vous êtes habitués à cette classe, maintenant... ;)
	public TestTab(){
		this.setLocationRelativeTo(null);
		this.setTitle("Gérer vos conteneurs");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(400, 200);
		
		//Création de plusieurs Panneau
		Onglet[] tPan = {	new Onglet(Color.RED),
							new Onglet(Color.GREEN),
							new Onglet(Color.BLUE)};
		
		//Création de notre conteneur d'onglets
		onglet = new JTabbedPane();
		int i = 0;
		for(Onglet pan : tPan){
			//Méthode d'ajout d'onglet
			onglet.add("Onglet N°"+(++i), pan);
			onglet.setBackground(Color.getHSBColor(0, 0, 0));
			//Vous pouvez aussi utiliser la méthode addTab
			//onglet.addTab("Onglet N°"+(++i), pan);

		}
		//on passe ensuite les onglets au contentPane
		this.getContentPane().add(onglet);
		this.setVisible(true);
	}
	
	public static void main(String[] args){
		TestTab fen = new TestTab();
	}
	
}



