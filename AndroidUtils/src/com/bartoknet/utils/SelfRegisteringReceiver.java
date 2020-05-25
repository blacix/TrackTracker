package com.bartoknet.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class SelfRegisteringReceiver extends BroadcastReceiver{

	protected Context TheContext;
	protected IntentFilter TheIntentFilter;
	
	
	/*************************************************************************************************
	 * 
	 */
	public SelfRegisteringReceiver(Context context) {
		TheContext = context;
		TheIntentFilter = new IntentFilter();
	}

	
	/*************************************************************************************************
	 * 
	 */
	public void registerReceiver() {
		TheContext.registerReceiver(this, TheIntentFilter);
	}
	
	
	/*************************************************************************************************
	 * 
	 */
	public void unregisterReceiver() {
		TheContext.unregisterReceiver(this);
	}


	/*************************************************************************************************
	 * 
	 */
	protected void addAction(String str) {
		TheIntentFilter.addAction(str);
	}
	

	/*************************************************************************************************
	 * 
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO : do nothing?	
	}
	

}
