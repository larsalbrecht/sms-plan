package com.lars_albrecht.android.smsplan.model;

import java.util.Date;

public class ScheduledEvent {

	private int id;
	private Date date;
	private String phoneNumber;
	private String message;
	private int sent;

	public ScheduledEvent() {
	}

	/**
	 * @param id
	 * @param date
	 * @param phoneNumber
	 * @param message
	 * @param sent
	 */
	public ScheduledEvent(final int id, final Date date, final String phoneNumber, final String message, final int sent) {
		super();
		this.id = id;
		this.date = date;
		this.phoneNumber = phoneNumber;
		this.message = message;
		this.sent = sent;
	}

	public int getId(){
		return this.id;
	}

	public void setId(final int id){
		this.id = id;
	}

	public int getSent(){
		return this.sent;
	}

	public void setSent(final int sent){
		this.sent = sent;
	}

	public Date getDate(){
		return this.date;
	}

	public void setDate(final Date date){
		this.date = date;
	}

	public String getPhoneNumber(){
		return this.phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber){
		this.phoneNumber = phoneNumber;
	}

	public String getMessage(){
		return this.message;
	}

	public void setMessage(final String message){
		this.message = message;
	}

	@Override
	public String toString(){
		return this.getDate() + " - " + this.getPhoneNumber();
	}

}
