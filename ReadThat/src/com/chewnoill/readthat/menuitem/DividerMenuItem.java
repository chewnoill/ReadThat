package com.chewnoill.readthat.menuitem;

import android.content.Context;
import android.view.View;

public class DividerMenuItem extends MenuItem {

	public DividerMenuItem(Context context) {
		super(context);
	}

	@Override
	public View getView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSelected() {
		//do nothing, cannot select a divider

	}

}
