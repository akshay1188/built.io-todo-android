package com.example.todoapp;

import java.util.HashMap;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageButton;

import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;

public class TaskViewHolder {

	public CheckedTextView taskNamewithStatus;
	public ImageButton deleteTask;
	public ProgressDialog progressDialog;
	public boolean isAlreadyExist = false;
	public HashMap<String, Boolean> updateValues = new HashMap<String, Boolean>();
	
	public void populateFrom(final TaskModel taskModel, final int position, HashMap<String, Boolean> updatedList) {

		updateValues = updatedList;
		
		taskNamewithStatus.setText(taskModel.getTaskName());	

		if(taskModel.getCheckStatus()){
			taskNamewithStatus.setPaintFlags(taskNamewithStatus.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
			taskNamewithStatus.setChecked(taskModel.getCheckStatus());
		}else{
			taskNamewithStatus.setPaintFlags(taskNamewithStatus.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
			taskNamewithStatus.setChecked(taskModel.getCheckStatus());
		}


		taskNamewithStatus.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				final BuiltObject updateObject = new BuiltObject("todo_task");
				updateObject.setUid(taskModel.getTaskUid());

				progressDialog.show();
				progressDialog.setTitle("Todo App");progressDialog.setMessage("Updating Task...");

				if(taskModel.getCheckStatus()){
					updateObject.set("task_status", false);
				}else{
					updateObject.set("task_status", true);
				}

				updateObject.save(new BuiltResultCallBack() {

					@Override
					public void onSuccess() {
						Log.i("success","--Updation done--");

						boolean updateValue = updateObject.getBoolean("task_status");
						updateValues.put(updateObject.getUid(), updateValue);
						taskNamewithStatus.toggle();
						
						if(taskNamewithStatus.isChecked()){
							taskNamewithStatus.setPaintFlags(taskNamewithStatus.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

						}else{
							taskNamewithStatus.setPaintFlags(taskNamewithStatus.getPaintFlags()& (~ Paint.STRIKE_THRU_TEXT_FLAG));
						}
					}

					@Override
					public void onError(BuiltError error) {
						AlertDialog.Builder alertDialog = new AlertDialog.Builder(taskModel.getContext());
						alertDialog.setTitle("Error").setMessage("ErrorMessage : "+error.getErrorMessage()+" ,\nResponseType : "+ error.getResponseType()).show();
					}

					@Override
					public void onAlways() {
						Log.i("--onAlways()--","Always get called after onSuccess() and onError");
						progressDialog.dismiss();
					}
				});

			}
		});

		deleteTask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				AlertDialog.Builder alertDialog = new AlertDialog.Builder(taskModel.getContext());
				alertDialog.setMessage("Do you want Delete task?")
				.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						progressDialog.show();
						progressDialog.setTitle("Todo App");progressDialog.setMessage("Deleting Tasks...");

						final BuiltObject object = new BuiltObject("todo_task");
						object.setUid(taskModel.getTaskUid());
						object.destroy(new BuiltResultCallBack() {

							@Override
							public void onSuccess() {
								taskModel.getList().deleteBuiltObjectAtIndex(position);
							}

							@Override
							public void onError(BuiltError error) {
								AlertDialog.Builder alertDialog = new AlertDialog.Builder(taskModel.getContext());
								alertDialog.setTitle("Error").setMessage("ErrorMessage : "+error.getErrorMessage()+" ,\nResponseType : "+ error.getResponseType()).show();
							}

							@Override
							public void onAlways() {
								Log.i("--onAlways()--","Always get called after onSuccess() and onError");
								progressDialog.dismiss();
								updateValues.remove(object.getUid());
							}
						});

					}
				})
				.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				})
				.show();
			}
		});
	}
}
