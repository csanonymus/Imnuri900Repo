package ro.tineribanat.imnuri900.unused;

import ro.tineribanat.imnuri900.DatabaseHelper;
import ro.tineribanat.imnuri900.ShowHymn;
import ro.tineribanat.imnuriazsmr.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class Random extends Activity {

	Button bRandom, bGoToHymn;
	TextView tvChosen, tvCategoryRandom;
	Spinner categories;
	String[] categoriesOptions = { "Oricare", "Cantari de lauda",
			"Cantari de dimineata", "Cantari de seara",
			"Cantari de deschidere", "Cantari de inchidere",
			"Cantari de Sabat", "Iubirea lui Dumnezeu", "Nasterea lui Isus",
			"Viata si lucrarea lui Isus", "Jertfa lui Isus",
			"Bucuria mantuirii", "Pocainta", "Consacrare", "Recunostinta",
			"Incredere", "Lupta credintei", "Mangaiere", "Nadejdea mantuirii",
			"Revenirea lui Isus", "Dor de patria cereasca", "Biruinta",
			"Calea vietii", "Duhul Sfant", "Familia", "Indemn si avertizare",
			"Misionare", "Sfanta cina/Botez", "Nunta", "Inmormantare",
			"Casa Domnului", "Natura", "Pentru cei mici", "Diverse",
			"Supliment" };

	long screenHeight, screenWidth;
	int rangeBottom = -1, rangeTop = -1;

	public String randomText; // titlul imnului obtinut din random
	public int randomNumber; // numarul imnului obtinut din random

	boolean goToFlag = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_random);

		init();
	}

	private void init() {
		categories = (Spinner) findViewById(R.id.sCategories);
		setSpinnerSize();
		ArrayAdapter<String> categoriesAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, categoriesOptions);
		categoriesAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		categories.setAdapter(categoriesAdapter);

		tvChosen = (TextView) findViewById(R.id.tvChosen);
		tvCategoryRandom = (TextView) findViewById(R.id.tvCategoryRandom);

		bRandom = (Button) findViewById(R.id.bRandom);
		bRandom.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int spinnerChoice = categories.getSelectedItemPosition();
				getSelectionRange(spinnerChoice);
				randomNumber = generateRandomNumber(rangeBottom, rangeTop);
				DatabaseHelper database = new DatabaseHelper(Random.this);
				Cursor cHymn = database.queryFor(randomNumber + "");
				if ((cHymn != null) && (cHymn.moveToFirst())) {
					randomText = cHymn.getString(cHymn.getColumnIndex("tName"));
					randomNumber = cHymn.getInt(cHymn.getColumnIndex("tNumber"));
					String randomCategory = cHymn.getString(cHymn
							.getColumnIndex("tCategory"));

					String chosenText = "Imnul ales este : " + randomText
							+ ", numarul " + randomNumber;
					tvChosen.setText(chosenText);
					bRandom.setText("ReGenereaza");
					tvCategoryRandom.setText("Categorie : " + randomCategory);
					makeDetailsVisible();
					goToFlag = true;
				}
			}
		});

		bGoToHymn = (Button) findViewById(R.id.bGoToHymn);
		bGoToHymn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (goToFlag) {
					Intent i = new Intent(Random.this, ShowHymn.class);
					i.putExtra("number", Random.this.randomNumber+"");
					i.putExtra("title", Random.this.randomText);
					startActivity(i);
					finish();
				}
			}
		});
		makeDetailsInvisible();
	}

	private void makeDetailsVisible() {
		tvCategoryRandom.setVisibility(View.VISIBLE);
		tvChosen.setVisibility(View.VISIBLE);
		bGoToHymn.setVisibility(View.VISIBLE);
	}

	private void setSpinnerSize() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		screenWidth = displaymetrics.widthPixels;
		screenHeight = displaymetrics.heightPixels;

		android.widget.Spinner.LayoutParams p = new LinearLayout.LayoutParams(
				(int) (screenWidth / 3), LinearLayout.LayoutParams.WRAP_CONTENT);
	}

	private int generateRandomNumber(int min, int max) {
		int range = max - min + 1;
		java.util.Random random = new java.util.Random();
		int returner = random.nextInt(range) + min;
		return returner;
	}

	private void getSelectionRange(int selection) {
		switch (selection) {
		case 0:
			rangeBottom = 1;
			rangeTop = 900;
			break;
		case 1:
			rangeBottom = 1;
			rangeTop = 73;
			break;
		case 2:
			rangeBottom = 74;
			rangeTop = 82;
			break;
		case 3:
			rangeBottom = 83;
			rangeTop = 92;
			break;
		case 4:
			rangeBottom = 93;
			rangeTop = 101;
			break;
		case 5:
			rangeBottom = 102;
			rangeTop = 106;
			break;
		case 6:
			rangeBottom = 107;
			rangeTop = 141;
			break;
		case 7:
			rangeBottom = 142;
			rangeTop = 174;
			break;
		case 8:
			rangeBottom = 175;
			rangeTop = 192;
			break;
		case 9:
			rangeBottom = 193;
			rangeTop = 195;
			break;
		case 10:
			rangeBottom = 196;
			rangeTop = 209;
			break;
		case 11:
			rangeBottom = 210;
			rangeTop = 250;
			break;
		case 12:
			rangeBottom = 251;
			rangeTop = 269;
			break;
		case 13:
			rangeBottom = 270;
			rangeTop = 302;
			break;
		case 14:
			rangeBottom = 303;
			rangeTop = 339;
			break;
		case 15:
			rangeBottom = 340;
			rangeTop = 355;
			break;
		case 16:
			rangeBottom = 356;
			rangeTop = 445;
			break;
		case 17:
			rangeBottom = 446;
			rangeTop = 459;
			break;
		case 18:
			rangeBottom = 460;
			rangeTop = 478;
			break;
		case 19:
			rangeBottom = 479;
			rangeTop = 488;
			break;
		case 20:
			rangeBottom = 489;
			rangeTop = 524;
			break;
		case 21:
			rangeBottom = 525;
			rangeTop = 566;
			break;
		case 22:
			rangeBottom = 567;
			rangeTop = 571;
			break;
		case 23:
			rangeBottom = 572;
			rangeTop = 582;
			break;
		case 24:
			rangeBottom = 583;
			rangeTop = 586;
			break;
		case 25:
			rangeBottom = 587;
			rangeTop = 597;
			break;
		case 26:
			rangeBottom = 598;
			rangeTop = 650;
			break;
		case 27:
			rangeBottom = 651;
			rangeTop = 693;
			break;
		case 28:
			rangeBottom = 694;
			rangeTop = 699;
			break;
		case 29:
			rangeBottom = 700;
			rangeTop = 714;
			break;
		case 30:
			rangeBottom = 715;
			rangeTop = 722;
			break;
		case 31:
			rangeBottom = 723;
			rangeTop = 737;
			break;
		case 32:
			rangeBottom = 738;
			rangeTop = 746;
			break;
		case 33:
			rangeBottom = 747;
			rangeTop = 770;
			break;
		case 34:
			rangeBottom = 771;
			rangeTop = 777;
			break;
		case 35:
			rangeBottom = 778;
			rangeTop = 900;
			break;
		}
	}

	private void makeDetailsInvisible() {
		tvCategoryRandom.setVisibility(View.INVISIBLE);
		tvChosen.setVisibility(View.INVISIBLE);
		bGoToHymn.setVisibility(View.INVISIBLE);
	}
}
