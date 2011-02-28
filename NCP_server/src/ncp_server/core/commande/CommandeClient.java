package ncp_server.core.commande;

import java.util.ArrayList;

import ncp_server.core.Server;
import ncp_server.core.client.Client;
import ncp_server.util.DateString;
import ncp_server.util.db.RequeteSQL;
import ncp_server.util.mail.Mail;
/**
 * Class qui gère les commande clientes
 * @author Poirier Kevin
 * @version 1.0.2
 */

public class CommandeClient extends Commande {

	protected Server server;
	protected RequeteSQL requeteSQL;
	private static CommandeClient instance;

	/**
	 * Constructeur de la class CommandeClient.
	 */
	private CommandeClient(){
		System.out.println("\t Initialisation des commandes clientes...");
		this.server=Server.getInstance();
		this.requeteSQL=RequeteSQL.getInstance();
	}

	public static CommandeClient getInstance(){
		if(null == instance)
			instance=new CommandeClient();
		return instance;
	}
	/**
	 * Permet de gerer les commandes clients 
	 * @param chaine
	 * @param client
	 */
	public void traitementCommande(String chaine,Client client){
		String commande;
		commande=recupCommande(chaine);
		if (commande.equalsIgnoreCase("connect")){
			this.connect(chaine, client);
		}else if (commande.equalsIgnoreCase("md5")){
			this.md5(chaine, client);
		}else if (commande.equalsIgnoreCase("register")){
			this.register(chaine, client);
		}else if(commande.equalsIgnoreCase("deconnexion")){
			this.deconnexion(client);
		}

	}
	/**
	 * Permet d'enregistrer un client dans la base de données.
	 * @param chaine
	 * @param client
	 */
	private void register(String chaine, Client client){
		if(client.getBddID()==0){
			String[] argument;
			argument=this.recupArgument(chaine, 4);
			//System.out.println(argument[1]);
			if(this.server.pseudoCo(argument[1]) && (!argument[1].equalsIgnoreCase(client.getPseudo()))){
				this.server.envoiePrive(client, "5");//Pseudo déjà en ligne
			}else{
				ArrayList<String> testMail = this.requeteSQL.verifMail(argument[3]);
				ArrayList<String> testExist = this.requeteSQL.verifClient(argument[1]);
				if(testMail!=null){
					//Mail utilisé
					this.server.envoiePrive(client, "3");
				}else if (testExist!=null){
					//pseudo utilisé
					this.server.envoiePrive(client, "2");
				}else{
					this.requeteSQL.insertClient(argument[1], argument[2], argument[3],
							0, client.getIp().toString(), new DateString().dateSQL());
					client.setCompte(argument[1]);
					client.setMail(argument[3]);
					client.setBddID(Integer.parseInt((this.requeteSQL.getBDDID(argument[1]).get(0))));
					new Mail().inscriptionMail(client);
					this.server.envoiePrive(client, "1");//Message de confirmation 
				}
			}
		}
		this.server.affichListClient();
	}
	/**
	 * La methode md5 Permet de verifier si le client correspond bien à ce que l'on attend.
	 * @param chaine
	 * @param client
	 */	
	private void md5(String chaine, Client client){
		if (!client.isActiver()){
			String[] argument;
			argument=this.recupArgument(chaine, 2);
			//System.out.println(option.lourdMD5);
			//System.out.println(argument[1]);
			if(!this.server.getOption().isProtectMD5() || this.server.getOption().getLourdMD5().equalsIgnoreCase(argument[1])){
				this.server.envoiePrive(client, "9"); //ok
				client.setChMD5(true);
			}else{
				this.server.envoiePrive(client, "7");
			}
		}
	}
	/**
	 * Permet de connecter le client
	 * @param chaine
	 * @param client
	 */
	private void connect(String chaine, Client client){
		if(!client.isActiver()){
			String[] argument = this.recupArgument(chaine, 3);
			String compte = argument[1];
			String mdp = argument [2];
			boolean CheckPseudo = false;
			if (mdp.equalsIgnoreCase("")){				
				if(this.server.existCompte(compte)){ //enregistrer dans la BDD
					CheckPseudo=true;
					this.server.envoiePrive(client, "4");
				}else{				
					if(this.server.pseudoCo(compte)){
						CheckPseudo= true;
						this.server.envoiePrive(client, "5");
					}					
				}
				if(!CheckPseudo){
					client.setLvAccess(0);
					client.setPseudo(compte);
					//methode Activer Client;					
					this.server.envoiePrive(client, "0"); //ok
					this.server.activationClient(client);
				}
			}else {
				ArrayList<String> resultCompte = this.requeteSQL.connexionClient(compte, mdp);
				boolean checkConnect =false;
				if (this.server.verifPseudoMDP(compte, mdp)){//Client reconnu
					String [] getValBDD = this.recupArgument(resultCompte.get(0), 4);
					if(this.server.compteCo(Integer.parseInt(getValBDD[0]))){
						checkConnect= true;
						this.server.envoiePrive(client, "5");
					}
					if(!checkConnect){
						// Gestion des information
						client.setBddID(Integer.parseInt(getValBDD[0]));
						client.setPseudo(getValBDD[1]);
						client.setCompte(getValBDD[1]);
						client.setMail(getValBDD[2]);
						client.setLvAccess(Integer.parseInt(getValBDD[3]));
						if(client.getLvAccess()==-1){
							this.server.envoiePrive(client, "a"); //COmpte banni trouvé un message d'erreur
							this.server.clientDeconnexion(client);
						}else{
							this.requeteSQL.updateLastCo(new DateString().dateSQL(), client.getBddID());
							this.requeteSQL.updateIP(client.getIp().toString(), client.getBddID());
							this.server.envoiePrive(client, "0");//ok
							this.server.activationClient(client);
						}
					}
				}else{
					//Utilisateur enregistrer
					this.server.envoiePrive(client, "6");
				}
			}
		}
		this.server.affichListClient();
	}
	/**
	 * Merthode qui deconnecte le client.
	 * @param client
	 */
	private void deconnexion(Client client){
		this.server.deconnexionUtilisateur(client);
	}
}
