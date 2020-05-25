package com.bartoknet.utils;

import android.net.NetworkInfo;

public interface NetworkStatusReceiverInterface {
	/*************************************************************************************************
	 * 
	 * @param src
	 * @param activeNetworkInfo
	 */
	void onNetworkStatusChanged(NetworkInfo activeNetworkInfo);
	
}
