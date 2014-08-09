package ro.tineribanat.imnuri900;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.textservice.TextInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MainActivity extends Activity implements TextWatcher {

	String title, number, content;

	LinearLayout llSearch;
	ListView listView;

	EditText etSearchString;
	DatabaseHelper database;

	ArrayList<String> list = new ArrayList<String>();
	Document doc;
	List<Node> docNodes;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setLLSearchDimensions();
		init();
		SharedPreferences sp = this.getSharedPreferences("initialStart",
				MODE_PRIVATE);
		String isFirstTime = sp.getString("firstTime", null);
		if (isFirstTime == "no") {
			populateListView();
		} else {
			Editor e = sp.edit();
			e.putString("initialStart", "no");
			e.commit();
			initialInsert();
			populateListView();
		}
	}

	private void init() {
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

			}
		});
	}

	private void populateListView() {
		Cursor cData = database.getAll();
		if (cData != null) {
			if (cData.moveToFirst()) {
				do {
					String number = cData.getString(cData
							.getColumnIndex("tNumber"));
					String title = cData.getString(cData
							.getColumnIndex("tName"));
					list.add(number + ". " + title);
				} while (cData.moveToNext());
			}
		}
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, list);
		listView.setAdapter(listAdapter);
	}

	private void initialInsert() {
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
				NodeList imnList = imn.getChildNodes();
				for (int j = 0; j < imnList.getLength(); j++) {
					Node nodeItem = imnList.item(j);
					String tagTitle = nodeItem.getNodeName().toString();
					if (nodeItem.getClass().getName().contains("ElementImpl")) {
						// String habarnam = "da e ok";
						// }
						// if (!tagTitle.equals(new String("#text"))) {
						if (tagTitle.equals(new String("Titlu"))) {
							Node hymnTitle = imnList.item(j).getFirstChild();
							title = hymnTitle.getNodeValue().toString();
						} else if (tagTitle.equals(new String("Numar"))) {
							Node hymnNumber = imnList.item(j).getFirstChild();
							number = hymnNumber.getNodeValue().toString();
						} else { // Strofe //acolo nue chiar gata..Mai am putin de calculat ::)))ok
							NodeList strofe = imnList.item(j).getChildNodes();
							content = "";
							for (int k = 0; k < strofe.getLength(); k++) {

								if (strofe.item(k).getClass().getName()
										.contains("ElementImpl")) {
									Node contentStrofa = strofe.item(k)
											.getFirstChild();
									content += contentStrofa.getNodeValue()
											.toString();
									content += "\n\r\n\r";
								}
							}
						}
					} else {
						/*
						 * String[] a = null; NodeList hymn = imnList; a[0] =
						 * hymn.item(0).getNodeName().toString(); a[1] =
						 * hymn.item(1).getNodeName().toString(); a[2] =
						 * hymn.item(2).getNodeName().toString(); a[3] =
						 * hymn.item(3).getNodeName().toString(); a[4] =
						 * hymn.item(4).getNodeName().toString(); a[5] =
						 * hymn.item(5).getNodeName().toString(); a[6] =
						 * hymn.item(6).getNodeName().toString(); int alpha = 2;
						 * alpha += 1;
						 */
					}
					Log.i("APP", imnList.item(j).getNodeName().toString());
				}
				String val = imn.getNodeName().toString();

			}
			int x = 1;
			x += 5.3;
			Log.i("APP", n.item(0).getNodeName() + "");
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
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (SAXException e) {
			Log.e("Error: ", e.getMessage());
			return null;
		} catch (IOException e) {
			Log.e("Error: ", e.getMessage());
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
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
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
		String text = etSearchString.getText().toString();
		Cursor c = database.queryFor(text);

	}
}
