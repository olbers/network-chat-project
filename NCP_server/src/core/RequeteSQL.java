package core;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * La class RequeteSQL va traiter toutes les requetes SQL via un des prepared Statement
 * @author Poirier Kevin 
 */

public class RequeteSQL {
	/**
	 * Permet de joindre la base de données.
	 */
	protected MySQL bdd;	
	/**
	 * @param bdd
	 */
	public RequeteSQL(MySQL bdd) {
		super();
		this.bdd = bdd;
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
	 * Permet de mettre à jour l'ip d'un client dans la BDD
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
	 * Verifie que le client n'existe pas
	 * @param compte
	 * @return Renvoi l'arrayList contenant l'utilisateur si il existe.
	 */
	public ArrayList verifClient (String compte){
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
	 * Permet de vérifier si un mail est déjà utilisé
	 * @param mail
	 * @return Renvoi l'arrayList contenant le mail si il existe.
	 */
	public ArrayList verifMail (String mail){
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
	/**
	 * Permet de recuperer l'id d'un compte
	 * @param compte
	 * @return Renvoi l'arrayList contenant l'id d'un compte.
	 */
	public ArrayList getBDDID (String compte){
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
	 * Permet de recuperer le compte si il existe et que le mot de passe corresponds.
	 * @param compte
	 * @param mail
	 * @return Renvoi l'arrayList contenant le nom du compte si mot de passe ok.
	 */
	public ArrayList connexionClient (String compte, String mdp){
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

}
