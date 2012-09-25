package com.lars_albrecht.android.smsplan.model;

/**
 * Represents a source for soup.
 * 
 * @author Lars Albrecht
 * 
 */
public class MessageSource {

	private String title = null;
	private String url = null;
	private int row = -1;
	private String split = null;

	/**
	 * 
	 * @param title
	 * @param url
	 * @param row
	 * @param split
	 */
	public MessageSource(final String title, final String url, final int row, final String split) {
		super();
		this.title = title;
		this.url = url;
		this.row = row;
		this.split = split;
	}

	public String getTitle(){
		return this.title;
	}

	public void setTitle(final String title){
		this.title = title;
	}

	public String getUrl(){
		return this.url;
	}

	public void setUrl(final String url){
		this.url = url;
	}

	public int getRow(){
		return this.row;
	}

	public void setRow(final int row){
		this.row = row;
	}

	public String getSplit(){
		return this.split;
	}

	public void setSplit(final String split){
		this.split = split;
	}

}
