package com.netsoft.netsms;
//test method sync mms
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class LoadMMSAsynsTask extends AsyncTask<String, Integer,ArrayList<SmsItem>>{

	Activity activity;
	ListSMSActivity listSMSActivity = new ListSMSActivity();
	public LoadMMSAsynsTask(Activity act){
		this.activity = act;
	}
	@Override
	protected ArrayList<SmsItem> doInBackground(String... arg0) {
		// TODO Auto-generated method stub
		String add = arg0[0];
		return SmsFetcher.getMMS(this.activity, add);
	}
	
	@Override
	protected void onPostExecute(ArrayList<SmsItem> result){
		super.onPostExecute(result);
		listSMSActivity.setStackRunning();
		Log.e("", "Setted stackrunning is false");
		listSMSActivity.notifiyActivityTaskCompleted(result);	
		Toast.makeText(activity, "kasfkhasfkljaklsfjklasf", Toast.LENGTH_LONG).show();
	}

}
