package core;

import java.io.*;
import java.util.Scanner;

// Classe qui gère les commandes tapées dans le serveur.
// Implémentation de l'interface runnable pour gérer un thread
public class Commandes implements Runnable {

	ThreadServeur threadServeur;
	Serveur serveur; // pour utilisation des méthodes de la classe principale
	BufferedReader in; // pour gestion du flux d'entrée (celui de la console)
	String strCommande=""; // contiendra la commande tapée
	Thread t; // contiendra le thread

	//** Constructeur : initialise les variables nécessaires **
	public Commandes(Serveur srv){
		serveur=srv; // passage de local en global
		// le flux d'entrée de la console sera géré plus pratiquement dans un BufferedReader
		in = new BufferedReader(new InputStreamReader(System.in));
		t = new Thread(this); // instanciation du thread
		t.start(); // demarrage du thread, la fonction run() est ici lancée
	}

	// Méthode qui attend les commandes tapées dans la console
	public void run(){

		try {
			// si aucune commande n'est tapée, on ne fait rien (bloquant)
			while ((strCommande=in.readLine())!=null){

				if (strCommande.equalsIgnoreCase("/quit")){ // commande "quit" detectée ...
					System.out.println("Fermeture du serveur");
					System.exit(0); // ... on ferme alors le serveur
				}

				else if(strCommande.equalsIgnoreCase("/total")){ // commande "total" detectée ...
					// ... on affiche le nombre de clients actuellement connectés
					System.out.println("Nombre de connectes : "+serveur.getNbClients());
					System.out.println("--------");
				}
				else if(strCommande.equalsIgnoreCase("/kick")){ // commande "kick" detectée ...
					// On demande le numéro du client à bannir.
					Scanner sc = new Scanner(System.in);
					System.out.println("Numéro du client à bannir?\n");
					int banClient = sc.nextInt();
				
					if(serveur.getNbClients()> banClient){
						serveur.delClient(banClient);
						threadServeur.stopSocket();
						System.out.println("Le client "+banClient+" a bien été banni.");

					}
					else{
						System.out.println("Le numéro du client que vous avez saisi n'existe pas.");
					}
				}
				else {
					// si la commande n'est ni "total", ni "quit" ni "kick", on informe l'utilisateur et on lui donne une aide
					System.out.println("Cette commande n'est pas supportee");
					System.out.println("Quitter : \"/quit\"");
					System.out.println("Nombre de connectes : \"/total\"");
					System.out.println("Bannir un utilisateur : \"/kick\"");
					System.out.println("--------");
				}
				System.out.flush(); // on affiche tout ce qui est en attente dans le flux
			}
		}
		catch (IOException e) {}
	}
}