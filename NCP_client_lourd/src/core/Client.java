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
	protected String pseudo=null;  //  @jve:decl-index=0:
	protected String pass=null;
	protected boolean erreurCo=false;
	protected ThreadEcoute ecoute;
	protected Vector<String> messagesGeneral;
	protected Vector<String> messagesPrives;
	protected Vector<String> clientsConnectes;
	protected Fenetre fenetre;
	public boolean conecte=false;

	public Client(Fenetre fenetre) {
		super();
		this.clientsConnectes = new Vector<String>();
		this.messagesGeneral = new Vector<String>();
		this.messagesPrives = new Vector<String>();
		this.fenetre=fenetre;
		ecoute = new ThreadEcoute(this);
	}

	public void verifConnexion(){
		if (this.connexion(adresseIP, port)){
			clavier = new BufferedReader(new InputStreamReader(System.in));
			ecoute.start();
			ecoute.setActif(true);
			System.out.println("Normalement il est connect�.");
		}
		else{
			erreurCo=true;
			System.err.println("Connexion impossible au serveur: v�rifiez les param�tres de connexion!");
			erreurConnexion();
		}
	}

	public boolean connexion(String serveurIP, int port) {
		System.out.println("Connexion au serveur...");

		try {
			this.socketClient = new Socket(serveurIP, port);
			this.versServeur = new PrintWriter(socketClient.getOutputStream());
			this.depuisServeur = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			conecte=true;
		} catch (UnknownHostException e) {
			System.out.println("Serveur " + serveurIP + ":" + port + " inconnu");
			conecte=false;
		} catch (IOException e) {
			System.out.println("Erreur Input/Output");
			e.printStackTrace();
			conecte=false;
		}
		return conecte;
	}

	public void envoiMessageGeneral(String message) {
		if(! message.equals("")){
			if(message.substring(0,1).equalsIgnoreCase("/") || message.substring(0,1).equalsIgnoreCase("@")){
				this.versServeur.println(message);
				this.versServeur.flush();
			}
			else{
				message="~"+message;
				this.versServeur.println(message);
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
			/* Incorporer ici les nouvelles commandes */
			else{
				System.err.println("Commande non support�e");
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
			messageSystem("Enregistrement effectu� avec succ�s!");
		}
		else if(message.substring(0,1).equals("2")){
			messageSystem("Enregistrement impossible: Pseudo d�j� r�serv� par un autre utilisateur.");
		}
		else if(message.substring(0,1).equals("3")){
			messageSystem("Enregistrement impossible: L'adresse E-Mail entr�e est d�j� enregistr�e pour un autre compte.");
		}
		else if(message.substring(0,1).equals("4")){
			messageSystem("Connexion impossible au serveur: Ce pseudo est d�j� r�serv�.");
		}
		else if(message.substring(0,1).equals("5")){
			messageSystem("Connexion impossible au serveur: Le pseudo que vous avez saisi est un utilisateur d�j� en ligne.");
		}
		else if(message.substring(0,1).equals("6")){
			messageSystem("Connexion impossible au serveur: Mot de passe erron�.");
		}
		else if(message.substring(0,1).equals("7")){
			messageSystem("Connexion impossible au serveur: Le serveur est satur�, r�essayez ult�rieurement.");
		}
		else if(message.substring(0,1).equals("8")){
			messageSystem("Connexion impossible au serveur: Votre application n'est pas conforme.");
		}
		else if(message.substring(0,1).equals("9")){
			System.out.println("MD5 OK");
			if(pass != null){
				envoiMessageGeneral("@connect "+pseudo+" "+pass);
				System.out.println("Envoi donn�es connexion: "+"@connect "+pseudo+" "+pass);
			}
			else{
				envoiMessageGeneral("@connect "+pseudo);
				System.out.println("Envoi donn�es connexion: "+"@connect "+pseudo);
			}
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

		while(messagesPrives.size() > 10){
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

		while(messagesGeneral.size() > 10){
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
		String messageErreur="<font color='red'>Connexion impossible au serveur: v�rifiez les param�tres de connexion!</font>";
		messageGeneral(messageErreur);
		printMessageGeneral();
	}

	public void parametresConnexion(String adresseIP,int port,String pseudo,char[] pass){

		this.adresseIP=adresseIP;
		this.port=port;
		this.pseudo=pseudo;
		this.pass=new String(pass);
		verifConnexion();
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

		String message = "$pseudo1|pseudo2|pseudo3|pseudo4|pseudo5|C�dric";
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
