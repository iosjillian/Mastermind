package edu.olemiss.jrbush.mastermind;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This class was mostly authored by Dr. Wilkins.
 * @author Dr. Wilkins
 *
 */
public class ShowScores extends Activity {

	DBAdapter db;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_scores);
		
		Button btn_done = (Button) findViewById(R.id.doneButton);
		btn_done.setBackgroundColor(Color.WHITE);
		btn_done.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				goBackToMain();
			}
		});
		
		RelativeLayout background = (RelativeLayout)findViewById(R.id.scoresBackground);
		background.setBackgroundColor(Color.DKGRAY);
		
		TextView tv = (TextView) findViewById(R.id.scores);
		tv.setTextColor(Color.WHITE);
		String s = "";
		db = new DBAdapter(this);
	    db.open(); 
	    for (int i=0; i<3; i++) {
	    	     Cursor c = db.getAllScores(i);
	    	     switch (i) {
	    	     case 0: s += "Easy\n";
	    	             break;
	    	     case 1: s += "\nMedium\n";
	    	             break;
	    	     case 2: s+= "\nHard\n";
	    	             break;
	    	     }
	    	     if (c.getCount() == 0) {
	    	    	    s += "     No scores\n";
	    	     }
	    	     else {
	    	    	    for (int pos=0; pos<c.getCount(); pos++) {
    	    	    	        c.moveToPosition(pos);
	    	    	    	    s += "     " + c.getInt(2) + "  " + c.getString(1) + "\n";

	    	    	    }
	    	     }
	    }
	    tv.setText(s);
	}
	
	private void goBackToMain(){
		Intent intent = new Intent(this, MainActivity.class);
	    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	    startActivity(intent);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}

}