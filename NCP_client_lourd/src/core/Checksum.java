package core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * La class cheksum permet de calculer le checksum d'un fichier en SHA1 et MD5
 * Source : {@link} http://www.javalobby.org/java/forums/t84420.html
 * @author Poirier Kevin
 * @version 1.0.0
 */

public class Checksum {
	/**
	 * Permet de recuperer le checksum en MD5.
	 * @param file
	 * @return checksum du fichier MD5
	 */
	public static String MD5(File file){
		return Checksum.hash(file, "MD5");
	}
	/**
	 * Permet de recuperer le checksum en SHA1.
	 * @param file
	 * @return checksum du fichier en SHA1
	 */
	public static String SHA1(File file){
		return Checksum.hash(file, "SHA1");
	}
	/**
	 * Permet de recuperer le checksum du SHA1 ou MD5.
	 * @param file
	 * @param algorithm
	 * @return checksum du fichier
	 */
	@SuppressWarnings("finally")
	public static String hash(File file,String algorithm){
		InputStream is= null;
		String output="";
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);
			File f = file;
			is = new FileInputStream(f);				
			byte[] buffer = new byte[8192];
			int read = 0;
			while( (read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}		
			byte[] sum = digest.digest();
			BigInteger bigInt = new BigInteger(1, sum);
			output = bigInt.toString(16);
			return output;
		}
		catch(IOException e) {
			throw new RuntimeException("Unable to process file for "+algorithm, e);
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
				throw new RuntimeException("Unable to close input stream for "+algorithm+" calculation", e);
			}
		}
	}
}
