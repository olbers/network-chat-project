package ncp_server.util.db;



import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;



/**
 * La class RequeteSQL va traiter toutes les requetes SQL via un des prepared Statement
 * @author Poirier Kevin 
 * @version 0.1.4
 */

public class RequeteSQL {
	
	protected MySQL bdd;	
	private static RequeteSQL instance;
	/**
	 * Constructeur
	 */
	public RequeteSQL() {
		super();
		this.bdd = MySQL.getInstance();
	}
	public static RequeteSQL getInstance(){
		if(null == instance){
			instance = new RequeteSQL();
		}
		return instance;
	}
	

	/**
	 * Permet de recuperer le compte si il existe et que le mot de passe corresponds.
	 * @param compte
	 * @param mdp
	 * @return Renvoi l'arrayList contenant le nom du compte si mot de passe ok.
	 */
	public ArrayList<String> connexionClient (String compte, String mdp){
		ArrayList<String> resultat=null;
		ArrayList<String> element = new ArrayList<String>();
		element.add("id");
		element.add("compte");
		element.add("mail");
		element.add("lvAccess");
		String sql = "Select * FROM user WHERE compte = ? AND mdp = ? ";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, compte);
			preState.setString(2, mdp);
			resultat = this.bdd.selecSQL(preState, element);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);			
		}
		return resultat;
	}
	/**
	 * Permet de recuperer l'id d'un compte
	 * @param compte
	 * @return Renvoi l'arrayList contenant l'id d'un compte.
	 */
	public ArrayList<String> getBDDID (String compte){
		ArrayList<String> resultat=null;
		ArrayList<String> element = new ArrayList<String>();
		element.add("id");
		String sql = "Select id FROM user WHERE compte = ?";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, compte);
			resultat = this.bdd.selecSQL(preState, element);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);			
		}
		return resultat;
	}
	/**
	 * Permet de recup�rer le niveau d'acc�s d'un compte.
	 * @param compte
	 * @return arrayList
	 */
	public ArrayList<String> getlvAccess (String compte){
		ArrayList<String> resultat=null;
		ArrayList<String> element = new ArrayList<String>();
		element.add("lvAccess");
		String sql = "Select lvAccess FROM user WHERE compte = ?";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, compte);
			resultat = this.bdd.selecSQL(preState, element);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);			
		}
		return resultat;
	}
	/**
	 * Permet de recup�rer les ip banni.
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getBanIP(){
		ArrayList<String> resultat=null;
		ArrayList<String> element = new ArrayList<String>();
		element.add("ip");
		element.add("finBan");
		String sql = "Select * FROM ban";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			resultat = this.bdd.selecSQL(preState, element);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);			
		}
		return resultat;		
	}
	/**
	 * Permet d'inserer un client dans la BDD
	 * @param compte
	 * @param mdp
	 * @param mail
	 * @param lvAccess
	 * @param ip
	 * @param lastCo
	 */
	public void insertClient(String compte, String mdp, String mail, int lvAccess,String ip, String lastCo){
		String sql="INSERT INTO user (compte,mdp,mail,lvAccess,ip,lastConnection) VALUES" +
		" (?,?,?,?,?,?)";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, compte);
			preState.setString(2, mdp);
			preState.setString(3, mail);
			preState.setInt(4, lvAccess);
			preState.setString(5, ip);
			preState.setString(6, lastCo);
			this.bdd.updateSQL(preState);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);
		}

	}
	/**
	 * Permet d'ajouter une ip banni
	 * @param ip
	 * @param timestamp
	 */
	public void insertBanIP(String ip,Timestamp timestamp ){
		String sql="INSERT INTO ban (ip,finBan) VALUES" +
		" (?,?)";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, ip);
			preState.setLong(2, timestamp.getTime());
			this.bdd.updateSQL(preState);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);
		}
	}
	/**
	 * Permet d'effacer les ip banni
	 * @param ip
	 */
	public void delBanIP(String ip){
		String sql = "DELETE FROM ban WHERE ip = ? LIMIT 1";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, ip);
			this.bdd.updateSQL(preState);
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);
		}
	}
	/**
	 * Permet de mettre � jour l'ip d'un client dans la BDD
	 * @param ip
	 * @param id
	 */
	public void updateIP(String ip, int id){
		String sql = "UPDATE user SET ip = ? WHERE id = ?";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, ip);
			preState.setInt(2, id);
			this.bdd.updateSQL(preState);
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);
		}
	}
	/**
	 * Permet de mettre � jour la derni�re connexion.
	 * @param lastCo
	 * @param id
	 */
	public void updateLastCo(String lastCo,int id){
		String sql = "UPDATE user SET lastConnection = ? WHERE id = ?";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, lastCo);
			preState.setInt(2, id);
			this.bdd.updateSQL(preState);
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);
		}
		
	}
	
	public void updatelvAccess(int id,int access){
		String sql="UPDATE user SET lvAccess = ? WHERE id = ?";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setInt(1, access);
			preState.setInt(2, id);
			this.bdd.updateSQL(preState);
			} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);
		}
	}
	/**
	 * Verifie que le client n'existe pas
	 * @param compte
	 * @return Renvoi l'arrayList contenant l'utilisateur si il existe.
	 */
	public ArrayList<String> verifClient (String compte){
		ArrayList<String> resultat=null;
		ArrayList<String> element = new ArrayList<String>();
		element.add("compte");
		String sql = "Select * FROM user WHERE compte = ?";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, compte);
			resultat = this.bdd.selecSQL(preState, element);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);			
		}
		return resultat;
	}
	/**
	 * Permet de v�rifier si un mail est d�j� utilis�
	 * @param mail
	 * @return Renvoi l'arrayList contenant le mail si il existe.
	 */
	public ArrayList<String> verifMail (String mail){
		ArrayList<String> resultat=null;
		ArrayList<String> element = new ArrayList<String>();
		element.add("mail");
		String sql = "Select * FROM user WHERE mail = ?";
		try {
			PreparedStatement preState = this.bdd.connexion.prepareStatement(sql);
			preState.setString(1, mail);
			resultat = this.bdd.selecSQL(preState, element);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			this.bdd.displaySQLErrors(e);			
		}
		return resultat;
	}


}
