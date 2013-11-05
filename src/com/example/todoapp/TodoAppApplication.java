package com.example.todoapp;


import com.raweng.built.Built;

import android.app.Application;

public class TodoAppApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			Built.initializeWithApiKey(getApplicationContext(), "api_key" , "app_uid");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
