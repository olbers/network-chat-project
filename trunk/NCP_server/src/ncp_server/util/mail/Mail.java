package ncp_server.util.mail;

import ncp_server.core.client.Client;
import ncp_server.util.Log;
import ncp_server.util.option.Option;

import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;

/**
 * La class Mail permet d'envoyer les différents mail système de l'application.
 * @author Poirier Kévin
 * @version 0.1.0
 */

public class Mail {
	/**
	 * Permet de recuperer les option.
	 */
	protected Option option;
	/**
	 * Permet d'envoyer les log d'erreur
	 */
	protected Log log;
	/**
	 * Adresse du serveur smtp
	 */
	protected final  String smtpServer;
	/**
	 * Login du serveur smtp
	 */
	protected final String smtpLog;
	/**
	 * Mdp du serveur smtp
	 */
	protected final String smtpMdp;
	/**
	 * Constructeur
	 * @param option
	 * @param log
	 */
	public Mail(Option option,Log log){
		super();
		this.option=option;
		this.log=log;
		this.smtpServer=this.option.getSmtp();
		this.smtpLog=this.option.getSmtpLog();
		this.smtpMdp=this.option.getSmtpMdp();
	}
	/**
	 * La methode envoiMail permet d'envoyer les mails aux clients.
	 * @param adresseFrom
	 * @param sujet
	 * @param message
	 * @param adresseTo
	 */
	private void envoiMail(String adresseFrom, String sujet, String message,String adresseTo){
		try {
			Email email = new SimpleEmail();
			email.setHostName(this.smtpServer);
			email.setSmtpPort(587);
			email.setAuthenticator(new DefaultAuthenticator(this.smtpLog, this.smtpMdp));
			email.setTLS(true);
			email.setFrom(adresseFrom);
			email.setSubject(sujet);
			email.setMsg(message);
			email.addTo(adresseTo);
			email.send();
		} catch (EmailException e) {
			// TODO Auto-generated catch block
			this.log.err(e.toString());
		}

	}

	/**
	 * Envoi le mail d'inscription
	 * @param client
	 */
	public void inscriptionMail(Client client){
		String message;
		message = " Bonjour "+client.getPseudo()+", \n" +
		"Bienvenue sur le serveur de chat "+this.option.getNameServer()+"\n\n"+
		"Votre inscription a bien été prise en compte\n"+
		"Votre identifiant de connexion est : "+client.getPseudo()+"\n\n\n"+
		"Cordialement \n\n"+
		"L'équipe de "+this.option.getNameServer()+".";
		this.envoiMail(this.option.getInscriptionMail(), "Inscription", message, client.getMail());

	}
	/**
	 * Envoi un mail d'erreur
	 * @param message
	 */
	public void errorAdminMail(String message){
		this.envoiMail(this.option.getInscriptionMail(), "Erreur sur le serveur NCP", message, this.option.getAdminMail());		
	}

}
