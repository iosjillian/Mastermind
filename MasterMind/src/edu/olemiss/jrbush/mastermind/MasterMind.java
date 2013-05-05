package edu.olemiss.jrbush.mastermind;

import java.util.Arrays;
import java.util.Random;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This class handles all of the Mastermind gameplay. 
 * This includes setup, game progression, win or loss feedback, score calculation, and storing scores in the database.
 * @author Jillian Offutt
 *
 */
public class MasterMind extends Activity {

	// Play specifications for difficulty settings
	public static final int EASY_PEG_NUMBER = 4;
	public static final int EASY_COLOR_NUMBER = 5;
	public static final int EASY_GUESS_NUMBER = 10;  

	public static final int MEDIUM_PEG_NUMBER = 4;
	public static final int MEDIUM_COLOR_NUMBER = 7;
	public static final int MEDIUM_GUESS_NUMBER = 10;

	public static final int HARD_PEG_NUMBER = 5;
	public static final int HARD_COLOR_NUMBER = 8;
	public static final int HARD_GUESS_NUMBER = 12;

	public static final int CIRCLE_SIZE = 30; // DP value to be converted into pixels for the primary circle size
	public static final int SELECTED_CIRCLE_SIZE = 60; // size when a color choice is selected

	//Array of color values that will be used throughout the app to color the pegs.
	public static final int colors[] = {Color.parseColor("#FBEF00"), Color.parseColor("#3614AD"), 
		Color.parseColor("#99EA00"), Color.parseColor("#FC7200"), Color.parseColor("#CC006F"), 
		Color.parseColor("#7A5CE3"), Color.parseColor("#48DBDB"), Color.parseColor("#002943")};

	public static boolean debugMode = false; // debugMode, when true, will display the answer after gameboard setup

	public static LinearLayout[] guessArray; // array of LinearLayouts up to the number of guesses allowed for the difficulty setting
	public static ImageView[] pegArray; // peg slots for the difficulty level
	public static ImageView[] colorArray; // color choices for the difficulty level
	public static TextView[] correctPlacementFeedback; // The "red pegs" that indicate a peg is the right color and in the right spot
	public static TextView[] correctColorFeedback; // The "white pegs" that indicate a peg is the right color but in the wrong spot
	public static int[] patternArray; // The randomly generated pattern the user will attempt to guess

	public static int selectedColor; // Color the user has selected
	public static int currentGuessRow; // Guess number the user is on
	public static int pegNumberForThisGame; // The number of pegs allowed in this game
	public static int correctPlacementNumber; // "red peg" count
	public static int correctColorNumber; // "white peg" count
	public static int currentLevel; // Difficulty level the user has chosen

	public static int fortyPixels; // Conversion of CIRCLE_SIZE to pixels
	public static int hundredPixels; // Conversion of SELECTED_CIRCLE_SIZE to pixels

	public static long startTime; // Start of gameplay (in milliseconds)
	public static long elapsedTime; // Total gameplay time (in seconds)
	public static int numGuessesTakenToWin; // Number of guesses the user took to win

	public static String name; // Name of user. If user does not input a name, default is "Anonymous"

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_master_mind);

		Intent i = getIntent();
		name = i.getStringExtra("name");
		debugMode = i.getBooleanExtra("debug", false);

		if(debugMode){
			showToast("Hi, Dr. Wilkins!");
		}

		int difficulty = i.getIntExtra("difficulty", 0);
		switch(difficulty){
		case 0:
			return;
		case 1:
			currentLevel = 0;
			setUpGameBoard(EASY_PEG_NUMBER, EASY_COLOR_NUMBER, EASY_GUESS_NUMBER);
			break;
		case 2:
			currentLevel = 1;
			setUpGameBoard(MEDIUM_PEG_NUMBER, MEDIUM_COLOR_NUMBER, MEDIUM_GUESS_NUMBER);
			break;
		case 3:
			currentLevel = 2;
			setUpGameBoard(HARD_PEG_NUMBER, HARD_COLOR_NUMBER, HARD_GUESS_NUMBER);
		}
	}

	/**
	 * This method builds the game board based on the difficulty parameters.
	 * @param pegNumber
	 * @param colorNumber
	 * @param guessNumber
	 */
	private void setUpGameBoard(int pegNumber, int colorNumber, int guessNumber){

		guessArray = new LinearLayout[guessNumber];
		pegArray = new ImageView[pegNumber];
		colorArray = new ImageView[colorNumber];
		correctPlacementFeedback = new TextView[guessNumber];
		correctColorFeedback = new TextView[guessNumber];
		pegNumberForThisGame = pegNumber;

		// Get the display metrics for this device and convert dps to pixels for use in the "drawOval" method
		Resources r = getResources();
		fortyPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, CIRCLE_SIZE, r.getDisplayMetrics());
		hundredPixels = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, SELECTED_CIRCLE_SIZE, r.getDisplayMetrics());

		setUpTextViews();

		LinearLayout colorLayout = (LinearLayout) findViewById(R.id.colorContainer);
		LinearLayout.LayoutParams colorLayoutSettings = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT
				);

		colorLayoutSettings.setMargins(30, 5, 5, 30);

		// Dynamically add one circle of each color in the "colors" array to the LinearLayout
		for (int i=0; i < colorNumber; i++) {
			colorArray[i] = new ImageView(this);
			ShapeDrawable circle = drawOval(colors[i], fortyPixels, fortyPixels);
			colorArray[i].setImageDrawable(circle);	
			colorArray[i].setId(i);
			colorArray[i].setOnClickListener(circleClick);
			colorLayout.addView(colorArray[i], colorLayoutSettings);
		}

		LinearLayout pegLayout = (LinearLayout) findViewById(R.id.pegContainer);
		pegLayout.setBackgroundColor(Color.DKGRAY);
		colorLayout.setBackgroundColor(Color.DKGRAY);
		pegLayout.setOrientation(LinearLayout.VERTICAL);
		pegLayout.setDividerPadding(20);

		// build the rows of LinearLayouts that will hold the game pegs, feedback numbers, and check answer button
		for(int i=0; i < guessNumber; i++){
			guessArray[i] = new LinearLayout(this);
			guessArray[i].setOrientation(LinearLayout.HORIZONTAL);
			guessArray[i].setBackgroundColor(Color.DKGRAY);
			guessArray[i].setId(i);
			guessArray[i].setPadding(5, 5, 5, 5);
			for(int j = 0; j < pegNumber ; j++){
				pegArray[j] = new ImageView(this);
				ShapeDrawable circle = drawOval(Color.WHITE, fortyPixels, fortyPixels);
				pegArray[j].setImageDrawable(circle);
				pegArray[j].setId(j);
				pegArray[j].setPadding(5, 5, 5, 5);
				pegArray[j].setOnClickListener(pegListener);
				pegArray[j].setTag(-1);
				guessArray[i].addView(pegArray[j]);
			}

			guessArray[i].addView(correctPlacementFeedback[i]);
			guessArray[i].addView(correctColorFeedback[i]);

			String ascii = "\u2714"; // Checkmark!
			Button check = new Button(this);
			check.setTextSize(20);
			check.setText(ascii);
			check.setBackgroundColor(Color.WHITE);
			check.setOnClickListener(checkButtonListener);

			guessArray[i].addView(check);

			pegLayout.addView(guessArray[i]);
		}

		// Set the first color to the selected color, and convey this to the user by displaying a larger circle
		ImageView firstColor = (ImageView)colorArray[0];
		selectedColor = colorArray[0].getId();
		firstColor.setImageDrawable(drawOval(colors[0], hundredPixels, hundredPixels));

		startGame(pegNumber, colorNumber);
	}

	/**
	 * This method creates the feedback textviews and stores them in their respective arrays.
	 */
	private void setUpTextViews() {
		for(int i = 0; i < guessArray.length; i++){

			correctPlacementFeedback[i] = new TextView(this);
			correctPlacementFeedback[i].setId(i);
			correctPlacementFeedback[i].setPadding(10, 5, 10, 5);
			correctPlacementFeedback[i].setTextSize(20);
			correctPlacementFeedback[i].setTextColor(Color.WHITE);
			correctPlacementFeedback[i].setText("0");

			correctColorFeedback[i] = new TextView(this);
			correctColorFeedback[i].setId(i);
			correctColorFeedback[i].setPadding(10, 5, 10, 5);
			correctColorFeedback[i].setTextSize(20);
			correctColorFeedback[i].setTextColor(Color.WHITE);
			correctColorFeedback[i].setText("0");

		}	
	}

	/**
	 * Creates the random pattern the user attempts to guess,
	 * starts the clock, and hides future guess rows.
	 * @param pegNumber
	 * @param colorNumber
	 */
	private void startGame(int pegNumber, int colorNumber) {

		createPattern(pegNumber, colorNumber);
		startTime = System.currentTimeMillis();

		currentGuessRow = 0;
		for(int i=currentGuessRow + 1; i < guessArray.length; i++){
			guessArray[i].setVisibility(View.INVISIBLE);
		}	
	}

	/**
	 * Sets the next playable peg row to visible and resets feedback numbers.
	 */
	private void continueGame(){
		guessArray[currentGuessRow].setVisibility(View.VISIBLE);
		correctPlacementNumber = 0;
		correctColorNumber = 0;
	}

	/**
	 * Creates the random pattern the user attempts to guess.
	 * @param pegNumber
	 * @param colorNumber
	 */
	private void createPattern(int pegNumber, int colorNumber) {
		patternArray = new int[pegNumber];
		Random generator = new Random();
		for(int i = 0; i < pegNumber; i++){
			patternArray[i] = generator.nextInt(colorNumber);
		}

		// Displays the pattern to the user if debug mode is on
		if(debugMode){
			showToast("Pattern: " + patternToString());
			showToast("Pattern: " + patternToString()); // Longer toast time!
		}
	}

	/**
	 * A display method for the pattern
	 * @return	Numbers from the colors[] array that represent the pattern
	 */
	private String patternToString(){
		String pattern = "";
		for(int i=0; i < patternArray.length; i++){
			pattern += patternArray[i] + " ";
		}
		return pattern;
	}

	/**
	 * Creates a ShapeDrawable object
	 * @param c	The fill color
	 * @param h	The height in pixels
	 * @param w	The width in pixels
	 * @return
	 */
	public ShapeDrawable drawOval(int c, int h, int w) {
		ShapeDrawable oval = new ShapeDrawable(new OvalShape());
		oval.setIntrinsicHeight(h);
		oval.setIntrinsicWidth(w);
		oval.getPaint().setColor(c);
		return oval;	
	}

	public OnClickListener circleClick = new OnClickListener() {
		public void onClick(View v) {
			selectedColor = v.getId();
			ImageView selected = (ImageView)v;
			int id = selectedColor; // I use the id variable for readability. I didn't find "selectedColor + 1" to be very clear.
			selected.setImageDrawable(drawOval(colors[id], hundredPixels, hundredPixels)); // This creates the "selected" color

			// Return all "unselected" colors are their original size
			for(int i = id + 1; i < colorArray.length; i++){
				colorArray[i].setImageDrawable(drawOval(colors[i], fortyPixels, fortyPixels));
			}

			if(id > 0){
				for(int i = id -1; i > 0; i--){
					colorArray[i].setImageDrawable(drawOval(colors[i], fortyPixels, fortyPixels));
				}
				colorArray[0].setImageDrawable(drawOval(colors[0], fortyPixels, fortyPixels));
			}

		}
	};

	public OnClickListener pegListener = new OnClickListener() {
		public void onClick(View v) {
			int id = v.getId();
			ImageView selected = (ImageView)v;
			selected.setImageDrawable(drawOval(colors[selectedColor], fortyPixels, fortyPixels)); // Replace the selected circle with a circle of the selected color
			selected.setTag(selectedColor); // set the color tag for pattern comparison
		}
	};

	public OnClickListener checkButtonListener = new OnClickListener() {
		public void onClick(View v) {

			v.setBackgroundColor(Color.LTGRAY);

			// get the color tags from the circles that are being checked
			int inputArray[] = new int[pegNumberForThisGame];
			for(int i=0; i < pegNumberForThisGame; i++){
				ImageView image = (ImageView)guessArray[currentGuessRow].getChildAt(i);
				int color = (Integer) image.getTag();
				if (color == -1){ // if the circle is white
					showToast("Please select a color and choose peg placement.");
					return;
				}
				inputArray[i] = color;
			}

			if(Arrays.equals(patternArray, inputArray)){ // Check to see if the user has the correct pattern
				// Get the scoring information
				long endTime = System.currentTimeMillis();
				elapsedTime = (endTime - startTime) / 1000;
				numGuessesTakenToWin = currentGuessRow;

				addScoreToDatabase();

				// Display a DialogFragment to the user that displays their score and gives the options "Play Again" and "View High Scores"
				showWinDialog();
			}
			else{

				// Make a copy of the correct pattern array
				int tempArray[] = new int[pegNumberForThisGame];
				for(int i=0; i < pegNumberForThisGame; i++){
					tempArray[i] = patternArray[i];
				}

				// Search for matches and cross them out
				for(int i=0; i < pegNumberForThisGame; i++){
					if (tempArray[i] == inputArray[i]){
						correctPlacementNumber++;
						// These values are no longer eligible
						inputArray[i] = -1;
						tempArray[i] = -2; 
					}
				}

				// Check for color matches in the remaining circles
				for(int i=0; i < pegNumberForThisGame; i++){
					for(int j=0; j < pegNumberForThisGame; j++){
						if(tempArray[i] == inputArray[j]){
							correctColorNumber++;
							inputArray[j] = -1;
							tempArray[i] = -2;
						}
					}
				}
				correctPlacementFeedback[currentGuessRow].setText("" + correctPlacementNumber);
				correctColorFeedback[currentGuessRow].setText("" + correctColorNumber);

				// if guesses remain, continue play
				if(currentGuessRow < guessArray.length - 1){
					currentGuessRow++;
					continueGame();
				}
				else{
					showLoseDialog();
				}

			}
		}
	};

	/**
	 * Uses DBAdapter to add new scores to the database
	 */
	private void addScoreToDatabase() {
		DBAdapter db = new DBAdapter(this);
		db.open();
		db.check_score(currentLevel, name, calculateScore());

	}

	/**
	 * Displays a toast
	 * @param message	Message to be displayed
	 */
	public void showToast(String message){
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}

	/**
	 * Creates a new EndGameDialog instance for the situation where the user has won
	 */
	private void showWinDialog() {
		DialogFragment newFragment = EndGameDialog.newInstance(R.string.you_win_, ("Your score: " + calculateScore()));
		newFragment.show(getFragmentManager(), "dialog");
	}

	/**
	 * Creates a new EndGameDialog instance for the situation where the user has won
	 */
	private void showLoseDialog(){
		DialogFragment newFragment = EndGameDialog.newInstance(R.string.you_lose, ("The correct pattern was " + patternToString()));
		newFragment.show(getFragmentManager(), "dialog");
	}

	/**
	 * Calculates the user's final score
	 * @return	user's score
	 */
	private int calculateScore() {
		return (int) ((80 * guessArray.length * pegNumberForThisGame) - (40 * numGuessesTakenToWin) - (2 * elapsedTime));
	}

	/**
	 * Moves to the ShowScores activity
	 */
	public void doPositiveClick() {
		Intent i = new Intent(this, ShowScores.class);
		startActivity(i);
	}

	/**
	 * Returns to the MainActivity and finishes activities in front of MainActivity on the stack
	 */
	public void doNegativeClick() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		this.startActivity(intent);

	}
}
