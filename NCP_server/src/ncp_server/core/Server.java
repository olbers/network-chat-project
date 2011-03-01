package ncp_server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ncp_server.core.client.Client;
import ncp_server.core.commande.CommandeClient;
import ncp_server.core.commande.CommandeUtilisateur;
import ncp_server.util.DateString;
import ncp_server.util.Log;
import ncp_server.util.db.MySQL;
import ncp_server.util.db.RequeteSQL;
import ncp_server.util.option.Option;
/**
 * Class Server, est la classe principale du serveur de chat NCP.
 * @author Poirier Kévin
 * @version 0.2.0.7
 *
 */

public class Server {

	public static final String version = "0.2.0.7";
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

	protected ArrayList<String[]> ListBanIP;

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
		this.ListBanIP = new ArrayList<String[]>();
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
			if(this.listClient.get(i).isActiver())
				if(this.isAdmin(this.listClient.get(i)))
					listeDePseudo.append("@");
				else if(this.isModerateur(this.listClient.get(i)))
					listeDePseudo.append("&");
				
				listeDePseudo.append(this.listClient.get(i).getPseudo()+"|");
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
		try {
			this.socketClient = socketServer.accept();			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println("Erreur lors de la connexion d'un client.");
			this.log.err("Erreur lors de la connexion d'un client.");
		}	
		this.verificationConnexion(this.socketClient);
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
	public void deconnexionUtilisateur(Client client){
		client.closeClient();
		this.listClient.remove(client);
		this.envoieATous("#"+client.getPseudo()+ " vient de se déconnecter.");
		this.affichListClient();
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
			e.printStackTrace();
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
			e.printStackTrace();
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
			System.out.println(this.option.getNameServer()+" est lance, et est a l'ecoute sur le port : "+this.option.getPort());			
			this.connexion= new ThreadConnexion();
			this.connexion.start();
		} catch (IOException e) {
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
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * @param autorisationConnexion the autorisationConnexion to set
	 */
	public void setAutorisationConnexion(boolean autorisationConnexion) {
		this.autorisationConnexion = autorisationConnexion;
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
			client.createThread();
			client.startThread();
			if(this.ipIsBan(client.getSocketClient().getInetAddress().toString())){
				this.envoiePrive(client, "b");//ip banni
				this.clientDeconnexion(client);
			}
		}else{
			this.envoiePrive(client, "7");
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
		if(raison.isEmpty()|| raison.equalsIgnoreCase(""))
			this.envoieATous("#"+clientAKicker.getPseudo() + " a été kick par "+kicker+".");
		else
			this.envoieATous("#"+clientAKicker.getPseudo() + " a été kick par "+kicker+"(Raison: "+raison+").");		
		this.clientDeconnexion(clientAKicker);
	}
	/**
	 * Permet de bannir un compte.
	 * @param clientABan
	 * @param banniseur
	 * @param raison
	 */
	public void ban(Client clientABan,Client banniseur,String raison ){
		if(raison.isEmpty()||raison.equalsIgnoreCase(""))
			this.envoieATous("#"+clientABan.getPseudo() + " a été banni par "+banniseur.getPseudo()+".");
		else
			this.envoieATous("#"+clientABan.getPseudo() + " a été banni par "+banniseur.getPseudo()+"(Raison: "+raison+").");
		this.clientDeconnexion(clientABan);
		this.requeteSQL.updatelvAccess(clientABan.getBddID(), -1);
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
		this.ListBanIP.clear();
		if(result!=null){
			for(i=0;i<result.size();i++){
				this.ListBanIP.add(i, this.recupArgument(result.get(i), 2));
			}
		}
	}
	/**
	 * Permet de savoir si une ip est banni
	 * @param ip
	 * @return boolean
	 */
	public boolean ipIsBan(String ip){
		for (int i = 0; i<this.ListBanIP.size();i++){
			if(this.ListBanIP.get(i)[0].equalsIgnoreCase(ip)){
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
	public void unBanIP(String ip){
		this.requeteSQL.delBanIP(ip);
		this.updateListBanIP();
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
}
