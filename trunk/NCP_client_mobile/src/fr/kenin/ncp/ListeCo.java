package fr.kenin.ncp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;


public class ListeCo extends Activity {

	protected ListView lv;
	protected TextView saveTV;
	protected ArrayList<String> userCO;
	protected Intent intentAssoClient;
	private Client mClient;

	private static final String TAG = "ListCo_NCP";
	/**
	 * Permet de mettre à jour la liste des connecté.
	 * 
	 */
	private void upDateList(){
		String liste="";		
		liste=mClient.getUtilisateurList();
		TextView tv = (TextView)findViewById(R.id.listeCoTextView2);
		Log.d(TAG, "Liste des connecté :"+liste);
		tv.setText(liste);

	}

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listeco);
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



	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		initConnexion();

	}

	private boolean initConnexion(){
		if(mClient != null){
			upDateList();
			return true;
		}else{
			return false;
		}
	}	

	private void doBindService() {
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
