package com.bartoknet.tracktracker;

import java.util.ArrayList;
import java.util.List;

import com.bartoknet.tracktracker.youtube.YoutubeManager;
import com.bartoknet.tracktracker.youtube.YoutubeManagerInterface;
import com.bartoknet.tracktracker.youtube.YoutubeVideo;
import com.bartoknet.tracktracker.youtube.YoutubeVideoAdapter;
import com.bartoknet.utils.Utils;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class VideoListActivity extends Activity implements YoutubeManagerInterface{

	private static final String LOG_TAG = VideoListActivity.class.getName();
	
	TextView TextViewVideoList;
	ListView ListViewVideos;
	YoutubeManager Youtube;
	
	ProgressDialog YoutubeProgress;
	
	
	YoutubeVideoAdapter VideoAdapter;
	ArrayList<YoutubeVideo> Videos;
	
	
	/*************************************************************************************************
	 * 
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_list);
		
		// gui
		ListViewVideos = (ListView) findViewById(R.id.listview_videolist);
		
		YoutubeProgress = new ProgressDialog(this);
		
		Youtube = new YoutubeManager(this);
		
		Intent intent = getIntent();
		String searchTerm = intent.getStringExtra("searchterm");
		Youtube.searchVideoFeed(searchTerm);
		YoutubeProgress = ProgressDialog.show(this, "Youtube", "Searching...");
		
		Videos = new ArrayList<YoutubeVideo>();
		VideoAdapter =  new YoutubeVideoAdapter(this, R.layout.listview_row, Videos);
		ListViewVideos = (ListView)findViewById(R.id.listview_videolist);
		ListViewVideos.setAdapter(VideoAdapter);
		ListViewVideos.setOnItemClickListener(ItemClickListener);
		ListViewVideos.setOnItemLongClickListener(ItemLongClicklistener);
	}

	
	/*************************************************************************************************
	 * 
	 */
	OnItemClickListener ItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			YoutubeVideo v = null;
			Intent data = new Intent();
			v = VideoAdapter.getItem(position);
			
			data.putExtra("title", v.getTitle());
			data.putExtra("viewcount", v.getViewCount());
			data.putExtra("descritption", v.getDescription());
			data.putExtra("uri", v.getUri().toString());
			data.putExtra("thumbnail", v.getThumbnail().toString());
			data.putExtra("thumbnail_image", v.getImage());
			
			setResult(RESULT_OK, data);
			finish();

		}
	};
	
	
	/*************************************************************************************************
	 * 
	 */
	OnItemLongClickListener ItemLongClicklistener = new OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			Log.d(LOG_TAG, "onItemLongClick");
			YoutubeVideo video = VideoAdapter.getItem(position);
			if(video != null) {
				Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("vnd.youtube:"+video.getYoutubeID()));
	            if(Utils.isAppInstalled("com.google.android.youtube", getApplicationContext())) {
	                intent.putExtra("VIDEO_ID", video.getYoutubeID()); 
		            startActivity(intent);
					return true;
	            } else {
	            	Toast.makeText(view.getContext(), "Youtube is not installed", Toast.LENGTH_LONG).show();
	            }
			}
			return false;
		}
		
	};

	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_video_list, menu);
		return true;
	}

	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onYoutubeSearchVideoFeedCompleted(List<YoutubeVideo> videos) {		
		Log.d(LOG_TAG, "onYoutubeSearchVideoFeedCompleted - list size: " + Integer.toString(videos.size()));
		
		//VideoAdapter.addAll(videos);
		for(YoutubeVideo v : videos) {
			VideoAdapter.add(v);
		}
		VideoAdapter.notifyDataSetChanged();
		
		YoutubeProgress.dismiss();
	}

}
