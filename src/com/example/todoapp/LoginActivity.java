package com.example.todoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltUser;
import com.raweng.built.userInterface.BuiltUILoginController;

public class LoginActivity extends BuiltUILoginController{

	private ProgressDialog progressDialog;
	BuiltUser user;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		user = BuiltUser.getCurrentUser();
		
		if(user != null){
			Intent startTodoIntent = new Intent(LoginActivity.this, TodoActivity.class);
			startTodoIntent.putExtra("userUid", user.getUserUid());
			startActivity(startTodoIntent);
			finish();
		}
		
		progressDialog = new ProgressDialog(LoginActivity.this);
		progressDialog.setMessage("Loging In...");
		progressDialog.setTitle("Please Wait...");
		progressDialog.setCancelable(false);
		progressDialog.setCanceledOnTouchOutside(false);

		//set progress dialog.
		setProgressDialog(progressDialog);
		
	}
	@Override
	public void loginSuccess(BuiltUser user) {

		try {
			//Save the session of logged in user.
			user.saveSession();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Intent startTodoIntent = new Intent(LoginActivity.this, TodoActivity.class);
		startTodoIntent.putExtra("userUid", user.getUserUid());
		startActivity(startTodoIntent);
		finish();
		
	}

	@Override
	public void loginError(BuiltError error) {
		
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
		alertDialog.setTitle("Error").setMessage("ErrorMessage : "+error.getErrorMessage()+" ,\nResponseType : "+ error.getResponseType()).show();
	}

}
