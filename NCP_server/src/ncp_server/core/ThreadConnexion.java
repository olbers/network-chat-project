package ncp_server.core;
/**
 * La class ThreadConnexion permet de gérer en continue les requetes de conenxion au serveur.
 * @author Poirier Kévin
 * @version 0.1.3
 */

public class ThreadConnexion extends Thread {
	/**
	 * L'attribut serveur est un objet de type Server.
	 */
	protected Server serveur;
	protected boolean authCo; //Authorisation de connexion
	/**
	 * Constructeur de Thread.
	 */
	public ThreadConnexion(){
		super();
		this.serveur=Server.getInstance();
		this.authCo=true;
	}
	/**
	 * Methode run du Thread.
	 */
	@Override
	public void run(){
		while(authCo){
			
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
	/**
	 * @return the authCo
	 */
	public boolean isAuthCo() {
		return authCo;
	}
	/**
	 * @param authCo the authCo to set
	 */
	public void setAuthCo(boolean authCo) {
		this.authCo = authCo;
	}
	
}
