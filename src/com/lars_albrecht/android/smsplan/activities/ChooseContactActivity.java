package com.lars_albrecht.android.smsplan.activities;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ChooseContactActivity extends Activity {

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

	public void iterateContacts(){
		final Cursor cursor = this.getContactsBirthdays();
		final int bDayColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Event.START_DATE);

		while (cursor.moveToNext()) {
			@SuppressWarnings("unused")
			final String bDay = cursor.getString(bDayColumn);
		}
	}
}
