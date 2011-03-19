package fr.kenin.ncp;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class NCP extends Activity {
	/** Called when the activity is first created. */

	protected Context context;

	private static final String TAG = "Main_NCP";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		context=this;
		affNetwork();
		((Button)findViewById(R.id.mainButton1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Log.d(TAG, "Lancement de la connexion.");
				Intent intent = new Intent(context,Connexion.class);
				startActivity(intent);
				finish();
			}
		});
		((Button)findViewById(R.id.mainButton2)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		finish();
	}

	/**
	 * Permet de connaître l'état du réseau.
	 */
	public void affNetwork(){
		TextView tv = (TextView)findViewById(R.id.maintextView2);
		ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if(ni!=null){
			State ns = ni.getState();
			if(ns.compareTo(State.CONNECTED)==0)			
				tv.setText(ni.getTypeName());
		}else{
			tv.setText("Aucune connexion");
		}

	}


}