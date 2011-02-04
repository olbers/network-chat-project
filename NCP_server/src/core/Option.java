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
 * @version 1.1.3
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
	private BufferedReader optionFile;
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
	 * Variable log qui permet la gestion des message d'erreur.
	 * @since 1.1.0
	 */
	protected Log log;
	/**
	 * Variable qui indiquera si on log ou non le chat.
	 * @since 1.0.3
	 */
	protected boolean logChat;
	/**
	 * Variable qui indiquera si on protège le serveur des versions modifier du client.
	 * @since 1.1.1
	 */
	protected boolean protectMD5;
	/**
	 * Variable qui contient le hash md5 du client lourd.
	 * @since 1.1.1
	 */
	protected String lourdMD5;
	/**
	 * Variable qui contient le hash md5 du client Android.
	 * @since 1.1.1
	 */
	protected String AndroidMD5;
	/**
	 * Variable qui contient l'adresse du serveur smtp
	 * @since 1.1.2
	 */
	protected String smtp;
	/**
	 * Variable qui contient le login du serveur smtp
	 * @since 1.1.2
	 */
	protected String smtpLog;
	/**
	 * Variable qui contient le pass du serveur smtp
	 * @since 1.1.2
	 */
	protected String smtpMdp;
	/**
	 * Variable qui contient le string du mail Admin
	 */
	protected String adminMail;
	/**
	 * Variable qui contient le String du mail de l'inscription
	 */
	protected String inscriptionMail;

	/**
	 * Constructeur de la classe option. Mettra tout les paramètres à leur valeur par defauts.
	 * @throws IOException 
	 * @see Option#nb_client_max
	 * @see Option#port
	 * @see Option#test_mdp_max
	 * @see Option#protect_mdp_server
	 * @see Option#nameServer
	 * @see Option#mdp_server
	 * @see Option#dbMySQL
	 * @see Option#userMySQL
	 * @see Option#pwdMySQL
	 * @see Option#logChat
	 * @see Option#log
	 * @see Option#protectMD5
	 * @see Option#AndroidMD5
	 * @see Option#lourdMD5
	 * @see Option#optionFile
	 */

	public Option(Log log) {
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
		this.logChat=true;
		this.log=log;
		this.protectMD5=true;
		this.AndroidMD5="0";
		this.lourdMD5="0";
		this.smtp= "";
		this.smtpLog = "";
		this.smtpMdp = "";
		this.adminMail = "";
		this.inscriptionMail = "";
		try {
			this.optionFile = new BufferedReader(new FileReader("option.conf"));
			this.Recup(optionFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.err.println("Fichier option.conf Introuvable.");
			this.log.err("Fichier option.conf Introuvable.");
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
									if(resultOption.substring(0, 1).equalsIgnoreCase("=")&& resultOption.length()>1){ 
										System.err.println("Erreur sur la syntaxe à la ligne :"+ligne);
										this.log.err("Erreur sur la syntaxe à la ligne :"+ligne);
										//Gestion Si on veux traiter le problème.
										/*String modifResult = "";
										for(int i=0;i<(resultOption.length()-1);i++){
											modifResult=modifResult+resultOption.charAt(i+1);
										}
										resultOption=modifResult;*/
									}
								}
								if(resultOption!=null && option!=null){
									if(!resultOption.equalsIgnoreCase(option)){
										this.setOption(option, resultOption); //On appelle la méthode que va permettre de modifier les réglages.
									}
								}else{
									System.err.println("Erreur dans le fichier Option.conf à la ligne suivante:"+ligne);
									this.log.err("Erreur dans le fichier Option.conf à la ligne suivante:"+ligne);
								}
							}
							mot = null; option = null;resultOption=null; //raz des var
						}
					}
				}			
			}while(ligne!=null);
			this.optionFile.close();
			this.log.init(this);
		} catch (IOException e) {
			this.log.err("Impossible de traiter le fichier option.conf.");
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
			}else if (option.equalsIgnoreCase("logChat")){
				this.setLogChat(Boolean.parseBoolean(result));
			}else if (option.equalsIgnoreCase("protectMD5")){
				this.setProtectMD5(Boolean.parseBoolean(result));
			}else if (option.equalsIgnoreCase("lourdMD5")){
				this.setLourdMD5(result);
			}else if (option.equalsIgnoreCase("androidMD5")){
				this.setAndroidMD5(result);
			}else if (option.equalsIgnoreCase("smtp")){
				this.setSmtp(result);
			}else if (option.equalsIgnoreCase("smtpLog")){
				this.setSmtpLog(result);
			}else if (option.equalsIgnoreCase("smtpMdp")){
				this.setSmtpMdp(result);
			}else if (option.equalsIgnoreCase("adminMail")){
				this.setAdminMail(result);
			}else if (option.equalsIgnoreCase("InscriptionMail")){
				this.setInscriptionMail(result);
			}else {
				System.err.println("Erreur, l'option '"+option+"' est non reconnue.");
				this.log.err("Erreur, l'option '"+option+"' est non reconnue.");
			}			
		}
		//System.out.println(this.toString());
	}

	/**
	 * toString de la classe Option.
	 * @return un String de tout les options du fichier
	 */
	public String toString() {
		return "Option [port=" + port + ", nb_client_max=" + nb_client_max
				+ ", test_mdp_max=" + test_mdp_max + ", protect_mdp_server="
				+ protect_mdp_server + ", mdp_server=" + mdp_server
				+ ", nameServer=" + nameServer + ", optionFile=" + optionFile
				+ ", dbMySQL=" + dbMySQL + ", userMySQL=" + userMySQL
				+ ", pwdMySQL=" + pwdMySQL + ", log=" + log + ", logChat="
				+ logChat + ", protectMD5=" + protectMD5 + ", lourdMD5="
				+ lourdMD5 + ", AndroidMD5=" + AndroidMD5 + ", smtp=" + smtp
				+ ", smtpLog=" + smtpLog + ", smtpMdp=" + smtpMdp
				+ ", adminMail=" + adminMail + ", inscriptionMail="
				+ inscriptionMail + "]";
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
	/**
	 * Getter de la variable logChat
	 * @return vrai si on log le chat
	 * @since 1.0.2
	 */
	public boolean isLogChat() {
		return logChat;
	}
	/**
	 * Setter de la variable logChat
	 * @param logChat
	 * @since 1.0.2
	 */
	public void setLogChat(boolean logChat) {
		this.logChat = logChat;
	}
	/**
	 * @return the protectMD5
	 * @since 1.1.1
	 */
	public boolean isProtectMD5() {
		return protectMD5;
	}
	/**
	 * @param protectMD5 the protectMD5 to set
	 * @since 1.1.1
	 */
	public void setProtectMD5(boolean protectMD5) {
		this.protectMD5 = protectMD5;
	}
	/**
	 * @return the lourdMD5
	 * @since 1.1.1
	 */
	public String getLourdMD5() {
		return lourdMD5;
	}
	/**
	 * @param lourdMD5 the lourdMD5 to set
	 * @since 1.1.1
	 */
	public void setLourdMD5(String lourdMD5) {
		this.lourdMD5 = lourdMD5;
	}
	/**
	 * @return the androidMD5
	 * @since 1.1.1
	 */
	public String getAndroidMD5() {
		return AndroidMD5;
	}
	/**
	 * @param androidMD5 the androidMD5 to set
	 * @since 1.1.1
	 */
	public void setAndroidMD5(String androidMD5) {
		AndroidMD5 = androidMD5;
	}
	/**
	 * @return the smtp
	 */
	public String getSmtp() {
		return smtp;
	}
	/**
	 * @param smtp the smtp to set
	 */
	public void setSmtp(String smtp) {
		this.smtp = smtp;
	}
	/**
	 * @return the smtpLog
	 */
	public String getSmtpLog() {
		return smtpLog;
	}
	/**
	 * @param smtpLog the smtpLog to set
	 */
	public void setSmtpLog(String smtpLog) {
		this.smtpLog = smtpLog;
	}
	/**
	 * @return the smtpMdp
	 */
	public String getSmtpMdp() {
		return smtpMdp;
	}
	/**
	 * @param smtpMdp the smtpMdp to set
	 */
	public void setSmtpMdp(String smtpMdp) {
		this.smtpMdp = smtpMdp;
	}
	/**
	 * @return the adminMail
	 */
	public String getAdminMail() {
		return adminMail;
	}
	/**
	 * @param adminMail the adminMail to set
	 */
	public void setAdminMail(String adminMail) {
		this.adminMail = adminMail;
	}
	/**
	 * @return the inscriptionMail
	 * @since 1.1.3
	 */
	public String getInscriptionMail() {
		return inscriptionMail;
	}
	/**
	 * @param inscriptionMail the inscriptionMail to set
	 * @since 1.1.3
	 */
	public void setInscriptionMail(String inscriptionMail) {
		this.inscriptionMail = inscriptionMail;
	}
	
	
	
}
