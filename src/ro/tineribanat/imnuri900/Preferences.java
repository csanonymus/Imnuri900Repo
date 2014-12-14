package ro.tineribanat.imnuri900;

import ro.tineribanat.imnuri900.workers.BarColorizer;
import ro.tineribanat.imnuri900.workers.DialogColors;
import ro.tineribanat.imnuri900.workers.PrefManager;
import ro.tineribanat.imnuri900.workers.SeekBarDialog;
import ro.tineribanat.imnuriazsmr.R;
import android.app.ActionBar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.view.MenuItem;

public class Preferences extends PreferenceActivity {

	Preference textSizePref, actionBarColor, donation;
	CheckBoxPreference cbNightMode;

	PrefManager prefs;

	Context c;
	ActionBar actionBar;
	BarColorizer barColorizer;

	boolean sdkPermits = false;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		prefs = new PrefManager(getApplicationContext());
		boolean night = prefs.getNightMode();
		if (night) {
			setTheme(R.style.ThemeHoloDark);
		}

		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);

		c = this;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= 11) {
			sdkPermits = true;
		}
		if (sdkPermits) {
			actionBar = getActionBar();
			barColorizer = new BarColorizer(this, actionBar);
		}
		prefs = new PrefManager(c);

		textSizePref = (Preference) findPreference("textSize");
		textSizePref
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						SeekBarDialog dialog = new SeekBarDialog(c);
						dialog.show();
						return false;
					}
				});

		cbNightMode = (CheckBoxPreference) findPreference("nightMode");
		cbNightMode
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// TODO Auto-generated method stub

						boolean isChecked = prefs.getNightMode();
						if (isChecked) {
							prefs.setNightModeOff();
						} else {
							prefs.setNightModeOn();
						}
						updateCheckBox();
						startActivity(getIntent());
						finish();
						return false;
					}
				});

		actionBarColor = findPreference("actionBarColor");
		actionBarColor
				.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

					@Override
					public boolean onPreferenceClick(Preference preference) {
						// TODO Auto-generated method stub
						DialogColors colors = new DialogColors(c);
						colors.show();
						colors.setOnDismissListener(new DialogInterface.OnDismissListener() {

							@Override
							public void onDismiss(DialogInterface dialog) {
								// TODO Auto-generated method stub
								if (sdkPermits) {
									barColorizer = new BarColorizer(c,
											actionBar);
								}
							}
						});

						return false;
					}
				});

		donation = findPreference("donate");
		donation.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				// TODO Auto-generated method stub
				String url = "http://imnuri.tineribanat.ro/doneaza";
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(url));
				startActivity(i);
				return false;
			}
		});

		if (android.os.Build.VERSION.SDK_INT > 11) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	private void updateCheckBox() {
		boolean checkState = prefs.getNightMode();
		cbNightMode.setChecked(checkState);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		super.onBackPressed();
		return super.onMenuItemSelected(featureId, item);
	}

}
