package com.example.todoapp;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;
import com.raweng.built.userInterface.BuiltListViewResultCallBack;
import com.raweng.built.userInterface.BuiltUIListViewController;

public class TodoActivity extends Activity {

	ProgressDialog progressDialog;

	BuiltUIListViewController listView;
	String userUid;

	ArrayList<BuiltObject> listObjects = new ArrayList<BuiltObject>();
	private HashMap<String, Boolean> updatedList = new HashMap<String, Boolean>();


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);

		userUid = getIntent().getExtras().getString("userUid");

		listView = new BuiltUIListViewController(TodoActivity.this, "todo_task");
		((RelativeLayout)findViewById(R.id.taskListLayout)).addView(listView.getLayout());

		progressDialog	= new ProgressDialog(TodoActivity.this);

		fetchTaskWithBuiltQuery();
		
	}

	public void fetchTaskWithBuiltQuery(){

		progressDialog.show();
		progressDialog.setTitle("Todo App");
		progressDialog.setMessage("Fetching Tasks...");

		listView.builtQueryInstance.descending("updated_at");
		listView.loadData(new BuiltListViewResultCallBack() {

			@Override
			public void onError(BuiltError error) {
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodoActivity.this);
				alertDialog.setTitle("Error").setMessage("ErrorMessage : "+error.getErrorMessage()+" ,\nResponseType : "+ error.getResponseType()).show();
			}

			@Override
			public void onAlways() {
				Log.i("--onAlways()--","Always get called after onSuccess() and onError");
				progressDialog.dismiss();
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent, BuiltObject builtObject) {

				TaskViewHolder viewholder = null;

				if(convertView == null){
					LayoutInflater inflater = LayoutInflater.from(TodoActivity.this);
					convertView             = inflater.inflate(R.layout.todo_task, parent, false);

					viewholder          	= new TaskViewHolder();
					viewholder.taskNamewithStatus = (CheckedTextView)convertView.findViewById(R.id.task);
					viewholder.deleteTask		  = (ImageButton) convertView.findViewById(R.id.deleteTask);
					viewholder.progressDialog	  = progressDialog ;
					convertView.setTag(viewholder);

				}else{
					viewholder = (TaskViewHolder) convertView.getTag();
					viewholder.taskNamewithStatus.setPaintFlags(viewholder.taskNamewithStatus.getPaintFlags()& (~ Paint.STRIKE_THRU_TEXT_FLAG));
					viewholder.taskNamewithStatus.setText("");
				}

				final String taskName = builtObject.getString("task_name");
				final String taskUid  = builtObject.getUid();
				boolean taskStatus = builtObject.getBoolean("task_status");
				
				if(viewholder.updateValues != null){
					for (String iterable_element : viewholder.updateValues.keySet()) {
						if(viewholder.updateValues.containsKey(taskUid)){
							taskStatus = viewholder.updateValues.get(iterable_element);
						}
					}
				}

				if (builtObject != null) {
					viewholder.populateFrom(new TaskModel(taskName,taskUid,taskStatus,listView,TodoActivity.this),position,updatedList);
				}
				listView.notifyDataSetChanged();
				return convertView;
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.todo, menu);
		return true;
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch(item.getItemId()){
		
		case R.id.addTaskMenu:
			
			final EditText addTextEditText = new EditText(TodoActivity.this);
			addTextEditText.setSingleLine();
			addTextEditText.setHint("Buy Milk...");
			
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodoActivity.this);
			alertDialog.setView(addTextEditText);
			alertDialog.setTitle("Add Task");
			alertDialog.setPositiveButton("Add Task", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					final String newTask = addTextEditText.getText().toString();
					addTextEditText.setText("");

					if(!TextUtils.isEmpty(newTask)){

						progressDialog.show();
						progressDialog.setTitle("Todo App");progressDialog.setMessage("Creating Task...");

						final BuiltObject newTaskBuiltObject = new BuiltObject("todo_task");
						newTaskBuiltObject.set("task_name", newTask);
						newTaskBuiltObject.set("task_status", false);

						BuiltACL acl = new BuiltACL();

						acl.setUserDeleteAccess(userUid, true);
						acl.setUserReadAccess(userUid, true);
						acl.setUserWriteAccess(userUid, true);

						acl.setPublicDeleteAccess(false);
						acl.setPublicReadAccess(false);
						acl.setPublicWriteAccess(false);

						newTaskBuiltObject.setACL(acl);
						newTaskBuiltObject.save(new BuiltResultCallBack() {

							@Override
							public void onSuccess() {
								listView.insertBuiltObjectAtIndex(0,newTaskBuiltObject);
								listView.notifyDataSetChanged();
								
							}

							@Override
							public void onError(BuiltError error) {
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodoActivity.this);
								alertDialog.setTitle("Error").setMessage("ErrorMessage : "+error.getErrorMessage()+" ,\nResponseType : "+ error.getResponseType()).show();
							}

							@Override
							public void onAlways() {
								Log.i("--onAlways()--","Always get called after onSuccess() and onError");
								progressDialog.dismiss();
								updatedList.put(newTaskBuiltObject.getUid(), false);
							}
						});

					}else{
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodoActivity.this);
						alertDialog.setTitle("Todo Task").setMessage("Please Provide Task").show();
					}
					
				}
			});
			alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					
				}
			});
			alertDialog.show();
			
			break;
			
		case R.id.sign_Out:

			progressDialog.show();
			progressDialog.setTitle("Todo App");progressDialog.setMessage("Loging out...");

			BuiltUser user = new BuiltUser();
			user.logout(new BuiltResultCallBack() {

				@Override
				public void onSuccess() {
					finish();				
				}

				@Override
				public void onError(BuiltError error) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodoActivity.this);
					alertDialog.setTitle("Error").setMessage("ErrorMessage : "+error.getErrorMessage()+" ,\nResponseType : "+ error.getResponseType()).show();

				}

				@Override
				public void onAlways() {
					Log.i("--onAlways()--","Always get called after onSuccess() and onError");
					progressDialog.dismiss();
				}
			});
		}


		return false;
	}

}
