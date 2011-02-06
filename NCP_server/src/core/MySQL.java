package core;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


/**
 * La class MySQL permettra la liason entre le serveur de chat et le serveur MySQL.
 * @author Poirier Kevin
 * @version 2.0.0
 *
 */

public class MySQL {
	/**
	 * l'objet connexion servira pour la connexion � la base de donn�es MySQL.
	 */
	protected Connection connexion;
	/**
	 * La chaine db permettra de recup�r� l'adresse du serveur MySQL et quel base de donn�es utiliser.
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
	 * L'objet option permettra de r�cuprer les option de connexion � la base de donn�es.
	 * @see Option
	 */
	protected Option option;
	/**
	 * Variable log qui permet la gestion des message d'erreur.
	 * @since 1.1.0
	 */
	protected Log log;

	/**
	 * Setter de la variable db.
	 * @param db
	 */
	public void setDb(String db) {
		this.db = db;
	}
	/**
	 * Setter de la variable user.
	 * @param user
	 */
	public void setUser(String user) {
		this.user = user;
	}
	/**
	 * Setter de la variable pwd.
	 * @param pwd
	 */
	public void setPwd(String pwd) {
		this.pwd = pwd;
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
	 * Constructeur de la class MySQL.
	 * Recup�re les options du serveur pour la base de donn�es � partir du fichier
	 * @param option
	 * @see Option
	 */
	public MySQL(Option option,Log log){
		this.option=option;
		this.db=option.getDbMySQL();
		this.user=option.getUserMySQL();
		this.pwd=option.getPwdMySQL();
		this.log=log;
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			this.connexion=this.connectToBDD();
		}
		catch (Exception e) {
			e.printStackTrace();
			System.err.println("Unable to find and load driver");
			this.log.err("Unable to find and load driver");
			this.log.exit();
			System.exit(1);
		}
		
	}
	/**
	 * Permet la connexion � la base de donn�es/
	 * @return connexion
	 */
	private Connection connectToBDD(){
		try {
			//System.err.println(this.pwd);
			this.connexion = DriverManager.getConnection(this.db,this.user,this.pwd);
			
		}
		catch(SQLException e) {
			displaySQLErrors(e);
			this.log.exit();
			System.exit(1);
		}
		return connexion;

	}
	/**
	 * Methode qui permet de faire des INSERT / UPDATE / DROP
	 * @param requete
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
	
	//La m�thode selectSQL est surcharg� pour recuperer le nombre d'element n�cessaire qui sont renvoy� dans un arrayList.
	/**
	 * Cette m�thode effectue une requete SQL renvoyant un seul �lement par r�sultat dans un ArrayList
	 * @param prState
	 * @param elem
	 * @return Une ArrayList contenant les diff�rents r�sultat de la requete
	 */
	public ArrayList selecSQL(PreparedStatement prState, ArrayList<String> elem){
		ArrayList<String> resultatSelect = null ;
		try {
			PreparedStatement prepState = prState; 
			ResultSet rs = prepState.executeQuery();
			int i=0;
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
					System.err.println("Erreur dans les �lements de la requete sql.");
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
	 * Permet de fermer la connexion � la BDD.
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
	 * @return the connexion
	 * @since 2.0.0
	 */
	public Connection getConnexion() {
		return connexion;
	}
	
}
