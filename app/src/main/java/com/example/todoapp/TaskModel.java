package com.example.todoapp;

import android.content.Context;

import com.raweng.built.BuiltObject;
import com.raweng.built.userInterface.BuiltUIListViewController;

public class TaskModel {
	
	private String taskName;
	private String taskUid;

	private boolean taskStatus;

	BuiltObject builtObject;
	private BuiltUIListViewController list;
	private Context context;
	
	public TaskModel(String taskName, String taskUid, boolean taskStatus, BuiltUIListViewController listView, TodoActivity todoActivity) {
		this.taskName = taskName;
		this.taskStatus = taskStatus;
		this.taskUid = taskUid;
		list = listView;
		context = todoActivity;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}



	public void setCheckedStatus(boolean status) {
		taskStatus = status;
	}

	public boolean getCheckStatus() {
		return taskStatus;
	}


	
	public BuiltUIListViewController getList() {
		return list;
	}

	public void setList(BuiltUIListViewController list) {
		this.list = list;
	}



	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}


	public String getTaskUid() {
		return taskUid;
	}

	public void setTaskUid(String taskUid) {
		this.taskUid = taskUid;
	}

}
