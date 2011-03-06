package fr.kenin.ncp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Connexion extends Activity {
	
	protected Context context;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context=this;
		setContentView(R.layout.connexion);
		
		((Button)findViewById(R.id.coButton1)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(context,Chat.class);
				startActivity(intent);
			}
		});
	}
	
}
