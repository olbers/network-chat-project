package core;

import java.io.*;
import java.util.Scanner;

// Classe qui g�re les commandes tap�es dans le serveur.
// Impl�mentation de l'interface runnable pour g�rer un thread
public class Commandes implements Runnable {

	ThreadServeur threadServeur;
	Serveur serveur; // pour utilisation des m�thodes de la classe principale
	BufferedReader in; // pour gestion du flux d'entr�e (celui de la console)
	String strCommande=""; // contiendra la commande tap�e
	Thread t; // contiendra le thread

	//** Constructeur : initialise les variables n�cessaires **
	public Commandes(Serveur srv){
		serveur=srv; // passage de local en global
		// le flux d'entr�e de la console sera g�r� plus pratiquement dans un BufferedReader
		in = new BufferedReader(new InputStreamReader(System.in));
		t = new Thread(this); // instanciation du thread
		t.start(); // demarrage du thread, la fonction run() est ici lanc�e
	}

	// M�thode qui attend les commandes tap�es dans la console
	public void run(){

		try {
			// si aucune commande n'est tap�e, on ne fait rien (bloquant)
			while ((strCommande=in.readLine())!=null){

				if (strCommande.equalsIgnoreCase("/quit")){ // commande "quit" detect�e ...
					System.out.println("Fermeture du serveur");
					System.exit(0); // ... on ferme alors le serveur
				}

				else if(strCommande.equalsIgnoreCase("/total")){ // commande "total" detect�e ...
					// ... on affiche le nombre de clients actuellement connect�s
					System.out.println("Nombre de connectes : "+serveur.getNbClients());
					System.out.println("--------");
				}
				else if(strCommande.equalsIgnoreCase("/kick")){ // commande "kick" detect�e ...
					// On demande le num�ro du client � bannir.
					Scanner sc = new Scanner(System.in);
					System.out.println("Num�ro du client � bannir?\n");
					int banClient = sc.nextInt();
				
					if(serveur.getNbClients()> banClient){
						serveur.delClient(banClient);
						threadServeur.stopSocket();
						System.out.println("Le client "+banClient+" a bien �t� banni.");

					}
					else{
						System.out.println("Le num�ro du client que vous avez saisi n'existe pas.");
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