package test;

import java.io.File;

import core.MD5;

public class MainTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("C:/Install.log");
		System.out.println(new MD5().checkSum(file));
		String chaine = "Salut";
		System.out.println(new MD5().encode(chaine));

	}

}
