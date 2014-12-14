package ro.tineribanat.imnuri900.unused;

import java.util.ArrayList;
import java.util.List;

import ro.tineribanat.imnuri900.DatabaseHelper;
import ro.tineribanat.imnuri900.ListViewRow;
import ro.tineribanat.imnuri900.ShowHymn;
import ro.tineribanat.imnuriazsmr.R;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class ShowFavorites extends Activity {
	
	ListView lvFavorites;
	List<String> listRows = new ArrayList<String>();
	
	ListViewRow lvr;
	List<ListViewRow> listViewText = new ArrayList<ListViewRow>();
	
	DatabaseHelper database;
	
	EditText etSearch;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_fav);
		init();
		populateFavorites();
	}
	
	private void init() {
		database = new DatabaseHelper(this);
		
		lvr = new ListViewRow();
		
		etSearch = (EditText) findViewById(R.id.etSearchboxFav);
		etSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
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
				listRows.clear();
				
				String text = etSearch.getText().toString();
				if (text.equals(new String(""))) {
					populateFavorites();
				} else {
					Cursor c = database.queryFor(text);
					loadCursor(c);
				}
			}
		});
		
		lvFavorites = (ListView) findViewById(R.id.lvFavorites);
		lvFavorites.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				lvr = listViewText.get(position);
				String title = lvr.title;
				String number = lvr.number;

				Intent i = new Intent(ShowFavorites.this, ShowHymn.class);
				i.putExtra("title", title);
				i.putExtra("number", number);
				ShowFavorites.this.startActivity(i);
				overridePendingTransition(R.anim.slide_in_right,
						R.anim.slide_out_left);
			}
		});
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		populateFavorites();
	}
	
	private void populateFavorites() {
		Cursor cFavorites = database.getAllFavorites();
		if(cFavorites!=null) {
			loadCursor(cFavorites);
		}
	}
	
	private void loadCursor(Cursor c) {
		listRows.clear();
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listRows);
		lvFavorites.setAdapter(listAdapter);
		if (c != null) {
			if (c.moveToFirst()) {
				do {
					String number = c.getString(c.getColumnIndex("tNumber"));
					String title = c.getString(c.getColumnIndex("tName"));

					lvr = new ListViewRow();
					lvr.number = number;
					lvr.title = title;
					listViewText.add(lvr);

					listRows.add(number + ". " + title);
				} while (c.moveToNext());
			}
		}
		listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, listRows);
		lvFavorites.setAdapter(listAdapter);
	}
}
