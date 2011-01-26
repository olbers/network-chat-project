package core;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Class qui permet d'obtenir les hash md5 d'un fichier ou d'une chaine de caracteres.
 * @author Poirier Kevin
 * @version 1.0.0
 */

public class MD5 {
	
	/**
	 * Permet d'encoder une chaine de caracteres en chaine MD5.
	 * Source : {@link} http://www.respawner.fr/blog/index.php?post/2008/09/03/Generation-d-un-MD5-avec-Java
	 * @param password
	 * @return
	 */
	public static String encode(String password)
    {
        byte[] uniqueKey = password.getBytes();
        byte[] hash      = null;

        try
        {
            hash = MessageDigest.getInstance("MD5").digest(uniqueKey);
        }
        catch (NoSuchAlgorithmException e)
        {
            throw new Error("No MD5 support in this VM.");
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
	/**
	 * Methode qui permet de recuperer le checksum MD5 d'un fichier.
	 * Source : {@link} http://www.javalobby.org/java/forums/t84420.html
	 * @param file
	 * @return
	 */
	
	public static String checkSum(File file){
		InputStream is= null;
		String output="";
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			File f = file;
			is = new FileInputStream(f);				
			byte[] buffer = new byte[8192];
			int read = 0;
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] md5sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, md5sum);
			output = bigInt.toString(16);
			return output;
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (is!=null){
					is.close();
				}				
				return output;
			}
			catch(IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
	}
   

}
