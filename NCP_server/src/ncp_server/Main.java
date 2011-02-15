package ncp_server;

import ncp_server.core.Server;
import ncp_server.util.Log;
import ncp_server.util.option.Option;

public class Main {

	/**
	 * La class Main permet de lancer l'application, et creer les premier objet neccessaire au bon fonctionnement de l'application.
	 * @author Poirier Kévin
	 * @Version 0.2.0
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Log log = Log.getInstance(); //Creation de l'object de log
		Option option = Option.getInstace(); //Creation de l'object Option
		Server server = Server.getInstance(); //Creation de la class principale Server.
		server.createServer();

	}

}
