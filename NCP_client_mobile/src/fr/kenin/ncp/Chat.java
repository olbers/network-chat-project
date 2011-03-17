package fr.kenin.ncp;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Chat extends Activity {
	protected Context context;
	protected boolean isRegister;
	protected Intent intentAssoClient;
	private Client mClient;

	private final static int MENU_PARAMETRE = 1;
	private final static int MENU_REGISTER = 2;
	private final static int MENU_QUITTER = 3;
	
	private static final String TAG = "Chat_NCP";

	final Handler handler = new Handler(){

		/* (non-Javadoc)
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			updateText(mClient.getMessage());
		}


	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		context=this;		
		Bundle extra = this.getIntent().getExtras();
		if(extra != null){
			this.isRegister=extra.getBoolean("isRegister");
		}
		intentAssoClient = new Intent(this, Client.class);
		doBindService();
		updateText("");
		
		((Button)findViewById(R.id.chatButton1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = (EditText)findViewById(R.id.ChatEditText1);
				String message=et.getText().toString();
				mClient.recupMessageTaper(message);	
				et.setText("");
			}
			
		});
		
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onStart()
	 */
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
			
	}
	
	private void initChat(){
		if(mClient != null){
			updateList.start();
		}
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
		updateList.interrupt();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		doBindService();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0,MENU_PARAMETRE,Menu.NONE,"Liste des connectés");
		if(!isRegister)
			menu.add(0,MENU_REGISTER,Menu.NONE,"Enregistrement");		
		menu.add(0,MENU_QUITTER,Menu.NONE,"Quitter");
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case  MENU_PARAMETRE :

			Intent listCo = new Intent(context,ListeCo.class);
			startActivity(listCo);

			return true;
		case MENU_REGISTER :

			Intent register = new Intent(context,Enregistrement.class);
			startActivity(register);

			return true;
		case MENU_QUITTER :
			if(mClient!=null){
				mClient.deconnexion();
			}
			finish();
			return true;

		default : 
			return true;
		}
	}

	private void updateText(String message){
		TextView text = ((TextView)findViewById(R.id.chatTextView1));
		text.setText(Html.fromHtml(message));
		text.postInvalidate();
	}

	private ServiceConnection mConnexion = new ServiceConnection(){

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			mClient = ((Client.ClientBinder)service).getService();
			initChat();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			mClient=null;
		}

	};
	
	private void doBindService() {
		bindService(intentAssoClient, mConnexion, Context.BIND_AUTO_CREATE);
	}
	        
	private void doUnbindService() {
		mClient.stopSelf();
		unbindService(mConnexion);
		stopService(intentAssoClient);
	}

	Thread updateList = new Thread(new Runnable(){
		@Override
		public void run() {
			if(mClient!=null){
				while(!mClient.getSocketClient().isClosed()){
					Message msg = handler.obtainMessage();
					handler.sendMessageAtFrontOfQueue(msg);
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}else{
				Log.d(TAG, "mClient is null");
			}
		}

	});


}
