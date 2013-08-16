package com.chewnoill.readthat;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

public class SubredditDetailView extends ScrollView {
	private static final String TAG = "SubredditDetailView";
	private static final float THRESHOLD = 200;
	private static boolean STATE_NEXT_PAGE_ENABLED = false;
	private static boolean STATE_REFRESH_ENABLED = false;
	
	public static final int REFRESH_EVENT = 0x001;
	public static final int NEXT_PAGE_EVENT = 0x002;
	
	private float startY;
	private boolean STATE_REFRESHING=true;
	private EventListener mEventListener = dummyEventListener;
	private static final EventListener dummyEventListener = new EventListener(){

		@Override
		public void refreshEventListener() {}

		@Override
		public void nextPageEventListener() {}

		@Override
		public void refreshProgressListener(float progress) {}
		
	};
	interface EventListener {
		public void refreshEventListener();
		public void refreshProgressListener(float progress);
		public void nextPageEventListener();
		
	}
	public SubredditDetailView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		
		
	}
	public SubredditDetailView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public void setEventListener(EventListener mEventListener){
		this.mEventListener = mEventListener;
	}
	@Override
	protected void onOverScrolled (int scrollX, 
			int scrollY, 
			boolean clampedX, boolean clampedY){
		if(clampedY){
			//clampedY=false;
			if(getScrollY()==0){
				//make user scroll up to refresh, fire in onTouchEvent
				STATE_REFRESH_ENABLED = true;
			} else {
				if(!STATE_REFRESHING){
					//fire next page event right away
					STATE_NEXT_PAGE_ENABLED = true;
					fireNextPageEvent();
				}
			}
		}
		super.onOverScrolled(scrollX,scrollY,clampedX,clampedY);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {

	    float y = event.getY();

	    switch (event.getAction()) {
	        case MotionEvent.ACTION_MOVE: {
	            if (STATE_REFRESH_ENABLED && !STATE_REFRESHING ) {
	            	if(( y - startY) > THRESHOLD){
	            		Log.d(TAG,"fire refresh event");
	            		fireRefreshEvent();
	            	}else{
	            		float progress = (y - startY)/THRESHOLD;
	            		fireRefreshProgress(progress);
	            	}
	            }
	        }
	        break;
	        case MotionEvent.ACTION_DOWN: {
	            startY = y;
	            
	        }
	        case MotionEvent.ACTION_UP: {
	            STATE_REFRESHING = false;
	            STATE_NEXT_PAGE_ENABLED = false;
	            fireRefreshProgress(0);
	        }

	    }
	    
	    return super.onTouchEvent(event);
	}
	private void fireRefreshEvent() {
		STATE_REFRESHING=true;
		mEventListener.refreshEventListener();
	}
	public void refreshComplete(){
		STATE_REFRESHING=false;
	}
	private void fireRefreshProgress(float progress){
		mEventListener.refreshProgressListener(progress);
	}
	private void fireNextPageEvent(){
		STATE_REFRESHING=true;
		mEventListener.nextPageEventListener();
	}
	

}
