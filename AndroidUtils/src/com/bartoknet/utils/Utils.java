package com.bartoknet.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;

public class Utils {
	
	/*************************************************************************************************
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		final ConnectivityManager connMgr = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		final android.net.NetworkInfo wifi = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		final android.net.NetworkInfo mobile = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

		return wifi.isConnected() || mobile.isConnected();
	}
	
	
	
	/*************************************************************************************************
	 * 
	 * @param InputStream to convert
	 * @return string 
	 */	
	public static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();

	}
	
	
	public static boolean isAppInstalled(String uri, Context context) {
	    PackageManager pm = context.getPackageManager();
	    boolean installed = false;
	    try {
	        pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
	        installed = true;
	    } catch (PackageManager.NameNotFoundException e) {
	        installed = false;
	    }
	    return installed;
	}
}

