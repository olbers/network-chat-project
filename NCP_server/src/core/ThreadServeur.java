package core;

import java.net.*;
import java.io.*;

// Cette classe sera associée à chaque clients.
// Il y aura autant d'instances de cette classe que de clients connectés
// on implémente l'interface Runnable, une méthode pour créer un thread.

class ThreadServeur implements Runnable {
	protected Thread t; // contiendra le thread du client
	protected Socket s; // recevra le socket liant au client
	protected PrintWriter out; // flux de sortie
	protected BufferedReader in; // flux d'entrée
	protected Serveur serveur; // Objet pour utiliser les méthodes de la classe mère
	protected int numClient=0; // contiendra le numéro de client géré par ce thread

	//Constructeur qui permet de créer les éléments permettant le dialogue avec le client
	ThreadServeur(Socket s, Serveur srv){ // le paramètre s (socket) est donné par la classe mère

		serveur=srv;
		s=s;
		try {

			// on créé une variable reçevant les flux de sortie
			out = new PrintWriter(s.getOutputStream());
			// On créé une variable reçevent les flux d'entrée
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			// ajoute le flux de sortie dans la liste et récupération de son numero
			numClient = serveur.addClient(out);
		}
		catch (IOException e){ }

		t = new Thread(this); // instanciation du thread
		t.start(); // On lance le thread (run())
	}
	public void stopSocket(){
		try {
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Méthode exécutée lorsqu'on lance t.start()
	// Elle attend les messages en provenance du client et les redirige
	public void run(){

		String message = ""; // déclaration de la variable qui recevra les messages du client

		// on indique dans la console la connection d'un nouveau client
		System.out.println("Un nouveau client s'est connecte, no "+numClient);
		try {
			
			if(serveur.getNbClients() >= 10){	// Si le nombre de clients dépassent 100, alors on considère que le serveur subit une attaque et on déconecte tous les utilisateurs suivants le 100ème connecté pour que le serveur reste en fonctionnement.
				System.out.println("Présence d'un hack par ajout de clients en boucle!");
				for(int i=serveur.getNbClients();i>=10;i--){
					serveur.delClient(i);
				}
			}

			// On lit les données entrantes caractère par caractère jusqu'à trouver un caractère de fin de chaine.
			char charCur[] = new char[1]; // déclaration d'un tableau de char d'un élement.
			while(in.read(charCur, 0, 1)!=-1) // attente en boucle des messages provenant du client.
			{
				// on regarde si on arrive à la fin d'une chaine
				if (charCur[0] != '\u0000' && charCur[0] != '\n' && charCur[0] != '\r')
					message += charCur[0]; // ... si non, on concatène le caractère dans le message
				else if(!message.equalsIgnoreCase("")) {// sécurité concernant le massage.

					if(charCur[0]=='\u0000') // le dernier caractère était '\u0000' qui est le caractère de terminaison nulle
						// on envoi le message en disant qu'il faudra concaténer '\u0000' lors de l'envoi au client
						serveur.sendAll(message,""+charCur[0]);
					else serveur.sendAll(message+"\n",""); // sinon on envoi le message à tous
					message = ""; // Réinitialisation de la chaine pour réutilisation
				}
			}
		}
		catch (Exception e){ }
		finally {// Se produira quand un client se déconnecte.
		
			try {
			
				// on indique à la console la deconnexion du client
				System.out.println("Le client numéro "+numClient+" s'est deconnecte");
				serveur.delClient(numClient); // on supprime le client de la liste.
				s.close(); // fermeture du socket si il ne l'a pas déjà été (à cause de l'exception levée plus haut)
			}
			catch (IOException e){ }
		}
	}
}