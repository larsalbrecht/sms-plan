package com.lars_albrecht.android.smsplan.activities;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.lars_albrecht.android.smsplan.R;
import com.lars_albrecht.android.smsplan.database.EventsDataSource;
import com.lars_albrecht.android.smsplan.helper.SoupMessages;
import com.lars_albrecht.android.smsplan.helper.Utilities;
import com.lars_albrecht.android.smsplan.model.ContactInfo;
import com.lars_albrecht.android.smsplan.model.ScheduledEvent;
import com.lars_albrecht.android.smsplan.model.parceable.ParceableContactInfo;

@SuppressLint({ "NewApi", "NewApi" })
public class CreateEventActivity extends Activity implements OnClickListener {

	private final static String TAG = "CreateActivity";
	private static final int TIME_DIALOG_ID = 999;

	public static int EVENT_TYPE = -1;
	public static final int TYPE_OWN = -1;
	public static final int TYPE_CHRISTMAS = 0;
	public static final int TYPE_NEWYEAREVE = 1;
	public static final int TYPE_BIRTHDAY = 2;
	public static final int REQUEST_CODE = 0;

	private Button selectEventTypeButton = null;
	private Button chooseContactsButton = null;
	private Button generateMessage = null;
	private Button cancelButton = null;
	private Button save = null;
	private ArrayList<ContactInfo> selectedContactInfos = null;

	private int minute;
	private int hourOfDay;
	private int dayOfMonth;
	private int month;
	private int year;

	@Override
	public void onCreate(final Bundle savedInstanceState){
		final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		SoupMessages.loadMessageSnippets();
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.activity_create);
		this.selectEventTypeButton = (Button) this.findViewById(R.id.btnSelectEventType);
		this.selectEventTypeButton.setOnClickListener(this);
		this.chooseContactsButton = (Button) this.findViewById(R.id.btnChooseContacts);
		this.chooseContactsButton.setOnClickListener(this);
		this.generateMessage = (Button) this.findViewById(R.id.btnGenerateMessage);
		this.generateMessage.setOnClickListener(this);
		this.cancelButton = (Button) this.findViewById(R.id.btnCreateEventCancel);
		this.cancelButton.setOnClickListener(this);
		this.setCurrentDate();
		this.save = (Button) this.findViewById(R.id.btnCreateEventSave);
		this.save.setOnClickListener(this);

		this.selectedContactInfos = new ArrayList<ContactInfo>();

		final OnDateSetListener onDateSetListener = new OnDateSetListener() {

			@Override
			public void onDateSet(final DatePicker arg0, final int year, final int month, final int dayOfMonth){
				CreateEventActivity.this.setYear(year);
				CreateEventActivity.this.setMonth(month);
				CreateEventActivity.this.setDayOfMonth(dayOfMonth);

				final Button dateButton = (Button) CreateEventActivity.this.findViewById(R.id.buttonOpenDatePicker);
				dateButton.setText(dayOfMonth + ". " + month + ". " + year);
			}
		};
		final Button dateText = (Button) this.findViewById(R.id.buttonOpenDatePicker);
		dateText.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v){
				final Calendar cal = Calendar.getInstance();
				final DatePickerDialog datePickDiag = new DatePickerDialog(CreateEventActivity.this, onDateSetListener, cal
						.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
				datePickDiag.show();
			}
		});
		final Button buttonTimePicker = (Button) this.findViewById(R.id.buttonCreateEventTime);
		buttonTimePicker.setOnClickListener(new View.OnClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onClick(final View v){
				CreateEventActivity.this.showDialog(CreateEventActivity.TIME_DIALOG_ID);
			}
		});
	}

	/**
	 * 
	 */
	private void setNewMessageText(){
		final EditText msgText = (EditText) this.findViewById(R.id.messageInput);
		final String message = SoupMessages.getRandomMessage(CreateEventActivity.EVENT_TYPE);

		if (message != null) {
			Log.d(CreateEventActivity.TAG, "new message set: " + message);
			msgText.setText(message);
		} else {
			Log.d(CreateEventActivity.TAG, "no message set");
		}
	}

	/**
	 * 
	 */
	private void setCurrentDate(){
		final Calendar cal = Calendar.getInstance();
		this.setYear(cal.get(Calendar.YEAR));
		this.setMonth(cal.get(Calendar.MONTH));
		this.setDayOfMonth(cal.get(Calendar.DAY_OF_MONTH));
		this.setHourOfDay(cal.get(Calendar.HOUR_OF_DAY));
		this.setMinute(cal.get(Calendar.MINUTE));
		final Button dateText = (Button) this.findViewById(R.id.buttonOpenDatePicker);
		dateText.setText(this.dayOfMonth + ". " + this.month + ". " + this.year);
		final Button buttonTimePicker = (Button) this.findViewById(R.id.buttonCreateEventTime);
		buttonTimePicker.setText(new StringBuilder().append(CreateEventActivity.pad(this.hourOfDay)).append(":")
				.append(CreateEventActivity.pad(this.minute)));
	}

	/**
	 * Will be called when an activity has results. In this case, the activity
	 * "SelectContactsActivity" will return a result, to parse the selected
	 * contacts.
	 */
	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data){
		if (resultCode == Activity.RESULT_CANCELED) { return; }
		super.onActivityResult(requestCode, resultCode, data);
		// if the contacts exists
		if (data.getExtras().containsKey("contacts")) {
			@SuppressWarnings("unchecked")
			// load parcels (contacts) from data intent.
			final ArrayList<ParceableContactInfo> parceableSelectedContacts = (ArrayList<ParceableContactInfo>) data.getExtras().get(
					"contacts");
			final ArrayList<ContactInfo> selectedContacts = new ArrayList<ContactInfo>();
			// transform parceableContactInfos into contactInfos
			for (final ParceableContactInfo parceableContactInfo : parceableSelectedContacts) {
				selectedContacts.add(parceableContactInfo.getContactInfo());
			}
			// return contacts to save them later
			this.selectedContactInfos = selectedContacts;
		} else {
			Log.d(CreateEventActivity.TAG, "CONTACTS CONTAINS FAILED!");
		}
	}

	private final TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

		@Override
		public void onTimeSet(final TimePicker view, final int selectedHour, final int selectedMinute){
			CreateEventActivity.this.hourOfDay = selectedHour;
			CreateEventActivity.this.minute = selectedMinute;

			final Button buttonTimePicker = (Button) CreateEventActivity.this.findViewById(R.id.buttonCreateEventTime);
			buttonTimePicker.setText(new StringBuilder().append(CreateEventActivity.pad(CreateEventActivity.this.hourOfDay)).append(":")
					.append(CreateEventActivity.pad(CreateEventActivity.this.minute)));
		}
	};

	@Override
	protected Dialog onCreateDialog(final int id){
		switch (id) {
			case TIME_DIALOG_ID:
				if (0 == this.hourOfDay && 0 == this.minute) {
					final Calendar cal = Calendar.getInstance();
					this.hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
					this.minute = cal.get(Calendar.MINUTE);
				}
				return new TimePickerDialog(this, this.timePickerListener, this.hourOfDay, this.minute, false);
		}
		return null;
	}

	@Override
	public void onCreateContextMenu(final ContextMenu menu, final View v, final ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		menu.setHeaderTitle("Wähle einen Typen");
		menu.add(0, v.getId(), 0, "Eigenes Event");
		menu.add(0, v.getId(), 0, "Geburtstag");
		menu.add(0, v.getId(), 0, "Weihnachten");
		menu.add(0, v.getId(), 0, "Sylvester");
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item){
		if (item.getTitle() == "Eigenes Event") {
			((Button) this.findViewById(R.id.buttonCreateEventTime)).setVisibility(View.VISIBLE);
			((Button) this.findViewById(R.id.buttonOpenDatePicker)).setVisibility(View.VISIBLE);
			((TextView) this.findViewById(R.id.txtDate)).setVisibility(View.VISIBLE);
			// ((Button)
			// this.findViewById(R.id.btnChooseContacts)).setVisibility(View.INVISIBLE);
			((Button) this.findViewById(R.id.btnGenerateMessage)).setVisibility(View.INVISIBLE);
			CreateEventActivity.EVENT_TYPE = CreateEventActivity.TYPE_OWN;
		} else if (item.getTitle() == "Weihnachten" || item.getTitle() == "Sylvester" || item.getTitle() == "Geburtstag") {
			((Button) this.findViewById(R.id.btnGenerateMessage)).setVisibility(View.VISIBLE);
			((Button) this.findViewById(R.id.buttonCreateEventTime)).setVisibility(View.INVISIBLE);
			((Button) this.findViewById(R.id.buttonOpenDatePicker)).setVisibility(View.INVISIBLE);
			((Button) this.findViewById(R.id.btnChooseContacts)).setVisibility(View.VISIBLE);
			CreateEventActivity.EVENT_TYPE = item.getTitle() == "Weihnachten" ? CreateEventActivity.TYPE_CHRISTMAS
					: item.getTitle() == "Sylvester" ? CreateEventActivity.TYPE_NEWYEAREVE : CreateEventActivity.TYPE_BIRTHDAY;
		} else {
			Toast.makeText(this, "Nothing", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	/**
	 * 
	 */
	private void save(){
		final EventsDataSource dataSource = EventsDataSource.getInstance(this.getApplicationContext());
		final ArrayList<ScheduledEvent> enteredEvents = this.getEnteredEvent();
		// save all entered events
		for (final ScheduledEvent scheduledEvent : enteredEvents) {
			dataSource.createEvent(scheduledEvent);
		}

		// set next event
		final ScheduledEvent nextEvent = dataSource.getNextEvent();
		Utilities.setAlarm(this.getApplicationContext(), nextEvent);
	}

	/**
	 * 
	 * @return
	 */
	private ArrayList<ScheduledEvent> getEnteredEvent(){
		final ArrayList<ScheduledEvent> resultList = new ArrayList<ScheduledEvent>();

		final EditText txtMessage = (EditText) this.findViewById(R.id.messageInput);
		String phoneNumber = null;
		String message = null;

		if (CreateEventActivity.EVENT_TYPE >= -1) {
			// create event for each selected contact
			for (final ContactInfo contactInfo : this.selectedContactInfos) {
				phoneNumber = contactInfo.getNumber();
				message = txtMessage.getText().toString();
				resultList.add(this.scheduleEvent(message, phoneNumber));
			}
		} else {
			// for later use
			// use input box for phone numbers
			// final EditText txtPhoneNumber = (EditText)
			// this.findViewById(R.id.txtCreateEventPhonenumber);
		}

		return resultList;
	}

	private ScheduledEvent scheduleEvent(final String message, final String phoneNumber){
		final ScheduledEvent resultEvent = new ScheduledEvent();
		final Calendar cal = Calendar.getInstance();
		cal.set(this.year, this.month, this.dayOfMonth, this.hourOfDay, this.minute);
		final Date createDate = cal.getTime();

		resultEvent.setDate(createDate);
		resultEvent.setMessage(message);
		resultEvent.setPhoneNumber(phoneNumber);
		resultEvent.setSent(0);

		return resultEvent;

	}

	private static String pad(final int c){
		if (c >= 10) {
			return String.valueOf(c);
		} else {
			return "0" + String.valueOf(c);
		}
	}

	protected void setDayOfMonth(final int dayOfMonth){
		this.dayOfMonth = dayOfMonth;
	}

	protected void setMonth(final int month){
		this.month = month;
	}

	protected void setYear(final int year){
		this.year = year;
	}

	protected void setMinute(final int minute){
		this.minute = minute;
	}

	protected void setHourOfDay(final int hourOfDay){
		this.hourOfDay = hourOfDay;
	}

	private boolean isDateValid(){
		if (CreateEventActivity.EVENT_TYPE == CreateEventActivity.TYPE_OWN
				&& (this.year > Calendar.getInstance().get(Calendar.YEAR) || this.year == Calendar.getInstance().get(Calendar.YEAR)
						&& (this.month > Calendar.getInstance().get(Calendar.MONTH) || this.month == Calendar.getInstance().get(
								Calendar.MONTH)
								&& (this.dayOfMonth > Calendar.getInstance().get(Calendar.DAY_OF_MONTH) || this.dayOfMonth == Calendar
										.getInstance().get(Calendar.DAY_OF_MONTH)
										&& (this.hourOfDay > Calendar.getInstance().get(Calendar.HOUR_OF_DAY) || this.hourOfDay == Calendar
												.getInstance().get(Calendar.HOUR_OF_DAY)
												&& this.minute > Calendar.getInstance().get(Calendar.MINUTE)))))

		) { return true; }

		return false;
	}

	@Override
	public void onClick(final View v){
		if (v == this.chooseContactsButton) {
			final Intent intent = new Intent(CreateEventActivity.this, SelectContactsActivity.class);
			final Bundle b = new Bundle();
			b.putString("key", "value");
			this.startActivityForResult(intent, CreateEventActivity.REQUEST_CODE);
		} else if (v == this.selectEventTypeButton) {
			this.registerForContextMenu(v);
			this.openContextMenu(v);
		} else if (v == this.generateMessage) {
			this.setNewMessageText();
		} else if (v == this.cancelButton) {
			this.finish();
		} else if (v == this.save) {
			if (CreateEventActivity.EVENT_TYPE == CreateEventActivity.TYPE_OWN && this.isDateValid()
					|| CreateEventActivity.EVENT_TYPE > CreateEventActivity.TYPE_OWN) {
				this.save();
				this.finish();
			} else {
				// TODO translate this
				Toast.makeText(this, "Das Datum liegt in der Vergangenheit", Toast.LENGTH_LONG).show();
			}

		}
	}
}
