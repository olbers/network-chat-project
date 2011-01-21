package core;

import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 * La class DateString permet de r�cuperer la date et l'heure, ou la date et l'heure en fonction des besoins.
 * @author Poirier K�vin
 * @version 1.0.0
 */

public class DateString {
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
        heure = cal.get(Calendar.HOUR);
        minute= cal.get(Calendar.MINUTE);
        seconde= cal.get(Calendar.SECOND);
		date="["+jour+"/"+mois+" "+heure+":"+minute+":"+seconde+"]";
		return date;
	}
	/**
	 * La fonction dateChat permet de donner la l'heyre pour l'afficher dans le chat.
	 * @return L'heure  sous la forme [hh:mm:ss]
	 */
	public String dateChat(){
		String date="";
		int heure,minute,seconde;
		GregorianCalendar cal = new GregorianCalendar();
        heure = cal.get(Calendar.HOUR);
        minute= cal.get(Calendar.MINUTE);
        seconde= cal.get(Calendar.SECOND);
		date="["+heure+":"+minute+":"+seconde+"]";
		return date;
	}
}
