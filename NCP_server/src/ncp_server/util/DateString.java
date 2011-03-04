package ncp_server.util;

import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * La class DateString permet de récuperer la date et l'heure, ou la date et l'heure en fonction des besoins.
 * @author Poirier Kévin
 * @version 0.2.0
 */

public class DateString {
	/**
	 * La fonction dateChat permet de donner la l'heure pour l'afficher dans le chat.
	 * @return L'heure  sous la forme [hh:mm:ss]
	 */
	public String dateChat(){
		String date="";
		int heure,minute,seconde;
		GregorianCalendar cal = new GregorianCalendar();
        heure = cal.get(Calendar.HOUR_OF_DAY);
        minute= cal.get(Calendar.MINUTE);
        seconde= cal.get(Calendar.SECOND);
		date="["+heure+":"+minute+":"+seconde+"]";
		return date;
	}
	/**
	 * La fonction dateLog permet de donner la date et l'heure pour les logs.
	 * @return La date et l'heure  sous la forme [jj/MM hh:mm:ss]
	 */
	public String dateLog(){
		String date="";
		int jour,mois,heure,minute,seconde;
		GregorianCalendar cal = new GregorianCalendar();
		mois = cal.get(Calendar.MONTH)+1;
        jour = cal.get(Calendar.DAY_OF_MONTH);
        heure = cal.get(Calendar.HOUR_OF_DAY);
        minute= cal.get(Calendar.MINUTE);
        seconde= cal.get(Calendar.SECOND);
		date="["+jour+"/"+mois+" "+heure+":"+minute+":"+seconde+"]";
		return date;
	}
	/**
	 * La fonction dateSQL permer de donner la date qui sera affiche dans la BDD
	 * @return La date sous la forme jj-mm-aa
	 * @since 1.1.0
	 */ 
	public String dateSQL(){
		String date="";
		int jour,mois,annee;
		GregorianCalendar cal = new GregorianCalendar();
		mois = cal.get(Calendar.MONTH)+1;
        jour = cal.get(Calendar.DAY_OF_MONTH);
        annee = cal.get(Calendar.YEAR);
		date=jour+"-"+mois+"-"+annee;
		return date;
		
	}
}
