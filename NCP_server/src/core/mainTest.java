package core;

import java.io.IOException;
import java.util.*;

public class mainTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String requete;
		ArrayList user=null;
		Option optionTest = new Option();
		MySQL BDD = new MySQL(optionTest);
		requete = "INSERT INTO user VALUES (2,'Kenin')";
		//BDD.updateSQL(requete);
		requete = "Select * from user";
		user = BDD.selecSQL(requete, "ID","pseudo");
		for (int i=0;i<(user.size());i++){
			System.out.println(user.get(i));
		}
		user = BDD.selecSQL(requete,"pseudo");
		for (int i=0;i<(user.size());i++){
			System.out.println(user.get(i));
		}
		
	}

}
