package ncp_server.util;

import ncp_server.core.Server;

/**
 * Permet de lancer un thread qui va faire un compte à rebours qui ensuite lancera la procédure de stop ou de redémarrage du serveur.
 * @author Poirier Kévin
 * @version 1.0.0
 */

public class CountDown extends Thread {
	
	private int compteur;
	
	private boolean restart;
	
	private Server server;
	
	/**
	 * Constructeur du CountDown. compteur prend le temps en seconde du compte à rebours. restart true pour un restart du server, false pour le stopper.
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
					this.server.envoieATous("#Redémarrage du serveur dans 5 minutes.");
				else
					this.server.envoieATous("#Coupure du serveur dans 5 minutes.");
			}else if(this.compteur==60){
				if(restart)
					this.server.envoieATous("#Redémarrage du serveur dans 1 minute.");
				else
					this.server.envoieATous("#Coupure du serveur dans 1 minute.");				
			}else if(this.compteur==30){
				if(restart)
					this.server.envoieATous("#Redémarrage du serveur dans 30 secondes.S'il vous plait, veuillez vous déconnecter.");
				else
					this.server.envoieATous("#Coupure du serveur dans 30 secondes.S'il vous plait, veuillez vous déconnecter.");	
				
			}else if(this.compteur<=10 && this.compteur>1){
				if(restart)
					this.server.envoieATous("#Redémarrage du serveur dans "+this.compteur+" secondes.S'il vous plait, veuillez vous déconnecter.");
				else
					this.server.envoieATous("#Coupure du serveur dans "+this.compteur+" secondes.S'il vous plait, veuillez vous déconnecter.");	
				
			}else if(this.compteur==1){
				if(restart)
					this.server.envoieATous("#Redémarrage du serveur dans 1 seconde.S'il vous plait, veuillez vous déconnecter.");
				else
					this.server.envoieATous("#Coupure du serveur dans 1 seconde.S'il vous plait, veuillez vous déconnecter.");	
				
			}			
			--this.compteur;
			try {
				sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println("Erreur lors du compte à rebours.");
				this.server.getLog().err("Erreur lors du compte à rebours.");
			}
			if(this.compteur==0){
				if(restart){
					this.server.envoieATous("#Redémarrage du serveur MAINTENANT !!");
					this.server.restartServer();
				}else{
					this.server.envoieATous("#Coupure du serveur MAINTENANT !!");	
					this.server.stopServer(true);
				}
			}
		}
	}

}
