package jp.ac.jaist.skdlab.nvcsys;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class DiscussionActivity extends Activity {
	
	private Button buttonBegin = null;
	private EditText editTextTitle = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set layout XML
		setContentView(R.layout.discussion);
		
		// Components
		buttonBegin = (Button) this.findViewById(R.id.buttonBegin);
		editTextTitle = (EditText) this.findViewById(R.id.editTextTitle);
		
		// Button actions
		buttonBegin.setOnClickListener(mBeginListener);
		
		//
		editTextTitle.setHint(NVCClient.name + "'s discussion");
		
		NVCClient.setCurrentActivity(this);
	}
	
    OnClickListener mBeginListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		String title = editTextTitle.getText().toString();
    		if (title == null) {
    			title = NVCClient.name + "'s discussion";
    		}
    		
    		NVCClient.title = title;
    		NVCClient client = NVCClient.getInstance();
    		client.sendMessage("ADDD " + title);
    	}
    };
    
    OnClickListener mCancelListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		NVCClient client = NVCClient.getInstance();
    		try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    };
    
    public void changeToActionAcvtivity() {
		Intent intent = new Intent(
				DiscussionActivity.this, ActionActivity.class);
		startActivity(intent);
    }
}
