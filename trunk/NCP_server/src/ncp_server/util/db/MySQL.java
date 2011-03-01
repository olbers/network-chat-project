package ncp_server.util.db;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import ncp_server.util.Log;
import ncp_server.util.option.Option;


/**
 * La class MySQL permettra la liason entre le serveur de chat et le serveur MySQL.
 * @author Poirier Kevin
 * @version 2.1.1
 *
 */

public class MySQL {
	/**
	 * l'objet connexion servira pour la connexion à la base de données MySQL.
	 */
	protected Connection connexion;
	/**
	 * La chaine db permettra de recupéré l'adresse du serveur MySQL et quel base de données utiliser.
	 */
	protected String db;
	/**
	 * La chaine user recevra l'identifiant de connexion au serveur MySQL.
	 */
	protected String user;
	/**
	 * La chaine pwd recevra le mot de passe de connexion au serveur MySQL.
	 */
	protected String pwd;
	/**
	 * L'objet option permettra de récuprer les option de connexion à la base de données.
	 * @see Option
	 */
	protected Option option;
	/**
	 * Variable log qui permet la gestion des message d'erreur.
	 * @since 1.2.0
	 */
	protected Log log;
	private static MySQL instance;

	/**
	 * Constructeur de la class MySQL.
	 * Recupère les options du serveur pour la base de données à partir du fichier
	 * @see Option
	 */
	public MySQL(){
		this.option=Option.getInstace();
		this.db=option.getDbMySQL();
		this.user=option.getUserMySQL();
		this.pwd=option.getPwdMySQL();
		this.log=Log.getInstance();
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.connexion=this.connectToBDD();
			System.out.println("[OK]");
		}
		catch (Exception e) {
			System.err.println("[FAIL]");
			//e.printStackTrace();
			System.err.println("Unable to find and load driver");
			this.log.err("Unable to find and load driver");
			this.log.exit();
			System.exit(1);
		}

	}
	/**
	 * Methode singleton qui permet d'assurer une seul instance de la classe.
	 * @return instance
	 */
	public static MySQL getInstance(){
		if(null == instance){
			instance = new MySQL();
		}
		return instance;
	}
	/**
	 * Permet de fermer la connexion à la BDD.
	 * @since 2.0.0
	 */
	public void closeBDD(){
		try {
			connexion.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			displaySQLErrors(e);
			System.err.println(e.getMessage()+"\n"+e.getSQLState()); 
			this.log.err(e.getMessage()+"\n"+e.getSQLState());
		}
	}
	/**
	 * Permet la connexion à la base de données/
	 * @return connexion
	 */
	private Connection connectToBDD(){
		try {
			Properties props = new Properties();
			props.setProperty("user",this.user);
			props.setProperty("password",this.pwd);
			props.setProperty("autoReconnect", "true");
			this.connexion = DriverManager.getConnection(this.db,props);
		}
		catch(SQLException e) {
			System.err.println("[FAIL]");
			displaySQLErrors(e);
			this.log.exit();
			System.exit(1);
		}
		return connexion;

	}
	/**
	 * Gestion des erreurs SQL.
	 * @param e
	 */
	public void displaySQLErrors(SQLException e) {
		System.out.println("SQLException: " + e.getMessage());
		this.log.err("SQLException: " + e.getMessage());
		System.out.println("SQLState:     " + e.getSQLState());
		this.log.err("SQLState:     " + e.getSQLState());
		System.out.println("VendorError: " + e.getErrorCode());
		this.log.err("VendorError: " + e.getErrorCode());
	}
	/**
	 * @return the connexion
	 * @since 2.0.0
	 */
	public Connection getConnexion() {
		return connexion;
	}
	//La méthode selectSQL est surchargé pour recuperer le nombre d'element nécessaire qui sont renvoyé dans un arrayList.
	/**
	 * Cette méthode effectue une requete SQL renvoyant un seul élement par résultat dans un ArrayList
	 * @param prState
	 * @param elem
	 * @return Une ArrayList contenant les différents résultat de la requete
	 */
	public ArrayList<String> selecSQL(PreparedStatement prState, ArrayList<String> elem){
		ArrayList<String> resultatSelect = null ;
		try {
			PreparedStatement prepState = prState; 
			ResultSet rs = prepState.executeQuery();
			while (rs.next()) {
				if (resultatSelect==null){
					resultatSelect=new ArrayList<String>();
				}
				if(elem.size()==1)
					resultatSelect.add(rs.getString(elem.get(0)));
				else if (elem.size()==2)
					resultatSelect.add(rs.getString(elem.get(0))+" "+rs.getString(elem.get(1)));
				else if(elem.size()==3)
					resultatSelect.add(rs.getString(elem.get(0))+" "+rs.getString(elem.get(1)+" "+rs.getString(elem.get(2))));
				else if(elem.size()==4)
					resultatSelect.add(rs.getString(elem.get(0))+" "+rs.getString(elem.get(1))+" "+rs.getString(elem.get(2))+" "+rs.getString(elem.get(3)));
				else
					System.err.println("Erreur dans les élements de la requete sql.");
			}
			rs.close();
			prepState.close();
		}
		catch(SQLException e) {
			displaySQLErrors(e);
		}
		return resultatSelect;
	}
	/**
	 * Setter de la variable db.
	 * @param db
	 */
	public void setDb(String db) {
		this.db = db;
	}

	/**
	 * Setter de la variable pwd.
	 * @param pwd
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	/**
	 * Setter de la variable user.
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * Methode qui permet de faire des INSERT / UPDATE / DROP
	 */
	public void updateSQL(PreparedStatement prState){
		try {
			PreparedStatement prepState = prState;
			prepState.executeUpdate();		
			prepState.close();			
		}
		catch(SQLException e) {
			displaySQLErrors(e);
			System.err.println(e.getMessage()+"\n"+e.getSQLState()); 
			this.log.err(e.getMessage()+"\n"+e.getSQLState());
		}
	}

}
