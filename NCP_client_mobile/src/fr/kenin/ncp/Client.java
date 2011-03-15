package fr.kenin.ncp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;



public class Client extends Service {

	protected Socket socketClient;
	protected BufferedReader in;
	protected PrintWriter out;
	protected ArrayList<String> messageList;
	protected String utilisateurList;
	protected String requeteConnexion;
	protected String chat;

	private Runnable ruClient = new ThreadClient();
	private Thread thClient;
	protected final IBinder mBinder = new ClientBinder();
	private static final String TAG = "Service_NCP";

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		messageList = new ArrayList<String>(100);

	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		try {
			envoiMessage("@deconnexion");
			in.close();
			out.close();
			socketClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	/**
	 * Permet de cr�er le socket
	 * @param ip
	 * @param port
	 * @return boolean
	 */
	public boolean createClient(String ip, int port){
		try {
			socketClient = new Socket(ip,port);
			createBuffer();
			return true;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
	/**
	 * Permet de cr�et les flux
	 * @return boolean
	 */
	public boolean createBuffer(){
		try {
			in = new BufferedReader(new InputStreamReader(socketClient.getInputStream()));
			out = new PrintWriter(socketClient.getOutputStream());
			thClient =new Thread(ruClient);
			thClient.start();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}
	}
	
	public void deconnexion(){
		this.stopSelf();
	}
	
	/**
	 * Permet d'envoyer un message � tout le serveur.
	 * @param message
	 */
	public void envoiMessage(String message){
		out.println(message);
		out.flush();
		Log.d(TAG, "Message envoyer au serveur."+message);
	}

	public void recupMessage(){
		try {
			if(in.ready()){
				Log.d(TAG, "Message recuperer");
				String chaineRecu=in.readLine();
				if(chaineRecu!=null && !chaineRecu.equals("")){
					traitementMessage(chaineRecu);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void traitementMessage(String chaineRecu){
		char firstChar = chaineRecu.charAt(0);
		if(firstChar == '*'){ //Message g�n�ral
			messageGeneral(supprChar(chaineRecu));
		}else if(firstChar == '%'){ //Message priv�
			messagePrivate(supprChar(chaineRecu));
		}else if(firstChar == '$'){ //Liste utilisateur
			updateListUser(supprChar(chaineRecu));
		}else if(firstChar == '&'){ // Commande serveur
			commandeServer(supprChar(chaineRecu));
		}else if(firstChar == '#'){ // Message Syst�me
			messageSysteme(supprChar(chaineRecu));
		}else{ //Autre commande trait� s�par�ment

		}

	}
	/**
	 * Permet de faire un message en message g�n�ral.
	 * @param message
	 * @return
	 */
	private String messageAll(String message){
		return "~"+message;
	}
	/**
	 * Permet d'envoyer les messages qui seront des commande au serveur.
	 * @param message
	 * @return string
	 */
	private String commandeClient(String message){
		return "@"+message;
	}

	private void messageGeneral(String message){
		message=message+"<br>";
		messageList.add(message);
		Log.d(TAG, "Message ajouter dans la liste");
	}

	private void messagePrivate(String message){
		message="<font color='grey'>"+message+"</font><br>";
		messageList.add(message);
	}

	private void messageSysteme(String message){
		message="<font color='red'>"+message+"</font><br>";
		messageList.add(message);
	}

	private void commandeServer(String commande){
		if (commande.equalsIgnoreCase("verif")){
			envoiMessage(commandeClient("md5 0"));
			envoiMessage(commandeClient(requeteConnexion));
		}else if(commande.equalsIgnoreCase("deconnexion")){
			
		}
	}

	public void updateListUser(String message){
		StringTokenizer st = new StringTokenizer(message,"|");
		String pseudo;
		StringBuffer listePrete = new StringBuffer("");
		while (st.hasMoreTokens()) {
			pseudo = st.nextToken();
			listePrete.append(pseudo+"\n");
		}
		Log.d(TAG, "Liste des utilisateurs faites");
		
		utilisateurList = listePrete.toString();
		Log.d(TAG, utilisateurList);
	}

	public void autreCommande(String message){
		//Gerer les autre commande		
	}

	/**
	 * Permet de supprimer le premier charact�re.
	 * @param message
	 * @return
	 */
	public String supprChar(String message){
		String chaineModif="";
		for(int i=0;i<message.length()-1;i++){
			chaineModif=chaineModif+message.charAt(i+1);
		}
		return chaineModif;
	}
	public String getMessage(){
		StringBuffer listMessage=new StringBuffer("");
		for(int i=0;i<messageList.size();i++){
			listMessage.append(messageList.get(i));
		}
		return listMessage.toString();
	}

	public void recupMessageTaper(String message){
		if(message!=null && !message.equals("")){
			if(message.charAt(0)=='/'){
				envoiMessage(message);
			}else{
				envoiMessage(messageAll(message));
			}
		}
	}
	/**
	 * @return the requeteConnexion
	 */
	public String getRequeteConnexion() {
		return requeteConnexion;
	}
	/**
	 * @param requeteConnexion the requeteConnexion to set
	 */
	public void setRequeteConnexion(String requeteConnexion) {
		this.requeteConnexion = requeteConnexion;
	}
	/**
	 * @return the chat
	 */
	public String getChat() {
		return chat;
	}
	/**
	 * @param chat the chat to set
	 */
	public void setChat(String chat) {
		this.chat = chat;
	}

	/**
	 * @return the messageList
	 */
	public ArrayList<String> getMessageList() {
		return messageList;
	}
	/**
	 * @param messageList the messageList to set
	 */
	public void setMessageList(ArrayList<String> messageList) {
		this.messageList = messageList;
	}

	/**
	 * @return the utilisateurList
	 */
	public String getUtilisateurList() {
		return utilisateurList;
	}
	/**
	 * @return the socketClient
	 */
	public Socket getSocketClient() {
		return socketClient;
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}

	public class ClientBinder extends Binder{
		Client getService(){
			return Client.this;
		}

	}
	class ThreadClient implements Runnable{
		@Override
		public void run() {
			while(socketClient.isConnected()){
				recupMessage();
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
		}

	}

}