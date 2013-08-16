package com.chewnoill.readthat;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chewnoill.readthat.menuitem.DefaultMenu;
import com.chewnoill.readthat.menuitem.SubredditMenuItem;
/**
 * A fragment representing a single subreddit detail screen. This fragment is
 * either contained in a {@link SubredditListActivity} in two-pane mode (on
 * tablets) or a {@link SubredditDetailActivity} on handsets.
 */
public class SubredditDetailFragment extends Fragment implements 
	SubredditDetailView.EventListener {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_LINK = "link";
	public static final String ARG_ITEM_NAME = "name";
	public static final String ARG_ITEM_CONTENT = "content";
	protected static final String TAG = null;
	public static final String ARG_ITEM_NUMBER = "number";

	

	public interface Callback {

		
	}
	private Callback mCallbacks;

	private LayoutInflater inflater;

	private String after;

	private String mValues;
	private int mMenuNumber=-1;
	private View rootView;
	

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SubredditDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"onCreate");
		if (getArguments().containsKey(ARG_ITEM_CONTENT)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			String values = getArguments().getString(ARG_ITEM_CONTENT);
			try {
				loadView(new JSONObject(values),0);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(getArguments().containsKey(ARG_ITEM_NUMBER)){
			mMenuNumber = getArguments().getInt(ARG_ITEM_NUMBER);
			
		}
	}

	public void loadView(JSONObject values,int page_num){
		LinearLayout list = (LinearLayout) rootView.findViewById(R.id.subreddit_detail);
		if(page_num == 0){
			list.removeAllViews();
		} else {
			View page_number = inflater.inflate(R.layout.article_page_number,
					null, false);
			((TextView)page_number.findViewById(R.id.page_number)).setText("page: "+(page_num+1));
			list.addView(page_number);
		}
		Log.d(TAG,"loadView");
		
		
		ArrayList<View> content = loadContent(values);
		for (View v: content){
			list.addView(v);
		}
		SubredditDetailView sdv = (SubredditDetailView) rootView.findViewById(R.id.content_view);
		sdv.refreshComplete();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG,"onCreateView");
		this.inflater = inflater;
		rootView = inflater.inflate(R.layout.fragment_subreddit_detail,
				container, false);
		SubredditDetailView sdv = (SubredditDetailView) rootView.findViewById(R.id.content_view);
		sdv.setEventListener(this);
		if(mMenuNumber!=-1){
			((SubredditMenuItem)DefaultMenu.MENU_LIST.get(mMenuNumber)).loadFirstPage(this);
		}
		
		
		
		return rootView;
	}
	
	
	public ArrayList<View> loadContent(JSONObject content){
		String kind = content.optString("kind","");
		if(kind.equals("Listing")){
			ArrayList<View> views = new ArrayList<View>(); 
			try {
				JSONObject data = content.getJSONObject("data");
				//use this to get next page
				after = data.getString("after");
				JSONArray children = data.getJSONArray("children");
				for(int x=0;x<children.length();x++){
					JSONObject thing = children.getJSONObject(x);
					kind = thing.getString("kind");
					if(kind.equals("t3")){
						views.add(toView(thing));
					}
					
				}
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return views;
		}
		
		return new ArrayList<View>();
	}
	public View toView(JSONObject obj) throws JSONException{
		JSONObject data = obj.getJSONObject("data");
		String title = data.getString("title");
		String score = data.getInt("score")+"";
		String thumbnail = data.getString("thumbnail");
		String subreddit_id = data.getString("subreddit_id");
		String num_comments = data.getInt("num_comments")+"";
		String subreddit = data.getString("subreddit");
		String author = data.getString("author");
		int created_utc = data.getInt("created_utc");
		
		
		
		View article = inflater.inflate(R.layout.article_small, null, false);
		((TextView) article.findViewById(R.id.title)).setText(title);
		((TextView) article.findViewById(R.id.score)).setText(score);
		((TextView) article.findViewById(R.id.submitted_by)).setText(author);
		
		((TextView) article.findViewById(R.id.submitted_to)).setText(subreddit);
		((TextView) article.findViewById(R.id.comments)).setText(num_comments+" comments");
		if(thumbnail.equals("")||
				thumbnail.equals("self")){
			return article;
		}
		final ImageView thumbnail_view = (ImageView) article.findViewById(R.id.thumbnail);
		
		LoadImage li = new LoadImage(new LoadImage.Callback(){

			@Override
			public void onPostExecute(Bitmap image) {
				if(image!=null){
					thumbnail_view.setImageBitmap(image);
				}
				
			}
			
		});
		li.execute(thumbnail);
		return article;
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callback)) {
			throw new IllegalStateException(
					"Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callback) activity;
	}

	@Override
	public void refreshEventListener() {
		Log.d(TAG,"refreshEventListener");
		((SubredditMenuItem)DefaultMenu.MENU_LIST.get(mMenuNumber)).loadFirstPage(this);
	}

	@Override
	public void refreshProgressListener(float progress) {
		View childLayout = rootView.findViewById(R.id.refresh_notify);
		
		if(progress == 0){
			childLayout.setVisibility(View.GONE);
		} else{
			Log.d(TAG,"refreshProgressListener: "+progress);
			int width = (int) (progress*rootView.getWidth());
			int height = 15;
			
			childLayout.setVisibility(View.VISIBLE);
			LayoutParams lp = new LayoutParams(width, height);
			lp.gravity = Gravity.CENTER;
			childLayout.setLayoutParams(lp);
		}
		rootView.invalidate();
		
	}

	@Override
	public void nextPageEventListener() {
		Log.d(TAG,"nextPageEventListener");
		((SubredditMenuItem)DefaultMenu.MENU_LIST.get(mMenuNumber)).loadNextPage(this);
		
	}
}
