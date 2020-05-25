package com.bartoknet.tracktracker.youtube;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import android.os.AsyncTask;
import android.util.Log;

public class YoutubeManager {
	
	final static String LOG_TAG = YoutubeManager.class.getName();

	YoutubeManagerInterface TheClient;

	/*************************************************************************************************
	 * 
	 */
	public YoutubeManager(YoutubeManagerInterface client) {
		TheClient = client;
	}


	/*************************************************************************************************
	 * 
	 */
	public void searchVideoFeed(String searchTerm) {
		
		AsyncTask<String, Void, List<YoutubeVideo>> searchTask = new AsyncTask<String, Void, List<YoutubeVideo>>() {

			@Override
			protected List<YoutubeVideo> doInBackground(String... params) {
				// create query
				String gdataQuery = YoutubeManager.createVideoFeedSearchQuery(params[0]);
				
				// perform query
				InputStream queryResult = YoutubeManager.performQuery(gdataQuery);				
				
				// parse result
				return YoutubeGdataPareser.parse(queryResult);
								
			}
			
			@Override
			protected void onPostExecute (List<YoutubeVideo> videos) {
				TheClient.onYoutubeSearchVideoFeedCompleted(videos);
			}			
		};
		
		searchTask.execute(searchTerm);
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	// TODO
	public void cancelSearch() {
		
	}
	
	/*************************************************************************************************
	 * 
	 */	
	private static InputStream performQuery(String query) {
		URI uri;
		InputStream queryResult = null;
		try {
			// get XML from youtube
			uri = new URI(query);
			URLConnection connection = uri.toURL().openConnection();
			connection.connect();
			queryResult = connection.getInputStream();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return queryResult;
	}
	
	
		
	/*************************************************************************************************
	 * 
	 */
	public static String createVideoFeedSearchQuery(String searchTerm) {
		// create link

		String query = "https://gdata.youtube.com/feeds/api/videos?q=";

		/*
		String encodedSearchTerm = "";
		
		List<String> splittedSearchTerm = Arrays.asList(searchTerm.split(" "));
		Iterator<String> it = splittedSearchTerm.iterator();
		while(it.hasNext()) {
			encodedSearchTerm += it.next();
			if(it.hasNext())
				encodedSearchTerm += "+";
		}
		
		//String query = gdataUrl + URLEncoder.encode(encodedSearchTerm);
		 */
		
		try {
			query += URLEncoder.encode(searchTerm, "UTF-8");
		} catch( UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		Log.d(LOG_TAG,"query: " + query);
		
		return query;
	}
}
