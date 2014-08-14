package ro.tineribanat.imnuri900;

import ro.tineribanat.imnuriazsmr.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class ShowHymn extends Activity {

	TextView tvTitle, tvHymn, tvCategory;
	ImageView ivSheet, ivMP3;
	SeekBar hymnTextSize;

	String hymnNumber, hymnTitle;

	DatabaseHelper database;

	SharedPreferences sp;
	int textSize = 24;

	boolean textReady = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_hymn);

		init();
		showText();
	}

	private void showText() {
		String textSizeString = sp.getString("size", null);
		if(textSizeString == null) {
			textSizeString = "25";
		}
		textSize = Integer.parseInt(textSizeString);
		if (textSizeString != null) {
			tvHymn.setTextSize(textSize);
		}

		Intent i = getIntent();
		Bundle b = i.getExtras();

		hymnNumber = b.getString("number");
		hymnTitle = b.getString("title");

		Cursor c = database.queryFor(hymnNumber);
		if (c != null && c.moveToFirst()) {
			int count = c.getCount();
			String number = c.getString(c.getColumnIndex("tNumber"));
			String title = c.getString(c.getColumnIndex("tName"));
			String hymn = c.getString(c.getColumnIndex("tContent"));
			String category = c.getString(c.getColumnIndex("tCategory"));

			Log.i("APP", title + "   ///////   " + category + "  aaaaaaa   "
					+ hymn);
			tvTitle.setText(number + ". " + title);
			tvHymn.setText(hymn);
			tvCategory.setText(category);
			String hasSheet, hasMP3;
			hasSheet = c.getString(c.getColumnIndex("tHasSheet"));
			hasMP3 = c.getString(c.getColumnIndex("tHasMP3"));
			if (Boolean.parseBoolean(hasSheet)) {
				Bitmap bitmap = new BitmapFactory().decodeResource(
						getResources(), R.drawable.pdf);
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 30, 30,
						false);
				ivMP3.setImageBitmap(scaledBitmap);
			}
			if (Boolean.parseBoolean(hasMP3)) {
				Bitmap bitmap = new BitmapFactory().decodeResource(
						getResources(), R.drawable.mp3);
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 30, 30,
						false);
				ivMP3.setImageBitmap(scaledBitmap);
			}
			textReady = true;
			hymnTextSize.setProgress(textSize);
		}
	}

	private void init() {
		sp = ShowHymn.this.getSharedPreferences("textSize", MODE_PRIVATE);

		database = new DatabaseHelper(this);

		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvTitle.setTypeface(null, Typeface.BOLD);
		
		tvHymn = (TextView) findViewById(R.id.tvHymn);
		
		tvCategory = (TextView) findViewById(R.id.tvCategory);
		tvCategory.setTypeface(null, Typeface.BOLD_ITALIC);
		
		ivSheet = (ImageView) findViewById(R.id.ivSheet);
		ivMP3 = (ImageView) findViewById(R.id.ivMP3);

		hymnTextSize = (SeekBar) findViewById(R.id.sbTextSize);
		hymnTextSize.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				// TODO Auto-generated method stub
				if (textReady) {
					String size = progress + "";
					Editor spEditor = sp.edit();
					spEditor.putString("size", size);
					spEditor.commit();
					tvHymn.setTextSize(progress);
				}
			}
		});
		
	}
}
