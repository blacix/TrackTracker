package com.bartoknet.tracktracker.facebook;

import java.util.Arrays;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.Session.StatusCallback;
import com.facebook.SessionState;
public class FacebookInterface {
	
	//Context AppContext;
	List<String> PublishPermissions = Arrays.asList("publish_stream", "publish_actions");
	
	
	Activity AppActivity;
	Session FacebookSession;
	Bundle SavedSession;
	boolean PermissionsRequested;

	private static final String LOG_TAG = FacebookInterface.class.getName();
	
	/*************************************************************************************************
	 * 
	 */
	public FacebookInterface(Activity appActivity) {
		AppActivity = appActivity;
		PermissionsRequested = false;
	}
	
	
	/*************************************************************************************************
	 * 
	 */
    private StatusCallback sessionStatusCallback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
        	Log.d(LOG_TAG, "StatusCallback - facebook callback: " + state);
        	if(session.isOpened())
        	{
        		Log.d(LOG_TAG, "StatusCallback - session opened. token: " + session.getAccessToken() );
        		//if(!PermissionsRequested) {
        		if(!hasPublishPermissions()) {
        			requestPermissions();
        		}        			
        	}
        	else
        	{
        		//Log.d(LOG_TAG, "StatusCallback - session NOT opened ");
        	}
        	
        }
    };
    
    
    
	/*************************************************************************************************
	 * 
	 */
    public void login() {
    	if(!isLoggedIn()) {
			FacebookSession = Session.getActiveSession();
			if (FacebookSession != null && !FacebookSession.isOpened() && !FacebookSession.isClosed()) {
				FacebookSession.openForRead(new Session.OpenRequest(AppActivity).setCallback(sessionStatusCallback));
			} else {
				FacebookSession = Session.openActiveSession(AppActivity, true, sessionStatusCallback);
			}
		}
    }
    
    
	/*************************************************************************************************
	 * 
	 */
    public void requestPermissions() {
    	FacebookSession.requestNewPublishPermissions(new Session.NewPermissionsRequest(AppActivity, PublishPermissions));
    	PermissionsRequested = true;
    }    
    
    
	/*************************************************************************************************
	 * 
	 */
    public boolean isLoggedIn() {
    	if(FacebookSession != null) {
    		return FacebookSession.isOpened();
    	}
    	return false;
    }
    
    
	/*************************************************************************************************
	 * 
	 */
    public void postToFeed(String message, String name, String caption, String description, String link, String picture) {
    	if(!PermissionsRequested) {
    		requestPermissions();
    		return;
    	}
    	
	    if (FacebookSession != null){

	        Bundle postParams = new Bundle();
	        postParams.putString("message", message);
	        postParams.putString("name", name);
	        postParams.putString("caption", caption);
	        postParams.putString("description", description);
	        postParams.putString("link", link);
	        postParams.putString("picture", "");

	        Request.Callback callback= new Request.Callback() {
	            public void onCompleted(Response response) {
	                JSONObject graphResponse = response
	                                           .getGraphObject()
	                                           .getInnerJSONObject();
	                String postId = null;
	                try {
	                    postId = graphResponse.getString("id");
	                } catch (JSONException e) {
	                    Log.d(LOG_TAG,
	                        "JSON error "+ e.getMessage());
	                }
	                FacebookRequestError error = response.getError();
	                if (error != null) {
	                    Toast.makeText(AppActivity
	                         .getApplicationContext(),
	                         "Post failed: " + error.getErrorMessage(),
	                         Toast.LENGTH_SHORT).show();
	                    } else {
	                        Toast.makeText(AppActivity
	                             .getApplicationContext(), 
	                             "Post suceeded",
	                             Toast.LENGTH_LONG).show();
	                }
	            }
	        };

	        Request request = new Request(FacebookSession, "me/feed", postParams, 
	                              HttpMethod.POST, callback);

	        RequestAsyncTask task = new RequestAsyncTask(request);
	        task.execute();
	    }
    }
    
    
	/*************************************************************************************************
	 * 
	 */
    public static void logPermissions(Session session) {
    	List<String> sessionPermissions = session.getPermissions();
		String permissionsString = "";
		for(String s : sessionPermissions)
		{
			permissionsString = permissionsString + " " + s;
		}
		
		Log.d(LOG_TAG, "permission:" + permissionsString);
    }
    
    
	/*************************************************************************************************
	 * 
	 */
    boolean hasPublishPermissions() {
    	if(FacebookSession != null) {
    		return FacebookSession.getPermissions().containsAll(PublishPermissions);
    	}    	
    	return false;
    }
}
