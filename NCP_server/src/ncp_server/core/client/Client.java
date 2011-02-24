package ncp_server.core.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * La class Client, permet la gestion de tout les clients connectés au serveur.
 * @author Poirier Kévin
 * @version 0.1.2
 */
public class Client {
	protected int id;
	protected String pseudo;
	protected Socket socketClient;
	protected BufferedReader in;
	protected PrintWriter out;
	protected ThreadClient threadClient;
	protected int lvAccess;
	protected String ip;
	protected long lastMessage;
	protected boolean activer;
	protected String mail;
	protected String compte;
	protected int bddID;
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
		this.ip = this.socketClient.getInetAddress().toString();
		this.lastMessage = 0;
		this.activer=false;
		this.bddID=0;
		this.chMD5=false;
	}
	/**
	 * Permet de fermer la connexion au client
	 */
	public void closeClient(){
			this.threadClient.setActif(false);
			this.threadClient.interrupt();
			try {
				this.in.close();
				this.out.close();			
				this.socketClient.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	}
	/**
	 * Permet de créer le thread client
	 */
	public void createThread(){
		this.threadClient=new ThreadClient(this.socketClient, this);
	}
	/**
	 * @return the bddID
	 */
	public int getBddID() {
		return bddID;
	}
	/**
	 * @return the compte
	 */
	public String getCompte() {
		return compte;
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the in
	 */
	public BufferedReader getIn() {
		return in;
	}

	/**
	 * @return the ip
	 */
	public String getIp() {
		return ip;
	}

	/**
	 * @return the lastMessage
	 */
	public long getLastMessage() {
		return lastMessage;
	}

	/**
	 * @return the lvAccess
	 */
	public int getLvAccess() {
		return lvAccess;
	}

	/**
	 * @return the mail
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * @return the out
	 */
	public PrintWriter getOut() {
		return out;
	}

	/**
	 * @return the pseudo
	 */
	public String getPseudo() {
		return pseudo;
	}

	/**
	 * @return the socketClient
	 */
	public Socket getSocketClient() {
		return socketClient;
	}

	/**
	 * @return the threadClient
	 */
	public Thread getThreadClient() {
		return threadClient;
	}

	/**
	 * @return the activer
	 */
	public boolean isActiver() {
		return activer;
	}

	/**
	 * @return the chMD5
	 */
	public boolean isChMD5() {
		return chMD5;
	}

	/**
	 * @param activer the activer to set
	 */
	public void setActiver(boolean activer) {
		this.activer = activer;
	}
	/**
	 * @param bddID the bddID to set
	 */
	public void setBddID(int bddID) {
		this.bddID = bddID;
	}
	/**
	 * @param chMD5 the chMD5 to set
	 */
	public void setChMD5(boolean chMD5) {
		this.chMD5 = chMD5;
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
	public void setIp(String ip) {
		this.ip = ip;
	}
	/**
	 * @param lastMessage the lastMessage to set
	 */
	public void setLastMessage(long lastMessage) {
		this.lastMessage = lastMessage;
	}
	/**
	 * @param lvAccess the lvAccess to set
	 */
	public void setLvAccess(int lvAccess) {
		this.lvAccess = lvAccess;
	}
	/**
	 * @param mail the mail to set
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}
	/**
	 * @param pseudo the pseudo to set
	 */
	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}
	/**
	 * Permet de lancer le thread client.
	 */
	public void startThread(){
		this.threadClient.start();
	}






}
