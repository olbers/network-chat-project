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

import ui.Fenetre;



public class Client {
	protected Socket socketClient;  //  @jve:decl-index=0:
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
	protected boolean erreurCo=false;
	protected Thread ecoute;
	protected Vector<String> messagesGeneral;
	protected Vector<String> messagesPrives;
	protected Vector<String> clientsConnectes;
	protected Fenetre fenetre;

	public Client(Fenetre fenetre) {
		super();
		this.clientsConnectes = new Vector<String>();
		this.messagesGeneral = new Vector<String>();
		this.messagesPrives = new Vector<String>();
		this.fenetre=fenetre;
	}

	public void verifConnexion(){
		if (this.connexion(adresseIP, port)){
			clavier = new BufferedReader(new InputStreamReader(System.in));
			ThreadEcoute ecoute = new ThreadEcoute(this);
			ecoute.start();
			this.connexion(adresseIP, port);
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
			this.versServeur = new PrintWriter(socketClient.getOutputStream());
			this.depuisServeur = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			return true;
		} catch (UnknownHostException e) {
			System.out.println("Serveur " + serveurIP + ":" + port + " inconnu");
		} catch (IOException e) {
			System.out.println("Erreur Input/Output");
			e.printStackTrace();
		}
		return false;
	}

	public void envoiMessageGeneral(String message) {
		this.versServeur.println(message);
		this.versServeur.flush();
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
				// Renvoyer HASH MD5 de l'application au serveur
			}
			/* Incorporer ici les nouvelles commandes */
			else{
				System.err.println("Commande non supportée");
			}

		}
		else if(message.substring(0,1).equalsIgnoreCase("#")){	// MESSAGE SYSTEM
			message=supprChar(message);
			messageSystem(message);
		}
		else{
			System.err.println("SYNTAX MESSAGE ERROR: The message sent by server is not allowed by the protocol.");
		}
		return message;
	}

	public void listerConnectes(String message) {

		StringTokenizer st = new StringTokenizer(message,"|");
		while (st.hasMoreTokens()) {
			//System.out.println(st.nextToken());
			this.clientsConnectes.add(st.nextToken());
		}
		this.fenetre.getJListClients().setListData(this.getClientsConnectes());

	}

	public void messagePrive(String message) {		// Revoir selon ARRAYLIST
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
		message="<font color='red'><b>"+message+"</b></font>";
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
	
	public void messageGeneral(String message){	// Print message system en rouge.
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

		if(ecoute != null){
			commandeDeconnexion="&deconnexion";
			envoiMessageGeneral(commandeDeconnexion);

			try {
				ecoute.stop();
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
		String messageErreur="Connexion impossible au serveur: vérifiez les paramètres de connexion!";
		messageGeneral(messageErreur);
		printMessageGeneral();
	}

	public void parametresConnexion(String adresseIP,int port,String pseudo){
		this.adresseIP=adresseIP;
		this.port=port;
		this.pseudo=pseudo;
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
