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
		Log newLog =new Log();
		Option optionTest = new Option(newLog);
		newLog.chat("Blabla");
		/*String err="Erreur de ";
		newLog.err(err);
		String chat="BlablaBla";
		newLog.chat(chat);
		MySQL BDD = new MySQL(optionTest);
		requete = "INSERT INTO user VALUES (2,'Kenin')";
		//BDD.updateSQL(requete);
		requete = "Select * from user ";
		user = BDD.selecSQL(requete, "ID","pseudo");
		System.out.println(user.size());
		for (int i=0;i<(user.size());i++){
			if (user.get(i)==""){
				System.out.println("Client in trouvable");
			}
			System.out.println(user.get(i));
		}
		requete = "Select * from user  WHERE pseudo = 'test02'";
		user = BDD.selecSQL(requete, "ID");
		System.out.println(user.size());
		for (int i=0;i<(user.size());i++){
			System.out.println(user.get(i));
		}
		*/
	}

}
