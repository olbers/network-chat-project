package fr.kenin.ncp;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

public class Chat extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chat);
		TextView text = ((TextView)findViewById(R.id.chatTextView1));
		String message = "Salut <br> <font color='red'>tu va<br>  bien mois <br> nickqs dqsdqs<br>  dqsd</font>sqdq sdqs f<br>  fffff fffffffffffff<br>  ffffffff<br> " +
				"fdq<br>plop jhdqhd <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ijij <br>  ij<br>" +
				"Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> " +
				"Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> " +
				"Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> " +
				"Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> " +
				"Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br> Salut <br>  ";
		text.setText(Html.fromHtml(message));
	}
}
