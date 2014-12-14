package ro.tineribanat.imnuri900.fragment;

import java.util.ArrayList;
import java.util.List;

import ro.tineribanat.imnuri900.DatabaseHelper;
import ro.tineribanat.imnuri900.ListViewRow;
import ro.tineribanat.imnuri900.ShowHymn;
import ro.tineribanat.imnuriazsmr.R;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;

public class FragmentFavorites extends Fragment {

	View thisView;

	DatabaseHelper database;

	ListView lvFavorites;
	EditText etSearch;

	String searchString;

	List<String> listViewAdapter = new ArrayList<String>();
	List<ListViewRow> listViewText = new ArrayList<ListViewRow>();
	ListViewRow lvr;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_favorites,
				container, false);

		init(rootView);
		database = new DatabaseHelper(getActivity());
		populateFavorites();

		return rootView;
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		populateFavorites();
	}

	private void init(View v) {
		database = new DatabaseHelper(getActivity());

		lvr = new ListViewRow();

		etSearch = (EditText) v.findViewById(R.id.etSearchboxFav);
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

				String text = etSearch.getText().toString();
				if (text.equals(new String(""))) {
					populateFavorites();
				} else {
					Cursor c = database.queryFor(text);
					Cursor data = database.getAllFavorites();
					loadCursor(c, data);
				}
			}
		});

		lvFavorites = (ListView) v.findViewById(R.id.lvFavorites);
		lvFavorites
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

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
						startActivity(i);
						getActivity().overridePendingTransition(
								R.anim.slide_in_right, R.anim.slide_out_left);
					}
				});
	}

	public void populateFavorites() {
		Cursor cFavorites = database.getAllFavorites();
		if (cFavorites != null) {
			loadCursor(cFavorites, null);
		}
	}

	private void loadCursor(Cursor baseCursor, Cursor favorites) {
		listViewAdapter.clear();
		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.simple_list_item_1,
				listViewAdapter);
		lvFavorites.setAdapter(listAdapter);
		if (baseCursor != null) {
			if (baseCursor.moveToFirst()) {
				do {
					String number = baseCursor.getString(baseCursor
							.getColumnIndex("tNumber"));
					String title = baseCursor.getString(baseCursor
							.getColumnIndex("tName"));

					lvr = new ListViewRow();
					lvr.number = number;
					lvr.title = title;
					listViewText.add(lvr);
					
					Cursor temp = favorites;
					
					if (favorites != null) {
						temp.moveToFirst();
						do {
							String nr = temp.getString(temp
									.getColumnIndex("tNumber"));
							if(nr.equals(number)) {
								listViewAdapter.add(number + ". " + title);
							}
						} while(temp.moveToNext());
					} else {
						listViewAdapter.add(number + ". " + title);
					}
				} while (baseCursor.moveToNext());
			}
		}
		listAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, listViewAdapter);
		lvFavorites.setAdapter(listAdapter);
	}

}
