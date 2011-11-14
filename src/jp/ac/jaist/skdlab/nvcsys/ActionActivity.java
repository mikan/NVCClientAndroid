package jp.ac.jaist.skdlab.nvcsys;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

public class ActionActivity extends Activity {

	private NVCClient client = null;
	private TextView textViewTitle = null;
	private TextView textViewStatus = null;
	private TextView textViewRemoteOptions = null;
	private Button buttonQuit = null;
	private Button buttonUpAll = null;
	private Button buttonDownAll = null;
	private Button buttonUp = null;
	private Spinner spinnerUsers = null;
	private ArrayAdapter<String> adapter = null;
	public Handler mHandler = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set layout XML
		setContentView(R.layout.action);
		
		mHandler = new Handler();
		
		// Components
		textViewTitle = (TextView) this.findViewById(R.id.textViewTitle);
		textViewStatus = (TextView) this.findViewById(R.id.textViewStatus);
		textViewRemoteOptions = 
				(TextView) this.findViewById(R.id.textViewRemoteOptions);
		buttonQuit = (Button) this.findViewById(R.id.buttonQuit);
		buttonUpAll = (Button) this.findViewById(R.id.buttonUpAll);
		buttonDownAll = (Button) this.findViewById(R.id.buttonDownAll);
		buttonUp = (Button) this.findViewById(R.id.buttonUp);
		
		
		// Button actions
		buttonQuit.setOnClickListener(mQuitListener);
		buttonUpAll.setOnClickListener(mUpAllListener);
		buttonDownAll.setOnClickListener(mDownAllListener);
		buttonUp.setOnClickListener(mUpListener);
		
		//
		textViewTitle.setText("Title: " + NVCClient.title);
		
		if (!NVCClient.debug) {
			textViewRemoteOptions.setVisibility(View.GONE);
			buttonUpAll.setVisibility(View.GONE);
			buttonDownAll.setVisibility(View.GONE);
			buttonUp.setVisibility(View.GONE);
		}
		
		// Client
		client = NVCClient.getInstance();
				
		// Spinner
		spinnerUsers = (Spinner) this.findViewById(
				R.id.spinnerUsers);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		List<String> discussionList = client.getDiscussionUserList();
		for (String s : discussionList) {
			adapter.add(s);
		}
		spinnerUsers.setAdapter(adapter);
		
		NVCClient.setCurrentActivity(this);
		
		// Keep screen on
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
	
	public void changeBrightness(float value) {
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = 1.0f;
		getWindow().setAttributes(lp);
	}
	
	OnClickListener mUpAllListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		NVCClient client = NVCClient.getInstance();
    		client.sendMessage("UP_ALL");
    	}
    };
    
    OnClickListener mDownAllListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		NVCClient client = NVCClient.getInstance();
    		client.sendMessage("DOWN_ALL");
    	}
    };
    
    OnClickListener mUpListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		NVCClient client = NVCClient.getInstance();
//    		client.sendMessage("DOWN_ALL");
    		client.sendMessage("UP " + (String) spinnerUsers.getSelectedItem());
    	}
    };
	
	OnClickListener mQuitListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		// Turn off "Keep screen on" flag
    		ActionActivity.this.getWindow().clearFlags(
    				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    		
    		NVCClient client = NVCClient.getInstance();
    		client.sendMessage("EXIT " + NVCClient.title);    			
    		
    		// Previous window: EchoClientAndroidctivity
    		Intent intent = new Intent(
    				ActionActivity.this, NVCClientActivity.class);
    		startActivity(intent);
    	}
    };
    
	public void upBrightness() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = NVCClientUtility.upperBrightness;
		getWindow().setAttributes(lp);
	}

	public void downBrightness() {
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = NVCClientUtility.lowerBrightness;
		getWindow().setAttributes(lp);
	}
    
    public void setCurrentStatus(String status) {
    	
    	if (status == null) {
    		status = "";
    	}
    	try {
    		textViewStatus.setText("Status: " + status);
    	} catch (Exception e) {
    		// through exceptions
    	}
    }
    
    public void setDiscussionMemberList(List<String> userList) {
    	adapter.clear();
    	for (String s : userList) {
			adapter.add(s);
		}
    	spinnerUsers.setAdapter(adapter);
    }
}
