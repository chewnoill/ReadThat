package com.chewnoill.readthat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.chewnoill.shared.Global;

public class RedditInterface extends AsyncTask<JSONObject, Integer, JSONObject> {
	public static long LAST_REQUEST;
	static {
		LAST_REQUEST = (new Date()).getTime();
	}
	private static final String REDDIT = "http://www.reddit.com";
	private static final String TAG = "RedditInterface";
	private String link;
	private String type;
	private Callback callback;
	
	public RedditInterface(String link,String type,Callback callback){
		this.callback = callback;
		//TODO make sure link is valid
		this.link = REDDIT + link;
		this.type = type;
	}
	private JSONObject link_post(JSONObject args) throws ClientProtocolException, IOException{
		
		StringEntity se = new StringEntity(args.toString());
		DefaultHttpClient httpclient = new DefaultHttpClient();
		HttpPost httpost = new HttpPost(this.link);
		httpost.setEntity(se);
		httpost.setHeader("Content-type", "application/json");
		httpost.setHeader("User-Agent",Global.USER_AGENT);
		HttpResponse response = httpclient.execute(httpost);
		
		String file = "";
		String line = "";
		
		BufferedReader in = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		while((line=in.readLine())!=null) {
			file += line;				
		}
		Log.d(TAG,file);
		response.getEntity().consumeContent();
		JSONObject ret;
		try{
			ret = new JSONObject(file);
		} catch (JSONException e) {
			e.printStackTrace();
			HashMap<String,String> t = new HashMap<String,String>();
			t.put("error","JSONException");
			t.put("file",file);
			ret = new JSONObject(t);
		}
		return ret;
		

	}
	
	private JSONObject link_get(JSONObject args) throws ClientProtocolException, IOException{
		
		DefaultHttpClient httpclient = new DefaultHttpClient();
		String arg_string = "";
		JSONArray arg_list = args.names();
		if(arg_list!=null && 
				arg_list.length()>0){
			arg_string ="?";
			for(int x = 0; x<arg_list.length(); x++){
				arg_string += arg_list.optString(x) + "=" + args.optString(arg_list.optString(x));
				if(x+1<arg_list.length()){
					arg_string+="&";
				}
			}
		}
		Log.d(TAG,"getting: "+this.link+arg_string);
		HttpGet httpget = new HttpGet(this.link+arg_string);
		httpget.setHeader("Content-type", "application/json");
		httpget.setHeader("User-Agent",Global.USER_AGENT);
		HttpResponse response = httpclient.execute(httpget);
		
		String file = "";
		String line = "";
		
		BufferedReader in = new BufferedReader(
				new InputStreamReader(response.getEntity().getContent()));
		while((line=in.readLine())!=null) {
			file += line;				
		}
		Log.d(TAG,file);
		response.getEntity().consumeContent();
		JSONObject ret;
		try{
			ret = new JSONObject(file);
		} catch (JSONException e) {
			e.printStackTrace();
			HashMap<String,String> t = new HashMap<String,String>();
			t.put("error","JSONException");
			t.put("file",file);
			ret = new JSONObject(t);
		}
		return ret;
	}
	@Override
	protected JSONObject doInBackground(JSONObject... arg0) {
		JSONObject ret = new JSONObject();
		try {
			long t;
			while((t=(new Date()).getTime()-LAST_REQUEST)<=5000){
				try {
					t=5000;
					//try not to be a jerk
					Log.d(TAG,"sleeping "+t+" milliseconds");
					Log.d(TAG,"link: "+link);
					Log.d(TAG,"args: "+arg0[0].toString());
				    Thread.sleep(t);
				    
				} catch(InterruptedException ex) {
				    Thread.currentThread().interrupt();
				}
			}
			LAST_REQUEST=(new Date()).getTime();
			if(type.equals("POST")){
				ret = link_post(arg0[0]);
			}else if(type.equals("GET")){
				ret = link_get(arg0[0]);
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return ret;
	}
	@Override
	protected void onPostExecute(JSONObject result){
		try {
			callback.onPostExecute(result);
		} catch (SubRedditNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static abstract class Callback{
		public abstract void onPostExecute(JSONObject result) throws SubRedditNotFoundException;
	}

}
