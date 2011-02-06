package core;

import java.net.Socket;

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
	/**
	 * @param serveur
	 * @param socketClient
	 * @param client
	 */
	public ThreadClient(Server serveur, Socket socketClient, Client client) {
		super();
		this.serveur = serveur;
		this.socketClient = socketClient;
		this.client = client;
	}
	/**
	 * Methode run de la classe ThreadClient.
	 */
	@Override
	public void run()
	{
		while(true){
			this.serveur.recupChaine(this.client.getIn(), this.client);
			try {
				sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
