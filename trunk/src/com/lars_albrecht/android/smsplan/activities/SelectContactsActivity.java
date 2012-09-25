package com.lars_albrecht.android.smsplan.activities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.lars_albrecht.android.smsplan.R;
import com.lars_albrecht.android.smsplan.helper.InteractiveArrayAdapter;
import com.lars_albrecht.android.smsplan.model.ContactInfo;

public class SelectContactsActivity extends Activity {

	@TargetApi(11)
	@Override
	public void onCreate(final Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_select_contacts);
		this.getActionBar().setDisplayHomeAsUpEnabled(true);

		final ListView contactsList = (ListView) this.findViewById(R.id.contactsList);
		final List<ContactInfo> contacts = this.getContacts();
		contactsList.setAdapter(new InteractiveArrayAdapter(this, contacts));
		final Button cancelButton = (Button) this.findViewById(R.id.btnCancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v){
				SelectContactsActivity.this.finish();
			}
		});
		final Button saveButton = (Button) this.findViewById(R.id.btnSave);
		saveButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(final View v){
				final ArrayList<ContactInfo> selectedContacs = new ArrayList<ContactInfo>();
				for (final ContactInfo info : contacts) {
					if (info.isSelected()) {
						selectedContacs.add(info);
					}
				}
				final Bundle resultBundle = new Bundle();
				resultBundle.putParcelableArrayList("contacts", selectedContacs);
				final Intent contacts = new Intent();
				contacts.putExtras(resultBundle);
				SelectContactsActivity.this.setResult(Activity.RESULT_OK, contacts);
				Toast.makeText(SelectContactsActivity.this, "Kontaktdaten übernommen", Toast.LENGTH_LONG).show();
				SelectContactsActivity.this.finish();
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(final MenuItem item){
		switch (item.getItemId()) {
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// method to get name, contact id, and birthday
	private Cursor getContactsBirthdays(){
		final Uri uri = ContactsContract.Data.CONTENT_URI;
		final String[] projection = new String[] { ContactsContract.Contacts.DISPLAY_NAME,
				ContactsContract.CommonDataKinds.Event.CONTACT_ID, ContactsContract.CommonDataKinds.Event.START_DATE };
		final String where = ContactsContract.Data.MIMETYPE + "= ? AND " + ContactsContract.CommonDataKinds.Event.TYPE + "="
				+ ContactsContract.CommonDataKinds.Event.TYPE_BIRTHDAY;
		final String[] selectionArgs = new String[] { ContactsContract.CommonDataKinds.Event.CONTENT_ITEM_TYPE };
		final String sortOrder = null;
		return this.managedQuery(uri, projection, where, selectionArgs, sortOrder);
	}

	public List<ContactInfo> getContacts(){
		final Cursor cursor = this.getContactsBirthdays();
		final int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);
		final int nameColumn = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
		final int phoneColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

		if (cursor.getCount() <= 0) { return null; }

		final List<ContactInfo> result = new LinkedList<ContactInfo>();
		while (cursor.moveToNext()) {
			final ContactInfo info = new ContactInfo();
			info.setName(cursor.getString(nameColumn));
			info.setBirthday(cursor.getString(bDayColumn));
			info.setNumber(cursor.getString(phoneColumn));
			result.add(info);
			// result.add(cursor.getString(nameColumn) + " am " +
			// cursor.getString(bDayColumn);
		}
		return result;
	}
}
