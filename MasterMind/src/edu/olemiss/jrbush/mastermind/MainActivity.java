package edu.olemiss.jrbush.mastermind;

import edu.olemiss.jrbush.mastermind.NameInputDialogFragment.NameInputDialogListener;
import android.os.Bundle;
import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class is the MainActivity for MasterMind. The user can select difficulty,
 * enter their name, and/or turn on debug mode. This class implements NameInputDialogListener
 * in order to receive String input from the user.
 * @author Jillian Offutt
 *
 */
public class MainActivity extends Activity implements NameInputDialogListener {
	
	public static String name = "Anonymous";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		RelativeLayout background = (RelativeLayout)findViewById(R.id.background);
		background.setBackgroundColor(Color.DKGRAY);
		
		Button easy = (Button)findViewById(R.id.easy);
		easy.setBackgroundColor(Color.WHITE);
		
		Button medium = (Button)findViewById(R.id.medium);
		medium.setBackgroundColor(Color.WHITE);
		
		Button hard = (Button)findViewById(R.id.hard);
		hard.setBackgroundColor(Color.WHITE);
		
		Button nameEntry = (Button)findViewById(R.id.nameEntry);
		nameEntry.setBackgroundColor(Color.WHITE);
		nameEntry.setOnClickListener(myListener);
		
		TextView logo = (TextView)findViewById(R.id.logo);
		logo.setTextColor(Color.BLACK);
		
	}
	
	/**
	 * This method bundles gameplay values and sends them to the MasterMind
	 * activity to be handled.
	 * @param v
	 */
	public void chooseDifficulty(View v){
		Switch debugSwitch = (Switch) findViewById(R.id.switch1);
		boolean debug = debugSwitch.isChecked();
		
		Intent i = new Intent("edu.olemiss.jrbush.mastermind.MasterMind");
		switch(v.getId()){
		case R.id.easy:
			i.putExtra("difficulty", 1);
			i.putExtra("debug", debug);
			i.putExtra("name", name);
			startActivity(i);
			break;
		case R.id.medium:
			i.putExtra("difficulty", 2);
			i.putExtra("debug", debug);
			i.putExtra("name", name);
			startActivity(i);
			break;
		case R.id.hard:
			i.putExtra("difficulty", 3);
			i.putExtra("debug", debug);
			i.putExtra("name", name);
			startActivity(i);
			break;
		}
	}
	
	public OnClickListener myListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
				FragmentManager fragmentManager = getFragmentManager();
		     	NameInputDialogFragment inputNameDialog = new NameInputDialogFragment();
		        inputNameDialog.setCancelable(false);
		        inputNameDialog.setDialogTitle("Enter Name");
		        inputNameDialog.show(fragmentManager, "input dialog");
		}
	};
	
	/**
	 * Displays a toast
	 * @param message	Message to be displayed
	 */
	public void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onFinishInputDialog(String inputText) {
		name = inputText; // this value will be sent in the intent bundle
		
	}
	
}
	
