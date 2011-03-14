package fr.kenin.ncp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class ListeCo extends Activity {

	protected ListView lv;
	protected ArrayList<String> userCO;
	/**
	 * Permet de mettre à jour la liste des connecté.
	 * @param liste
	 */
	public String setUserCo(String liste){		
		StringTokenizer st = new StringTokenizer(liste,"|");
		String pseudo;
		StringBuffer listePrete = new StringBuffer("");
		while (st.hasMoreTokens()) {
			pseudo = st.nextToken();
			listePrete.append(pseudo+"\n");
		}
		return listePrete.toString();
	}

	private void upDateList(String message){
		String liste=setUserCo(message);
		TextView tv = (TextView)findViewById(R.id.listeCoTextView2);
		tv.setText(liste);
		
	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listeco);
		upDateList("");		
	}

}
