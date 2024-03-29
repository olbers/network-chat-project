package ncp_server.core.commande;

import java.util.StringTokenizer;

import ncp_server.core.client.Client;

/**
 * Cette classe permettra d'impl�ment� les methodes de bases pour les commandes
 * @author Poirier Kevin
 * @version 1.1.0
 */

public abstract class Commande {	
	
	
	protected abstract void traitementCommande(String chaine,Client client);
	/**
	 * Permer de recuperer les argument qui ont �t� envoyer.
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
	 * Permet de recuperer la commande qui � �t� envoyer
	 * @param chaine
	 * @return String
	 */
	public String recupCommande(String chaine){
		StringTokenizer token;
		token = new StringTokenizer(chaine);
		if(token.hasMoreTokens()){
			chaine = token.nextToken();
		}
		return chaine;
	}
	/**
	 * Permet de supprimer les commandes de la chaine
	 * @param chaine
	 * @param nbarg
	 * @return message
	 */
	public String getMessage(String chaine,int nbarg){
		String message="";
		StringTokenizer token;
		token = new StringTokenizer(chaine);
		int i = 0;
		while(token.hasMoreElements()){
			if(i==nbarg){
				message=token.nextToken();
			}else if(i>nbarg){
				message=message+" "+token.nextToken();
			}else{
				token.nextToken();
			}
			++ i;
		}		
		return message;
	}

}
