package com.example.todoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageButton;

import com.raweng.built.BuiltApplication;
import com.raweng.built.BuiltError;
import com.raweng.built.BuiltObject;
import com.raweng.built.BuiltResultCallBack;
import com.raweng.built.userInterface.BuiltUIListViewController;
import com.raweng.built.utilities.BuiltConstant;

import java.util.HashMap;

public class TaskViewHolder {

    private static final String TAG = TaskViewHolder.class.getName();
    public CheckedTextView taskNameWithStatus;
    public ImageButton deleteTask;
    public ProgressDialog progressDialog;

    public void populateFrom(final TaskModel taskModel, final int position, final BuiltUIListViewController listView, final HashMap<String, Boolean> updateValues, final BuiltApplication builtApplication) {

        taskNameWithStatus.setText(taskModel.getTaskName());

        if(taskModel.getCheckStatus()){
            taskNameWithStatus.setPaintFlags(taskNameWithStatus.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            taskNameWithStatus.setChecked(taskModel.getCheckStatus());
        }else{
            taskNameWithStatus.setPaintFlags(taskNameWithStatus.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
            taskNameWithStatus.setChecked(taskModel.getCheckStatus());
        }


        taskNameWithStatus.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final BuiltObject updateObject = builtApplication.classWithUid("todo_task").object(taskModel.getTaskUid());

                progressDialog.show();
                progressDialog.setTitle("Todo App");
                progressDialog.setMessage("Updating Task...");

                if (taskModel.getCheckStatus()) {
                    updateObject.set("task_status", false);
                } else {
                    updateObject.set("task_status", true);
                }

                updateObject.saveInBackground(new BuiltResultCallBack() {

                    @Override
                    public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                        progressDialog.dismiss();
                        if (builtError == null) {

                            boolean updateValue = updateObject.getBoolean("task_status");
                            //taskModel.setCheckedStatus(updateValue);
                            Log.d(TAG, "--updated object" + updateObject.getUid() + "--isContaineKey--" + updateValues.containsKey(updateObject.getUid()) + "--name--" + taskModel.getTaskName());
                            updateValues.put(updateObject.getUid(), updateValue);

                            taskNameWithStatus.toggle();

                            if (taskNameWithStatus.isChecked()) {
                                taskNameWithStatus.setPaintFlags(taskNameWithStatus.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                            } else {
                                taskNameWithStatus.setPaintFlags(taskNameWithStatus.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                            }

                            listView.replaceBuiltObjectAtIndex(position, updateObject);

                            taskModel.getList().notifyDataSetChanged();
                        } else {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(taskModel.getContext());
                            alertDialog.setTitle("Error").setMessage("ErrorMessage : " + builtError.getErrorMessage() + " ,\nResponseType : " + builtError.getResponseType()).show();
                        }

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

                                final BuiltObject object = builtApplication.classWithUid("todo_task").object(taskModel.getTaskUid());
                                object.destroyInBackground(new BuiltResultCallBack() {

                                    @Override
                                    public void onCompletion(BuiltConstant.ResponseType responseType, BuiltError builtError) {

                                        progressDialog.dismiss();

                                        if (builtError == null){
                                            taskModel.getList().deleteBuiltObjectAtIndex(position);
                                            updateValues.remove(object.getUid());
                                            taskModel.getList().notifyDataSetChanged();
                                            Log.d(TAG , "-updateValues-size---"+updateValues.size());
                                        }else {
                                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(taskModel.getContext());
                                            alertDialog.setTitle("Error").setMessage("ErrorMessage : " + builtError.getErrorMessage() + " ,\nResponseType : " + builtError.getResponseType()).show();
                                        }


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
