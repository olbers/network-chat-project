package ncp_server.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ncp_server.core.client.Client;
import ncp_server.core.commande.CommandeClient;
import ncp_server.core.commande.CommandeUtilisateur;
import ncp_server.util.CountDown;
import ncp_server.util.DateString;
import ncp_server.util.Log;
import ncp_server.util.db.MySQL;
import ncp_server.util.db.RequeteSQL;
import ncp_server.util.mail.Mail;
import ncp_server.util.option.Option;
/**
 * Class Server, est la classe principale du serveur de chat NCP.
 * @author Poirier Kévin
 * @version 0.2.0.25
 *
 */

public class Server {

	public static final String version = "0.2.0.25";
	/**
	 * socketServer contiendra le socket du serveur qui permettra de se connecter au serveur.
	 */
	protected ServerSocket socketServer;
	/**
	 * socketCLient contiendra le socket du client.
	 */
	protected Socket socketClient;
	/**
	 * Permet de récupérer les flux venant des clients
	 */
	protected BufferedReader in;
	/**
	 * Permet d'envoyer des flux au clients.
	 */
	protected PrintWriter out;
	/**
	 * Permet de stocker les clients dans une Liste.
	 */
	protected ArrayList<Client> listClient;
	/**
	 * Objet qui permet la gestion des requetes avec la BDD.
	 * @see MySQL
	 */
	protected MySQL BDD;
	/**
	 * Objet qui permet la gestion des logs dans un fichier
	 * @see Log
	 */
	protected Log log;
	/**
	 * Objet qui permet de récuperer les options du fichier option.conf
	 * @see Option
	 */
	protected Option option;
	/**
	 * L'attribut autorisationConnexion permettra de bloquer ou non les connexion au serveur.
	 */
	protected boolean autorisationConnexion;
	/**
	 * L'attribut client permet de garder un client.
	 */
	public Client client;
	/**
	 * L'attribut connexion permet de gerer le thread qui gère les connexions des clients au serveur.
	 */
	public ThreadConnexion connexion;
	/**
	 * L'attribut requeteSQL permet de gerer la class qui gère les requetes SQL.
	 */
	protected RequeteSQL requeteSQL;

	private static Server instance;

	protected CommandeClient comCli;

	protected CommandeUtilisateur commUser;

	protected ArrayList<String[]> listBanIP;

	protected CountDown countDown;

	protected boolean verifSQl;

	protected int countRessource;

	protected Supervisor supervisor;

	/**
	 * Constructeur de la class Server.
	 * @param log
	 * @param option
	 */
	private Server(){
		super();
		this.log=Log.getInstance();
		this.option=Option.getInstace();
		this.listClient = new ArrayList<Client>();
		this.autorisationConnexion=true;
		this.BDD=MySQL.getInstance();
		this.requeteSQL= RequeteSQL.getInstance();
		this.verifSQl=false;
		this.countRessource=1;
		this.listBanIP = new ArrayList<String[]>();
		this.updateListBanIP();
	}
	/**
	 * Methode singleton qui permet d'assurer une seul instance de la classe.
	 * @return instance
	 */
	public static Server getInstance(){
		if(null == instance)
			instance=new Server();		
		return instance;
	}	
	/**
	 * Permet d'activer un client.
	 * @param client
	 */
	public void activationClient(Client client){
		if(client.isChMD5()){
			client.setActiver(true);
			this.envoieATous("#"+client.getPseudo()+" vient de se connecter.");
		}
	}
	/**
	 * Methode qui envoie au clients la liste de tout les connectés.
	 */
	public void affichListClient(){
		StringBuffer listeDePseudo = new StringBuffer();
		listeDePseudo.append("$");
		for(int i=0;i<this.listClient.size();i++){
			if(this.listClient.get(i).isActiver()){
				if(this.isAdmin(this.listClient.get(i)))
					listeDePseudo.append("@");
				else if(this.isModerateur(this.listClient.get(i)))
					listeDePseudo.append("&");

				listeDePseudo.append(this.listClient.get(i).getPseudo()+"|");
			}
		}
		this.envoieATous(listeDePseudo.toString());
	}

	/**
	 * La méthode ajoutClient permet de créer un client et de l'ajouter dans la liste.
	 * @param socketClient
	 */
	public Client ajoutClient(Socket socketClient){
		this.client = new Client(this.getListClient().size(), "Anonymous"+this.getListClient().size()+1,
				socketClient, this.createIn(socketClient), this.createOUT(socketClient));
		this.listClient.add(client);
		return client;
	}
	/**
	 * La méthode clientConnexion permet d'effectuer la connexion d'un client.
	 * Cette methode sera appeller par notre ThreadConnexion.
	 * @see ThreadConnexion
	 */
	public void clientConnexion(){
		if(!this.socketServer.isClosed()){
			try {
				this.socketClient = socketServer.accept();	
				this.verificationConnexion(this.socketClient);
			} catch (IOException e) {
				this.log.err("Socket Fermer");
			}
		}
	}
	/**
	 * Permet d'indiquer au client qu'il va être déconnecter.
	 * @param client
	 */
	public void clientDeconnexion(Client client){
		this.envoiePrive(client, "&deconnexion");
		client.closeClient();
		this.listClient.remove(client);
		this.affichListClient();
	}
	public void clientDeconnexionBoucle(Client client){
		this.envoiePrive(client, "&deconnexion");
		client.closeClient();
	}
	/**
	 * Deconnexion demandé par utilisateuré
	 * @param client
	 */
	public void deconnexionUtilisateur(Client client){
		client.closeClient();
		this.listClient.remove(client);
		if(client.isActiver()){
			this.envoieATous("#"+client.getPseudo()+ " vient de se déconnecter.");
			this.affichListClient();
		}
	}

	/**
	 * Creer le flux d'entre pour le client.
	 * @param socketClient
	 * @return the in
	 */
	public BufferedReader createIn(Socket socketClient){
		try {
			this.in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			return this.in;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			System.err.println("Erreur lors de la creation du flux d'entre pour le client");
			this.log.err("Erreur lors de la creation du flux d'entre pour le client");
		}
		return null;
	}
	/**
	 * Creer le flux de sortie pour le client.
	 * @param socketClient
	 * @return the out
	 */
	public PrintWriter createOUT(Socket socketClient){
		try {
			this.out=new PrintWriter(socketClient.getOutputStream());
			return this.out;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			System.err.println("Erreur lors de la creation du flux de sortie pour le client");
			this.log.err("Erreur lors de la creation du flux de sortie pour le client");
		}
		return null;
	}
	/**
	 * Cette méthode permet de creer le serveur, via le ServerSocket.
	 */
	public void createServer(){
		try {
			this.socketServer= new ServerSocket(this.option.getPort());
			System.out.println("[OK]");
			this.initCommandes();						
			this.connexion= new ThreadConnexion();
			this.connexion.start();
			this.supervisor=Supervisor.getInstance();
			this.supervisor.start();
			System.out.println(this.option.getNameServer()+" est lance, et est a l'ecoute sur le port : "+this.option.getPort());
		}catch (IOException e) {
			System.err.println("[FAIL]");
			this.log.err("Impossible de créer le serveur.");
			System.err.println("Impossible de créer le serveur.");
			System.err.println(e);
			//e.printStackTrace();
			System.exit(1);
		}		
	}
	/**
	 *Permet d'initialiser les différent système de commandes.
	 */
	public void initCommandes(){
		this.comCli = CommandeClient.getInstance();
		this.commUser = CommandeUtilisateur.getInstance();
	}
	/**
	 * Permet de stopper le serveur
	 */
	public void stopServer(boolean kill){
		this.supervisor.setRun(false);
		this.connexion.setAuthCo(false);
		this.decoAllClient();	
		this.BDD.closeBDD();
		this.connexion.interrupt();
		this.supervisor.interrupt();
		this.countDown.interrupt();
		try {
			if(!this.socketServer.isClosed())
				this.socketServer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			this.log.err("Problème lors de la fermeture du socket Server.");
			if(kill)
				System.exit(1);
		}
		if(kill){
			System.out.println("Coupure du serveur !!");
			System.exit(0);
		}
	}
	/**
	 * Permet de redémarrer le serveur.
	 */
	public void restartServer(){
		this.stopServer(false);
		boolean isJar=false;
		File jarFile=null;

		try {			
			jarFile = new File(ncp_server.core.Server.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			this.log.err(e.toString());
		}

		if ( jarFile!=null &&jarFile.getName().endsWith(".jar") )  
			isJar=true;

		if(isJar){
			System.out.println("Redémarrage du serveur !!");
			System.exit(5); //5 renvoie une erreur qui permet de relancer le serveur.
		}else{
			System.out.println("Redémarrage impossible, le fichier n'est pas un jar.");
		}

	}
	/**
	 * Lancement du thread de compte à rebours.
	 * @param compteur
	 * @param restart
	 */
	public void procedureRestartorStop(int compteur, boolean restart, String pseudo){
		if(restart){
			this.log.chat("Redémarrage du serveur dans "+compteur+" secondes, demandé par "+pseudo);
			System.out.println("Redémarrage du serveur dans "+compteur+" secondes, demandé par "+pseudo);
		}else{
			this.log.chat("Coupure du serveur dans "+compteur+" secondes, demandé par "+pseudo);
			System.out.println("Coupure du serveur dans "+compteur+" secondes, demandé par "+pseudo);
		}

		this.countDown = new CountDown(compteur, restart);
		this.countDown.start();
	}

	/**
	 * Permet de déconnecter tout les clients.
	 */
	public void decoAllClient(){
		for(int i=0; i<this.listClient.size();i++){
			this.clientDeconnexionBoucle(this.listClient.get(i));
		}
	}
	/**
	 * La methode envoieATous permet d'envoyer les messages à tout les clients connecter.
	 * @param message
	 */
	public void envoieATous(String message){
		for(int i=0;i<this.listClient.size();i++){
			if(this.listClient.get(i).isActiver()){
				//System.out.println(message);
				this.listClient.get(i).getOut().println(message);
				this.listClient.get(i).getOut().flush();
			}
		}
	}
	/**
	 * La methode envoiePrive permet d'envoyer les messages a un client connecté.
	 * @param client
	 * @param message
	 */
	public void envoiePrive(Client client, String message){
		//System.out.println(message);
		client.getOut().println(message);
		client.getOut().flush();
	}
	/**
	 * Permet d'envoyer un message juste au administrateur et au moderateur.
	 * @param message
	 */
	public void envoieAdminModo(String message){
		for(int i=0;i<this.listClient.size();i++){
			if(this.isAdmin(this.listClient.get(i)) || this.isModerateur(this.listClient.get(i)) ){
				this.envoiePrive(this.listClient.get(i), message);
			}
		}
	}

	/**
	 * Permer de recuperer les argument qui ont été envoyer.
	 * @param chaine
	 * @param nbArgument
	 * @return Tableau de String
	 */
	public String[] recupArgument(String chaine, int nbArgument){
		String[] argument = new String[nbArgument];
		StringTokenizer token;
		token = new StringTokenizer(chaine);
		for (int i=0;i<nbArgument;i++){
			if (token.hasMoreElements()){
				argument[i]=token.nextToken();
			}else{
				argument[i]="";
			}			
		}		
		return argument;
	}
	/**
	 * La methode recupChaine permet de recuperer les chaines de caracteres envoyer par les clients.
	 * @param in
	 * @param client
	 */
	public void recupChaine(BufferedReader in,Client client){
		String chaineRecu= null;
		try {
			if(in.ready()){
				chaineRecu = in.readLine();;
				if (chaineRecu!=null && !chaineRecu.equals("")){
					this.traitementChaine(chaineRecu,client);
					client.setLastMessage(System.currentTimeMillis());//Mise à jour du dernier message
					client.setCompteurMSG(this.client.getCompteurMSG()+1); //Mise à jour du compteur de message
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Cette methode permet de supprimer le premier caracteres qui permet de distinguer les commandes.
	 * @param chaine
	 * @return chaineModif
	 */
	public String suppr1Car(String chaine){
		StringBuffer chaineModif=new StringBuffer();
		for(int i=0;i<(chaine.length()-1);i++){
			chaineModif.append(chaine.charAt(i+1));
		}		
		return chaineModif.toString();
	}
	/**
	 * La methode traitementChaine permet de traiter les chaines de caracteres envoyer par les clients.
	 * @param chaine
	 */
	public void traitementChaine(String chaine,Client client){
		if(!chaine.isEmpty()|| !"".equals(chaine)){
			if (chaine.substring(0,1).equalsIgnoreCase("@")){
				this.comCli.traitementCommande(this.inhibHTLM(this.suppr1Car(chaine)),client);
			}else if (chaine.substring(0,1).equalsIgnoreCase("/")){
				this.commUser.traitementCommande(this.inhibHTLM(this.suppr1Car(chaine)),client);
			}else if (chaine.substring(0,1).equals("~")) {
				this.traitementMessageAll(this.inhibHTLM(this.suppr1Car(chaine)), client);
			}
		}
	}
	/**
	 * Cette methode permet de gerer l'envoi des messages à tout les clients.
	 * @param chaine
	 * @param client
	 */
	public void traitementMessageAll(String chaine,Client client){
		if(client.isActiver()){
			String message,messageToLog;
			messageToLog=client.getPseudo() + ": "+chaine;
			this.log.chat(messageToLog);
			message="*"+new DateString().dateChat() +" "+ messageToLog;
			this.envoieATous(message);
		}
	}
	/**
	 * La méthode verificationConnexion permet de verifier que les connexion sont autorisées.
	 * @param socketClient
	 */
	public void verificationConnexion(Socket socketClient){
		Client client;
		client=this.ajoutClient(socketClient);
		if(this.isAutorisationConnexion()){
			this.envoiePrive(client, "&verif");
			if(this.ipIsBan(client.getSocketClient().getInetAddress().toString())){
				this.envoiePrive(client, "b");//ip banni
				this.clientDeconnexion(client);
			}else{
				client.createThread();
				client.startThread();
			}
		}else{
			this.envoiePrive(client, "7");
			this.clientDeconnexionBoucle(client);
		}
	}
	/**
	 * Permet de verifier l'existance d'un compte
	 * @param compte
	 * @return boolean
	 */
	public boolean existCompte(String compte){
		ArrayList<String> testExist = this.requeteSQL.verifClient(compte);
		if(testExist != null){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Permet de verifier si un pseudo est déjà connecté
	 * @param pseudo
	 * @return boolean
	 */
	public boolean pseudoCo(String pseudo){
		for (int i=0;i<this.getListClient().size();i++){
			if (pseudo.equalsIgnoreCase(this.getListClient().get(i).getPseudo())){
				return true;
			}
		}
		return false;
	}
	/**
	 * Permet de verifier si le pseudo correspond avec le mdp.
	 * @param compte
	 * @param mdp
	 * @return boolean
	 */
	public boolean verifPseudoMDP(String compte, String mdp){
		ArrayList<String> resultCompte = this.requeteSQL.connexionClient(compte, mdp);
		if(resultCompte!=null){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Permet de verifier si le compte rechercher est co
	 * @param bddID
	 * @return boolean
	 */
	public boolean compteCo(int bddID){
		for (int i=0;i<this.getListClient().size();i++){
			if (bddID==this.getListClient().get(i).getBddID()){
				return true;
			}					
		}
		return false;
	}
	/**
	 * Permer de verifier si un client est administrateur
	 * @param client
	 * @return boolean
	 */
	public boolean isAdmin(Client client){
		if(client.getLvAccess()==2){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Permet de verifier si un client est moderateur
	 * @param client
	 * @return boolean
	 */
	public boolean isModerateur(Client client){
		if(client.getLvAccess()==1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Permet de verifier si un client est banni
	 * @param client
	 * @return boolean
	 */
	public boolean isBan(Client client){
		if(client.getLvAccess()==-1){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * Permet d'envoyer un message si l'utilisateurs n'a pas les droits suffissants.
	 * @param client
	 */
	public void commandeRefuse(Client client){
		this.envoiePrive(client, "#Vous n'avez pas les droits suffisants pour effectuer cette commande.");
	}
	/**
	 * Permet d'informer que l'utilisateur n'est pas en ligne
	 * @param pseudo
	 * @param client
	 */
	public void pseudoNonCo(String pseudo,Client client){
		this.envoiePrive(client, "#L'utilisateur "+pseudo+" n'est pas en ligne.");
	}
	/**
	 * 
	 * @param pseudo
	 * @return Client
	 */
	public Client getClient(String pseudo){
		for(int i=0;i<this.listClient.size();i++){
			if(this.listClient.get(i).getPseudo().equalsIgnoreCase(pseudo)){
				return this.listClient.get(i);
			}
		}		
		return null;
	}
	/**
	 * Methode pour kick un client
	 * @param clientAKicker
	 * @param kicker
	 * @param raison
	 */
	public void kick(Client clientAKicker,String kicker,String raison){
		if(!isAdmin(clientAKicker)){
			if(raison.isEmpty()|| raison.equalsIgnoreCase(""))
				this.envoieATous("#"+clientAKicker.getPseudo() + " a été kick par "+kicker+".");
			else
				this.envoieATous("#"+clientAKicker.getPseudo() + " a été kick par "+kicker+"(Raison: "+raison+").");		
			this.clientDeconnexion(clientAKicker);
		}else{
			if(!kicker.equalsIgnoreCase("Système")){
				this.envoiePrive(this.getClient(kicker), "#On ne kick pas un administrateur");
			}
		}
	}
	/**
	 * Permet de bannir un compte.
	 * @param clientABan
	 * @param banniseur
	 * @param raison
	 */
	public void ban(Client clientABan,Client banniseur,String raison ){
		if(!isAdmin(clientABan)){
			if(raison.isEmpty()||raison.equalsIgnoreCase(""))
				this.envoieATous("#"+clientABan.getPseudo() + " a été banni par "+banniseur.getPseudo()+".");
			else
				this.envoieATous("#"+clientABan.getPseudo() + " a été banni par "+banniseur.getPseudo()+"(Raison: "+raison+").");
			this.clientDeconnexion(clientABan);
			this.requeteSQL.updatelvAccess(clientABan.getBddID(), -1);
		}else{
			this.envoiePrive(banniseur, "#On ne banni pas un administrateur.");
		}
	}
	/**
	 * Permet de débannir un compte. Attention la vérification de l'existence du compte doit être fait en amont.
	 * @param compte
	 * @param client
	 */
	public void unban(String compte,Client client){
		int id = Integer.parseInt(this.requeteSQL.getBDDID(compte).get(0));
		if(Integer.parseInt(this.requeteSQL.getlvAccess(compte).get(0))==-1){
			this.requeteSQL.updatelvAccess(id, 0);
			this.envoiePrive(client, "#Le compte: "+compte+" n'est désormains plus banni.");
		}else{
			this.envoiePrive(client, "#Le compte: "+compte+" n'est pas banni.");
		}

	}
	/**
	 * Permet de retirer toute les balises html sauf <b> et <i>
	 * @param message
	 * @return String
	 */
	public String inhibHTLM(String message){
		String mot;
		int i;
		boolean ecrit;
		boolean isExistINF;
		StringBuffer phrase = new StringBuffer();
		StringBuffer motInhib = new StringBuffer();
		StringTokenizer token = new StringTokenizer(message);

		while(token.hasMoreTokens()){
			ecrit=true;
			isExistINF=false;
			mot=token.nextToken();
			//Vérifie que le mot contient le charactère > et <
			if(mot.contains("<") && mot.contains(">")){
				//vérifie que la phrase contient les balises autorisé.
				if(mot.contains("<b>") || mot.contains("</b>") || mot.contains("<i>")||
						mot.contains("</i>") || mot.contains("<s>") || mot.contains("</s>")){
					phrase.append(mot+" ");
				}else{
					//Permet d'afficher les char si il ne sont pas des balises même si ils contiennent < et >
					motInhib.setLength(0);
					for (i=0;i<mot.length();i++){							
						if (mot.charAt(i)=='>')
							isExistINF=true;

						if (mot.charAt(i)=='<'&& !isExistINF)
							ecrit=false;
						else if(!ecrit && mot.charAt(i-1)=='>')
							ecrit=true;						

						if(ecrit){
							motInhib.append(mot.charAt(i));
						}					
					}
					if(motInhib.length()!=0)
						phrase.append(motInhib.toString()+" ");	
				}				
			}else{				
				phrase.append(mot+" ");
			}
		}
		return phrase.toString();
	}
	/**
	 * Permet de mettre à jour la liste des ip banni.
	 */
	public void updateListBanIP(){
		ArrayList<String> result = this.requeteSQL.getBanIP();
		int i;
		this.listBanIP.clear();
		if(result!=null){
			for(i=0;i<result.size();i++){
				this.listBanIP.add(i, this.recupArgument(result.get(i), 2));
			}
		}
	}
	/**
	 * Permet de savoir si une ip est banni
	 * @param ip
	 * @return boolean
	 */
	public boolean ipIsBan(String ip){
		for (int i = 0; i<this.listBanIP.size();i++){
			if(this.listBanIP.get(i)[0].equalsIgnoreCase(ip)){
				return true;
			}
		}
		return false;
	}
	/**
	 * Recupère un client si il y'a une concordance avec l'ip.
	 * @param ip
	 * @return Client
	 */
	public Client clientIP(String ip){
		for(int i = 0; i<this.listClient.size();i++){
			if(this.listClient.get(i).getIp().equalsIgnoreCase(ip)){
				return this.listClient.get(i);
			}
		}
		return null;
	}
	/**
	 * Permet de bannir une ip. Les vérification doivent avoir été faite avant.
	 * @param ip
	 * @param tempsBan
	 */
	public void banIP(String ip, int tempsBan, String banisseur){
		this.requeteSQL.insertBanIP(ip, new Timestamp(System.currentTimeMillis()+tempsBan));
		Client clientKick=this.clientIP(ip);
		if(clientKick!=null){
			this.kick(clientKick, banisseur, "Ip Banni");
		}
		this.updateListBanIP();
	}
	/**
	 * Permet de retirer des ip banni. Attention la vérification que l'ip est banni doit être fait avant.
	 * @param ip
	 */
	public void unBanIP(String ip, boolean updateList){
		this.requeteSQL.delBanIP(ip);
		if(updateList)
			this.updateListBanIP();
	}
	/**
	 * Methode qui retourne le nombre total de clients actifs.
	 * @return int
	 */
	public int totalClient(){
		int compteur=0;
		for (int i=0;i<this.getListClient().size();i++){
			if(this.getListClient().get(i).isActiver())
				compteur++;
		}
		return compteur;
	}
	/**
	 * Permet de modifier le niveau d'accès d'un utilisateur.
	 * @param clientModif
	 * @param clientChangeur
	 * @param newLvAcess
	 */
	public void updateLvAcess(Client clientModif, Client clientChangeur, int newLvAcess){
		boolean lvChange=false;
		if(newLvAcess==-1){
			this.envoiePrive(clientChangeur,"#Il existe une commande pour bannir, merci de ne pas utiliser cette commande à cette fin.");
		}else if(newLvAcess>2 && newLvAcess<-1){
			this.envoiePrive(clientChangeur,"#Erreur de niveau d'accès. 0=Utilisateur normal, 1=Moderateur, 2=Administrateur");
		}else{
			if(clientModif.getBddID()!=0){
				if(newLvAcess==clientModif.getLvAccess()){
					this.envoiePrive(clientChangeur, "#Le nouveau niveau d'accès est identique à l'actuelle.");
				}else{
					if(clientModif.getLvAccess()==2){
						this.envoiePrive(clientChangeur, "#On ne modifie pas les droits d'un administrateurs.");
					}else if(clientModif.getLvAccess()==1){
						if(newLvAcess==0){
							this.envoiePrive(clientModif, "#Vous avez été rétrogradé au rang d'utilisateur normal.");
							lvChange=true;
						}else if(newLvAcess==2){
							this.envoiePrive(clientModif, "#Vous avez été promu au rang d'administrateur.");
							lvChange=true;
						}
					}else if(clientModif.getLvAccess()==0){
						if(newLvAcess==1){
							this.envoiePrive(clientModif, "#Vous avez été promu au rang de modérateur.");
							lvChange=true;
						}else if(newLvAcess==2){
							this.envoiePrive(clientModif, "#Vous avez été promu au rang d'administrateur.");
							lvChange=true;
						}
					}
				}			
			}else{
				this.envoiePrive(clientChangeur, "#Le client n'a pas de compte.");
				this.envoiePrive(clientModif, "#L'administrateur à voulu vous donner des droits, mais vous n'êtes pas enregistré.");
			}
		}
		if(lvChange){
			clientModif.setLvAccess(newLvAcess);
			this.requeteSQL.updatelvAccess(clientModif.getBddID(), newLvAcess);
			this.envoiePrive(clientChangeur,"#La modification à bien été prise en compte.");
			this.envoiePrive(clientModif,"#Pour que la modification sois prise en compte veuillez vous reconnecter.");
		}				
	}

	/**
	 * Permet d'effacer les ip banni qui ont expiré le delais.
	 */
	public void cleanBanIP(){
		for (int i = 0; i <this.listBanIP.size(); i++){
			if(Long.parseLong(this.listBanIP.get(i)[1])<=System.currentTimeMillis()){
				this.unBanIP(this.listBanIP.get(i)[0],false);
			}
		}
		this.updateListBanIP();
	}
	/**
	 * Permet de nettoyer la liste des utilisateur non activer.
	 */
	public void cleanListClient(){
		long timeLastMSG=0;
		if(this.countRessource>=1 && this.countRessource<=2){
			timeLastMSG=30000;
		}else{
			timeLastMSG=15000;
		}		
		for (int i = 0; i <this.listClient.size(); i++){
			if((System.currentTimeMillis()-this.listClient.get(i).getLastMessage())>=timeLastMSG && 
					!this.listClient.get(i).isActiver()){//Dernier message + de 1 min et client non activer
				this.clientDeconnexionBoucle(this.listClient.get(i));
			}
		}
	}

	/**
	 * Methode qui permet l'antiflood
	 */
	public void antiFlood(){
		for (int i=0;i<this.listClient.size();i++){
			if(this.listClient.get(i).getCompteurMSG()>=5)
				this.kick(this.listClient.get(i), "Système", "Flood !!");
			else
				this.listClient.get(i).setCompteurMSG(0);				
		}
	}
	/**
	 * Permet de kick les afk.
	 */
	public void antiAFK(){
		for (int i=0;i<this.listClient.size();i++){
			if(this.listClient.get(i).isActiver() && 
					(System.currentTimeMillis()-this.listClient.get(i).getLastMessage()>=1800000)){
				this.kick(this.listClient.get(i), "Système", "AFK...");
			}
		}
	}
	/**
	 * Permet de surveiller la connection de la base SQL, sinon reboot du serveur.
	 */
	public void chSQL(){
		try {
			if(this.BDD.getConnexion().isClosed()){
				if(this.verifSQl){
					System.out.println("Probleme avec la connection SQL, restart du serveur");
					this.log.err("Probleme avec la connection SQL, restart du serveur");
					this.procedureRestartorStop(60, true, "Système");
					this.mailError("Erreur avec la BDD", this.supervisor.updateCPUusage(), this.supervisor.updateFreeRam(),this.supervisor.updateMemoryJVM());
				}else{
					this.verifSQl=true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			this.log.err(e.toString());
		}		
	}
	/**
	 * Permet de surveiller la charge de la machine hôte.
	 * @param chargeCPU
	 * @param ramRest
	 */
	public void checkRessource(double chargeCPU, double ramRest, double jvmRAM){
		boolean overLoad=false;
		if(chargeCPU>0.9 || ramRest<10){
			overLoad=true;
		}else{
			this.countRessource=1;
			if(!this.isAutorisationConnexion()){
				System.out.println("Nouvelle connexion prise à nouveau en charge");
				this.envoieAdminModo("#Nouvelle connexion prise à nouveau en charge");
				this.log.err("Nouvelle connexion non prise à nouveau en charge");
				this.setAutorisationConnexion(true);
				this.connexion.setAuthCo(true);
			}
		}
		if(overLoad){
			if(this.countRessource==1 || this.countRessource==2)
				this.countRessource++;
			else if(this.countRessource==3){
				this.cleanListClient();
				this.envoieAdminModo("#Surcharge serveur : Nouvelle connexion non prise en charge");
				System.out.println("Nouvelle connexion non prise en charge");
				this.log.err("Nouvelle connexion non prise en charge");
				this.setAutorisationConnexion(false);
				this.connexion.setAuthCo(false);
				this.countRessource++;
			}else if(this.countRessource==4){
				this.antiAFK();
				this.countRessource++;
			}else if(this.countRessource==5){
				this.procedureRestartorStop(30, true, "Système");
				String message = "Un problème de surcharge à été détecté sur le serveur";
				this.mailError(message,chargeCPU,ramRest,jvmRAM);
				System.out.println("Probleme de charge sur le serveur");
				this.log.err("Probleme de charge sur le serveur");
			}

		}
		if(jvmRAM>40){
			if(!this.connexion.isAuthCo()){
				this.connexion.setAuthCo(true);
				this.envoieAdminModo("#Sortie du mode de protection de la JVM.");
				System.out.println("Réactivation de la connexion via socket.");
				this.log.err("Réactivation de la connexion via socket.");
			}			
		}else if(jvmRAM<40){
			if(this.connexion.isAuthCo()){
				this.connexion.setAuthCo(false);
				this.mailError("Mise en mode protection de la JVM", chargeCPU, ramRest, jvmRAM);
				this.envoieAdminModo("#Surcharge de la JVM: Mise en mode protection");
				System.out.println("Un problème de surcharge sur la JVM à été détecté sur le serveur");
				System.out.println("Désactivation de la connexion via socket.");
				this.log.err("Désactivation de la connexion via socket.");
			}
		}
		if(jvmRAM<20){
			this.procedureRestartorStop(30, true, "Système");
			String message = "Un problème de surcharge sur la JVM à été détecté sur le serveur";
			this.mailError(message,chargeCPU,ramRest,jvmRAM);
			System.out.println("Probleme de charge sur la JVM du serveur");
			this.log.err("Probleme de charge sur la JVM du serveur");
		}

	}
	/**
	 * Permet d'envoyer un rapport d'erreur au admins
	 * @param error
	 * @param chargeCPU
	 * @param ramRest
	 */
	public void mailError(String error,double chargeCPU, double ramRest, double jvmRAM){
		DecimalFormat df = new DecimalFormat("########.000");
		String CPU=df.format(chargeCPU*100);
		String ram=df.format(ramRest);
		String jvm=df.format(jvmRAM);
		String booBDD;
		try {
			booBDD=""+this.BDD.getConnexion().isClosed();
		} catch (SQLException e) {	
			booBDD="Error";
		}
		String rapport = error+",\n" +
		"\nInformation Hôte \n"+
		"\nSystème d'exploitation hôte : "+System.getProperty("os.name")+
		"\nArchitecture sytème d'exploitation : "+System.getProperty("os.arch")+
		"\nVersion sytème d'exploitation : "+System.getProperty("os.version")+
		"\n\n\nJRE \n"+
		"\nVersion du JRE : "+System.getProperty("java.version")+
		"\nPourcentage de mémoire utilisé de la JVM : "+jvm+
		"\n\n\nUtilisation Ressources\n"+
		"\nCharge CPU : "+CPU+
		"\nPourcentage de ram Disponible : "+ram+
		"\n\n\nInformation NCP Server\n"+
		"\nVersion du serveur NCP : "+Server.version+
		"\nLocalisation des fichiers du serveur : "+System.getProperty("user.dir")+
		"\nNombre de Client actif : "+this.totalClient()+
		"\nNombre total de client : "+this.listClient.size()+
		"\nBDD déconnecté : "+booBDD;
		new Mail().errorAdminMail(rapport);

	}

	/**
	 * @return the bDD
	 */
	public MySQL getBDD() {
		return BDD;
	}
	/**
	 * @return the in
	 */
	public BufferedReader getIn() {
		return in;
	}
	/**
	 * @return the listClient
	 */
	public ArrayList<Client> getListClient() {
		return listClient;
	}
	/**
	 * @return the log
	 */
	public Log getLog() {
		return log;
	}
	/**
	 * @return the option
	 */
	public Option getOption() {
		return option;
	}
	/**
	 * @return the out
	 */
	public PrintWriter getOut() {
		return out;
	}

	/**
	 * @return the socketClient
	 */
	public Socket getSocketClient() {
		return socketClient;
	}
	/**
	 * @return the socketServer
	 */
	public ServerSocket getSocketServer() {
		return socketServer;
	}
	/**
	 * @return the autorisationConnexion
	 */
	public boolean isAutorisationConnexion() {
		return autorisationConnexion;
	}
	/**
	 * @param in the in to set
	 */
	public void setIn(BufferedReader in) {
		this.in = in;
	}
	/**
	 * @param out the out to set
	 */
	public void setOut(PrintWriter out) {
		this.out = out;
	}
	/**
	 * @param autorisationConnexion the autorisationConnexion to set
	 */
	public void setAutorisationConnexion(boolean autorisationConnexion) {
		this.autorisationConnexion = autorisationConnexion;
	}
	/**
	 * @return the supervisor
	 */
	public Supervisor getSupervisor() {
		return supervisor;
	}


}
