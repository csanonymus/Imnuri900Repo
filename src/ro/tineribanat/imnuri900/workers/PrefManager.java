package ro.tineribanat.imnuri900.workers;

import ro.tineribanat.imnuriazsmr.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

public class PrefManager {

	private static String INITIAL_START = "initialStart";
	private static String HYMN_TEXT_SIZE = "hymnTextSize";
	private static String NIGHT_MODE = "nightMode";

	Context context;
	SharedPreferences sp;

	public PrefManager(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		sp = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
	}
	
	//PagerSlidingTabStrip color	
	public int getActionBarColor() {
		return sp.getInt("actionBarColor", 0);
	}
	
	public void setCurrentColor(int color) {
		Editor e = sp.edit();
		e.putInt("actionBarColor", color);
		e.commit();
	}
	
	
	////////***NIGHT MODE***////////
	public void setNightModeOn() {
		Editor e = sp.edit();
		e.putBoolean(NIGHT_MODE, true);
		e.commit();
	}
	
	public void setNightModeOff() {
		Editor e = sp.edit();
		e.putBoolean(NIGHT_MODE, false);
		e.commit();
	}
	
	public boolean getNightMode() {
		boolean nightMode = sp.getBoolean(NIGHT_MODE, false);
		return nightMode;
	}

	
	////////***HYMN TEXT SIZE***///////
	public void setHymnTextSize(int size) {
		Editor e = sp.edit();
		e.putInt(HYMN_TEXT_SIZE, size);
		e.commit();
	}

	public int getHymnTextSize() {
		int size = sp.getInt(HYMN_TEXT_SIZE, -1);
		if (size != -1) {
			return size;
		}
		return 24;
	}

	
	////////***FIRST TIME STARTING APP***///////
	public void setFirstTime() {
		Editor e = sp.edit();
		e.putString(INITIAL_START, "no");
		e.commit();
	}

	public boolean getFirstTime() {
		String firstTime = sp.getString(INITIAL_START, null);
		if (firstTime == null) {
			return true;
		}
		return false;
	}
}
