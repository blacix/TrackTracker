package com.bartoknet.tracktracker;


import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bartoknet.tracktracker.facebook.FacebookInterface;
import com.bartoknet.tracktracker.musicplayer.MusicPlayerReceiver;
import com.bartoknet.tracktracker.musicplayer.MusicPlayerReceiverInterface;
import com.bartoknet.tracktracker.youtube.YoutubeVideo;
import com.bartoknet.utils.NetworkStatusReceiverInterface;
import com.bartoknet.utils.NetworkStatusReceiver;
import com.bartoknet.utils.Utils;
import com.bartoknet.tracktracker.R;
import com.facebook.Session;


public class MainActivity extends Activity implements NetworkStatusReceiverInterface, MusicPlayerReceiverInterface {

	private static final String LOG_TAG = MainActivity.class.getName();
	private static final int VIDEO_SEARCH_REQUEST_CODE = 100;

	NetworkStatusReceiver NetworkReceiver;
	MusicPlayerReceiver MusicPlayer;
	YoutubeVideo VideoToPost;
	
	// GUI elements	
	TextView TextViewNetworkStatus;
	
	TextView TextViewYoutubeSearch;
	EditText EditTextYoutubeSearch;
	Button ButtonYoutubeSearch;

	TextView TextViewFacebookPost;
	EditText EditTextFacebookPost;
	CheckBox CheckBoxSilentPost;
	Button ButtonFacebookPost;
	
	TextView TextViewVideos;
	
	View VideoView;
	
	FacebookInterface Facebook;
	
	
	// TODO: maybe put this in a separate file
	View.OnClickListener ClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.button_main_youtube_search:
				onButtonYoutubeSearchPressed();
				break;
				
			case R.id.button_main_facebook_post:
				onButtonFacebookPostPressed();
				break;

			}
		}
	};
	


	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(LOG_TAG, "onCreate");
		setContentView(R.layout.activity_main);

		
		// hide keyboard
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		// get GUI components
		TextViewNetworkStatus = (TextView) findViewById(R.id.textview_main_networkstatus);
		
		TextViewYoutubeSearch = (TextView) findViewById(R.id.textview_main_search);
		EditTextYoutubeSearch = (EditText) findViewById(R.id.edittext_main_youtube_search);
		ButtonYoutubeSearch = (Button) findViewById(R.id.button_main_youtube_search);
		ButtonYoutubeSearch.setOnClickListener(ClickListener);

		TextViewFacebookPost = (TextView) findViewById(R.id.textview_main_facebook_post);
		EditTextFacebookPost = (EditText) findViewById(R.id.edittext_main_facebook_post);
		ButtonFacebookPost = (Button) findViewById(R.id.button_main_facebook_post);
		ButtonFacebookPost.setOnClickListener(ClickListener);
		
		TextViewVideos = (TextView)findViewById(R.id.textview_main_videos);
		
		

		// subscribe for NW state changes
		NetworkReceiver = new NetworkStatusReceiver(getApplicationContext());
		NetworkReceiver.addNotifier(this);
		NetworkReceiver.registerReceiver();
		
		
		// music player interface init
		MusicPlayer = new MusicPlayerReceiver(this);
		MusicPlayer.addNotifier(this);
		MusicPlayer.registerReceiver();


		// init facebook
		Facebook = new FacebookInterface(this);
		Facebook.login();

		VideoToPost = null;
		
		this.updateButtonFacebookPost();
		this.updateButtonYoutubeSearch();
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		NetworkReceiver.unregisterReceiver();
		MusicPlayer.unregisterReceiver();

	}
	
	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		//Log.d(LOG_TAG, "onCreateOptionsMenu");
		return true;
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d(LOG_TAG, "onActivityResult" + " - requestCode: " + Integer.toString(requestCode));
		
		if(requestCode == VIDEO_SEARCH_REQUEST_CODE) {
			// TODO: outsorurce this to eg.: handleVideoSearchActivityResult();
			if(resultCode == Activity.RESULT_OK) {

				String title = data.getStringExtra("title");
				String viewcount = data.getStringExtra("viewcount");
				String descritption = data.getStringExtra("descritption");
				String uri = data.getStringExtra("uri");
				String thumbnail = data.getStringExtra("thumbnail");
				Bitmap thumbnailImage = data.getParcelableExtra("thumbnail_image");
				VideoToPost = new YoutubeVideo(title, descritption, viewcount, uri, thumbnail);
				
				LayoutInflater inflater = this.getLayoutInflater();
				RelativeLayout parent = (RelativeLayout)findViewById(R.id.relative_layout_main);
				
				parent.removeView(VideoView);
				
				VideoView = inflater.inflate(R.layout.listview_row, parent,false);
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				lp.addRule(RelativeLayout.BELOW, R.id.button_main_facebook_post);  
				parent.addView(VideoView, lp);
				
				TextView t = (TextView)VideoView.findViewById(R.id.textview_listview_row_title);
				t.setText(title);
				
				ImageView i = (ImageView)VideoView.findViewById(R.id.imageview_listview_row);
				i.setImageBitmap(thumbnailImage);
				
				
				updateButtonFacebookPost();
				
			}
			
		} else {			
			
			Session activeSession = Session.getActiveSession();
			if(activeSession != null) {
				activeSession.onActivityResult(this, requestCode, resultCode, data);
				Log.d(LOG_TAG, "onActivityResult - session state: " + activeSession.getState());				
			} else {
				Log.d(LOG_TAG, "onActivityResult - session is null ");	
			}			
		}
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onNetworkStatusChanged(NetworkInfo activeNetworkInfo) {
		Log.d(LOG_TAG, "onNetworkStatusChanged");
		String text = "No network!";
		if (activeNetworkInfo != null) {
			switch (activeNetworkInfo.getType()) {
			case ConnectivityManager.TYPE_MOBILE:
				text = "Mobile data connected";
				break;
			case ConnectivityManager.TYPE_WIFI:
				text = "WIFI connected";
				break;
			default:
				text = "Some network is connected";
				break;
			}
			
			if(!Facebook.isLoggedIn()) {
				Facebook.login();
			}

		}
		
		TextViewNetworkStatus.setText(text);
		
		updateButtonFacebookPost();
		updateButtonYoutubeSearch();
	}

	
	/*************************************************************************************************
	 * 
	 */
	void onButtonYoutubeSearchPressed() {
		Log.d(LOG_TAG, "onButtonYoutubeSearchPressed");
		if(Utils.isNetworkAvailable(this)) {
			
			Intent yt = new Intent(this, VideoListActivity.class);
			yt.putExtra("searchterm", EditTextYoutubeSearch.getText().toString());
			
			startActivityForResult(yt, VIDEO_SEARCH_REQUEST_CODE);
			
		}
		else {
			Toast.makeText(this, "No data network!", Toast.LENGTH_SHORT).show();
			this.updateButtonYoutubeSearch();
		}
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	void onButtonFacebookPostPressed() {
		Log.d(LOG_TAG, "onButtonFacebookPostPressed");
		if(VideoToPost != null) {
			try {
				Facebook.postToFeed(EditTextFacebookPost.getText().toString(), 
						VideoToPost.getTitle(), "caption", VideoToPost.getDescription(), 
						VideoToPost.getUri().toURL().toString(), "");
			}
			catch(Exception e) {
				
			}
		}
	}
	
	
	/*************************************************************************************************
	 * 
	 */	
	private void updateButtonFacebookPost() {
		ButtonFacebookPost.setEnabled(VideoToPost != null 
		&& Utils.isNetworkAvailable(getApplicationContext())
		&& Facebook.isLoggedIn());
	}

	
	/*************************************************************************************************
	 * 
	 */		
	private void updateButtonYoutubeSearch() {
		ButtonYoutubeSearch.setEnabled(Utils.isNetworkAvailable(getApplicationContext()));
	}

	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onMusicPlayerDataReceived(String data) {
		this.EditTextYoutubeSearch.setText(data);
	}

}
