package ncp_server.core.commande;

import java.util.StringTokenizer;

/**
 * Cette classe permettra d'implémenté les methodes de bases pour les commandes
 * @author Poirier Kevin
 * @Version 1.0.0
 */

public class Commande {	
	/**
	 * Permer de recuperer les argument qui ont été envoyer.
	 * @param chaine
	 * @param nbArgument
	 * @return Tableau de String
	 */
	public String[] recupArgument(String chaine, int nbArgument){
		String[] argument = new String[nbArgument];
		StringTokenizer token;
		token = new StringTokenizer(chaine);
		for (int i=0;i<nbArgument;i++){
			if (token.hasMoreElements()){
				argument[i]=token.nextToken();
			}else{
				argument[i]="";
			}			
		}		
		return argument;
	}
	/**
	 * Permet de recuperer la commande qui à été envoyer
	 * @param chaine
	 * @return String
	 */
	public String recupCommande(String chaine){
		StringTokenizer token;
		token = new StringTokenizer(chaine);
		chaine = token.nextToken();
		return chaine;
	}

}
