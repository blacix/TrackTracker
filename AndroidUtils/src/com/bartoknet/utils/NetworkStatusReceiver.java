package com.bartoknet.utils;

import java.util.Iterator;
import java.util.Vector;

import android.content.Context;
import android.content.Intent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;




public class NetworkStatusReceiver extends SelfRegisteringReceiver{
	
	static final String LOG_TAG = NetworkStatusReceiver.class.getSimpleName();
	public static final String ACTION_NETWORKCHANGED 	= "android.net.conn.CONNECTIVITY_CHANGE";
	
	Vector<NetworkStatusReceiverInterface> Notifiers;
	
	
	/*************************************************************************************************
	 * 
	 * @param toNotify
	 */
	public NetworkStatusReceiver(Context context) {
		super(context);
		Log.d(LOG_TAG, "NetworkStatusReceiver");
		addAction(ACTION_NETWORKCHANGED);
		
		Notifiers = new Vector<NetworkStatusReceiverInterface>();
	}

	
	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(LOG_TAG, "onReceive");
		//String action = intent.getAction();
		//Bundle extras = intent.getExtras();
		//deprecated
		//NetworkInfo nwInfo = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		
		// new api stuff
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		notifyObservers(connectivityManager.getActiveNetworkInfo());

	}
	
	
	
	/*************************************************************************************************
	 * @param
	 * 
	 */
	public void addNotifier(NetworkStatusReceiverInterface notifier) {
		Notifiers.add(notifier);
	}
	
	
	
	/*************************************************************************************************
	 * @param
	 * 
	 */
	private void notifyObservers(NetworkInfo nwi) {
		Iterator<NetworkStatusReceiverInterface> it = Notifiers.iterator();
		while(it.hasNext()) {
				it.next().onNetworkStatusChanged(nwi);
		}
	}

}
