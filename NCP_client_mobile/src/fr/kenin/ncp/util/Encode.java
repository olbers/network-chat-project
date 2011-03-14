package fr.kenin.ncp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Permet de recuperer le hash MD5 ou SHA1 d'un String.
 * Source : {@link} http://www.respawner.fr/blog/index.php?post/2008/09/03/Generation-d-un-MD5-avec-Java
 * @author Poirier Kevin
 * @version 1.0.0
 *
 */

public class Encode {
	/**
	 * Permet de recuperer le hash d'un String en MD5.
	 * @param chaine
	 * @return hash de la chaine en MD5
	 */
	public static String MD5(String chaine){
		return Encode.hash(chaine, "MD5");
	}
	/**
	 * Permet de recuperer le hash d'un String en SHA1.
	 * @param chaine
	 * @return hash de la chaine en SHA1
	 */
	public static String SHA1(String chaine){
		return Encode.hash(chaine, "SHA1");
	}
	/**
	 * Permet de recuperer le hash d'un String en MD5 ou SHA1.
	 * @param password
	 * @param algorithm
	 * @return hash de la chaine
	 */
	public static String hash(String password,String algorithm)
    {
        byte[] uniqueKey = password.getBytes();
        byte[] hash      = null;

        try
        {
            hash = MessageDigest.getInstance(algorithm).digest(uniqueKey);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new Error("No "+algorithm+" support in this VM.");
        }

        StringBuilder hashString = new StringBuilder();
        for (int i = 0; i < hash.length; i++)
        {
            String hex = Integer.toHexString(hash[i]);
            if (hex.length() == 1)
            {
                hashString.append('0');
                hashString.append(hex.charAt(hex.length() - 1));
            }
            else
                hashString.append(hex.substring(hex.length() - 2));
        }
        return hashString.toString();
    }

}
