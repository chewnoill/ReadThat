package com.chewnoill.readthat.menuitem;

import java.util.ArrayList;
import java.util.HashMap;

public class DefaultMenu {
	public static HashMap<String,MenuItem> MENU = new HashMap<String,MenuItem>();
	public static ArrayList<MenuItem> MENU_LIST = new ArrayList<MenuItem>(); 
	public static void add(String name, MenuItem menuItem){
		if(MENU.get(name)==null){
			MENU.put(name,menuItem);
			MENU_LIST.add(menuItem);
		}
	}
	
}
