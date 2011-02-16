package core;

import java.io.IOException;

public class ThreadEcoute extends Thread {

	protected Client client;


	public ThreadEcoute(Client client){
		super();
		this.client=client;
	}

	public void run() {
		// TODO Auto-generated method stub
		while(true){
			String message=null;
			client.depuisServeur=null;
			try {
				if(this.client.getDepuisServeur().ready()){
					message = this.client.getDepuisServeur().readLine();
					if(message != null){
						this.client.typeMessage(message);
					}
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
