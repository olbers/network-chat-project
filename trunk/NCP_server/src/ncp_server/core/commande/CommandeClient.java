package ncp_server.core.commande;

import java.util.ArrayList;

import ncp_server.core.Server;
import ncp_server.core.client.Client;
import ncp_server.util.DateString;
import ncp_server.util.db.RequeteSQL;
import ncp_server.util.mail.Mail;
/**
 * 
 * @author Poirier Kevin
 * @version 1.0.0
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
	public void traitementCommandeClient(String chaine,Client client){
		String commande;
		commande=recupCommande(chaine);
		if (commande.equalsIgnoreCase("connect")){
			this.connect(chaine, client);
		}else if (commande.equalsIgnoreCase("md5")){
			this.md5(chaine, client);
		}else if (commande.equalsIgnoreCase("register")){
			this.register(chaine, client);
		}

	}
	/**
	 * Permet d'enregistrer un client dans la base de données.
	 * @param chaine
	 * @param client
	 */
	public void register(String chaine, Client client){
		if(client.getBddID()==0){
			String[] argument= new String[4];
			argument=this.recupArgument(chaine, 4);
			//System.out.println(argument[1]);
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
	/**
	 * La methode md5 Permet de verifier si le client correspond bien à ce que l'on attend.
	 * @param chaine
	 * @param client
	 */	
	public void md5(String chaine, Client client){
		if (!client.isActiver()){
			String[] argument= new String[2];
			argument=this.recupArgument(chaine, 2);
			//System.out.println(option.lourdMD5);
			//System.out.println(argument[1]);
			if(!this.server.getOption().isProtectMD5() || this.server.getOption().getLourdMD5().equalsIgnoreCase(argument[1])){
				this.server.envoiePrive(client, "1");
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
	public void connect(String chaine, Client client){
		if(!client.isActiver()){
			String[] argument = this.recupArgument(chaine, 3);
			String compte = argument[1];
			String mdp = argument [2];						
			if (mdp.equalsIgnoreCase("")){
				boolean CheckPseudo = false;
				ArrayList<String> testExist = this.requeteSQL.verifClient(compte);
				if(testExist!=null){ //Pas enregistrer dans la BDD
					CheckPseudo=true;
					this.server.envoiePrive(client, "4");
				}else{				
					for (int i=0;i<this.server.getListClient().size();i++){
						if (compte.equalsIgnoreCase(this.server.getListClient().get(i).getPseudo())){
							CheckPseudo= true;
							this.server.envoiePrive(client, "5");
							break;
						}					
					}
				}
				if(!CheckPseudo){
					client.setLvAccess(0);
					client.setPseudo(compte);
					//methode Activer Client;					
					this.server.envoiePrive(client, "1");
					this.server.activationClient(client);
				}

			}else {
				ArrayList<String> resultCompte = this.requeteSQL.connexionClient(compte, mdp);
				boolean checkConnect =false;
				if (resultCompte != null){//Client reconnu
					String [] getValBDD = this.recupArgument(resultCompte.get(0), 4);
					for (int i=0;i<this.server.getListClient().size();i++){
						if (Integer.parseInt(getValBDD[0])==this.server.getListClient().get(i).getBddID()){
							checkConnect= true;
							this.server.envoiePrive(client, "5");
							break;
						}					
					}
					if(!checkConnect){
						// Gestion des information
						client.setBddID(Integer.parseInt(getValBDD[0]));
						client.setPseudo(getValBDD[1]);
						client.setCompte(getValBDD[1]);
						client.setMail(getValBDD[2]);
						client.setLvAccess(Integer.parseInt(getValBDD[3]));
						this.requeteSQL.updateIP(client.getIp().toString(), client.getBddID());
						this.server.envoiePrive(client, "1");
						this.server.activationClient(client);
					}
				}else{
					//Utilisateur enregistrer
					this.server.envoiePrive(client, "6");
				}
			}

		}

	}
}
