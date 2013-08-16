package com.chewnoill.readthat.menuitem;

import android.content.Context;
import android.view.View;

abstract public class MenuItem {
	protected Context context;
	public MenuItem(Context context){
		this.context = context;
	}
	public abstract View getView();
	public abstract void onSelected();
}
