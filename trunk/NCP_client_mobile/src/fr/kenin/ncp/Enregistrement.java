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

public class Enregistrement extends Activity {
	
	protected Intent intentAssoClient;
	protected EditText ePseudo;
	protected EditText eMail ;
	protected EditText eMailVerif ;
	protected EditText ePWD ;
	protected EditText ePWDVerif ;
	private Client mClient;
	private String requeteRegister;
	protected Context context;
	
	private static final String TAG = "Enregistrement_NCP";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.enregistrement);
		context=this;
		intentAssoClient = new Intent(this, Client.class);
		doBindService();
		
		ePseudo = (EditText)findViewById(R.id.reEditText1);
		eMail = (EditText)findViewById(R.id.reEditText4);
		eMailVerif = (EditText)findViewById(R.id.reEditText5);
		ePWD = (EditText)findViewById(R.id.reEditText2);
		ePWDVerif = (EditText)findViewById(R.id.reEditText3);
		
		((Button)findViewById(R.id.reButton5)).setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(checkText()){
					if(bindService(intentAssoClient, mConnexion, Context.BIND_AUTO_CREATE)){
						Log.d(TAG, "Service bind ok");
						if(initConnexion()){
							Log.d(TAG, "Connect init ok");
							mClient.envoiRegister(requeteRegister);
							retourChat();
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
	private boolean checkText(){
		String pseudo = ePseudo.getText().toString();
		String mail  = eMail.getText().toString();
		String mailVerif = eMailVerif.getText().toString();
		String pwd = ePWD.getText().toString();
		String pwdVerif = ePWDVerif.getText().toString();
		Log.d(TAG, pseudo.length()+" "+ mail.length()+" "+ mailVerif.length()+" "+pwd.length()+" "+ pwdVerif.length());
		if ( pseudo.length()==0 || mail.length()==0 || mailVerif.length()==0 || pwd.length()==0 || pwdVerif.length()==0 ){
				Toast.makeText(this, "Tous les champs doivent être remplies.", Toast.LENGTH_LONG).show();
				return false;
		}else {
			if(!mail.equalsIgnoreCase(mailVerif)){
				Log.d(TAG, "Mail diff");
				Toast.makeText(this, "Adresse mail différente.", Toast.LENGTH_LONG).show();
				return false;
			}
			else if(!pwd.equalsIgnoreCase(pwdVerif)){
				Log.d(TAG, "MDP diff");
				Toast.makeText(this, "Mot de passe différent.", Toast.LENGTH_LONG).show();
				return false;
			}		
			else {
				requeteRegister="register "+pseudo+" "+Encode.MD5(pwd)+" "+mail;
				return true;
			}			
		}		
	}
	private void retourChat(){
		char retourRegister;
		do{
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			retourRegister = mClient.getRetourRegister();
			Log.d(TAG, "Entrer dans la methode retourchat.");
			Log.d(TAG, "RetourRegister :"+mClient.getRetourRegister());
			if(mClient.getRetourRegister()!='`'){
				if(mClient.getRetourRegister()=='1'){
					setResult(RESULT_OK);
					Toast.makeText(context, "Enregistrement effectué avec succès!", Toast.LENGTH_SHORT).show();
					finish();
				}else{
					returnError();
				}
			}
		}while(retourRegister=='`');
	}
	private void returnError(){
		Log.d(TAG, "Entrer dans la methode returnError.");
		Log.d(TAG, "RetourRegister :"+mClient.getRetourRegister());
		if(mClient.getRetourRegister()=='2'){
			Toast.makeText(context, "Enregistrement impossible: Pseudo déjà réservé par un autre utilisateur.", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='3'){
			Toast.makeText(context, "Enregistrement impossible: L'adresse E-Mail entrée est déjà enregistrée pour un autre compte.", Toast.LENGTH_LONG).show();
		}else if(mClient.getRetourConnect()=='c'){
			Toast.makeText(context, "Vous avez déjà un compte enregistré sur le serveur.", Toast.LENGTH_LONG).show();
		}
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
