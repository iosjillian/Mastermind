package edu.olemiss.jrbush.mastermind;

import android.annotation.TargetApi;
import android.app.DialogFragment;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;

/**
 * This class builds a DialogFragment with an EditText field and a Done button,
 * and declares an interface that must be implemented by the calling activity.
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class NameInputDialogFragment extends DialogFragment {

	EditText txtName;
	Button btn;
	static String dialogTitle;

	// Interface containing methods to be implemented 
	// by the calling activity
	public interface NameInputDialogListener {
		void onFinishInputDialog(String inputText);
	}

	// Set the title of the dialog window
	public void setDialogTitle(String title) {
		dialogTitle = title;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.activity_name_input_dialog_fragment, container);

		// get the EditText and Button views
		txtName = (EditText) view.findViewById(R.id.txtName);
		btn = (Button) view.findViewById(R.id.btnDone);


		// Event handler for the button
		btn.setOnClickListener(new View.OnClickListener() 
		{
			public void onClick(View view) {
				// Gets the calling activity
				NameInputDialogListener activity = (NameInputDialogListener) getActivity();

				activity.onFinishInputDialog(txtName.getText().toString());

				dismiss();         	
			}
		});  

		// Show the keyboard automatically
		txtName.requestFocus();
		getDialog().getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_VISIBLE);

		// Set the title for the dialog
		getDialog().setTitle(dialogTitle);

		return view;
	}

}
