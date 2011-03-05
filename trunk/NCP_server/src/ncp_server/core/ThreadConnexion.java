package ncp_server.core;
/**
 * La class ThreadConnexion permet de gérer en continue les requetes de conenxion au serveur.
 * @author Poirier Kévin
 * @version 0.2.1
 */

public class ThreadConnexion extends Thread {
	/**
	 * L'attribut serveur est un objet de type Server.
	 */
	protected Server serveur;
	protected boolean run;
	protected boolean authCo;
	/**
	 * Constructeur de Thread.
	 */
	public ThreadConnexion(){
		super();
		this.serveur=Server.getInstance();
		this.run=true;
		this.authCo=true;
	}
	/**
	 * Methode run du Thread.
	 */
	@Override
	public void run(){
		while(run){

			try {
				if(authCo)
					this.serveur.clientConnexion();
				sleep(50); //permet de ralentir les vérification du thread. (Vérification que cela ne pose pas de soucis...)
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				this.run=false;
				this.serveur.log.err("Erreur lors de la temporisation du thread de connexion");
			}
		}
	}

	/**
	 * @return the authCo
	 */
	public boolean isAuthCo() {
		return run;
	}
	/**
	 * @param authCo the authCo to set
	 */
	public void setAuthCo(boolean authCo) {
		this.run = authCo;
	}
	/**
	 * @return the run
	 */
	public boolean isRun() {
		return run;
	}
	/**
	 * @param run the run to set
	 */
	public void setRun(boolean run) {
		this.run = run;
	}


}
