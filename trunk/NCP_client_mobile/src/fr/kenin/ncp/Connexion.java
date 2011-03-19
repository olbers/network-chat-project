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
	private Activity connexion;
	private static final String TAG = "Connexion_NCP";

	private String connexionString;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context=this;
		setContentView(R.layout.connexion);
		ipTV = (EditText)findViewById(R.id.coEditText1);
		portTV = (EditText)findViewById(R.id.coEditText2);
		pseudoTV = (EditText)findViewById(R.id.coEditText3);
		mdpTV = (EditText)findViewById(R.id.coEditText4);
		isRegister=false;
		connexion=this;
		intentAssoClient = new Intent(this, Client.class);
		doBindService();


		((Button)findViewById(R.id.coButton1)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkText()){
					Log.d(TAG, "Check des cond ok");
					if(bindService(intentAssoClient, mConnexion, Context.BIND_AUTO_CREATE)){
						Log.d(TAG, "Service bind ok");
						if(initConnexion()){
							Log.d(TAG, "Connect init ok");
							mClient.recupAPKMD5(connexion);
							mClient.setRequeteConnexion(connexionString);
							if(mClient.getSocketClient()==null){
								Log.d(TAG, "Socket null");
								if(mClient.createClient(ip, port)){
									Log.d("sync_NCP", "Recup main dans connexion (Create Return true)");
									lancementChat();
								}else{
									Toast.makeText(context, "Erreur de connexion au serveur.", Toast.LENGTH_LONG).show();
								}
							}else{
								mClient.envoiConnect();
								lancementChat();
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

	private void lancementChat(){
		char retourMD5;
		char retourConnect;
		do{
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			retourConnect=mClient.getRetourConnect();
			retourMD5=mClient.getRetourMD5();
			Log.d(TAG, "Entrer dans la methode lancementChat.");
			Log.d(TAG, "RetourMD5 :"+mClient.getRetourMD5()+"  RetourConnect :"+mClient.getRetourConnect());
			if((mClient.getRetourMD5()!='`' && mClient.getRetourConnect()!='`')|| retourConnect=='b'){
				if(mClient.getRetourMD5()=='9' && mClient.getRetourConnect()=='0'){
					Intent intent = new Intent(context,Chat.class);
					intent.putExtra("isRegister", isRegister);
					startActivity(intent);
					finish();
				}else{
					returnError();
				}
			}
		}while((retourMD5=='`' || retourConnect=='`')|| retourConnect=='b');
	}

	private void returnError(){
		Log.d(TAG, "Entrer dans la methode returnError.");
		Log.d(TAG, "RetourMD5 :"+mClient.getRetourMD5()+"  RetourConnect :"+mClient.getRetourConnect());
		if(mClient.getRetourMD5()=='8'){
			Toast.makeText(context, "Connexion impossible au serveur: Votre application n'est pas conforme", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='4'){
			Toast.makeText(context, "Connexion impossible au serveur: Ce pseudo est déjà réservé.", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='5'){
			Toast.makeText(context, "Connexion impossible au serveur: Le pseudo que vous avez saisi est un utilisateur déjà en ligne.", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='6'){
			Toast.makeText(context, "Connexion impossible au serveur: Mot de passe erroné.", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='7'){
			Toast.makeText(context, "Connexion impossible au serveur: Le serveur est saturé, réessayez ultérieurement.", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='a'){
			Toast.makeText(context, "Le compte que vous tentez d'utiliser est banni!", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='b'){
			Toast.makeText(context, "Votre adresse IP est bannie du serveur!", Toast.LENGTH_LONG).show();
		}
	}

	private boolean checkText(){
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
			mClient.recupAPKMD5(this);
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
