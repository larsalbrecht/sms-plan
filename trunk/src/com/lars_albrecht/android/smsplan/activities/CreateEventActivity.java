package com.lars_albrecht.android.smsplan.activities;

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
import com.lars_albrecht.android.smsplan.model.ScheduledEvent;

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

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(CreateEventActivity.TAG, Integer.toString(requestCode));
		Log.d(CreateEventActivity.TAG, Integer.toString(resultCode));
		Log.d(CreateEventActivity.TAG, data.getDataString());
		if (resultCode == Activity.RESULT_OK) {
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
			((TextView) this.findViewById(R.id.textView3)).setVisibility(View.VISIBLE);
			((TextView) this.findViewById(R.id.textView4)).setVisibility(View.VISIBLE);
			((Button) this.findViewById(R.id.btnChooseContacts)).setVisibility(View.INVISIBLE);
			((Button) this.findViewById(R.id.btnGenerateMessage)).setVisibility(View.INVISIBLE);
			CreateEventActivity.EVENT_TYPE = CreateEventActivity.TYPE_OWN;
		} else if (item.getTitle() == "Weihnachten" || item.getTitle() == "Sylvester" || item.getTitle() == "Geburtstag") {
			((Button) this.findViewById(R.id.btnGenerateMessage)).setVisibility(View.VISIBLE);
			((Button) this.findViewById(R.id.buttonCreateEventTime)).setVisibility(View.INVISIBLE);
			((Button) this.findViewById(R.id.buttonOpenDatePicker)).setVisibility(View.INVISIBLE);
			((TextView) this.findViewById(R.id.textView2)).setVisibility(View.INVISIBLE);
			((TextView) this.findViewById(R.id.textView3)).setVisibility(View.INVISIBLE);
			((TextView) this.findViewById(R.id.textView4)).setVisibility(View.INVISIBLE);
			((Button) this.findViewById(R.id.btnChooseContacts)).setVisibility(View.VISIBLE);
			CreateEventActivity.EVENT_TYPE = item.getTitle() == "Weihnachten" ? CreateEventActivity.TYPE_CHRISTMAS
					: item.getTitle() == "Sylvester" ? CreateEventActivity.TYPE_NEWYEAREVE : CreateEventActivity.TYPE_BIRTHDAY;
			((EditText) this.findViewById(R.id.txtCreateEventPhonenumber)).setVisibility(View.INVISIBLE);
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
		final EventsDataSource model = new EventsDataSource(this.getApplicationContext());
		final ScheduledEvent enteredEvent = this.getEnteredEvent();
		model.createEvent(enteredEvent);
		Log.d(CreateEventActivity.TAG, "Entered Date " + enteredEvent.getDate().getTime());
		final ScheduledEvent nextEvent = model.getNextEvent();
		Utilities.setAlarm(this.getApplicationContext(), nextEvent);
		model.close();
	}

	/**
	 * 
	 * @return
	 */
	private ScheduledEvent getEnteredEvent(){
		final EditText txtMessage = (EditText) this.findViewById(R.id.messageInput);
		final EditText txtPhoneNumber = (EditText) this.findViewById(R.id.txtCreateEventPhonenumber);
		final ScheduledEvent scheduledEvent = new ScheduledEvent();
		final Calendar cal = Calendar.getInstance();
		cal.set(this.year, this.month, this.dayOfMonth, this.hourOfDay, this.minute);
		final Date createDate = cal.getTime();

		scheduledEvent.setDate(createDate);
		scheduledEvent.setMessage(txtMessage.getText().toString());
		scheduledEvent.setPhoneNumber(txtPhoneNumber.getText().toString());
		scheduledEvent.setSent(0);
		return scheduledEvent;
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
			this.save();
		}
	}
}
