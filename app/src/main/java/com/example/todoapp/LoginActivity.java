package com.example.todoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import com.raweng.built.Built;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltUser;
import com.raweng.built.userInterface.BuiltUILoginController;

public class LoginActivity extends BuiltUILoginController{

	private ProgressDialog progressDialog;
	BuiltUser user;
    private BuiltApplication builtApplication;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        try {
            builtApplication = Built.application(LoginActivity.this , "bltad1e7e5f0cabdd5a");
            user = builtApplication.getCurrentUser();
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        //set application api key for login
        setApplicationKey("bltad1e7e5f0cabdd5a");
		
	}
	@Override
	public void loginSuccess(BuiltUser user) {
		
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
