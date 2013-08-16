package com.chewnoill.readthat;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.chewnoill.readthat.menuitem.DefaultMenu;
import com.chewnoill.readthat.menuitem.SubredditMenuItem;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;

public class CommentFragment extends Fragment {

	private static final String TAG = "CommentFragment";
	private LayoutInflater inflater;
	private View rootView;
	private ArrayList<comment_tree> top_comments;
	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public CommentFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		this.inflater = inflater;
		rootView = inflater.inflate(R.layout.article_comment_list,
				container, false);
		
		return rootView;
	}
	public void loadCommentRoot(JSONArray comments){
		try {
			JSONObject top = comments.getJSONObject(0)
					.getJSONObject("data")
					.getJSONArray("children")
					.getJSONObject(0);
			int score = top.getInt("score");
			String html_content = top.getString("selftext_html");
			
			View top_view = inflater.inflate(R.layout.article_comment, null);
			((TextView)top_view.findViewById(R.id.score)).setText(score+"");
			((WebView)top_view.findViewById(R.id.comment)).loadData(html_content, "text/html", "utf-8");
			JSONArray replies = comments.getJSONObject(1)
					.getJSONObject("data")
					.getJSONArray("children");
			ArrayList<comment_tree> top_comments = new ArrayList<comment_tree>();
			for(int x=0; x<replies.length(); x++){
				top_comments.add(new comment_tree(replies.getJSONObject(x)));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	private class comment_tree{
		String kind;
		View comment;
		ArrayList<comment_tree> children;
		public comment_tree(JSONObject comments){
			kind = comments.optString("kind");
			children = new ArrayList<comment_tree>();
			if(kind.equals("t1")){
				JSONObject data = comments.optJSONObject("data");
				String body_html = data.optString("body_html");
				boolean score_hidden = data.optBoolean("score_hidden",true);
				String score = "????";//if hidden
				if(!score_hidden){
					score = (data.optInt("ups")-data.optInt("downs"))+"";
				}
				comment = inflater.inflate(R.layout.article_comment, null);
				((TextView)comment.findViewById(R.id.score)).setText(score+"");
				((WebView)comment.findViewById(R.id.comment)).loadData(body_html, "text/html", "utf-8");
				
				JSONArray jChildren = data.optJSONObject("replies")
						.optJSONObject("data")
						.optJSONArray("children");
				if(jChildren!=null){
					for(int x=0; x<jChildren.length(); x++){
						children.add(new comment_tree(jChildren.optJSONObject(x)));
						
					}
				}
				
				
			}else if(kind.equals("more")){
				
			}
		}
	}
}
