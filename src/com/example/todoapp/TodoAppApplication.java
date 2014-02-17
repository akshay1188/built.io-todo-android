package com.example.todoapp;


import android.app.Application;

import com.raweng.built.Built;

public class TodoAppApplication extends Application{

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			Built.initializeWithApiKey(getApplicationContext(), "bltad1e7e5f0cabdd5a" , "todoapp");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
