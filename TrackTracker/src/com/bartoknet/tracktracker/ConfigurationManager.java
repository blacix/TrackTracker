package com.bartoknet.tracktracker;


import com.facebook.AccessToken;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.android.Facebook;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class ConfigurationManager {
	
	private static final String LOG_TAG = ConfigurationManager.class.getSimpleName();
    
    private static final String FB_TOKEN = "access_token";
    private static final String FB_EXPIRES = "expires_in";
    private static final String FB_SESSION_KEY = "facebook-session";
    private static final String SERVICE_KEY = "service_key";
    
	/*************************************************************************************************
	 * 
	 */
    public static boolean saveFacebookSession(Session session, Context context) {
    	Log.d(LOG_TAG, "saveFacebookSession");
        Editor editor =
            context.getSharedPreferences(FB_SESSION_KEY, Context.MODE_PRIVATE).edit();
        editor.putString(FB_TOKEN, session.getAccessToken());
        editor.putString(FB_EXPIRES, session.getExpirationDate().toString());
        return editor.commit();
    }

	/*************************************************************************************************
	 * 
	 */
    public static boolean restoreFacebookSession(Session session, Context context,
    		StatusCallback callback) {
    	
    	Log.d(LOG_TAG, "restoreFacebookSession");
    	SharedPreferences savedSession =
            context.getSharedPreferences(FB_SESSION_KEY, Context.MODE_PRIVATE);
    	String tokenString = savedSession.getString(FB_TOKEN, null);
    	if(tokenString != null && tokenString != "")
    	{
    		AccessToken t = AccessToken.createFromExistingAccessToken(tokenString, null, null, null, null);
    		session.open(t, callback);
    		return true;
    	}
    	return false;
    }

	/*************************************************************************************************
	 * 
	 */
    public static void clearFacebookSession(Context context) {
    	Log.d(LOG_TAG, "clearFacebookSession");
        Editor editor = 
            context.getSharedPreferences(FB_SESSION_KEY, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.commit();
    }
    
	/*************************************************************************************************
	 * 
	 */
    public static boolean saveServiceState(Context context, boolean enabled) {
    	Log.d(LOG_TAG, "saveServiceState");
    	Editor editor =
            context.getSharedPreferences(SERVICE_KEY, Context.MODE_PRIVATE).edit();
        editor.putBoolean(SERVICE_KEY, enabled);
        return editor.commit();
    }
    
	/*************************************************************************************************
	 * 
	 */
    public static boolean getServiceState(Context context) {
    	Log.d(LOG_TAG, "getServiceState");
    	SharedPreferences serviceState =
            context.getSharedPreferences(SERVICE_KEY, Context.MODE_PRIVATE);
        
        return serviceState.getBoolean(SERVICE_KEY, false);
    }

    
}
