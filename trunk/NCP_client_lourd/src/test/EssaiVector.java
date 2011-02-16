package test;

import java.util.Vector;

public class EssaiVector {
	
	
	protected Vector liste;
	
	public EssaiVector(){
		this.liste=new Vector();
		liste.add(0, "essai");
		liste.add(1, "test2");
	}

	public Vector getListe() {
		return liste;
	}

	public void setListe(Vector liste) {
		this.liste = liste;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EssaiVector essai=new EssaiVector();
		
//		Vector liste2=new Vector();
//		liste2.add(0, "salut");
//		liste2.add(1, "element");
//		System.out.println(liste2);
		Vector help= essai.getListe();
		System.out.println(help.get(0));
		System.out.println(help);
		
//		System.out.println(help);
	}

}
