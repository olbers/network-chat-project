package ncp_server.util;

import ncp_server.core.Server;

/**
 * Permet de lancer un thread qui va faire un compte � rebours qui ensuite lancera la proc�dure de stop ou de red�marrage du serveur.
 * @author Poirier K�vin
 * @version 1.0.0
 */

public class CountDown extends Thread {
	
	private int compteur;
	
	private boolean restart;
	
	private Server server;
	
	/**
	 * Constructeur du CountDown. compteur prend le temps en seconde du compte � rebours. restart true pour un restart du server, false pour le stopper.
	 * @param compteur
	 * @param restart
	 */
	public CountDown(int compteur, boolean restart){
		super();
		this.compteur=compteur;
		this.restart=restart;
		this.server=Server.getInstance();
	}
	/**
	 * Methode run du thread.
	 */
	public void run(){
		while(this.compteur>0){
			if(this.compteur==300){
				if(restart)
					this.server.envoieATous("#Red�marrage du serveur dans 5 minutes.");
				else
					this.server.envoieATous("#Coupure du serveur dans 5 minutes.");
			}else if(this.compteur==60){
				if(restart)
					this.server.envoieATous("#Red�marrage du serveur dans 1 minute.");
				else
					this.server.envoieATous("#Coupure du serveur dans 1 minute.");				
			}else if(this.compteur==30){
				if(restart)
					this.server.envoieATous("#Red�marrage du serveur dans 30 secondes.S'il vous plait, veuillez vous d�connecter.");
				else
					this.server.envoieATous("#Coupure du serveur dans 30 secondes.S'il vous plait, veuillez vous d�connecter.");	
				
			}else if(this.compteur<=10 && this.compteur>1){
				if(restart)
					this.server.envoieATous("#Red�marrage du serveur dans "+this.compteur+" secondes.S'il vous plait, veuillez vous d�connecter.");
				else
					this.server.envoieATous("#Coupure du serveur dans "+this.compteur+" secondes.S'il vous plait, veuillez vous d�connecter.");	
				
			}else if(this.compteur==1){
				if(restart)
					this.server.envoieATous("#Red�marrage du serveur dans 1 seconde.S'il vous plait, veuillez vous d�connecter.");
				else
					this.server.envoieATous("#Coupure du serveur dans 1 seconde.S'il vous plait, veuillez vous d�connecter.");	
				
			}			
			--this.compteur;
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Erreur lors du compte � rebours.");
				this.server.getLog().err("Erreur lors du compte � rebours.");
			}
			if(this.compteur==0){
				if(restart){
					this.server.envoieATous("#Red�marrage du serveur MAINTENANT !!");
					this.server.restartServer();
				}else{
					this.server.envoieATous("#Coupure du serveur MAINTENANT !!");	
					this.server.stopServer(true);
				}
			}
		}
	}

}
