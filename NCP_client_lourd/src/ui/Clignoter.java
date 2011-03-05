package ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import ui.Fenetre;

/**
 * @author Asus
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class Clignoter {

	static ActionListener taskPerformer2;
	static ActionListener taskPerformer;
	public  Timer t2;
	public Fenetre fenetre;
	
	public Clignoter(){
		taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {

				fenetre.jTabbedPane.setBackgroundAt(fenetre.jTabbedPane.indexOfTab(fenetre.getTitle()),new Color(255,240,100));

				fenetre.jPanelMP.setBackground(new Color(255,240,100));
				Timer t=new Timer(250,taskPerformer2);
				t.start();
				t.setRepeats(false);
			}
		};
		taskPerformer2 = new ActionListener() {
			public void actionPerformed(ActionEvent ev) {

				fenetre.jTabbedPane.setBackgroundAt(fenetre.jTabbedPane.indexOfTab(fenetre.getTitle()),new Color(153,153,153));
				fenetre.jPanelMP.setBackground(new Color(255,240,100));					
			}
		};
		t2=new Timer(500,taskPerformer);
	}
}