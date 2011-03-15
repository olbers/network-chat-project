package fr.kenin.ncp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import fr.kenin.ncp.util.Encode;

public class Connexion extends Activity {

	protected Context context;
	protected EditText ipTV;
	protected EditText portTV;
	protected EditText pseudoTV;
	protected EditText mdpTV;
	protected boolean isRegister;
	protected String ip;
	protected int port;
	private Client mClient;
	protected Intent intentAssoClient;
	private static final String TAG = "Connexion_NCP";

	private String connexionString;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context=this;
		setContentView(R.layout.connexion);
		this.ipTV = (EditText)findViewById(R.id.coEditText1);
		this.portTV = (EditText)findViewById(R.id.coEditText2);
		this.pseudoTV = (EditText)findViewById(R.id.coEditText3);
		this.mdpTV = (EditText)findViewById(R.id.coEditText4);
		this.isRegister=false;

		intentAssoClient = new Intent(this, Client.class);
		doBindService();


		((Button)findViewById(R.id.coButton1)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkText()){
					Log.d(TAG, "Check des cond ok");
					if(bindService(intentAssoClient, mConnexion, Context.BIND_AUTO_CREATE)){
						if(initConnexion()){
							mClient.setRequeteConnexion(connexionString);
							if(mClient.createClient(ip, port)){
								Intent intent = new Intent(context,Chat.class);
								intent.putExtra("isRegister", isRegister);;
								intent.putExtra("requetteCo", connexionString);
								startActivity(intent);
								finish();
							}else{
								Toast.makeText(context, "Erreur de connexion au serveur.", Toast.LENGTH_LONG).show();
							}
						}
					}else{
						Toast.makeText(context, "Service non lié.", Toast.LENGTH_LONG).show();
					}

				}				
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
		doUnbindService();
	}

	public boolean checkText(){
		ip = this.ipTV.getText().toString();
		try{
			port = Integer.parseInt( portTV.getText().toString());
		}catch (NumberFormatException e) {
			Toast.makeText(context, "Le port doit être un nombre.", Toast.LENGTH_LONG).show();
			return false;
		}
		String pseudo = this.pseudoTV.getText().toString();
		String mdp = this.mdpTV.getText().toString();
		if(mdp.length()!=0){
			mdp=Encode.MD5(mdp);
		}
		if(ip.length()==0 || port==0 || pseudo.length()==0){
			Toast.makeText(context, "Les champs Adresse, port et pseudo doivent être remplies.", Toast.LENGTH_LONG).show();
		}else{
			if(mdp.length()!=0){
				this.connexionString = "connect "+pseudo+" "+mdp;
				this.isRegister=true;
				return true;
			}else{
				this.connexionString = "connect "+pseudo;
				return true;
			}
		}
		return false;
	}

	private boolean initConnexion(){
		if(mClient != null){
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
