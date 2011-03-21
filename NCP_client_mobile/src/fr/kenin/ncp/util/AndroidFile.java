package fr.kenin.ncp.util;

import android.app.Activity;
/**
 * La class JarFile permet de recuperer le chemin du fichier jar executer, et de vérifier qu'il s'agit bien d'un jar. 
 * @author Poirier Kevin
 * @version 1.0.0
 */

public class AndroidFile {
	/**
	 * On passe a cette methode le chemin du fichier jar, et retourne vrai si celui ci est bien un dex.
	 * @param path
	 * @return boolean
	 */
	public static boolean isAPK(String path){
		if(path.substring(path.length()-3).equalsIgnoreCase("apk")){
			return true;
		}		
		return false;
	}
	
	public static String getPathAPK(Activity activity){
		String path="";
		path=activity.getApplication().getApplicationContext().getPackageResourcePath();
		return path;
	}
}
