package core;

import java.net.*;
import java.io.*;

// Cette classe sera associ�e � chaque clients.
// Il y aura autant d'instances de cette classe que de clients connect�s
// on impl�mente l'interface Runnable, une m�thode pour cr�er un thread.

class ThreadServeur implements Runnable {
	protected Thread t; // contiendra le thread du client
	protected Socket s; // recevra le socket liant au client
	protected PrintWriter out; // flux de sortie
	protected BufferedReader in; // flux d'entr�e
	protected Serveur serveur; // Objet pour utiliser les m�thodes de la classe m�re
	protected int numClient=0; // contiendra le num�ro de client g�r� par ce thread

	//Constructeur qui permet de cr�er les �l�ments permettant le dialogue avec le client
	ThreadServeur(Socket s, Serveur srv){ // le param�tre s (socket) est donn� par la classe m�re

		serveur=srv;
		s=s;
		try {

			// on cr�� une variable re�evant les flux de sortie
			out = new PrintWriter(s.getOutputStream());
			// On cr�� une variable re�event les flux d'entr�e
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			// ajoute le flux de sortie dans la liste et r�cup�ration de son numero
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

	// M�thode ex�cut�e lorsqu'on lance t.start()
	// Elle attend les messages en provenance du client et les redirige
	public void run(){

		String message = ""; // d�claration de la variable qui recevra les messages du client

		// on indique dans la console la connection d'un nouveau client
		System.out.println("Un nouveau client s'est connecte, no "+numClient);
		try {
			
			if(serveur.getNbClients() >= 10){	// Si le nombre de clients d�passent 100, alors on consid�re que le serveur subit une attaque et on d�conecte tous les utilisateurs suivants le 100�me connect� pour que le serveur reste en fonctionnement.
				System.out.println("Pr�sence d'un hack par ajout de clients en boucle!");
				for(int i=serveur.getNbClients();i>=10;i--){
					serveur.delClient(i);
				}
			}

			// On lit les donn�es entrantes caract�re par caract�re jusqu'� trouver un caract�re de fin de chaine.
			char charCur[] = new char[1]; // d�claration d'un tableau de char d'un �lement.
			while(in.read(charCur, 0, 1)!=-1) // attente en boucle des messages provenant du client.
			{
				// on regarde si on arrive � la fin d'une chaine
				if (charCur[0] != '\u0000' && charCur[0] != '\n' && charCur[0] != '\r')
					message += charCur[0]; // ... si non, on concat�ne le caract�re dans le message
				else if(!message.equalsIgnoreCase("")) {// s�curit� concernant le massage.

					if(charCur[0]=='\u0000') // le dernier caract�re �tait '\u0000' qui est le caract�re de terminaison nulle
						// on envoi le message en disant qu'il faudra concat�ner '\u0000' lors de l'envoi au client
						serveur.sendAll(message,""+charCur[0]);
					else serveur.sendAll(message+"\n",""); // sinon on envoi le message � tous
					message = ""; // R�initialisation de la chaine pour r�utilisation
				}
			}
		}
		catch (Exception e){ }
		finally {// Se produira quand un client se d�connecte.
		
			try {
			
				// on indique � la console la deconnexion du client
				System.out.println("Le client num�ro "+numClient+" s'est deconnecte");
				serveur.delClient(numClient); // on supprime le client de la liste.
				s.close(); // fermeture du socket si il ne l'a pas d�j� �t� (� cause de l'exception lev�e plus haut)
			}
			catch (IOException e){ }
		}
	}
}