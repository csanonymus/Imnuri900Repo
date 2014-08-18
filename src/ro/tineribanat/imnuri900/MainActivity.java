package ro.tineribanat.imnuri900;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import ro.tineribanat.imnuriazsmr.R;

import android.app.Activity;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity implements TextWatcher {

	boolean imnReady = false;
	boolean firstTimeLoadingSpinner = false;
	boolean choosingHymn = true;
	boolean inCategory = false;
	ProgressDialog progress;

	String title, number, content;

	LinearLayout llSearch;
	ListView listView;
	ListViewRow lvr;

	EditText etSearchString;
	DatabaseHelper database;

	ArrayList<String> list = new ArrayList<String>();
	Document doc;
	List<Node> docNodes;
	List<ListViewRow> listViewText;
	
	public static String progressValue;
	
	public Handler handler;

	final String[] categories = { "Cantari de lauda", "Cantari de dimineata",
			"Cantari de seara", "Cantari de deschidere",
			"Cantari de inchidere", "Cantari de Sabat", "Iubirea lui Dumnezeu",
			"Nasterea lui Isus", "Viata si lucrarea lui Isus",
			"Jertfa lui Isus", "Bucuria mantuirii", "Pocainta", "Consacrare",
			"Recunostinta", "Incredere", "Lupta credintei", "Mangaiere",
			"Nadejdea mantuirii", "Revenirea lui Isus",
			"Dor de patria cereasca", "Biruinta", "Calea vietii",
			"Duhul Sfant", "Familia", "Indemn si avertizare", "Misionare",
			"Sfanta cina/Botez", "Nunta", "Inmormantare", "Casa Domnului",
			"Natura", "Pentru cei mici", "Diverse", "Supliment" };
	
	//Timing 
	long start, stop, time;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		start = System.currentTimeMillis();//Timing start
		setLLSearchDimensions();
		init();
		SharedPreferences sp = this.getSharedPreferences("initialStart",
				MODE_PRIVATE);
		String isFirstTime = sp.getString("firstTime", null);
		if (isFirstTime != null) {
			populateListView();
		} else {
			Editor e = sp.edit();
			e.putString("firstTime", "no");
			e.commit();
			buildAlertDialog();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					initialInsert();
					listView.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							populateListView();
						}
					});
				}
			});
			t.start();

		}
	}

	private void init() {
		
		listViewText = new ArrayList<ListViewRow>();
		
		database = new DatabaseHelper(this);

		docNodes = new ArrayList<Node>();

		etSearchString = (EditText) findViewById(R.id.etSeachbox);
		etSearchString.addTextChangedListener(this);

		listView = (ListView) findViewById(R.id.lvHymns);
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (choosingHymn) {
					lvr = listViewText.get(position);
					String title = lvr.title;
					String number = lvr.number;

					Intent i = new Intent(MainActivity.this, ShowHymn.class);
					i.putExtra("title", title);
					i.putExtra("number", number);
					MainActivity.this.startActivity(i);
					overridePendingTransition(R.anim.slide_in_right,
							R.anim.slide_out_left);
				} else {
					String chosenCategory = categories[position];
					loadACategory(chosenCategory);
					choosingHymn = true;
					inCategory = true;
				}
			}
		});
	}

	private void buildAlertDialog() {
		firstTimeLoadingSpinner = true;
		progress = new ProgressDialog(this);
		progress.setCancelable(false);
		progress.setMessage("Generez baza de date. Va rugam asteptati...");
		progress.show();
	}

	private void populateListView() {
		Cursor cData = database.getAll();
		loadCursor(cData);
		if (firstTimeLoadingSpinner) {
			progress.dismiss();
		}
		stop = System.currentTimeMillis();
	}

	private void loadCursor(Cursor c) {
		list.clear();
		listViewText.clear();
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		listView.setAdapter(listAdapter);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String number = c.getString(c.getColumnIndex("tNumber"));
					String title = c.getString(c.getColumnIndex("tName"));

					lvr = new ListViewRow();
					lvr.number = number;
					lvr.title = title;
					listViewText.add(lvr);

					list.add(number + ". " + title);
				} while (c.moveToNext());
			}
		}
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		listView.setAdapter(listAdapter);
	}

	private void initialInsert() {
		
		
		List<String> queries = new ArrayList<String>();
		try {
			AssetManager assetManager = getAssets();
			InputStream is;
			is = assetManager.open("index.xml");
			doc = getDocument(is);
			NodeList n = doc.getChildNodes();
			Node node = n.item(0);
			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				String nodeName = nodeList.item(i).getNodeName().toString();
				boolean isEqual = nodeName.equals(new String("Imn"));
				if (isEqual) {
					docNodes.add(nodeList.item(i));
				}
			}

			for (int i = 0; i < docNodes.size(); i++) {
				Node imn = docNodes.get(i);

				Element hymn = (Element) docNodes.get(i);
				String sheet = hymn.getAttribute("IMG");
				String mp3 = hymn.getAttribute("MP3");
				NodeList imnNodes = imn.getChildNodes();
				for (int j = 0; j < imnNodes.getLength(); j++) {
					Node nodeItem = imnNodes.item(j);
					String tagName = nodeItem.getNodeName().toString();
					if (nodeItem.getClass().getName().contains("ElementImpl")) {
						if (tagName.equals(new String("Titlu"))) {
							Node hymnTitle = imnNodes.item(j).getFirstChild();
							title = hymnTitle.getNodeValue().toString();
						} else if (tagName.equals(new String("Numar"))) {
							Node hymnNumber = imnNodes.item(j).getFirstChild();
							number = hymnNumber.getNodeValue().toString();
						} else {
							NodeList strofe = imnNodes.item(j).getChildNodes();
							content = "";
							for (int k = 2; k < strofe.getLength(); k++) {

								if (strofe.item(k).getClass().getName()
										.contains("ElementImpl")) {
									Node contentStrofa = strofe.item(k)
											.getFirstChild();
									content += contentStrofa.getNodeValue()
											.toString();
									content += "\n\r\n\r";
								}
							}
							imnReady = true;
						}
					}

					if (imnReady) {
						String query = "INSERT INTO Imnuri ('tNumber','tName','tContent', 'tCategory', 'tHasSheet', 'tHasMP3' "
								+ ") VALUES ("
								+ number
								+ ", "
								+ "'"
								+ title
								+ "',"
								+ "'"
								+ content
								+ "','"
								+ DatabaseHelper.getCategory(Integer.parseInt(number))
								+ "','"
								+ sheet
								+ "','"
								+ mp3 + "');";
						queries.add(query);
						imnReady = false;
					}
				}
			}
			database.insertAll(queries);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public Document getDocument(InputStream inputStream) {
		Document document = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource(inputStream);
			document = db.parse(inputSource);
		} catch (ParserConfigurationException e) {
			return null;
		} catch (SAXException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return document;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_cat) {
			inCategory = true;
			choosingHymn = false;
			list.clear();
			ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, list);
			listView.setAdapter(listAdapter);

			loadCategories();

			return true;
		} else if (id == R.id.action_all) {
			inCategory = false;
			choosingHymn = true;
			populateListView();
			choosingHymn = true;
		} else if(id == R.id.action_random){
			Intent i = new Intent(this, Random.class);
			startActivity(i);
		}
		/*else if (id == R.id.action_settings) {
			return true;
			Intent i = new Intent(this, Settings.class);
			startActivity(i);
		}*/
		return super.onOptionsItemSelected(item);
	}

	private void loadCategories() {
		choosingHymn = false;

		for (int i = 0; i < categories.length; i++) {
			list.add(categories[i]);
		}
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		listView.setAdapter(listAdapter);
	}

	private void loadACategory(String categoryToLoad) {
		Cursor content = database.getCategory(categoryToLoad);
		if (content != null) {
			if (content.moveToFirst()) {
				choosingHymn = false;
				loadCursor(content);
			}
		}
	}

	private void setLLSearchDimensions() {

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		int screenHeight = displaymetrics.heightPixels;
		int height = screenHeight / 5; // width este latimea edittext-ului

		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, height);
		// llSearch.setLayoutParams(p);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		list.clear();

		String text = etSearchString.getText().toString();
		if (text.equals(new String(""))) {
			populateListView();
		} else {
			Cursor c = database.queryFor(text);
			loadCursor(c);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if(inCategory && choosingHymn) {
				Cursor cData = database.getAll();
				loadCursor(cData);
				choosingHymn = true;
				inCategory = false;
				return false;
			}
			if (inCategory && !choosingHymn) {
				Cursor cData = database.getAll();
				loadCursor(cData);
				choosingHymn = true;
				return false;
			}
			if(choosingHymn && !inCategory) {
				finish();
				return true;
			}
		}
		return false;
	}
}
