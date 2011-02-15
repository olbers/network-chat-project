package ncp_server.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

import ncp_server.core.client.Client;
import ncp_server.util.DateString;
import ncp_server.util.Log;
import ncp_server.util.db.MySQL;
import ncp_server.util.db.RequeteSQL;
import ncp_server.util.mail.Mail;
import ncp_server.util.option.Option;
/**
 * Class Server, est la classe principale du serveur de chat NCP.
 * @author Poirier Kévin
 * @version 0.1.0.4
 *
 */

public class Server {

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
		String listeDePseudo="$";
		for(int i=0;i<this.listClient.size();i++){
			if(this.listClient.get(i).isActiver())
				listeDePseudo=listeDePseudo+this.listClient.get(i).getPseudo()+"|";
		}
		this.envoieATous(listeDePseudo);
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
	}
	/**
	 * Permet de connecter le client
	 * @param chaine
	 * @param client
	 */
	public void connect(String chaine, Client client){
		if(!client.isActiver()){
			String[] argument = this.recupArgument(chaine, 3);
			String compte = argument[1];
			String mdp = argument [2];						
			if (mdp.equalsIgnoreCase("")){
				boolean CheckPseudo = false;
				ArrayList<String> testExist = this.requeteSQL.verifClient(compte);
				if(testExist!=null){ //Pas enregistrer dans la BDD
					CheckPseudo=true;
					this.envoiePrive(client, "4");
				}else{				
					for (int i=0;i<this.listClient.size();i++){
						if (compte.equalsIgnoreCase(this.listClient.get(i).getPseudo())){
							CheckPseudo= true;
							this.envoiePrive(client, "5");
							break;
						}					
					}
				}
				if(!CheckPseudo){
					client.setLvAccess(0);
					client.setPseudo(compte);
					//methode Activer Client;					
					this.envoiePrive(client, "1");
					this.activationClient(client);
				}

			}else {
				ArrayList<String> resultCompte = this.requeteSQL.connexionClient(compte, mdp);
				boolean checkConnect =false;
				if (resultCompte != null){//Client reconnu
					String [] getValBDD = this.recupArgument(resultCompte.get(0), 4);
					for (int i=0;i<this.listClient.size();i++){
						if (Integer.parseInt(getValBDD[0])==this.listClient.get(i).getBddID()){
							checkConnect= true;
							this.envoiePrive(client, "5");
							break;
						}					
					}
					if(!checkConnect){
						// Gestion des information
						client.setBddID(Integer.parseInt(getValBDD[0]));
						client.setPseudo(getValBDD[1]);
						client.setCompte(getValBDD[1]);
						client.setMail(getValBDD[2]);
						client.setLvAccess(Integer.parseInt(getValBDD[3]));
						this.requeteSQL.updateIP(client.getIp().toString(), client.getBddID());
						this.envoiePrive(client, "1");
						this.activationClient(client);
					}
				}else{
					//Utilisateur enregistrer
					this.envoiePrive(client, "6");
				}
			}

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
		} catch (IOException e) {
			this.log.err("Impossible de créer le serveur.");
			System.err.println("Impossible de créer le serveur.");
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println(this.option.getNameServer()+" est lance, et est a l'ecoute sur le port : "+this.option.getPort());			
		this.connexion= new ThreadConnexion();
		this.connexion.start();
	}
	/**
	 * La methode envoieATous permet d'envoyer les messages à tout les clients connecter.
	 * @param message
	 */
	public void envoieATous(String message){
		for(int i=0;i<this.listClient.size();i++){
			if(this.listClient.get(i).isActiver()){
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
		client.getOut().println(message);
		client.getOut().flush();
	}
	/**
	 * La methode md5 Permet de verifier si le client correspond bien à ce que l'on attend.
	 * @param chaine
	 * @param client
	 */	
	public void md5(String chaine, Client client){
		if (!client.isActiver()){
			String[] argument= new String[2];
			argument=this.recupArgument(chaine, 2);
			//System.out.println(option.lourdMD5);
			//System.out.println(argument[1]);
			if(!option.isProtectMD5() || option.getLourdMD5().equalsIgnoreCase(argument[1])){
				this.envoiePrive(client, "1");
				client.setChMD5(true);
			}else{
				this.envoiePrive(client, "7");

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
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/**
	 * Permet de recuperer la commande qui à été envoyer
	 * @param chaine
	 * @return String
	 */
	public String recupCommande(String chaine){
		StringTokenizer token;
		token = new StringTokenizer(chaine);
		chaine = token.nextToken();
		return chaine;
	}
	/**
	 * Permet d'enregistrer un client dans la base de données.
	 * @param chaine
	 * @param client
	 */
	public void register(String chaine, Client client){
		if(client.getBddID()==0){
			String[] argument= new String[4];
			argument=this.recupArgument(chaine, 4);
			//System.out.println(argument[1]);
			ArrayList<String> testMail = this.requeteSQL.verifMail(argument[3]);
			ArrayList<String> testExist = this.requeteSQL.verifClient(argument[1]);
			if(testMail!=null){
				//Mail utilisé
				this.envoiePrive(client, "3");
			}else if (testExist!=null){
				//pseudo utilisé
				this.envoiePrive(client, "2");
			}else{
				this.requeteSQL.insertClient(argument[1], argument[2], argument[3],
						0, client.getIp().toString(), new DateString().dateSQL());
				client.setCompte(argument[1]);
				client.setMail(argument[3]);
				client.setBddID(Integer.parseInt((this.requeteSQL.getBDDID(argument[1]).get(0))));
				new Mail().inscriptionMail(client);
				this.envoiePrive(client, "1");//Message de confirmation
			}
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
		String chaineModif="";
		for(int i=0;i<(chaine.length()-1);i++){
			chaineModif=chaineModif+chaine.charAt(i+1);
		}		
		return chaineModif;
	}
	/**
	 * La methode traitementChaine permet de traiter les chaines de caracteres envoyer par les clients.
	 * @param chaine
	 */
	public void traitementChaine(String chaine,Client client){
		if(chaine!=null || !"".equals(chaine)){
			if (chaine.substring(0,1).equalsIgnoreCase("@")){
				this.traitementCommandeClient(this.suppr1Car(chaine),client);
			}else if (chaine.substring(0,1).equalsIgnoreCase("/")){
				this.traitementCommandeUtilisateur(this.suppr1Car(chaine),client);
			}else {
				this.traitementMessageAll(chaine, client);
			}
		}
	}
	/**
	 * Permet de gerer les commandes clients @
	 * @param chaine
	 * @param client
	 */
	public void traitementCommandeClient(String chaine,Client client){
		String commande;
		commande=this.recupCommande(chaine);
		if (commande.equalsIgnoreCase("connect")){
			this.connect(chaine, client);
		}else if (commande.equalsIgnoreCase("md5")){
			this.md5(chaine, client);
		}else if (commande.equalsIgnoreCase("register")){
			this.register(chaine, client);
		}

	}
	/**
	 * Permet de gerer les commandes utilisateurs /
	 * @param chaine
	 * @param client
	 */
	public void traitementCommandeUtilisateur(String chaine,Client client){

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
			message="*"+new DateString().dateChat() + messageToLog;
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
		}else{
			this.envoiePrive(client, "7");
		}
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
