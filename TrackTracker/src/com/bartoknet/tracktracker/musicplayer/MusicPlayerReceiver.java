package com.bartoknet.tracktracker.musicplayer;

import java.util.Iterator;
import java.util.Vector;

import com.maxmpz.audioplayer.player.PowerAMPiAPI;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.bartoknet.utils.SelfRegisteringReceiver;

//import com.maxmpz.audioplayer.player.PowerAMPiAPI;


public class MusicPlayerReceiver extends SelfRegisteringReceiver {
	
	private static final String LOG_TAG = MusicPlayerReceiver.class.getName();;	

	public static final String ACTION_METACHANGED_POWERAMP = PowerAMPiAPI.ACTION_TRACK_CHANGED;
	public static final String ACTION_GOOGLE_METACHANGED = "com.google.android.music.metachanged";
	public static final String ACTION_DEFAULT_METACHANGED = "com.android.music.metachanged"; 
	public static final String ACTION_WINAMP_METACHANGED = "com.nullsoft.winamp.metachanged";
	public static final String ACTION_WINAMP_PLAYSTATECHANGED = "com.nullsoft.winamp.playstatechanged";
	public static final String ACTION_MIUI_METACHANGED = "com.miui.player.metachanged";
	public static final String ACTION_SEC_METACHANGED = "com.sec.android.app.music.metachanged"; // sony
	public static final String ACTION_SEC2_METACHANGED = "com.sonyericsson.music.metachanged";
	public static final String ACTION_SAMSUNG_METACHANGED = "com.samsung.sec.android.MusicPlayer.metachanged";
	public static final String ACTION_AMAZON_METACHANGED = "com.amazon.mp3.metachanged";
	public static final String ACTION_APOLLO_METACHANGED = "com.andrew.apollo.metachanged";
	public static final String ACTION_LAST_FM_METACHANGED = "fm.last.android.metachanged";
	public static final String ACTION_REAL_METACHANGED = "com.real.IMP.metachanged";
	public static final String ACTION_HTC_METACHANGED = "com.htc.music.metachanged";
	public static final String ACTION_RDIO_METACHANGED = "com.rdio.android.metachanged";
	public static final String ACTION_JRT_METACHANGED = "com.jrtstudio.music.metachanged";
	public static final String ACTION_JRT_ROCKET_METACHANGED = "com.jrtstudio.AnotherMusicPlayer.metachanged";
	
	

	Vector<MusicPlayerReceiverInterface> Notifiers;
	
	
	/*************************************************************************************************
	 * 
	 */
	public MusicPlayerReceiver(Context context) {
		super(context);

		Notifiers = new Vector<MusicPlayerReceiverInterface>();
		
		addAction(ACTION_METACHANGED_POWERAMP);
		addAction(ACTION_GOOGLE_METACHANGED);
		addAction(ACTION_DEFAULT_METACHANGED);
		addAction(ACTION_WINAMP_METACHANGED);
		addAction(ACTION_WINAMP_PLAYSTATECHANGED);
		addAction(ACTION_MIUI_METACHANGED);
		addAction(ACTION_SEC_METACHANGED);
		addAction(ACTION_SEC2_METACHANGED);
		addAction(ACTION_SAMSUNG_METACHANGED);
		addAction(ACTION_AMAZON_METACHANGED);
		addAction(ACTION_APOLLO_METACHANGED);
		addAction(ACTION_LAST_FM_METACHANGED);
		addAction(ACTION_REAL_METACHANGED);
		addAction(ACTION_HTC_METACHANGED);
		addAction(ACTION_RDIO_METACHANGED);
		addAction(ACTION_JRT_METACHANGED);
		addAction(ACTION_JRT_ROCKET_METACHANGED);
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive: " + intent.getAction());
		String action = intent.getAction();

		String artist = "";
		String album = "";
		String track = "";

		String currentTrack = "";
		String currentTrackSimple = "";

		if (action.equals((ACTION_GOOGLE_METACHANGED))
				|| action.equals(ACTION_DEFAULT_METACHANGED)
				|| action.equals(ACTION_MIUI_METACHANGED)
				|| action.equals(ACTION_SEC_METACHANGED)
				|| action.equals(ACTION_SEC2_METACHANGED)
				|| action.equals(ACTION_SAMSUNG_METACHANGED)
				|| action.equals(ACTION_HTC_METACHANGED)
				|| action.equals(ACTION_APOLLO_METACHANGED)
				|| action.equals(ACTION_LAST_FM_METACHANGED)
				|| action.equals(ACTION_REAL_METACHANGED)
				|| action.equals(ACTION_WINAMP_PLAYSTATECHANGED)
				|| action.equals(ACTION_WINAMP_METACHANGED)
				|| action.equals(ACTION_JRT_METACHANGED)
				|| action.equals(ACTION_JRT_ROCKET_METACHANGED)) {

			artist = intent.getStringExtra("artist");
			album = intent.getStringExtra("album");
			track = intent.getStringExtra("track");
			
		} else if (action.equals((ACTION_METACHANGED_POWERAMP))) {
			// Log.d(LOG_TAG, "PowerAMP player Track changed! ");
			Bundle extras = intent.getBundleExtra(PowerAMPiAPI.TRACK);
			artist = extras.getString(PowerAMPiAPI.Track.ARTIST);
			album = extras.getString(PowerAMPiAPI.Track.ALBUM);
			track = extras.getString(PowerAMPiAPI.Track.TITLE);
		}
		
		// got sometging to care about?
		if(artist != null && track != null)
		{
			currentTrack = artist + " - " + album + " - " + track;
			currentTrackSimple = artist + " " + track;
			
			notifyObservers(currentTrackSimple);
		}
		
		
		Log.d(LOG_TAG, "onReceive - " + currentTrack);
		
		// has extras, log them..
		Bundle b = intent.getExtras();
		if (b != null) {
			Log.d(LOG_TAG, "has extras");
			for (String s : b.keySet()) {
				Log.d(LOG_TAG, "bundle key: " + s);
			}
		}
	}
	
	
	
	/*************************************************************************************************
	 * @param
	 * 
	 */
	public void addNotifier(MusicPlayerReceiverInterface notifier) {
		Notifiers.add(notifier);
	}
	
	
	/*************************************************************************************************
	 * @param
	 * 
	 */
	private void notifyObservers(String data) {
		Iterator<MusicPlayerReceiverInterface> it = Notifiers.iterator();
		while(it.hasNext()) {
				it.next().onMusicPlayerDataReceived(data);
		}
	}
	

}
