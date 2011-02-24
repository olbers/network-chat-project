package ncp_server;

import ncp_server.core.Server;
import ncp_server.util.Log;
import ncp_server.util.option.Option;

/**
 * La class Main permet de lancer l'application, et creer les premier objet neccessaire au bon fonctionnement de l'application.
 * @author Poirier Kévin
 * @version 0.2.1
 */
public class Main {
	public static void main(String[] args) {

		System.out.print("Lancement du système de log .....");
		Log.getInstance(); //Creation de l'object de log
		System.out.print("Lancement du système d'option ...");
		Option.getInstace(); //Creation de l'object Option
		System.out.print("Connexion à la BDD ..............");
		Server server = Server.getInstance(); //Creation de la class principale Server.
		System.out.print("Lancement du serveur ............");
		server.createServer();

	}

}
