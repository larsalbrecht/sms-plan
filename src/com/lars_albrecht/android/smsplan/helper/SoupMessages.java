package com.lars_albrecht.android.smsplan.helper;

import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import android.util.Log;
import android.util.SparseArray;

import com.lars_albrecht.android.smsplan.model.MessageSource;

public class SoupMessages {

	private final static String TAG = "SoupMessages";

	public static final Integer EVENT_TYPE_CHRISTMAS = 0;
	public static final Integer EVENT_TYPE_NEWYEAREVE = 1;
	public static final Integer EVENT_TYPE_BIRTHDAY = 2;

	private final static SparseArray<ArrayList<MessageSource>> lSources = new SparseArray<ArrayList<MessageSource>>();
	private final static SparseArray<ArrayList<String>> lMessages = new SparseArray<ArrayList<String>>();

	/**
	 * Fill source list with (dummy-)sources. Clears the source-list before
	 * adding.
	 */
	private static void populateSources(){
		SoupMessages.lSources.clear();
		final ArrayList<MessageSource> lBirthday = new ArrayList<MessageSource>();
		final ArrayList<MessageSource> lChristmas = new ArrayList<MessageSource>();
		final ArrayList<MessageSource> lNewYearsEve = new ArrayList<MessageSource>();

		SoupMessages.lSources.put(SoupMessages.EVENT_TYPE_BIRTHDAY, new ArrayList<MessageSource>());
		SoupMessages.lSources.put(SoupMessages.EVENT_TYPE_CHRISTMAS, new ArrayList<MessageSource>());
		SoupMessages.lSources.put(SoupMessages.EVENT_TYPE_NEWYEAREVE, new ArrayList<MessageSource>());

		lBirthday.add(new MessageSource("Geburtstags-Feste.de - Geburtstag", "http://www.geburtstags-feste.de/geburtstag-sms.html", 4,
				"ooooo"));
		SoupMessages.lSources.put(SoupMessages.EVENT_TYPE_BIRTHDAY, lBirthday);

		lChristmas.add(new MessageSource("Geburtstags-Feste.de - Weihnachten",
				"http://www.geburtstags-feste.de/glueckwuensche-weihnachten.html", 1, "ooooo"));
		SoupMessages.lSources.put(SoupMessages.EVENT_TYPE_CHRISTMAS, lChristmas);

		lNewYearsEve.add(new MessageSource("Geburtstags-Feste.de - Sylvester",
				"http://www.geburtstags-feste.de/neujahr-glueckwuensche.html", 2, "----------"));
		SoupMessages.lSources.put(SoupMessages.EVENT_TYPE_NEWYEAREVE, lNewYearsEve);

		Log.d(SoupMessages.TAG, "sources populated");
	}

	/**
	 * Returns a list of messages. Call to reload list of messages for the view.
	 * 
	 */
	public static void loadMessageSnippets(){
		SoupMessages.populateSources();

		new Thread(new Runnable() {

			@Override
			public void run(){
				try {
					// for each event-type
					for (int i = 0; i < 3; i++) {
						// for each source
						if (SoupMessages.lSources != null && SoupMessages.lSources.get(i) != null) {
							for (int j = 0; j < SoupMessages.lSources.get(i).size(); j++) {
								final ArrayList<String> messages = SoupMessages.getSingleMessageList(SoupMessages.lSources.get(i).get(j));
								Log.d(SoupMessages.TAG, messages.size() + " messages added");
								SoupMessages.lMessages.append(i, messages);
							}
						}
					}
				} catch (final IOException e) {
					e.printStackTrace();
				}
				Log.d(SoupMessages.TAG, "END OF ALL");
			}
		}).start();
	}

	/**
	 * Returns a random message. For "eventType" use the
	 * SoupMessages.EVENT_TYPE* types.
	 * 
	 * @param eventType
	 *            Integer
	 * @return String
	 */
	public static String getRandomMessage(final Integer eventType){
		if (SoupMessages.lMessages != null && SoupMessages.lMessages.get(eventType) != null) { return SoupMessages.lMessages.get(eventType)
				.get((int) (Math.random() * SoupMessages.lMessages.get(eventType).size() + 1)); }
		return "";
	}

	/**
	 * 
	 * @param mSource
	 *            MessageSource
	 * @return
	 * @throws IOException
	 */
	private static ArrayList<String> getSingleMessageList(final MessageSource mSource) throws IOException{
		final ArrayList<String> tempList = new ArrayList<String>();
		final org.jsoup.nodes.Document doc = Jsoup.connect(mSource.getUrl()).get();
		final Elements dummyParagraphs = doc.select("p em");
		final String content = dummyParagraphs.get(mSource.getRow()).text();
		final String msgs[] = content.split(mSource.getSplit());
		for (final String string : msgs) {
			tempList.add(string.trim());
		}
		return tempList;
	}
	// public List<String> getSylvesterMessages() {
	// return getMessageSnippets(
	// "http://www.geburtstags-feste.de/neujahr-glueckwuensche.html",
	// 2, "----------");
	// }
	//
	// public List<String> getXmasMessages() {
	// return getMessageSnippets(
	// "http://www.geburtstags-feste.de/glueckwuensche-weihnachten.html",
	// 1, "ooooo");
	// }
	//
	// public List<String> getBirthdayMessages() {
	// return getMessageSnippets(
	// "http://www.geburtstags-feste.de/geburtstag-sms.html", 4,
	// "ooooo");
	// }
}
