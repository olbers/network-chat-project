package core;

import java.io.File;
import java.net.URISyntaxException;
/**
 * La class JarFile permet de recuperer le chemin du fichier jar executer, et de vérifier qu'il s'agit bien d'un jar. 
 * @author Poirier Kevin
 * @version 1.0.0
 */

public class JarFile {
	/**
	 * On passe a cette methode le chemin du fichier jar, et retourne vrai si celui ci est bien un jar.
	 * @param path
	 * @return boolean
	 */
	public static boolean isJar(String path){
		if(path.substring(path.length()-3).equalsIgnoreCase("jar")){
			return true;
		}		
		return false;
	}
	/**
	 * Cette methode permet de connaitre le chemin du fichier qui s'execute.
	 * @return Chemin du fichier
	 */
	@SuppressWarnings("finally")
	public static String pathJar(){
		String path="";
		try {
			File jarFile = new File
			(core.JarFile.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
			path = jarFile.getPath()+"\\"+ jarFile.getName();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return path;
		}
	}
	/**
	 * Permet de recupère le fichier jar
	 * @return File
	 */
	public static File getJarFile(){
		File jarFile=null;
		try {
			jarFile = new File
			(core.JarFile.class.getProtectionDomain()
					.getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return jarFile;
	}
}
