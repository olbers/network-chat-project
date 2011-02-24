package ncp_server.core.client;

import java.net.Socket;

import ncp_server.core.Server;

/**
 * Cette classe permet de recuperer les flux clients.
 * @author Poirier Kevin
 * @version 0.1.0
 *
 */
public class ThreadClient extends Thread {
	/**
	 * Permet de recuperer un objet Server.
	 */
	protected Server serveur;
	/**
	 * Permet de recuperer l'objet Socket du client
	 */
	protected Socket socketClient;
	/**
	 * Permet de recuperer l'objet Client.
	 */
	protected Client client;
	protected boolean actif;
	/**
	 * @param socketClient
	 * @param client
	 */
	public ThreadClient(Socket socketClient, Client client) {
		super();
		this.serveur = Server.getInstance();
		this.socketClient = socketClient;
		this.client = client;
		this.actif=true;
	}
	/**
	 * Methode run de la classe ThreadClient.
	 */
	@Override
	public void run(){
		while(actif){
			this.serveur.recupChaine(this.client.getIn(), this.client);
			try {
				sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				this.actif=false;
			}
		}
	}
	/**
	 * @param actif the actif to set
	 */
	public void setActif(boolean actif) {
		this.actif = actif;
	}
	
}
