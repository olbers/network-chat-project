package ncp_server.core;

import com.jezhumble.javasysmon.CpuTimes;
import com.jezhumble.javasysmon.JavaSysMon;

/**
 * Permet de faire diverse fonctionnalité du surveillance du serveur.
 * @author Poirier Kévin
 * @version 0.0.1
 */
public class Supervisor extends Thread {

	protected boolean run;

	private static Supervisor instance;

	protected Server server;

	protected JavaSysMon monitor;

	protected double cpuUsage;

	protected int cleanListClient;

	/**
	 * Constructeur de la classe  SUpervisor
	 */
	private Supervisor(){
		super();
		this.run=true;
		this.server=Server.getInstance();
	}
	/**
	 * Methode du singleton pour recupérer l'instance du Supervisor
	 * @return Supervisor
	 */
	public Supervisor getInstance(){
		if(null == instance)
			instance=new Supervisor();		
		return instance;
	}

	/**
	 * Methode run du thread
	 */
	public void run(){
		while(run){

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

}
