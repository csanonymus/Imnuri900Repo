package ro.tineribanat.imnuri900;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
	
	private static String DB_NAME = "Imnuri";
	private static int DB_VERSION = 1;

	public DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		String stringToExecute = "CREATE TABLE Imnuri ( "+
				"tId INTEGER PRIMARY KEY AUTOINCREMENT,"+
                "tName TEXT NOT NULL,"+
                "tNumber INTEGER NOT NULL,"+ 
                "tContent TEXT NOT NULL, "+
                "tAuthor VARCHAR(100), "+
                "tCategory TEXT NOT NULL,"+
                "tHasSheet TEXT NOT NULL,"+
                "tHasMP3 TEXT NOT NULL);";
		db.execSQL(stringToExecute);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean initialInsert() {
		
		return false;
	}
	
	public Cursor getAll() {
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor c = database.rawQuery("SELECT * FROM Imnuri;", null);
		Cursor s = c;
		if(s.moveToFirst()) {
			return c;
		}
		return null;
	}
	
	public Cursor queryFor(String text) {
		String localQuery = null;
		boolean withNumber = false;
		SQLiteDatabase sqlite = this.getReadableDatabase();
		Integer myNumber = null;
		try {
			myNumber = Integer.parseInt(text);
			localQuery = "SELECT * FROM Imnuri WHERE tNumber = ?";
			withNumber = true;
		} catch(NumberFormatException e) {
			localQuery = "SELECT * FROM Imnuri WHERE tName = ?";
		}
		Cursor c = null;
		try {
			if(withNumber) {
				c = sqlite.rawQuery(localQuery, new String[] {myNumber.toString()});
			} else {
				c = sqlite.rawQuery(localQuery, new String[] {text.toString()});
			}
			return c;
		} catch(NullPointerException e) {
			e.printStackTrace();
			return c;
		}
	}

}
