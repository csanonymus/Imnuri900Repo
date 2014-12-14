package ro.tineribanat.imnuri900.fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ro.tineribanat.imnuri900.DatabaseHelper;
import ro.tineribanat.imnuri900.ListViewRow;
import ro.tineribanat.imnuri900.ShowHymn;
import ro.tineribanat.imnuri900.workers.PrefManager;
import ro.tineribanat.imnuriazsmr.R;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class FragmentHymns extends Fragment {

	View thisView;

	public static DatabaseHelper database;

	static ListView lvHymns;
	private static EditText etSearch;
	Button bClear;

	String searchString;

	static Context context;

	static List<String> listViewAdapter = new ArrayList<String>();
	static List<ListViewRow> listViewText = new ArrayList<ListViewRow>();
	static ListViewRow lvr;

	boolean firstTimeLoadingSpinner = false;
	ProgressDialog progress;

	PrefManager prefs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_imnuri, container,
				false);

		database = new DatabaseHelper(getActivity());
		context = getActivity();
		prefs = new PrefManager(getActivity());

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		etSearch = (EditText) getActivity().findViewById(R.id.etSeachbox);
		lvHymns = (ListView) getActivity().findViewById(R.id.lvHymnsFragment);

		lvHymns.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lvr = listViewText.get(position);
				String title = lvr.title;
				String number = lvr.number;

				Intent i = new Intent(getActivity(), ShowHymn.class);
				i.putExtra("title", title);
				i.putExtra("number", number);
				getActivity().startActivity(i);
			}
		});

		etSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				searchString = etSearch.getText().toString();
				if (searchString.equals("")) {
					bClear.setEnabled(false);
				} else {
					bClear.setEnabled(true);
				}
				Cursor c = database.queryFor(searchString);
				loadCursor(c);

			}
		});

		boolean isFirstTime = prefs.getFirstTime();
		if (isFirstTime == true) {
			prefs.setFirstTime();
			buildAlertDialog();
			Thread t = new Thread(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub

					initialInsert();
					lvHymns.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							populateListView();
						}
					});
				}
			});
			t.start();
		} else {
			populateListView();
		}
		bClear = (Button) getActivity().findViewById(R.id.bClear);
		bClear.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				etSearch.setText("");
			}
		});
		bClear.setEnabled(false);
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		etSearch.clearFocus();
		lvHymns.requestFocus();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

	}

	private void populateListView() {
		Cursor cData = database.getAll();
		loadCursor(cData);
		if (firstTimeLoadingSpinner) {
			progress.dismiss();
		}
	}

	private static void loadCursor(Cursor c) {
		listViewAdapter.clear();
		listViewText.clear();
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, listViewAdapter);
		lvHymns.setAdapter(listAdapter);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String number = c.getString(c.getColumnIndex("tNumber"));
					String title = c.getString(c.getColumnIndex("tName"));

					lvr = new ListViewRow();
					lvr.number = number;
					lvr.title = title;
					listViewText.add(lvr);

					listViewAdapter.add(number + ". " + title);
				} while (c.moveToNext());
			}
		}
		listAdapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, listViewAdapter);
		lvHymns.setAdapter(listAdapter);
	}

	ArrayList<String> list = new ArrayList<String>();
	Document doc;
	List<Node> docNodes = new ArrayList<Node>();
	String title, number, content;
	boolean imnReady = false;

	private void initialInsert() {

		List<String> queries = new ArrayList<String>();
		try {
			AssetManager assetManager = getActivity().getAssets();
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

				NodeList imnNodes = imn.getChildNodes();
				for (int j = 0; j < imnNodes.getLength(); j++) {
					Node nodeItem = imnNodes.item(j);
					String tagName = nodeItem.getNodeName().toString();
					if (nodeItem.getClass().getName().contains("ElementImpl")) {
						if (tagName.equals(new String("Titlu"))) {
							Node hymnTitle = imnNodes.item(j).getFirstChild();
							title = hymnTitle.getNodeValue().toString();
							title = title.replace("\n", "").replace("\r", "");
						} else if (tagName.equals(new String("Numar"))) {
							Node hymnNumber = imnNodes.item(j).getFirstChild();
							number = hymnNumber.getNodeValue().toString();
							number = number.replace("\n", "").replace("\r", "");
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
								}
							}
							imnReady = true;
						}
					}

					if (imnReady) {
						int initialValueFavorited = 0;
						String query = "INSERT INTO Imnuri ('tNumber','tName','tContent', 'tCategory', 'tIsFavorited' "
								+ ") VALUES ("
								+ number.replaceAll("\\s+","")
								+ ", "
								+ "'"
								+ title
								+ "',"
								+ "'"
								+ content
								+ "','"
								+ DatabaseHelper.getCategory(Integer
										.parseInt(number.replaceAll("\\s+","")))
								+ "','"
								+ initialValueFavorited + "');";
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

	private void buildAlertDialog() {
		firstTimeLoadingSpinner = true;
		progress = new ProgressDialog(getActivity());
		progress.setCancelable(false);
		progress.setMessage("Generez baza de date. Va rugam asteptati...");
		progress.show();
	}

	public static void categorySelected(String category) {
		etSearch.setText(category);
		listViewAdapter.clear();
		Cursor c = database.getCategory(category);
		loadCursor(c);
	}
}
