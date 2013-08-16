package com.chewnoill.readthat;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * An activity representing a single subreddit detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a
 * {@link SubredditListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link SubredditDetailFragment}.
 */
public class SubredditDetailActivity extends FragmentActivity implements 
	SubredditDetailFragment.Callback{

	private static final String TAG = "SubredditDetailActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_subreddit_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			String link = getIntent().getStringExtra(
					SubredditDetailFragment.ARG_ITEM_LINK);
			String name = getIntent().getStringExtra(
					SubredditDetailFragment.ARG_ITEM_NAME);
			setTitle(name);
			Bundle arguments = new Bundle();
			
			arguments.putString(
					SubredditDetailFragment.ARG_ITEM_LINK,
					link);
			arguments.putString(
					SubredditDetailFragment.ARG_ITEM_NAME,
					name);
			SubredditDetailFragment fragment = new SubredditDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.subreddit_detail_container, fragment).commit();
		}
	}
	public void login(String username,String password){
		RedditInterface ri = new RedditInterface("/api/login","POST",
				new RedditInterface.Callback(){

					@Override
					public void onPostExecute(JSONObject result) {
						Log.d(TAG,"results: "+result.toString());
					}
			
		});
		HashMap<String,Object> data = new HashMap<String,Object>();
		data.put("api_type","json");
		data.put("user",username);
		data.put("passwd",password);
		data.put("rem",true);
		ri.execute(new JSONObject(data));
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpTo(this, new Intent(this,
					SubredditListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
