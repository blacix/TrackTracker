package com.bartoknet.tracktracker.youtube;

import java.util.ArrayList;

import com.bartoknet.tracktracker.R;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class YoutubeVideoAdapter extends ArrayAdapter<YoutubeVideo> {
	private static final String LOG_TAG = YoutubeVideoAdapter.class.getName();
	
	Context TheContext;
	int LayoutResourceId;
	ArrayList<YoutubeVideo> VideoList = null;

	
	/*************************************************************************************************
	 * 
	 */
	public YoutubeVideoAdapter(Context context, int layoutResourceId, ArrayList<YoutubeVideo> data) {
		super(context, layoutResourceId, data);
		this.LayoutResourceId = layoutResourceId;
		this.TheContext = context;
		this.VideoList = data;
	}
	
	
	/*************************************************************************************************
	 * 
	 */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	//Log.d(LOG_TAG, "getView");
    	
        View row = convertView;
        YoutubeVideoHolder holder;
       
        boolean recycling = false;
        if(row==null || !recycling)
        {
            /* There is no view at this position, we create a new one. 
            In this case by inflating an xml layout */
            LayoutInflater inflater = ((Activity)TheContext).getLayoutInflater();
            row = inflater.inflate(LayoutResourceId, parent,false);
           
            holder = new YoutubeVideoHolder();
           
            holder.Title = (TextView)row.findViewById(R.id.textview_listview_row_title);
            holder.Thumbnail = (ImageView)row.findViewById(R.id.imageview_listview_row);            
            row.setTag(holder);
        } else {
        	/* We recycle a View that already exists */
            holder = (YoutubeVideoHolder) row.getTag();
        }
       
        
		if (VideoList.size() > 0 ) {
			YoutubeVideo video = VideoList.get(position);
			
			holder.Title.setText(video.getTitle());
			
			if(video.hasImage()) {
				holder.Thumbnail.setImageBitmap(video.getImage());
			} else {
				video.downloadImage(holder.Thumbnail, this);
			}
			
		}
        return row;
    }
    
    
	/*************************************************************************************************
	 * 
	 */
    static class YoutubeVideoHolder
    {
        TextView Title;
        ImageView Thumbnail;
    }
}
