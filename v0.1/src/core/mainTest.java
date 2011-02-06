package core;

import java.io.IOException;
import java.util.ArrayList;
public class mainTest {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String requete;
		String[] testTab;
		String test = "@md5 9d9e72cffa1ec681d40317b6ce40dc41";
		ArrayList user=null;
		Log newLog =new Log();
		Option optionTest = new Option(newLog);
		Server server = new Server(newLog, optionTest);
		new Mail(optionTest, newLog).errorAdminMail("Erratum dsl du spam je test");
		
		/*
		test = server.suppr1Car(test);
		System.out.println(test);
		System.out.println(server.recupCommande(test));
		System.out.println(test);
		testTab= server.recupArgument(test, 2);
		System.out.println(testTab[1]);*/
		/*try {
			PreparedStatement testState = server.getBDD().getConnexion().prepareStatement("select * from test");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		
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
