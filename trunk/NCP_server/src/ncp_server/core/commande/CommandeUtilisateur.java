package ncp_server.core.commande;

import ncp_server.core.Server;
import ncp_server.core.client.Client;
import ncp_server.util.DateString;
import ncp_server.util.db.RequeteSQL;
/**
 * Class qui gère les commande utilisateurs
 * @author Poirier Kevin
 * @version 1.0.1
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
	public void traitementCommande(String chaine,Client client){
		String commande;
		commande=recupCommande(chaine);
		if(commande.equalsIgnoreCase("me"))
			this.me(chaine, client);
		else if(commande.equals("nick"))
			this.nick(chaine, client);
		else if(commande.equals("total"))
			this.total(client);
		else if(commande.equals("who"))
			this.who(chaine,client);
		else if(commande.equals("mp"))
			this.mp(chaine,client);
		else if(commande.equalsIgnoreCase("kick"))
			this.kick(chaine,client);
		else if(commande.equalsIgnoreCase("ban"))
			this.ban(chaine, client);
		else if(commande.equalsIgnoreCase("unban"))
			this.unBan(chaine, client);
		else if(commande.equalsIgnoreCase("info"))
			this.info(client);
		else if(commande.equalsIgnoreCase("banIP"))
			this.banIp(chaine, client);
		else if(commande.equalsIgnoreCase("unbanIP"))
			this.unBanIP(chaine, client);
		else if(commande.equalsIgnoreCase("restart"))
			this.restart(chaine,client);
		else if(commande.equalsIgnoreCase("stop"))
			this.stop(chaine,client);
	}
	/**
	 * Gère la commande me
	 * @param chaine
	 * @param client
	 */
	private void me(String chaine, Client client){
		String message=client.getPseudo()+" "+getMessage(chaine, 1);
		this.server.getLog().chat(message);
		this.server.envoieATous("#->"+message);		
	}
	/**
	 * Permet de changer de pseudo
	 * @param chaine
	 * @param client
	 */
	private void nick(String chaine,Client client){
		boolean authChange = true;
		String newPseudo = getMessage(chaine, 1);
		if(this.server.pseudoCo(newPseudo)){
			this.server.envoiePrive(client, "#Pseudo deja utilise");
			authChange=false;
		}else if (this.server.existCompte(newPseudo)){
			if(client.getBddID()==Integer.parseInt(this.requeteSQL.getBDDID(newPseudo).get(0))){
			}else{
				this.server.envoiePrive(client, "#Pseudo enregistre dans la BDD. Compte non concordant");
				authChange=false;
			}
		}
		if(authChange){
			String oldNick=client.getPseudo();
			client.setPseudo(newPseudo);
			String message ="#"+ oldNick+ " s'appelle desormais "+client.getPseudo();
			this.server.getLog().chat(message);
			this.server.envoieATous(message);
			this.server.affichListClient();
		}
	}
	/**
	 * Permet d'envoyer le nombre total de connectes.
	 * @param client
	 */
	private void total(Client client){		
		this.server.envoiePrive(client, "#Il y a actuellement "+this.server.totalClient()+" connecte(s)");
	}
	/**
	 * Permet de connaitre des informations sur les utilisateurs.
	 * @param chaine
	 * @param client
	 */
	private void who(String chaine,Client client){
		if (!this.server.isAdmin(client) && !this.server.isModerateur(client))
			this.server.commandeRefuse(client);
		else{
			String[] argument = recupArgument(chaine, 2);
			if(!this.server.pseudoCo(argument[1])){
				this.server.pseudoNonCo(argument[1], client);
			}else{
				Client clientWho=this.server.getClient(argument[1]);
				if(clientWho!=null){
					String message = "#Voici les information concernant : "+clientWho.getPseudo() +
					"[IP : "+clientWho.getIp()+"]";
					if(clientWho.getCompte()!=null){
						message=message + " [Nom du Compte : "+clientWho.getCompte()+"]";
					}
					if(clientWho.getMail()!=null){
						message=message+ " [Mail : "+clientWho.getMail()+"]";
					}
					this.server.envoiePrive(client, message);
				}
			}
		}		
	}
	/**
	 * Permet d'envoyer des message privé
	 * @param chaine
	 * @param clientExpe
	 */
	private void mp(String chaine,Client clientExpe){
		String[] argument = recupArgument(chaine, 2);
		Client clientRecep=this.server.getClient(argument[1]);
		String message=getMessage(chaine, 2);
		if(clientRecep==null)
			this.server.pseudoNonCo(argument[1], clientExpe);
		else{
			String date = new DateString().dateChat();
			this.server.getLog().chat(date+" "+clientExpe.getPseudo() +" à "+clientRecep.getPseudo()+": "+message);
			this.server.envoiePrive(clientExpe, "%"+date+" "+clientExpe.getPseudo()+" à "+clientRecep.getPseudo()+":"+message);
			this.server.envoiePrive(clientRecep, "%"+date+" "+clientExpe.getPseudo()+": "+message);
		}		
	}
	/**
	 * Commande qui permet de kick un utilisateur
	 * @param chaine
	 * @param client
	 */
	private void kick(String chaine,Client client){
		if (!this.server.isAdmin(client) && !this.server.isModerateur(client))
			this.server.commandeRefuse(client);
		else{
			String[] argument = recupArgument(chaine, 2);
			String message=getMessage(chaine, 2);
			if(this.server.pseudoCo(argument[1])){
				Client clientKicker = this.server.getClient(argument[1]);
				this.server.kick(clientKicker, client.getPseudo(), message);
			}else{
				this.server.pseudoNonCo(argument[1], client);
			}
		}
	}
	/**
	 * Commande qui permet de bannir un utilisateur
	 * @param chaine
	 * @param client
	 */
	private void ban(String chaine,Client client){
		if (!this.server.isAdmin(client) && !this.server.isModerateur(client))
			this.server.commandeRefuse(client);
		else{
			String[] argument = recupArgument(chaine, 2);
			String message=getMessage(chaine, 2);
			if(this.server.pseudoCo(argument[1])){
				Client clientBan = this.server.getClient(argument[1]);
				if(clientBan.getCompte()==null){
					this.server.envoiePrive(client, "#"+clientBan.getPseudo()+" n'a pas de compte, cependant vous pouvez le kick ou bannir son ip.");
				}else{
					this.server.ban(clientBan, client, message);
				}					
			}else{
				this.server.pseudoNonCo(argument[1], client);
			}			
		}
	}
	/**
	 * Commande qui permet de débannir un utilisateur
	 * @param chaine
	 * @param client
	 */
	private void unBan(String chaine, Client client){
		if (!this.server.isAdmin(client) && !this.server.isModerateur(client))
			this.server.commandeRefuse(client);
		else{
			String[] argument = recupArgument(chaine, 2);
			if(this.server.existCompte(argument[1]))
				this.server.unban(argument[1], client);
			else
				this.server.envoiePrive(client, "#Le compte: "+ argument[1]+" n'existe pas.");
		}
	}
	/**
	 * Permet d'avoir des informations sur le serveur
	 * @param client
	 */
	private void info(Client client){
		this.server.envoiePrive(client, "#Le serveur est actuellement en version : "+Server.version);
	}
	/**
	 * Permet de bannir une ip
	 * @param chaine
	 * @param client
	 */
	public void banIp(String chaine, Client client){
		if (!this.server.isAdmin(client) && !this.server.isModerateur(client))
			this.server.commandeRefuse(client);
		else{
			boolean verif=true;
			String ip="";
			int banTime=0;
			String[] argument = recupArgument(chaine, 3);
			if ( argument[1].matches( "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$" ) == false )
			{
				this.server.envoiePrive(client, "#L'IP n'est pas dans un format valide.  Format valide: [0-255].[0-255].[0-255].[0-255]" );
				verif=false;
			}else{
				ip = "/"+argument[1];
			}

			try{
				banTime = Integer.parseInt(argument[2]);
			}catch (NumberFormatException e) {
				this.server.envoiePrive(client, "#Attention le second paramètre n'est pas un nombre");
				verif=false;
			}

			if(verif){
				if(!this.server.ipIsBan(ip)){
					this.server.banIP(ip, banTime, client.getPseudo());
					this.server.envoiePrive(client, "#L'ip "+ argument[1]+" à été banni.");
				}else{
					this.server.envoiePrive(client, "#L'ip:" +argument[1]+" est déjà banni");
				}
			}	
		}
	}
	/**
	 * Permet de débannir une ip.
	 * @param chaine
	 * @param client
	 */
	public void unBanIP(String chaine, Client client){
		if (!this.server.isAdmin(client) && !this.server.isModerateur(client))
			this.server.commandeRefuse(client);
		else{
			boolean verif = true;
			String[] argument = recupArgument(chaine, 2);
			if ( argument[1].matches( "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$" ) == false )
			{
				this.server.envoiePrive(client, "#L'IP n'est pas dans un format valide.  Format valide: [0-255].[0-255].[0-255].[0-255]" );
				verif=false;
			}
			String ip = "/"+argument[1];
			if(verif){
				if(this.server.ipIsBan(ip)){
					this.server.unBanIP(ip);
					this.server.envoiePrive(client, "#L'IP "+argument[1]+" n'est plus banni");
				}else{
					this.server.envoiePrive(client, "#L'IP "+argument[1]+" n'est pas banni.");
				}				
			}
		}
	}
	/**
	 * Commande qui permet de redémarrer le serveur.
	 * @param chaine
	 * @param client
	 */
	public void restart(String chaine,Client client){
		if (!this.server.isAdmin(client) )
			this.server.commandeRefuse(client);
		else{
			boolean verif=true;
			String[] argument = recupArgument(chaine, 2);
			int decompte=0;
			try{
				decompte = Integer.parseInt(argument[1]);
			}catch (NumberFormatException e) {
				this.server.envoiePrive(client, "#Attention le paramètre n'est pas un nombre");
				verif=false;
			}
			if(verif){
				this.server.procedureRestartorStop(decompte, true,client.getCompte());
			}
			
		}
	}
	/**
	 * Commande qui permet de stopper le serveur.
	 * @param chaine
	 * @param client
	 */
	public void stop(String chaine,Client client){
		if (!this.server.isAdmin(client) )
			this.server.commandeRefuse(client);
		else{
			boolean verif=true;
			String[] argument = recupArgument(chaine, 2);
			int decompte=0;
			try{
				decompte = Integer.parseInt(argument[1]);
			}catch (NumberFormatException e) {
				this.server.envoiePrive(client, "#Attention le paramètre n'est pas un nombre");
				verif=false;
			}
			if(verif){
				this.server.procedureRestartorStop(decompte, false,client.getCompte());
			}
			
		}
	}
}