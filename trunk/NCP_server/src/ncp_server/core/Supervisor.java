package ncp_server.core;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

/**
 * Permet de faire diverse fonctionnalité du surveillance du serveur.
 * @author Poirier Kévin
 * @version 1.0.2
 */
public class Supervisor extends Thread {

	protected boolean run;

	private static Supervisor instance;

	protected Server server;

	protected JavaSysMon monitor;

	protected double cpuUsage;

	protected long cleanListClient;

	protected long cleanlistBanIP;

	protected long antiFlood;

	protected long ressource;

	protected long afk;

	protected long chSQL;

	/**
	 * Constructeur de la classe  SUpervisor
	 */
	private Supervisor(){
		super();
		System.out.println("Lancement du superviseur...");
		this.run=true;
		this.monitor= new JavaSysMon();
		this.server=Server.getInstance();
		this.antiFlood=System.currentTimeMillis()+5000;//Toutes les 15 secondes
		this.cleanListClient=System.currentTimeMillis()+30000;//Toutes les 30 secondes
		this.chSQL=System.currentTimeMillis()+60000;//Toutes les minutes.
		this.ressource=System.currentTimeMillis()+60000; //Toutes les minutes
		this.cleanlistBanIP=System.currentTimeMillis()+120000;//toutes les 2 minutes
		this.afk=System.currentTimeMillis()+300000; //Toutes les 5 minutes
	}
	/**
	 * Methode du singleton pour recupérer l'instance du Supervisor
	 * @return Supervisor
	 */
	public static Supervisor getInstance(){
		if(null == instance)
			instance=new Supervisor();		
		return instance;
	}

	/**
	 * Methode run du thread
	 */
	public void run(){
		while(run){
			if((System.currentTimeMillis()-this.antiFlood)>=0){
				//System.out.println("Appelle anti flood");
				this.server.antiFlood();
				this.antiFlood=System.currentTimeMillis()+5000;//Toutes les 15 secondes
			}
			if((System.currentTimeMillis()-this.cleanListClient)>=0){
				//System.out.println("Appelle cleanListClient");
				this.server.cleanListClient();
				this.cleanListClient=System.currentTimeMillis()+30000;//Toutes les 30 secondes
			}
			if((System.currentTimeMillis()-this.chSQL)>=0){
				//System.out.println("Appelle chSQL");
				this.server.chSQL();
				this.chSQL=System.currentTimeMillis()+60000;//Toutes les minutes.
			}
			if((System.currentTimeMillis()-this.ressource)>=0){
				//System.out.println("Appelle ressource");
				this.server.checkRessource(this.updateCPUusage(), this.updateFreeRam(),this.updateMemoryJVM());
				this.ressource=System.currentTimeMillis()+60000; //Toutes les minutes
			}			
			if((System.currentTimeMillis()-this.cleanlistBanIP)>=0){
				//System.out.println("Appelle cleanlistBanIP");
				this.server.cleanBanIP();
				this.cleanlistBanIP=System.currentTimeMillis()+120000;//toutes les 2 minutes
			}
			if((System.currentTimeMillis()-this.afk)>=0){
				//System.out.println("Appelle afk");
				this.server.antiAFK();
				this.afk=System.currentTimeMillis()+300000; //Toutes les 5 minutes
			}
			try {
				sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				this.server.getLog().err("Erreur dans le superviseur.");
			}

		}
	}
	/**
	 * Permet de connaitre l'usage du cpu.
	 * @return Double
	 */
	@SuppressWarnings("finally")
	public double updateCPUusage(){
		CpuTimes initialTimes=this.monitor.cpuTimes();
		double cpuUsage=0;
		try {
			Thread.sleep(500);
			cpuUsage = monitor.cpuTimes().getCpuUsage(initialTimes);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			this.server.log.err(e.toString());
		} finally{
			return cpuUsage;
		}	
	}
	/**
	 * Permet de connaître le % restant de ram
	 * @return Double
	 */
	public double updateFreeRam(){
		double free = this.monitor.physical().getFreeBytes();
		double total = this.monitor.physical().getTotalBytes();
		return free/total*100;		
	}
	/**
	 * Permet de surveiller de le % de mémoire allouer JVM restante.
	 * @return double
	 */
	public double updateMemoryJVM(){
		double free = Runtime.getRuntime().freeMemory();
		double total = Runtime.getRuntime().totalMemory();
		return (free/total)*100;
	}
	/**
	 * @return the run
	 */
	public boolean isRun() {
		return run;
	}
	/**
	 * @param run the run to set
	 */
	public void setRun(boolean run) {
		this.run = run;
	}
	

}
