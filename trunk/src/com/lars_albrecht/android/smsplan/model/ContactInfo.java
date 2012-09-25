package com.lars_albrecht.android.smsplan.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ContactInfo implements Parcelable {

	private String name;
	private String birthday;
	private String number;

	public String getNumber(){
		return this.number;
	}

	public void setNumber(final String number){
		this.number = number;
	}

	private boolean selected;

	public ContactInfo() {
	}

	public ContactInfo(final Parcel p) {
		this.setName(p.readString());
		this.setBirthday(p.readString());
	}

	public String getDisplayName(){
		return this.getName() + "\nam " + this.getBirthday();
	}

	public String getName(){
		return this.name;
	}

	public void setName(final String name){
		this.name = name;
	}

	public String getBirthday(){
		return this.birthday;
	}

	public void setBirthday(final String birthday){
		this.birthday = birthday;
	}

	public boolean isSelected(){
		return this.selected;
	}

	public void setSelected(final boolean selected){
		this.selected = selected;
	}

	@Override
	public int describeContents(){
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags){
		dest.writeString(this.getName());
		dest.writeString(this.getBirthday());
		dest.writeString(this.getNumber());
	}

	public static final Parcelable.Creator<ContactInfo> CREATOR = new Parcelable.Creator<ContactInfo>() {

		@Override
		public ContactInfo createFromParcel(final Parcel in){
			return new ContactInfo(in);
		}

		@Override
		public ContactInfo[] newArray(final int size){
			return new ContactInfo[size];
		}
	};

}
