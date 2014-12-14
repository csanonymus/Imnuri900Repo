package ro.tineribanat.imnuri900.fragment;

import java.util.ArrayList;
import java.util.List;

import ro.tineribanat.imnuri900.ListViewRow;
import ro.tineribanat.imnuriazsmr.R;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
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

public class FragmentCategories extends Fragment {

	ListView lvCategories;
	EditText etSearchCaegories;

	ListViewRow lvr;

	List<String> listCategories = new ArrayList<String>();
	List<ListViewRow> listCategoriesObjects = new ArrayList<ListViewRow>();
	String searchString;

	ArrayAdapter<String> listAdapter;

	Communicator comm;

	final String[] allCategories = { "Cantari de lauda",
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

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_categories,
				container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		comm = (Communicator) getActivity();
		lvCategories = (ListView) getActivity().findViewById(
				R.id.lvCategoriesFragment);
		etSearchCaegories = (EditText) getActivity().findViewById(
				R.id.etSeachboxCategories);

		lvCategories
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						// TODO Auto-generated method stub
						comm.respond(allCategories[position]);
					}
				});

		etSearchCaegories.addTextChangedListener(new TextWatcher() {

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
				searchString = etSearchCaegories.getText().toString();
				List<String> categoriesContainingSearchString = new ArrayList<String>();
				for (int i = 0; i < allCategories.length; i++) {
					if (allCategories[i].toLowerCase().startsWith(searchString)) {
						categoriesContainingSearchString.add(allCategories[i]);
					}
				}
				listAdapter = new ArrayAdapter<String>(getActivity(),
						android.R.layout.simple_list_item_1,
						categoriesContainingSearchString);
				lvCategories.setAdapter(listAdapter);
			}
		});

		listAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_list_item_1, allCategories);
		lvCategories.setAdapter(listAdapter);

	}
}
