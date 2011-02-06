package core;
/**
 * La class ThreadConnexion permet de gérer en continue les requetes de conenxion au serveur.
 * @author Poirier Kévin
 * @version 0.1.1
 */

public class ThreadConnexion extends Thread {
	/**
	 * L'attribut serveur est un objet de type Server.
	 */
	protected Server serveur;
	/**
	 * Constructeur de Thread.
	 * @param serv
	 */
	public ThreadConnexion(Server serv){
		super();
		this.serveur=serv;
	}
	/**
	 * Methode run du Thread.
	 */
	@Override
	public void run(){
		while(true){
			
			try {
				this.serveur.clientConnexion();
				sleep(50); //permet de ralentir les vérification du thread. (Vérification que cela ne pose pas de soucis...)
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				this.serveur.log.err("Erreur lors de la temporisation du thread de connexion");
			}
		}
	}
}
