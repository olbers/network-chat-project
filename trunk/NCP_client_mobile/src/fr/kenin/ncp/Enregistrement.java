package fr.kenin.ncp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

public class Enregistrement extends Activity {
	
	protected Intent intentAssoClient;
	private Client mClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enregistrement);
		intentAssoClient = new Intent(this, Client.class);
		doBindService();
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		doUnbindService();
	}
	
	private boolean initConnexion(){
		if(mClient != null){
			return true;
		}else{
			return false;
		}
	}	
	private void doBindService(){
		bindService(intentAssoClient, mConnexion, Context.BIND_AUTO_CREATE);
	}
	        
	private void doUnbindService() {
		unbindService(mConnexion);
	}

	private ServiceConnection mConnexion = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mClient = ((Client.ClientBinder) service).getService();
			initConnexion();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mClient=null;
		}

	};

}
