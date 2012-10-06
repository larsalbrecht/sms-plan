package com.lars_albrecht.android.smsplan.activities;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.lars_albrecht.android.smsplan.R;
import com.lars_albrecht.android.smsplan.database.EventsDataSource;
import com.lars_albrecht.android.smsplan.exceptions.InvalidTypeException;
import com.lars_albrecht.android.smsplan.model.ScheduledEvent;

@SuppressLint("NewApi")
public class UpcomingActivity extends Activity {

	private final static String TAG = "UpcomingActivity";

	private ListView lvUpcoming = null;
	private ArrayList<ScheduledEvent> eventList = null;

	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.eventlist);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);
		this.lvUpcoming = (ListView) this.findViewById(R.id.historyList);
		this.lvUpcoming.setLongClickable(true);

		try {
			this.fillList();
		} catch (final InvalidTypeException e) {
			Toast.makeText(this, "An error occured during loading the list.", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
	}

	/**
	 * Fills the historyList with items.
	 * 
	 * @throws Exception
	 */
	private void fillList() throws InvalidTypeException{
		this.eventList = EventsDataSource.getInstance(this.getApplicationContext()).getAllEvents(EventsDataSource.TYPE_UNSENT);
		final String[] c_list = new String[this.eventList.size()];
		for (int i = 0; i < this.eventList.size(); i++) {
			c_list[i] = this.eventList.get(i).toString();
		}

		for (final String string : c_list) {
			Log.d(UpcomingActivity.TAG, string);
		}

		final ArrayAdapter<String> historyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, c_list);
		this.lvUpcoming.setAdapter(historyAdapter);

	}

	/**
	 * //TODO Refactor and replace with real amazing code.
	 * 
	 * @see "http://www.mikeplate.com/2010/01/21/show-a-context-menu-for-long-clicks-in-an-android-listview/"
	 */
	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo){
		if (v.getId() == R.id.historyList) {
			final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
			menu.setHeaderTitle(this.eventList.get(info.position).getDate() + " - " + this.eventList.get(info.position).getPhoneNumber());
			// TODO load from ressources

			final String[] exampleMenu = { "Show details", "Remove", "Remove All" };
			final String[] menuItems = exampleMenu;
			for (int i = 0; i < menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}
}
