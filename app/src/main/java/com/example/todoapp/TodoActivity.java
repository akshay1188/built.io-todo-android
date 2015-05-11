package com.example.todoapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
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
import android.widget.Toast;

import com.raweng.built.Built;
import com.raweng.built.BuiltACL;
import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.BuiltUser;
import com.raweng.built.userInterface.BuiltListViewResultCallBack;
import com.raweng.built.userInterface.BuiltUIListViewController;
import com.raweng.built.utilities.BuiltConstant;

import java.util.ArrayList;
import java.util.HashMap;

public class TodoActivity extends Activity {

    private static final String TAG = TodoActivity.class.getName();
    ProgressDialog progressDialog;

	BuiltUIListViewController listView;
	String userUid;
	ArrayList<BuiltObject> listObjects = new ArrayList<>();
	private HashMap<String, Boolean> updatedList = new HashMap<>();
    private BuiltApplication builtApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_todo);

        try {
            builtApplication = Built.application(TodoActivity.this , "bltad1e7e5f0cabdd5a");
        } catch (Exception e) {
            e.printStackTrace();
        }

        userUid = getIntent().getExtras().getString("userUid");

		listView = new BuiltUIListViewController(TodoActivity.this, "bltad1e7e5f0cabdd5a","todo_task");
		((RelativeLayout)findViewById(R.id.taskListLayout)).addView(listView.getLayout());

		progressDialog	= new ProgressDialog(TodoActivity.this);

		fetchTaskWithBuiltQuery();

	}

	public void fetchTaskWithBuiltQuery(){

		progressDialog.show();
		progressDialog.setTitle("Todo App");
		progressDialog.setMessage("Fetching Tasks...");

		listView.getBuiltQueryInstance()/*.descending("updated_at")*/;
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
					viewholder.taskNameWithStatus = (CheckedTextView)convertView.findViewById(R.id.task);
					viewholder.deleteTask		  = (ImageButton) convertView.findViewById(R.id.deleteTask);
					viewholder.progressDialog	  = progressDialog ;
					convertView.setTag(viewholder);

				}else{
					viewholder = (TaskViewHolder) convertView.getTag();
					viewholder.taskNameWithStatus.setText("");
				}

                final String taskName = builtObject.getString("task_name");
                final String taskUid  = builtObject.getUid();
                boolean taskStatus    = builtObject.getBoolean("task_status");

				if (builtObject != null) {
					viewholder.populateFrom(new TaskModel(taskName,taskUid,taskStatus,listView,TodoActivity.this), position, listView, updatedList, builtApplication);
				}
				return convertView;
			}

			@Override
			public int getItemViewType(int position) {
				return 0;
			}

			@Override
			public int getViewTypeCount() {
				return 0;
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
						progressDialog.setTitle("Todo App");
                        progressDialog.setMessage("Creating Task...");

						final BuiltObject newTaskBuiltObject = builtApplication.classWithUid("todo_task").object();
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
						newTaskBuiltObject.saveInBackground(new BuiltResultCallBack() {

                            @Override
                            public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                                if(builtError == null){
                                    listView.insertBuiltObjectAtIndex(0, newTaskBuiltObject);
                                    updatedList.put(newTaskBuiltObject.getUid(), false);
                                   // listView.notifyDataSetChanged();
                                }else {
                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodoActivity.this);
                                    alertDialog.setTitle("Error").setMessage("ErrorMessage : " + builtError.getErrorMessage() + " ,\nResponseType : " + builtError.getResponseType()).show();
                                }

                                progressDialog.dismiss();
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

		case R.id.log_Out:



			progressDialog.show();
			progressDialog.setTitle("Todo App");progressDialog.setMessage("Logging out...");

			BuiltUser user = builtApplication.getCurrentUser();
			user.logoutInBackground(new BuiltResultCallBack() {

                @Override
                public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                    progressDialog.dismiss();

                    if (builtError == null) {
                        Toast.makeText(TodoActivity.this, "logout successfully...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(TodoActivity.this , LoginActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(TodoActivity.this);
                        alertDialog.setTitle("Error").setMessage("ErrorMessage : " + builtError.getErrorMessage() + " ,\nResponseType : " + builtError.getResponseType()).show();
                    }
                }
            });
		}


		return false;
	}

}
