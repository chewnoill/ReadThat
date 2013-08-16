package com.chewnoill.readthat;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chewnoill.readthat.dummy.DummyContent;
import com.chewnoill.readthat.menuitem.DefaultMenu;
import com.chewnoill.readthat.menuitem.MenuItem;
import com.chewnoill.readthat.menuitem.SubredditMenuItem;


/**
 * A list fragment representing a list of subreddits. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link SubredditDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class SubredditListFragment extends ListFragment {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";


	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;


	private Callback mCallbacks;

	private static final Callback sDummyCallbacks = new Callback(){

		@Override
		public void onItemSelected(int pos) {}
		
	};

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callback {
		/**
		 * Callback for when an item has been selected.
		 */
		//public void onItemSelected(String link,String name);
		//public void onItemSelected(JSONObject content);
		public void onItemSelected(int pos);
	}
	

	private class SubredditAdapter extends ArrayAdapter<MenuItem>{

		private List<MenuItem> objects;
		private Context context;
		private int layoutResourceId;
		private int viewId;

		public SubredditAdapter(Context context,
				int layoutResourceId, 
				int viewId,
				List<MenuItem> objects) {
			super(context, layoutResourceId, viewId, objects);
			this.context = context;
			this.layoutResourceId = layoutResourceId;
			this.viewId = viewId;
			this.objects = objects;
		}
		public View getView(int position, View convertView, ViewGroup parent) {
			View row = convertView;
			if(row == null)
	        {
	            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
	            row = inflater.inflate(layoutResourceId, parent, false);
	            ((ViewGroup) row).addView(objects.get(position).getView());
	        }
			return row;
		}
		
	}

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public SubredditListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ArrayList<MenuItem> menu = new ArrayList<MenuItem>();

		
		//Front
		SubredditMenuItem front = new SubredditMenuItem(getActivity(), "Front Page", "/.json"); 
		DefaultMenu.add("Front Page",front);
		//All
		SubredditMenuItem all = new SubredditMenuItem(getActivity(), "/r/all", "/r/all/.json");
		DefaultMenu.add("/r/all",all);
		//Poop
		SubredditMenuItem poop = new SubredditMenuItem(getActivity(), "/r/poop", "/r/poop/.json");
		DefaultMenu.add("/r/poop",poop);
		//
		
		setListAdapter(new SubredditAdapter(getActivity(), 
				R.layout.subreddit_list, 
				R.id.menu_item, 
				DefaultMenu.MENU_LIST));

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null
				&& savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState
					.getInt(STATE_ACTIVATED_POSITION));
		}
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
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks ;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position,
			long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(position);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(
				activateOnItemClick ? ListView.CHOICE_MODE_SINGLE
						: ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
}
