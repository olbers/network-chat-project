package core;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JFrame;

import ui.Fenetre;



public class Client {
	public Socket socketClient;  //  @jve:decl-index=0:
	protected PrintWriter versServeur;  
	protected BufferedReader depuisServeur;  //  @jve:decl-index=0:
	protected BufferedReader clavier;
	protected String texteEnvoiGeneral=null; 
	protected String texteConnexion=null;
	protected String commandeDeconnexion=null;  //  @jve:decl-index=0:
	protected String texteRecu=null;  //  @jve:decl-index=0:
	protected String texteMP=null;
	protected String adresseIP;  //  @jve:decl-index=0:
	protected int port;
	public String pseudo="";  //  @jve:decl-index=0:
	protected String pass=null;
	protected boolean erreurCo=false;
	protected ThreadEcoute ecoute;
	protected Vector<String> messagesGeneral;
	protected Vector<String> messagesPrives;
	protected Vector<String> clientsConnectes;
	protected Fenetre fenetre;
	public boolean conecte=false;
	public boolean verifCheckConnexion;
	String adresseMail = null;
	String pseudoEnregistrement = null;
	String mdp = null;

	public Client(Fenetre fenetre) {
		super();
		this.clientsConnectes = new Vector<String>();
		this.messagesGeneral = new Vector<String>();
		this.messagesPrives = new Vector<String>();
		this.fenetre=fenetre;
		this.verifCheckConnexion = false;
		this.ecoute = new ThreadEcoute(this);
	}

	public void checkConnexion(){
		if(this.verifCheckConnexion){
			deconnexion();
			this.verifCheckConnexion = false;
			clearApplication();
			verifConnexion();
		}
		else{
			verifConnexion();
		}
	}


	public void verifConnexion(){

		if (this.connexion(adresseIP, port)){
			clavier = new BufferedReader(new InputStreamReader(System.in));
			this.ecoute = new ThreadEcoute(this);
			ecoute.start();
			ecoute.setActif(true);
			this.verifCheckConnexion = true;
			System.out.println("Normalement il est connecté.");
		}
		else{
			erreurCo=true;
			System.err.println("Connexion impossible au serveur: vérifiez les paramètres de connexion!");
			erreurConnexion();
		}
	}

	public boolean connexion(String serveurIP, int port) {
		System.out.println("Connexion au serveur...");

		try {
			this.socketClient = new Socket(serveurIP, port);
			this.versServeur = new PrintWriter(this.socketClient.getOutputStream());
			this.depuisServeur = new BufferedReader(new InputStreamReader(this.socketClient.getInputStream()));
			this.conecte=true;
		} catch (UnknownHostException e) {
			System.out.println("Serveur " + serveurIP + ":" + port + " inconnu");
			this.conecte=false;
		} catch (IOException e) {
			System.out.println("Erreur Input/Output");
			e.printStackTrace();
			this.conecte=false;
		}
		return conecte;
	}

	public void envoiMessageGeneral(String message) {
		if(! message.equals("")){
			if(message.substring(0,1).equalsIgnoreCase("/") || message.substring(0,1).equalsIgnoreCase("@")){
				this.versServeur.println(message);
				System.out.println("Envoi: "+message);
				this.versServeur.flush();
			}
			else{
				message="~"+message;
				this.versServeur.println(message);
				System.out.println("Envoi: "+message);
				this.versServeur.flush();
			}
		}
	}

	public void envoiMessagePrive(String message) {
		if(! message.equals("")){
			message="/mp "+message;
			this.versServeur.println(message);
			this.versServeur.flush();
		}
	}

	public String typeMessage(String message){
		if(message.substring(0,1).equalsIgnoreCase("%")){	// MESSAGE PRIVE
			message=supprChar(message);
			messagePrive(message);
		}
		else if(message.substring(0,1).equalsIgnoreCase("*")){	// MESSAGE GENERAL
			message=supprChar(message);
			messageGeneral(message);
		}
		else if(message.substring(0,1).equalsIgnoreCase("$")){	// LISTE CONNECTES
			message=supprChar(message);
			listerConnectes(message);
		}
		else if(message.substring(0,1).equalsIgnoreCase("&")){	// COMMANDE SERVER
			if(message.equalsIgnoreCase("&verif")){
				envoiMessageGeneral("@md5 0");
			}
			else if(message.equalsIgnoreCase("&deconnexion")){
				deconnexionParServeur();
			}
			/* Incorporer ici les nouvelles commandes */
			else{
				System.err.println("Commande non supportée");
				System.out.println(message);
			}
		}
		else if(message.substring(0,1).equalsIgnoreCase("#")){	// MESSAGE SYSTEM
			message=supprChar(message);
			messageSystem(message);
		}
		else if(message.substring(0,1).equals("0")){
			System.out.println("Connexion au serveur OK");
		}
		else if(message.substring(0,1).equals("1")){
			messageSystem("Enregistrement effectué avec succès!");
		}
		else if(message.substring(0,1).equals("2")){
			messageSystem("Enregistrement impossible: Pseudo déjà réservé par un autre utilisateur.");
		}
		else if(message.substring(0,1).equals("3")){
			messageSystem("Enregistrement impossible: L'adresse E-Mail entrée est déjà enregistrée pour un autre compte.");
		}
		else if(message.substring(0,1).equals("4")){
			messageSystem("Connexion impossible au serveur: Ce pseudo est déjà réservé.");
		}
		else if(message.substring(0,1).equals("5")){
			messageSystem("Connexion impossible au serveur: Le pseudo que vous avez saisi est un utilisateur déjà en ligne.");
		}
		else if(message.substring(0,1).equals("6")){
			messageSystem("Connexion impossible au serveur: Mot de passe erroné.");
		}
		else if(message.substring(0,1).equals("7")){
			messageSystem("Connexion impossible au serveur: Le serveur est saturé, réessayez ultérieurement.");
		}
		else if(message.substring(0,1).equals("8")){
			messageSystem("Connexion impossible au serveur: Votre application n'est pas conforme.");
		}
		else if(message.substring(0,1).equals("9")){
			System.out.println("MD5 OK");
			if(pass != null){
				envoiMessageGeneral("@connect "+pseudo+" "+pass);
				System.out.println("Envoi données connexion: "+"@connect "+pseudo+" "+pass);
			}
			else{
				envoiMessageGeneral("@connect "+pseudo);
				System.out.println("Envoi données connexion: "+"@connect "+pseudo);
			}
		}
		else if(message.substring(0,1).equalsIgnoreCase("a")){
			messageSystem("Le compte que vous tentez d'utiliser est banni!");
		}
		else if(message.substring(0,1).equalsIgnoreCase("b")){
			messageSystem("Votre adresse IP est bannie du serveur!");
		}
		else if(message.substring(0,1).equalsIgnoreCase("c")){
			messageSystem("Vous avez déjà un compte enregistré sur le serveur.");
		}
		else{
			System.err.println("SYNTAX MESSAGE ERROR: The message sent by server is not allowed by the protocol.");
		}
		return message;
	}

	public void listerConnectes(String message) {
		this.clientsConnectes.clear();
		StringTokenizer st = new StringTokenizer(message,"|");
		while (st.hasMoreTokens()) {
			//System.out.println(st.nextToken());
			this.clientsConnectes.add(st.nextToken());
		}

		this.fenetre.getJListClients().setListData(this.getClientsConnectes());
		this.fenetre.jLabelTotal.setText("En ligne: "+clientsConnectes.size());

	}

	public void messagePrive(String message) {		
		String textPrint="<HTML>";
		messagesPrives.add(message);

		while(messagesPrives.size() > 50){
			messagesPrives.remove(0);
		}
		for(int i=0;i<messagesPrives.size();i++){
			if (i == 0){
				textPrint=textPrint+messagesPrives.get(i);
			}
			else{
				textPrint=textPrint+"<br>"+messagesPrives.get(i);
			}
		}
		textPrint=textPrint+"</HTML>";
		this.fenetre.getJTextPaneMP().setText(textPrint);
	}

	public void messageSystem(String message){	// Print message system en rouge.
		message="<font color='red'>"+message+"</font>";
		messagesGeneral.add(message);
		printMessageGeneral();
	}

	public String supprChar(String message){
		String chaineModif="";
		for(int i=0;i<message.length()-1;i++){
			chaineModif=chaineModif+message.charAt(i+1);
		}
		return chaineModif;
	}

	public void messageGeneral(String message){	
		messagesGeneral.add(message);
		printMessageGeneral();
	}

	public void printMessageGeneral() {

		String textPrint="<HTML>";

		while(messagesGeneral.size() > 50){
			messagesGeneral.remove(0);
		}
		for(int i=0;i<messagesGeneral.size();i++){
			if (i == 0){
				textPrint=textPrint+messagesGeneral.get(i);
			}
			else{
				textPrint=textPrint+"<br>"+messagesGeneral.get(i);
			}
		}
		textPrint=textPrint+"</HTML>";
		this.fenetre.getJTextPaneGeneral().setText(textPrint);
	}

	public void deconnexionParServeur(){
		if(ecoute.isActif()){
			try {
				ecoute.setActif(false);
				ecoute.interrupt();
				this.depuisServeur.close();
				this.versServeur.close();
				this.socketClient.close();	
				clearApplication();
				messageSystem("Vous avez été déconecté du serveur.");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void deconnexion(){

		if(ecoute.isActif()){
			commandeDeconnexion="@deconnexion";
			envoiMessageGeneral(commandeDeconnexion);


			try {
				ecoute.setActif(false);
				ecoute.interrupt();
				this.depuisServeur.close();
				this.versServeur.close();
				this.socketClient.close();	

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void erreurConnexion(){
		String messageErreur="<font color='red'>Connexion impossible au serveur: vérifiez les paramètres de connexion!</font>";
		messageGeneral(messageErreur);
		printMessageGeneral();
	}

	public void parametresConnexion(String adresseIP,int port,String pseudo,char[] pass){

		this.adresseIP=adresseIP;
		this.port=port;
		this.pseudo=pseudo;
		this.pass=new String(pass);
		checkConnexion();
	}

	public void infosEnregistrement(String pseudoEnregistrement,String adresseMail,String mdp){
		this.adresseMail = adresseMail;
		this.pseudoEnregistrement=pseudoEnregistrement;
		this.mdp=mdp;
		System.out.println("@register "+this.pseudoEnregistrement+" "+this.mdp+" "+this.adresseMail);
		envoiMessageGeneral("@register "+this.pseudoEnregistrement+" "+this.mdp+" "+this.adresseMail);
	}

	public void clearApplication(){
		fenetre.jTextPaneGeneral.setText("");
		fenetre.saisieMP.setText("");
		clientsConnectes.clear();
		messagesGeneral.clear();
		messagesPrives.clear();
		fenetre.jListClients.setListData(clientsConnectes);
		fenetre.jLabelTotal.setText("");

	}

	public BufferedReader getDepuisServeur() {
		return depuisServeur;
	}

	public Vector getClientsConnectes() {
		return clientsConnectes;
	}
	public Vector getMessagesGeneraux() {
		return messagesGeneral;
	}

	public static void main(String[] args) {
		Client client1 = new Client(new Fenetre());

		String message = "$pseudo1|pseudo2|pseudo3|pseudo4|pseudo5|Cédric";
		client1.typeMessage(message);
		message = "%blabla1";
		client1.typeMessage(message);
		message = "%blabla2";
		client1.typeMessage(message);
		message = "#blabla1";
		client1.typeMessage(message);
		message = "*blabla2";
		client1.typeMessage(message);
		message = "#blabla1";
		client1.typeMessage(message);
		message = "*blabla2zdddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddzzzzzzzzzzzzzzzzzzzzzzzzzzzzzdzdzd";
		client1.typeMessage(message);
		message = "*blabla1";
		client1.typeMessage(message);
		message = "*blabla2";
		client1.typeMessage(message);
		message = "*blabla1dzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzzz";
		client1.typeMessage(message);
		message = "#blabla2";
		client1.typeMessage(message);



	}
}
