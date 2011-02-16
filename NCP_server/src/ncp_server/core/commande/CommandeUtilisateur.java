package ncp_server.core.commande;

import ncp_server.core.Server;
import ncp_server.core.client.Client;
import ncp_server.util.db.RequeteSQL;
/**
 * Class qui gère les commande utilisateurs
 * @author Poirier Kevin
 * @version 0.0.1
 */
public class CommandeUtilisateur extends Commande {
	
	protected Server server;
	protected RequeteSQL requeteSQL;
	private static CommandeUtilisateur instance;
	/**
	 * Constructeur de la class CommandeUtilisateur
	 */
	private CommandeUtilisateur(){
		System.out.println("\t Initialisation des commandes utilisateurs...");
		this.server=Server.getInstance();
		this.requeteSQL=RequeteSQL.getInstance();
	}
	/**
	 * Methode singleton qui permet d'assurer une seul instance de la classe.
	 * @return instance
	 */
	public static CommandeUtilisateur getInstance(){
		if(null == instance)
			instance=new CommandeUtilisateur();
		return instance;
	}
	/**
	 * Permet de gerer les commandes utilisateur 
	 * @param chaine
	 * @param client
	 */
	public void traitementCommandeUtilisateur(String chaine,Client client){
		String commande;
		commande=recupCommande(chaine);
		if(commande.equalsIgnoreCase("me")){
			this.me(chaine, client);
		}else if(commande.equals("nick")){
			this.nick(chaine, client);
		}else if(commande.equals("total")){
		this.total(client);
		}
	}
	/**
	 * Gère la commande me
	 * @param chaine
	 * @param client
	 */
	public void me(String chaine, Client client){
		String message=client.getPseudo()+getMessage(chaine, 1);
		this.server.getLog().chat(message);
		this.server.envoieATous("#->"+message);		
	}
	/**
	 * Permet de changer de pseudo
	 * @param chaine
	 * @param client
	 */
	public void nick(String chaine,Client client){
		boolean authChange = true;
		String newPseudo = getMessage(chaine, 1);
		if(this.server.pseudoCo(newPseudo)){
			this.server.envoiePrive(client, "#Pseudo deja utilise");
			authChange=false;
		}else if (this.server.existCompte(newPseudo)){
			if(client.getBddID()==Integer.parseInt(this.requeteSQL.getBDDID(newPseudo).get(0))){
			}else{
				this.server.envoiePrive(client, "#Pseudo enregistrer dans la BDD. Compte non concordant");
				authChange=false;
			}
		}
		if(authChange){
			String oldNick=client.getPseudo();
			client.setPseudo(newPseudo);
			String message = oldNick+ "s'appelle desormais "+client.getPseudo();
			this.server.getLog().chat(message);
			this.server.envoieATous(message);
		}
	}
	/**
	 * Permet d'envoyer le nombre total de connectes.
	 * @param client
	 */
	public void total(Client client){
		this.server.envoiePrive(client, "#Il y'a actuellement "+this.server.getListClient().size()+" connecte(s)");
	}

}
