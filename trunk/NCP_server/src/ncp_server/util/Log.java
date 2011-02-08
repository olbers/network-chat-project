package ncp_server.util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ncp_server.util.option.Option;
	/**
	 * La classe log permettra une gestion des log de conversation et des log d'erreur.
	 * @author Kevin Poirier
	 * @version 0.1.2
	 */

public class Log {
	/**
	 * La chaine de caractere logChat, contient l'adresse du fichier logChat;
	 */
	private String logChat;
	/**
	 * La chaine de caractere logErr, contient l'adresse du fichier logErr;
	 */
	private String logErr;
	/**
	 * Le flux logFileChat permet d'ecrire dans le fichier logChat.
	 */
	private PrintWriter logFileChat;
	/**
	 * Le flux logFileErr permet d'ecrire dans le fichier logErr.
	 */	
	private PrintWriter logFileErr;
	/**
	 * L'objet option permettra de récuprer les option de connexion à la base de données.
	 * @see Option
	 */
	protected Option option;
	/**
	 * Le boolean chat permet de vérifier si le on va utilisé le log du chat.
	 */
	private boolean Chat=false;
	
	/**
	 * Constructeur de la class Log.
	 */
	public Log(){
		super();
		this.logChat="./log/chat.log";
		this.logErr="./log/err.log";	
		try {
			this.logFileErr= new PrintWriter(new FileWriter(this.logErr, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Cette méthode va écrire dans le fichier chat.log.
	 * @param logChat
	 */
	public void chat(String logChat){
		if(this.Chat){
			String dateLog = new DateString().dateLog();
			this.logFileChat.println(dateLog+logChat);
			this.logFileChat.flush();
		}
	}
	
	/**
	 * Cette méthode va écrire dans le fichier err.log.
	 * @param logERR
	 */
	public void err(String logERR){
		String dateLog = new DateString().dateLog();
		this.logFileErr.println(dateLog+logERR);
		this.logFileErr.flush();
	}
	/**
	 * Cette méthode permet de fermer les différent flux.
	 */
	public void exit(){
		if(this.Chat){
			this.logFileChat.close();
		}
		this.logFileErr.close();
	}
	/**
	 * la fonction init permet de recuperer dans le fichier option si on va log le chat.
	 * @param option
	 */
	public void init (Option option){
		this.option=option;
		try {			
			if(this.option.isLogChat()){
				this.logFileChat= new PrintWriter(new FileWriter(this.logChat,true));
				this.Chat=true;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
