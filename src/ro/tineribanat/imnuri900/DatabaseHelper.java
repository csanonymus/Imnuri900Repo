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
		String stringToExecute = "CREATE TABLE Imnuri ( "
				+ "tId INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ "tName TEXT NOT NULL," + "tNumber INTEGER NOT NULL,"
				+ "tContent TEXT NOT NULL, " + "tCategory TEXT NOT NULL,"
				+ "tHasSheet TEXT NOT NULL," + "tHasMP3 TEXT NOT NULL);";
		db.execSQL(stringToExecute);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

	public boolean insert(String hymnNumber, String hymnTitle,
			String hymnContent, String hasSheet, String hasMP3) {
		boolean mp3 = false, sheet = false;
		if (hasMP3.equals(new String("true"))) {
			mp3 = true;
		}
		if (hasSheet.equals(new String("true"))) {
			sheet = true;
		}
		int number = Integer.parseInt(hymnNumber);
		String category = this.getCategory(number);
		SQLiteDatabase d = this.getWritableDatabase();
		String query = "INSERT INTO Imnuri ('tNumber','tName','tContent', 'tCategory', 'tHasSheet', 'tHasMP3' "
				+ ") VALUES ("
				+ hymnNumber
				+ ", "
				+ "'"
				+ hymnTitle
				+ "',"
				+ "'"
				+ hymnContent
				+ "','"
				+ category
				+ "','"
				+ sheet
				+ "','"
				+ mp3 + "');";
		boolean added = false;
		try {
			d.execSQL(query);
			added = true;
		} catch (Exception e) {
			e.printStackTrace();
			added = false;
		}
		d.close();
		if (added) {
			return true;
		}
		return false;
	}

	public Cursor getAll() {
		SQLiteDatabase database = this.getReadableDatabase();
		Cursor c = database.rawQuery("SELECT * FROM Imnuri;", null);
		Cursor s = c;
		if (s.moveToFirst()) {
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
			localQuery = "SELECT * FROM Imnuri WHERE tNumber LIKE '" + myNumber
					+ "%';";
			withNumber = true;
		} catch (NumberFormatException e) {
			text = text.substring(0, 1).toUpperCase()
					+ text.substring(1, text.length());
			localQuery = "SELECT * FROM Imnuri WHERE tName LIKE ' " + text
					+ "%';";
		}
		Cursor c = null;
		try {
			c = sqlite.rawQuery(localQuery, null);
			return c;
		} catch (NullPointerException e) {
			e.printStackTrace();
			sqlite.close();
			return null;
		}
	}

	public Cursor getCategory(String category) {
		SQLiteDatabase d = this.getReadableDatabase();
		String query = "SELECT * FROM 'Imnuri' WHERE tCategory LIKE '"
				+ category + "';";
		Cursor c = d.rawQuery(query, null);
		return c;
	}

	private String getCategory(int number) {
		String returner = null;
		if ((number >= 1) && (number <= 73)) {
			returner = "Cantari de lauda";
		} else if ((number >= 74) && (number <= 82)) {
			returner = "Cantari de dimineata";
		} else if ((number >= 83) && (number <= 92)) {
			returner = "Cantari de seara";
		} else if ((number >= 93) && (number <= 101)) {
			returner = "Cantari de deschidere";
		} else if ((number >= 102) && (number <= 106)) {
			returner = "Cantari de inchidere";
		} else if ((number >= 107) && (number <= 141)) {
			returner = "Cantari de Sabat";
		} else if ((number >= 142) && (number <= 174)) {
			returner = "Iubirea lui Dumnezeu";
		} else if ((number >= 175) && (number <= 192)) {
			returner = "Nasterea lui Isus";
		} else if ((number >= 193) && (number <= 195)) {
			returner = "Viata si lucrarea lui Isus";
		} else if ((number >= 196) && (number <= 209)) {
			returner = "Jertfa lui Isus";
		} else if ((number >= 210) && (number <= 250)) {
			returner = "Bucuria mantuirii";
		} else if ((number >= 251) && (number <= 269)) {
			returner = "Pocainta";
		} else if ((number >= 270) && (number <= 302)) {
			returner = "Consacrare";
		} else if ((number >= 303) && (number <= 339)) {
			returner = "Recunostinta";
		} else if ((number >= 340) && (number <= 355)) {
			returner = "Recunostinta";
		} else if ((number >= 356) && (number <= 445)) {
			returner = "Incredere";
		} else if ((number >= 446) && (number <= 459)) {
			returner = "Lupta credintei";
		} else if ((number >= 460) && (number <= 478)) {
			returner = "Mangaiere";
		} else if ((number >= 479) && (number <= 488)) {
			returner = "Nadejdea mantuirii";
		} else if ((number >= 489) && (number <= 524)) {
			returner = "Revenirea lui Isus";
		} else if ((number >= 525) && (number <= 566)) {
			returner = "Dor de patria cereasca";
		} else if ((number >= 567) && (number <= 571)) {
			returner = "Biruinta";
		} else if ((number >= 572) && (number <= 582)) {
			returner = "Calea vietii";
		} else if ((number >= 583) && (number <= 586)) {
			returner = "Duhul Sfant";
		} else if ((number >= 587) && (number <= 597)) {
			returner = "Familia";
		} else if ((number >= 598) && (number <= 650)) {
			returner = "Indemn si avertizare";
		} else if ((number >= 651) && (number <= 693)) {
			returner = "Misionare";
		} else if ((number >= 694) && (number <= 699)) {
			returner = "Sfanta cina/Botez";
		} else if ((number >= 700) && (number <= 714)) {
			returner = "Nunta";
		} else if ((number >= 715) && (number <= 722)) {
			returner = "Inmormantare";
		} else if ((number >= 723) && (number <= 737)) {
			returner = "Casa Domnului";
		} else if ((number >= 738) && (number <= 746)) {
			returner = "Natura";
		} else if ((number >= 747) && (number <= 770)) {
			returner = "Pentru cei mici";
		} else if ((number >= 771) && (number <= 777)) {
			returner = "Diverse";
		} else if ((number >= 778) && (number <= 900)) {
			returner = "Supliment";
		}
		return returner;
	}

}
