package core;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
/**
 * 
 * Cette classe permet de faire la gestion des options du serveur NCP.
 * Incrementation de la classe:
 * Ajout/modification/supression d'option incrémentation du 3ème numéro.
 * Modification légère incrémentation du second numéro
 * Lourde modification modification du premier numéro
 * @author Kevin Poirier
 * @version 1.0.1
 * 
 *
 */

public class Option {

	/**
	 * La variable int port permet de choisir quel sera le port sur lequel le serveur sera a l'ecoute. Defaut:  port 1999.
	 */
	protected int port;
	/**
	 * La variable int nb_client_max permet de choisir quel sera le nombre maximun de connecter simultanement. Defaut : 20.
	 */
	protected int nb_client_max;
	/**
	 * La variable int test_mdp_max permet de choisir à partir de quel nombre d'essai des différent mots de passeavant bannissement de l'ip. Defaut: 3.
	 * du client sera banni.
	 */
	protected int test_mdp_max;
	/**
	 * Permet de choisir si le serveur sera proteger par mot de passe. Defaut : False.
	 */
	protected boolean protect_mdp_server;
	/**
	 * Permet de choisir le mot de passe du serveur.
	 */
	protected String mdp_server;
	/**
	 * Permet de choisir le nom du serveur. Defaut : NPCserver.
	 */
	protected String nameServer;	
	/**
	 * Variable qui contiendra le flux du fichier option.conf.
	 */
	protected BufferedReader optionFile;
	/**
	 * Variable qui contiendra l'addresse du serveur MySQL et la base de données a utiliser.
	 * @since 1.0.1
	 */
	protected String dbMySQL;
	/**
	 * Variable qui contiendra l'identifiant au serveur MySQL.
	 * @since 1.0.1
	 */
	protected String userMySQL;
	/**
	 * Variable qui contiendra le mot de passe pour la connexion au serveur MySQL.
	 * @since 1.0.1
	 */
	protected String pwdMySQL;

	/**
	 * Constructeur de la classe option. Mettra tout les paramètres à leur valeur par defauts.
	 * @throws IOException 
	 * @see Option#nb_client_max
	 * @see Option#port
	 * @see Option#test_mdp_max
	 * @see Option#protect_mdp_server
	 * @see Option#nameServer
	 * @see Option#mdp_server
	 * @see Option#optionFile
	 */

	public Option() {
		super();
		this.nb_client_max=20;
		this.port=1999;
		this.test_mdp_max=3;
		this.protect_mdp_server = false;
		this.nameServer = "NCPserver";
		this.mdp_server = null;
		this.dbMySQL="jdbc:mysql://localhost/NCP";
		this.userMySQL="root";
		this.pwdMySQL="";
		try {
			this.optionFile = new BufferedReader(new FileReader("option.conf"));
			this.Recup(optionFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Fichier option.conf Introuvable");
		}


	}
	/**
	 * Cette méthode permet de récuperer les options dans le fichier option.conf.
	 * @param  optionFile
	 * 
	 */
	//Gerer les exception correctement
	public void Recup(BufferedReader optionFile) {
		String option=null,resultOption=null,ligne,mot;
		StringTokenizer token;
		try {
			do{
				ligne = optionFile.readLine(); //On mets la ligne du fichier dans ligne.
				if(ligne!=null){ //Vérification que la ligne ne sois pas null
					token = new StringTokenizer(ligne);	
					if (token.hasMoreTokens()){//On vérifie qu'il reste encore des element dans token
						mot=token.nextToken();					
						if (!mot.substring(0, 1).equalsIgnoreCase("#")){ //On vérifie que la ligne ne sois pas un commentaire
							if (token.countTokens()>2){ //On vérifie que la ligne à bien 3 mots
								System.err.println("Erreur dans le fichier Option.conf à la ligne suivante:"+ligne);
							}else{
								option=mot; //On mets le premier mot dans option
								while(token.hasMoreTokens()){
									resultOption=token.nextToken(); //On garde le dernier mot de la ligne dans resultOption
								}
								if(resultOption!=null && option!=null){
									if(!resultOption.equalsIgnoreCase(option)){
										this.setOption(option, resultOption); //On appelle la méthode que va permettre de modifier les réglages.
									}
								}else{
									System.err.println("Erreur dans le fichier Option.conf à la ligne suivante:"+ligne);
								}
							}
							mot = null; option = null;resultOption=null; //raz des var
						}
					}
				}			
			}while(ligne!=null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Cette méthode permet de mettre à jours les option qui ne sont pas garder par défaut.
	 * 
	 * @param  option
	 * @param  result
	 */
	public void setOption(String option, String result){
		//gerer les différentes options.
		//System.out.println("Resultat "+option +" = "+result);
		if(!option.isEmpty()){
			if(option.equalsIgnoreCase("nb_client_max")){
				this.setNb_client_max(Integer.parseInt(result));
			}else if(option.equalsIgnoreCase("port")){
				this.setPort(Integer.parseInt(result));
			}else if(option.equalsIgnoreCase("test_mdp_max")){
				this.setTest_mdp_max(Integer.parseInt(result));
			}else if(option.equalsIgnoreCase("protect_mdp_server")){
				this.setProtect_mdp_server(Boolean.parseBoolean(result));
			}else if(option.equalsIgnoreCase("nameServer")){
				this.setNameServer(result);
			}else if(option.equalsIgnoreCase("mdp_server")){
				this.setMdp_server(result);
			}else if(option.equalsIgnoreCase("dbMySQL")){
				String jdbc="jdbc:";
				this.setDbMySQL(jdbc+result);
			}else if (option.equalsIgnoreCase("userMySQL")){
				this.setUserMySQL(result);
			}else if (option.equalsIgnoreCase("pwdMySQL")){
				this.setPwdMySQL(result);
			}else {
				System.err.println("Erreur, Option non reconnu");
			}			
		}
		System.out.println(this.toString());
	}
	/**
	 * toString de la classe Option.
	 * @return un String de tout les options du fichier
	 */
	public String toString() {
		return "Option [port=" + port + ", nb_client_max=" + nb_client_max
				+ ", test_mdp_max=" + test_mdp_max + ", protect_mdp_server="
				+ protect_mdp_server + ", mdp_server=" + mdp_server
				+ ", nameServer=" + nameServer + ", dbMySQL=" + dbMySQL
				+ ", userMySQL=" + userMySQL + ", pwdMySQL=" + pwdMySQL + "]";
	}
	/**
	 * Getter de la variable port.
	 * @return le port du serveur 
	 * @see Option#port
	 */
	public int getPort() {
		return port;
	}
	/**
	 * Setter de la variable port.
	 * @param port
	 * @see Option#port
	 */
	public void setPort(int port) {
		this.port = port;
	}
	/**
	 * Getter de la variable nb_client_max.
	 * @return le nombre de client max
	 * @see Option#nb_client_max
	 */
	public int getNb_client_max() {
		return nb_client_max;
	}
	/**
	 * Setter de la variable nb_client_max
	 * @param nb_client_max
	 * @see Option#nb_client_max
	 */
	public void setNb_client_max(int nb_client_max) {
		this.nb_client_max = nb_client_max;
	}
	/**
	 * Getter de la variable test_mdp_max
	 * @return le nombre d'essai autorise
	 * @see Option#test_mdp_max
	 */
	public int getTest_mdp_max() {
		return test_mdp_max;
	}
	/**
	 * Setter de la variable test_mdp_max
	 * @param test_mdp_max
	 * @see Option#test_mdp_max
	 */
	public void setTest_mdp_max(int test_mdp_max) {
		this.test_mdp_max = test_mdp_max;
	}
	/**
	 * Getter de la variable protect_mdp_server
	 * @return si le serveur est protege par un mot de passe
	 * @see Option#protect_mdp_server
	 */
	public boolean isProtect_mdp_server() {
		return protect_mdp_server;
	}
	/**
	 * Setter de la variable protect_mdp_server
	 * @param protect_mdp_server
	 * @see Option#protect_mdp_server
	 */
	public void setProtect_mdp_server(boolean protect_mdp_server) {
		protect_mdp_server = protect_mdp_server;
	}
	/**
	 * Getter de la variable mdp_server
	 * @return le mot de passe du serveur
	 * @see Option#mdp_server
	 */
	public String getMdp_server() {
		return mdp_server;
	}
	/**
	 * Setter de la variable mdp_server
	 * @param mdp_server
	 * @see Option#mdp_server
	 */
	public void setMdp_server(String mdp_server) {
		this.mdp_server = mdp_server;
	}
	/**
	 * Getter de la variable nameServer
	 * @return le nom du serveur
	 * @see Option#nameServer
	 */
	public String getNameServer() {
		return nameServer;
	}
	/**
	 * Setter de la variable nameServer
	 * @param nameServer
	 * @see Option#nameServer
	 */
	public void setNameServer(String nameServer) {
		this.nameServer = nameServer;
	}
	/**
	 * Getter de la variable dbMySQL
	 * @return l'adresse de la base de données
	 * @see Option#dbMySQL
	 * @since 1.0.1
	 */
	public String getDbMySQL() {
		return dbMySQL;
	}
	/**
	 * Setter de la variable dbMySQL
	 * @param dbMySQL
	 * @see Option#dbMySQL
	 * @since 1.0.1
	 */
	public void setDbMySQL(String dbMySQL) {
		this.dbMySQL = dbMySQL;
	}
	/**
	 * Getter de la variable userMySQL
	 * @return l'identifiant pour la connexion a la base de donnees.
	 * @see Option#userMySQL
	 * @since 1.0.1
	 */
	public String getUserMySQL() {
		return userMySQL;
	}
	/**
	 * Setter de la variable userMySQL
	 * @param userMySQL
	 * @see Option#userMySQL
	 * @since 1.0.1
	 */
	public void setUserMySQL(String userMySQL) {
		this.userMySQL = userMySQL;
	}
	/**
	 * Getter de la variable pwdMySQL
	 * @return Le mot de passe pour la connexion a la base de donnees.
	 * @see Option#pwdMySQL
	 * @since 1.0.1
	 */
	public String getPwdMySQL() {
		return pwdMySQL;
	}
	/**
	 * Setter de la variable pwdMySQL
	 * @param pwdMySQL
	 * @see Option#pwdMySQL
	 * @since 1.0.1
	 */
	public void setPwdMySQL(String pwdMySQL) {
		this.pwdMySQL = pwdMySQL;
	}
	
}
