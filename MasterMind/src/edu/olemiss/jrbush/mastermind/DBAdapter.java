package edu.olemiss.jrbush.mastermind;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * A database adapter class authored by Dr. Wilkins.
 * @author Dr. Wilkins
 *
 */
public class DBAdapter {
	private static final String TAG = "DBAdapter";
	private static final String DATABASE_NAME = "Masterminddb";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_CREATE = 
			"create table scores(id integer primary key autoincrement, " +
			     "level integer not null, " +
			     "name text, " +
	             "score int);";
	
	private final Context context;
	
	protected DatabaseHelper DBHelper;
	protected SQLiteDatabase db;
	
	public DBAdapter(Context ctx) {
		this.context = ctx;
		DBHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(DATABASE_CREATE);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS scores");
			onCreate(db);
		}
	}
		
	public DBAdapter open() throws SQLException {
		db = DBHelper.getWritableDatabase();
		return this;
	}
		
	public void close() {
		DBHelper.close();
	}
		
	public long check_score(int level, String name, int score) {
		//if less than 5 scores in level, just insert
		//else find the id of the score at that level with lowest score
		// and update its name and score, if this score is (strictly) higher
		Cursor result = getAllScores(level);
		if (result.getCount() < 5) {
		     ContentValues initialValues = new ContentValues();
		     initialValues.put("level", level);
		     initialValues.put("name", name);
		     initialValues.put("score", score);
		     return db.insert("scores", null, initialValues);
		}
		else {
			result.moveToLast();  // since cursor is descending order by score
			long lowestScoreID = result.getInt(0);
			int lowestScore = result.getInt(2);
			if (score > lowestScore) {
			     if (updateScore(lowestScoreID, name, score))
				     return 1;
			     else return 0;
			}
			else return 0;
		}
	}
	
	public Cursor getAllScores(int level) {
		return db.query("scores", new String[] {"id", "name", "score"}, 
				"level" + "=" + level, null, null, null, "score desc");
	}
	
	public int resetScores(int level) {
		return db.delete("scores", "level="+level, null);
	}
		
	public boolean updateScore(long rowId, String name, int score) {
		ContentValues args = new ContentValues();
		args.put("name", name);
		args.put("score", score);
		return db.update("scores", args, "id="+rowId, null) > 0;
	}
}