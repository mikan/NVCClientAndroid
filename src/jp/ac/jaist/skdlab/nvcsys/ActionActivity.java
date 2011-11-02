package jp.ac.jaist.skdlab.nvcsys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActionActivity extends Activity {

	private TextView textViewTitle = null;
	private TextView textViewStatus = null;
	private TextView textViewRemoteOptions = null;
	private Button buttonQuit = null;
	private Button buttonUpAll = null;
	private Button buttonDownAll = null;
	private Button buttonUp = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set layout XML
		setContentView(R.layout.action);
		
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
		
		//
		textViewTitle.setText("Title: " + NVCClient.title);
		
		if (NVCClient.location.equals("local")) {
			textViewRemoteOptions.setVisibility(View.GONE);
			buttonUpAll.setVisibility(View.GONE);
			buttonDownAll.setVisibility(View.GONE);
			buttonUp.setVisibility(View.GONE);
		}
		
		NVCClient.setCurrentActivity(this);
	}
	
	public void changeBrightness(float value) {
		
		WindowManager.LayoutParams lp = getWindow().getAttributes();
		lp.screenBrightness = 1.0f;
		getWindow().setAttributes(lp);
	}
	
	OnClickListener mUpAllListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		NVCClient client = NVCClient.getInstance();
    		client.sendMessage("DOWN");
    	}
    };
	
	OnClickListener mQuitListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		NVCClient client = NVCClient.getInstance();

    		client.sendMessage("EXIT " + NVCClient.title);
    		
    		// Previous window: EchoClientAndroidctivity
    		Intent intent = new Intent(
    				ActionActivity.this, NVCClientActivity.class);
    		startActivity(intent);
    	}
    };
    
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
}
