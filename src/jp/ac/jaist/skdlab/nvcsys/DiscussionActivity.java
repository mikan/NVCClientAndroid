package jp.ac.jaist.skdlab.nvcsys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class DiscussionActivity extends Activity {
	
	private NVCClient client = null;
	private Button buttonBegin = null;
	private Button buttonJoin = null;
	private Button buttonEntryCancel = null;
	private EditText editTextTitle = null;
	private Spinner spinnerDiscussions = null;
	private ArrayAdapter<String> adapter = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Set layout XML
		setContentView(R.layout.discussion);
		
		// Components
		buttonBegin = (Button) this.findViewById(R.id.buttonBegin);
		buttonJoin = (Button) this.findViewById(R.id.buttonJoin);
		buttonEntryCancel = (Button) this.findViewById(R.id.buttonEntryCancel);
		editTextTitle = (EditText) this.findViewById(R.id.editTextTitle);
		
		// Button actions
		buttonBegin.setOnClickListener(mBeginListener);
		buttonJoin.setOnClickListener(mJoinListener);
		buttonEntryCancel.setOnClickListener(mEntryCancelListener);
		
		//
		editTextTitle.setHint(NVCClient.name + "Room");
		
		// Client
		client = NVCClient.getInstance();
		
		// Spinner
		spinnerDiscussions = (Spinner) this.findViewById(
				R.id.spinnerDiscussions);
		adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(
				android.R.layout.simple_spinner_dropdown_item);
		List<String> discussionList = client.getDiscussionList();
		if (discussionList == null) {
			discussionList = new ArrayList<String>();
		}
		for (String s : discussionList) {
			adapter.add(s);
		}
		spinnerDiscussions.setAdapter(adapter);
		
		if (!NVCClient.debug) {
			this.findViewById(R.id.textViewOR).setVisibility(View.GONE);
			this.findViewById(
					R.id.textViewBeginNewDiscussion).setVisibility(
							View.GONE);
			this.findViewById(
					R.id.textViewBeginNewDiscussionTitle).setVisibility(
							View.GONE);
			editTextTitle.setVisibility(View.GONE);
			buttonBegin.setVisibility(View.GONE);
		}
		
		NVCClient.setCurrentActivity(this);
	}
	
    OnClickListener mBeginListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		String title = editTextTitle.getText().toString();
    		if (title == null) {
    			title = new String();
    		}
    		if (title.length() == 0) {
    			title = NVCClient.name + "Room";
    		}
    		
    		NVCClient.title = title;
    		NVCClient.hosted = true;
    		NVCClient client = NVCClient.getInstance();
    		client.sendMessage("ADDD " + NVCClient.title);
    	}
    };
    
    OnClickListener mJoinListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		String title = (String) spinnerDiscussions.getSelectedItem();
    		if (title == null) {
    			return;
    		}
    		
    		NVCClient.title = title;
    		NVCClient.hosted = false;
    		NVCClient client = NVCClient.getInstance();
    		client.sendMessage("ENTER " + title);
    	}
    };
    
    OnClickListener mEntryCancelListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		NVCClient client = NVCClient.getInstance();
    		try {
				client.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
    		
			Intent intent = new Intent(
					DiscussionActivity.this, NVCClientActivity.class);
			startActivity(intent);
    	}
    };
    
    public void changeToActionAcvtivity() {
		Intent intent = new Intent(
				DiscussionActivity.this, ActionActivity.class);
		startActivity(intent);
    }
}
