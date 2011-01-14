package core;

import java.net.*;
import java.io.*;
import java.util.*;


public class Serveur {
	protected Vector _tabClients = new Vector(); // contiendra tous les flux de sortie vers les clients
	protected int _nbClients=0; // nombre total de clients connectés




	// Méthode qui affiche un message d'accueil.

	static protected void printWelcome(Integer port){
		System.out.println("Nom: ServeurChat");
		System.out.println("Auteur: HUCHEDÉ Cédric");
		System.out.println("Version 1.0");
		System.out.println("------------------------------");
		System.out.println();
		System.out.println("Le serveur est prêt.\n");
	}


	// Méthode qui envoie le message à tous les clients
	synchronized public void sendAll(String message,String sLast){
		PrintWriter out; // declaration d'une variable permettant l'envoi de texte vers le client
		for (int i = 0; i < _tabClients.size(); i++){   // parcours de la table des connectés
			out = (PrintWriter) _tabClients.elementAt(i); // On prend l'élément courrant
			if (out != null){  // Si l'élément est vide...
				// ecriture du texte passé en paramètre (et concaténation d'une string de fin de chaine si besoin)
				out.print(message+sLast);
				out.flush(); // envoi dans le flux de sortie
			}
		}
	}

	// Méthode permettant la destruction d'un client avec son identifiant.
	synchronized public void delClient(int i){
		_nbClients--; // On décrémente le nombre de clients.
		if (_tabClients.elementAt(i) != null) {  // Si l'élément existe
			_tabClients.removeElementAt(i); // on le supprime
		}
	}

	// Méthode qui permet d'ajouter un client à la liste des connectés
	synchronized public int addClient(PrintWriter out){
		_nbClients++; // On ajoute un client
		_tabClients.addElement(out); // on ajoute le nouveau flux de sortie au tableau
		return _tabClients.size()-1; // on retourne le numéro du client ajouté (size-1)
	}

	// Méthode permettant de retourner le nombre de clients connectés
	synchronized public int getNbClients(){
		
		return _nbClients; // retourne le nombre de clients connectés
	}

	public static void main(String args[]){
		Serveur serverChat = new Serveur(); // instance de la classe principale
		try
		{
			Integer port;
			if(args.length<=0){
				port=new Integer("1999"); //  port 1999 par défaut
			}
			else{
				port = new Integer(args[0]); // sinon il s'agit du numéro de port passé en argument
			}

			new Commandes(serverChat); // lance le thread de gestion des commandes

			ServerSocket ss = new ServerSocket(port.intValue()); // ouverture d'un socket serveur sur port
			printWelcome(port);
			
			while (true) {  // attente en boucle de connexion (bloquant sur ss.accept)
				new ThreadServeur(ss.accept(),serverChat); // un client se connecte, un nouveau thread client est lancé
				
			}
		}
		catch (Exception e) { }
	}
}