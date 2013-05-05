package edu.olemiss.jrbush.mastermind;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.view.Menu;

/**
 * This is a DialogFragment that can be customized aside from the
 * positiveButton and negativeButton text strings.
 * @author Jillian Offutt
 *
 */
public class EndGameDialog extends DialogFragment {

	/**
	 * Constructor for EndGameDialog
	 * @param title		Must be a string id
	 * @param message	Message to be displayed
	 * @return
	 */
	public static EndGameDialog newInstance(int title, String message) {
		EndGameDialog fragment = new EndGameDialog();
        Bundle args = new Bundle();
        args.putInt("title", title);
        args.putString("message", message);
        fragment.setArguments(args);
        return fragment;
    }
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        int title = getArguments().getInt("title");
        String message = getArguments().getString("message");

        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_launcher)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(R.string.view_high_scores,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((MasterMind) getActivity()).doPositiveClick();
                        } 
                    }
                )
                .setNegativeButton(R.string.play_again,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((MasterMind)getActivity()).doNegativeClick();
                        }
                    }
                )
                .create();
    }

}
