package com.chewnoill.readthat.menuitem;


import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chewnoill.readthat.R;
import com.chewnoill.readthat.RedditInterface;
import com.chewnoill.readthat.SubRedditNotFoundException;
import com.chewnoill.readthat.R.id;
import com.chewnoill.readthat.R.layout;
import com.chewnoill.readthat.RedditInterface.Callback;
import com.chewnoill.readthat.SubredditDetailFragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SubredditMenuItem extends MenuItem{

	private static final String TAG = "SubredditMenuItem";
	private boolean loading = false;
	
	private Callback mCallback;
	private String after;
	public String name;
	private String link;
	private ArrayList<JSONObject> pages;
	private int current_page;
	private SubredditDetailFragment detailFragment;
	
	public SubredditMenuItem(Context context,
			String name,
			String link) {
		super(context);
		this.name = name;
		this.link = link;
				
		pages = new ArrayList<JSONObject>();
		//downlaod first page
		//downloadNextPage();
		current_page = 0;
	}
	public View getView(){
		LayoutInflater inflater = ((Activity)context).getLayoutInflater();
		View view = inflater.inflate(R.layout.subreddit_list_item, null, false);
		((TextView) view.findViewById(R.id.list_item_text)).setText(name);
		return view;
	}
	public void loadNextPage(final SubredditDetailFragment detailFragment){
		if(current_page>=pages.size()){
			this.detailFragment = detailFragment;
			
			downloadNextPage();
		} else {
			detailFragment.loadView(pages.get(current_page),current_page);
			current_page++;
		}
	}
	public void loadFirstPage(final SubredditDetailFragment detailFragment){
		current_page=0;
		pages = new ArrayList<JSONObject>();
		loadNextPage(detailFragment);
	}
	public void downloadNextPage(){
		RedditInterface ri = new RedditInterface(link,"GET",
				new RedditInterface.Callback(){
					@Override
					public void onPostExecute(JSONObject result) throws SubRedditNotFoundException {
						loading = false;
						if(result != null &&
								result.length()>0){
							pages.add(result);
							if(detailFragment!=null){
								loadNextPage(detailFragment);
							}
						} else {
							//Log.e(TAG,result);
							throw new SubRedditNotFoundException();
						}
							
					}
			
		});
		HashMap<String,String> data = new HashMap<String,String>();
		if(pages.size()>0){
			JSONObject obj = pages.get(pages.size()-1);
			try {
				String after = obj.getJSONObject("data").optString("after",null);
				data.put("after",after);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		JSONObject jdata = new JSONObject(data);
		Log.d(TAG,"data: "+jdata.toString()+":"+jdata.length());
		if(!loading){
			loading = true;
			ri.execute(new JSONObject(data));
		}
	}
	@Override
	public void onSelected() {
		
	}
	public interface Callback{
		//public void loadView(JSONObject info);
		//public void onPostExecute(JSONObject info);
	}
	

}
