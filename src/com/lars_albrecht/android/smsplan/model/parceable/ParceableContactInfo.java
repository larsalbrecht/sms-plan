package com.lars_albrecht.android.smsplan.model.parceable;

import android.os.Parcel;
import android.os.Parcelable;

import com.lars_albrecht.android.smsplan.model.ContactInfo;

public class ParceableContactInfo implements Parcelable {

	private ContactInfo contactInfo = null;

	public ContactInfo getContactInfo(){
		return this.contactInfo;
	}

	public ParceableContactInfo(final ContactInfo contactInfo) {
		super();
		this.contactInfo = contactInfo;
	}

	private ParceableContactInfo(final Parcel in) {
		this.contactInfo = new ContactInfo();
		this.contactInfo.setName(in.readString());
		this.contactInfo.setBirthday(in.readString());
		this.contactInfo.setNumber(in.readString());
	}

	@Override
	public int describeContents(){
		return this.hashCode();
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags){
		dest.writeString(this.contactInfo.getName());
		dest.writeString(this.contactInfo.getBirthday());
		dest.writeString(this.contactInfo.getNumber());
	}

	public static final Parcelable.Creator<ParceableContactInfo> CREATOR = new Parcelable.Creator<ParceableContactInfo>() {

		@Override
		public ParceableContactInfo createFromParcel(final Parcel in){
			return new ParceableContactInfo(in);
		}

		@Override
		public ParceableContactInfo[] newArray(final int size){
			return new ParceableContactInfo[size];
		}
	};
}
