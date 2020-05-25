package com.bartoknet.tracktracker.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;
import android.util.Xml;

public class YoutubeGdataPareser {

	private static final String LOG_TAG = YoutubeGdataPareser.class.getName();
	
	
	/*************************************************************************************************
	 * 
	 */	
	public static List<YoutubeVideo> parse(InputStream in) {
		Log.d(LOG_TAG, "parse - START");
		List<YoutubeVideo> videos = new ArrayList<YoutubeVideo>();
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(in, null);

			while (parser.next() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG) {
					if ("feed".equals(parser.getName())) {
						//Log.d(LOG_TAG, "feed found");
						videos = readFeed(parser);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// in.close();
		}
		
		Log.d(LOG_TAG, "parse - END");
		
		return videos;
	}    
    

    
	/*************************************************************************************************
	 * 
	 */	
	private static List<YoutubeVideo> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		
		List<YoutubeVideo> videos = new ArrayList<YoutubeVideo>();
		
		boolean feed = true;
		while (parser.next() != XmlPullParser.END_DOCUMENT && feed) {
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				//log("checking name: " + parser.getName());
				
				if("entry".equals(parser.getName())) {
					//Log.d(LOG_TAG, "***entry***");
					videos.add(readEntry(parser));
				}
			} else if(parser.getEventType() == XmlPullParser.END_TAG){
				if(parser.getName().equals("feed")) {
					feed = false;
				}
			}
		}
		
		return videos;
	}
    
	
	/*************************************************************************************************
	 * 
	 */	
    private static YoutubeVideo readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
    	
		String tagName = "";
		String title = "";
		String link = "";
		String id = "";
		String description = "";
		String viewCount = "";
		String thumbNail = "";
    	
		boolean entry = true;		
		while(entry && parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() == XmlPullParser.START_TAG) {				
				tagName = parser.getName();
				
				if ("title".equals(tagName)) {
					if(parser.next() == XmlPullParser.TEXT) {
						title = parser.getText();
						//Log.d(LOG_TAG, "title: " + title);
					}					
				} else if ("id".equals(tagName)) {
					parser.next();
					id = parser.getText();
					Log.d(LOG_TAG, "id: " + id);
				} 
				else if ("media:player".equals(tagName)) {
					link = parser.getAttributeValue(0);
					//Log.d(LOG_TAG, "media:player: " + link);
				} else if("media:description".equals(tagName)) {
					if(parser.next() == XmlPullParser.TEXT) {
						description = parser.getText();
						//Log.d(LOG_TAG, "description: " + description);
					}
				} else if("yt:statistics".equals(tagName)) {
					viewCount = parser.getAttributeValue(null, "viewCount");
					//Log.d(LOG_TAG, "viewCount: " + viewCount);
				} else if("media:thumbnail".equals(tagName)) {
					String width = parser.getAttributeValue(null, "width" );
					if(width != null) {
						if(width.equals("120")) {
							thumbNail = parser.getAttributeValue(null, "url");
							//Log.d(LOG_TAG, "media:thumbnail: " + thumbNail);
						}
					}
				}
				
			} else if(parser.getEventType() == XmlPullParser.END_TAG) {
				if("entry".equals(parser.getName())) {
					entry = false;
				}	
			}
		}
		
		return new YoutubeVideo(title, description, viewCount, link, thumbNail);
    }
    
    
	/*************************************************************************************************
	 * 
	 */	
    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                depth--;
                break;
            case XmlPullParser.START_TAG:
                depth++;
                break;
            }
        }
     }
}
