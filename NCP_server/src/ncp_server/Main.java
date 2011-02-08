package ncp_server;

import ncp_server.core.Server;
import ncp_server.util.Log;
import ncp_server.util.option.Option;
public class Main {

	/**
	 * La class Main permet de lancer l'application, et creer les premier objet neccessaire au bon fonctionnement de l'application.
	 * @author Poirier Kévin
	 * @Version 0.1.0
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Log newLog =new Log();
		Option option = new Option(newLog); 
		Server server = new Server(newLog, option);
		

	}

}
