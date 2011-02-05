package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * La class Client, permet la gestion de tout les clients connectés au serveur.
 * @author Poirier Kévin
 * @version 0.1.1
 */
public class Client {
	protected int id;
	protected String pseudo;
	protected Socket socketClient;
	protected BufferedReader in;
	protected PrintWriter out;
	protected ThreadClient threadClient;
	protected int lvAccess;
	protected InetAddress ip;
	protected long lastMessage;
	protected boolean activer;
	protected String mail;
	protected String compte;
	protected int BddID;
	protected boolean chMD5;
	/**COnstructeur de la classe
	 * @param id
	 * @param pseudo
	 * @param socketClient
	 * @param in
	 * @param out
	 */
	public Client(int id, String pseudo, Socket socketClient,
			BufferedReader in, PrintWriter out) {
		super();
		this.id = id;
		this.pseudo = pseudo;
		this.socketClient = socketClient;
		this.in = in;
		this.out = out;
		this.lvAccess = 0;
		this.ip = this.socketClient.getInetAddress();
		this.lastMessage = 0;
		this.activer=false;
		this.BddID=0;
		this.chMD5=false;
	}
	/**
	 * Permet de créer le thread client
	 * @param serveur
	 */
	protected void createThread(Server serveur){
		this.threadClient=new ThreadClient(serveur, socketClient, this);
	}
	/**
	 * Permet de lancer le thread client.
	 */
	protected void startThread(){
		this.threadClient.start();
	}
	/**
	 * Permet de fermer la connexion au client
	 * @param client
	 */
	public void closeClient(){		
		try {
			this.threadClient.stop();
			this.in.close();
			this.out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.err.println(e);
		}
		
	}
	/**
	 * @return the pseudo
	 */
	public String getPseudo() {
		return pseudo;
	}

	/**
	 * @param pseudo the pseudo to set
	 */
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	/**
	 * @return the lastMessage
	 */
	public long getLastMessage() {
		return lastMessage;
	}

	/**
	 * @param lastMessage the lastMessage to set
	 */
	public void setLastMessage(long lastMessage) {
		this.lastMessage = lastMessage;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the socketClient
	 */
	public Socket getSocketClient() {
		return socketClient;
	}

	/**
	 * @return the in
	 */
	public BufferedReader getIn() {
		return in;
	}

	/**
	 * @return the out
	 */
	public PrintWriter getOut() {
		return out;
	}

	/**
	 * @return the threadClient
	 */
	public Thread getThreadClient() {
		return threadClient;
	}

	/**
	 * @return the ip
	 */
	public InetAddress getIp() {
		return ip;
	}

	/**
	 * @return the activer
	 */
	public boolean isActiver() {
		return activer;
	}

	/**
	 * @param activer the activer to set
	 */
	public void setActiver(boolean activer) {
		this.activer = activer;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}
	/**
	 * @return the lvAccess
	 */
	public int getLvAccess() {
		return lvAccess;
	}
	/**
	 * @param lvAccess the lvAccess to set
	 */
	public void setLvAccess(int lvAccess) {
		this.lvAccess = lvAccess;
	}
	/**
	 * @return the compte
	 */
	public String getCompte() {
		return compte;
	}
	/**
	 * @param compte the compte to set
	 */
	public void setCompte(String compte) {
		this.compte = compte;
		this.pseudo = compte;
	}
	/**
	 * @param ip the ip to set
	 */
	public void setIp(InetAddress ip) {
		this.ip = ip;
	}
	/**
	 * @return the bddID
	 */
	public int getBddID() {
		return BddID;
	}
	/**
	 * @param bddID the bddID to set
	 */
	public void setBddID(int bddID) {
		BddID = bddID;
	}
	/**
	 * @return the chMD5
	 */
	public boolean isChMD5() {
		return chMD5;
	}
	/**
	 * @param chMD5 the chMD5 to set
	 */
	public void setChMD5(boolean chMD5) {
		this.chMD5 = chMD5;
	}
	
	
	
	
	

}
