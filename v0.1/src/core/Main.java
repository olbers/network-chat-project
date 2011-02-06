package core;

public class Main {

	/**
	 * La class Main permet de lancer l'application, et creer les premier objet neccessaire au bon fonctionnement de l'application.
	 * @author Poirier Kévin
	 * @Version 0.1.0
	 */
	public static void main(String[] args) {
		Log newLog =new Log();
		Option option = new Option(newLog); 
		Server server = new Server(newLog, option);
		

	}

}
