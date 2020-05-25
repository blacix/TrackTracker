package com.bartoknet.tracktracker.youtube;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ImageView;


public class YoutubeVideo {

	private static final String LOG_TAG = YoutubeVideo.class.getName();
	private static int objCnt = 0;
	
	String Title;
	String Description;
	String ViewCount;
	URI ThumbnailUri;
	Bitmap ThumbnailBitmap;
	URI Uri;
	String YoutubeID;
	
	public int ID;
	public boolean downloading;
	
	
	
	/*************************************************************************************************
	 * 
	 */
	public YoutubeVideo(String title, String descreption, String viewCount, 
			String uri, String thumbNail) {
		
		//Log.d(LOG_TAG, "YoutubeVideo" + viewCount);
		ID = objCnt++;
		downloading = false;
		
		
		Title = title != null ? title : "";
		Description = descreption != null ? descreption : "";
		ViewCount = viewCount != null ? viewCount : "";
		ThumbnailBitmap = null;
		
		Uri = null;
		try {
			Uri = new URI(uri);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		ThumbnailUri = null;
		try {
			ThumbnailUri = new URI(thumbNail);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		ArrayList<String> strings1 = new ArrayList<String>(Arrays.asList(Uri.toString().split("=")));
		String tmp = strings1.get(1);
		ArrayList<String> strings2 = new ArrayList<String>(Arrays.asList(tmp.split("&")));
		YoutubeID = strings2.get(0);
		//Log.d(LOG_TAG, "YoutubeID: " + YoutubeID);
		
		//downloadImage(null, null);
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	public void downloadImage(final ImageView view, final BaseAdapter listAdapter) {
		
		if(downloading)
			return;

		Log.d(LOG_TAG, "downloading video: " + Integer.toString(ID));

		downloading = true;
		
		AsyncTask<YoutubeVideo, Void, Bitmap> downloadTask = new AsyncTask<YoutubeVideo, Void, Bitmap>() {

			@Override
			protected Bitmap doInBackground(YoutubeVideo... params) {
				try {
					URL url = params[0].getThumbnail().toURL();
					URLConnection connection = url.openConnection();
					connection.connect();
					InputStream is = connection.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(is);
					Bitmap bitmap = BitmapFactory.decodeStream(bis);
					bis.close();
					is.close();
					Log.d(LOG_TAG, "finished downloading image");
					return bitmap;
				} catch (MalformedURLException e) {
					e.printStackTrace();
				} catch(IOException e) {
					e.printStackTrace();
				}
				
				return null;

			}

			@Override
			protected void onPostExecute(final Bitmap bitmap) {
				downloading = false;

				if (!hasImage())
					setImage(bitmap);

				if (view != null)
					view.setImageBitmap(bitmap);

				if (listAdapter != null)
					listAdapter.notifyDataSetChanged();
			}
		};

		downloadTask.execute(this);

	}


	/*************************************************************************************************
	 * 
	 */
	public void setImage(Bitmap bitmap) {
		ThumbnailBitmap = bitmap;
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	public boolean hasImage() {
		return ThumbnailBitmap != null;
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	public Bitmap getImage() {
		return ThumbnailBitmap;
	}
	
	// TODO: implement Youtubevideo.print();
	
	/*************************************************************************************************
	 * 
	 */
	public String getTitle() {
		return Title;
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	public String getDescription() {
		return Description;
	}
	
	
	
	/*************************************************************************************************
	 * 
	 */
	public String getViewCount() {
		return ViewCount;
	}

	
	/*************************************************************************************************
	 * 
	 */
	public URI getThumbnail() {
		return ThumbnailUri;
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	public URI getUri() {
		return Uri;
	}
	
	/*************************************************************************************************
	 * 
	 */
	public String getYoutubeID() {
		return YoutubeID;
	}
}
