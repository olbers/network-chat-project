package core;

import java.io.IOException;

public class ThreadEcoute extends Thread {

	protected Client client;
	protected boolean actif;


	public ThreadEcoute(Client client){
		super();
		this.client=client;
		this.actif=false;
	}

	public void run() {
		// TODO Auto-generated method stub
		while(actif){
			String message=null;
			try {
				if(this.client.getDepuisServeur().ready()){
					message = this.client.getDepuisServeur().readLine();
					System.out.println(message);
					if(message != null){
						this.client.typeMessage(message);
					}
				}
				sleep(50);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				this.actif=false;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void setActif(boolean actif){
		this.actif = actif;
	}
	public boolean isActif(){
		return actif;
	}

}
