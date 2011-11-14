package jp.ac.jaist.skdlab.nvcsys;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

/**
 * 
 * @author Yutaka Kato
 * @version 0.3.0
 */
public class NVCClientActivity extends Activity {
	
	private Button buttonConnect = null;
	private Button buttonBacklight = null;
	private EditText editTextAddress = null;
	private EditText editTextPort = null;
	private EditText editTextName = null;
	private RadioButton radioClient = null;
	private RadioButton radioDebug = null;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        buttonConnect = (Button) this.findViewById(R.id.buttonConnect);
        buttonBacklight = (Button) this.findViewById(R.id.buttonBacklight);
        
        buttonConnect.setOnClickListener(mConnectListener);
        buttonBacklight.setOnClickListener(mBacklightListener);

        editTextAddress = (EditText) this.findViewById(R.id.editTextAddress);
        editTextPort = (EditText) this.findViewById(R.id.editTextPort);
        editTextName = (EditText) this.findViewById(R.id.editTextName);
        radioClient = (RadioButton) this.findViewById(R.id.radioClient);
        radioDebug = (RadioButton) this.findViewById(R.id.radioDebug);
        
        editTextAddress.setText(NVCClientUtility.getPreference(
        		this, "address", NVCClient.DEFAULT_ADDRESS));
        editTextPort.setText(Integer.toString(NVCClientUtility.getPreference(
        		this, "port", NVCClient.DEFAULT_PORT)));
        editTextName.setText(NVCClientUtility.getPreference(
        		this, "name", ""));
        editTextName.setHint("Please input your name");
        
        if (NVCClientUtility.getPreference(this, "debugable", false)) {
        	radioClient.setChecked(false);
        	radioDebug.setChecked(true);
        } else {
        	radioClient.setChecked(true);
        	radioDebug.setChecked(false);
        }
        
		((TextView) this.findViewById(R.id.textViewCredit)).setText(
				"NVCClient for Android " + NVCClient.VERSION + "\n" +
				"2011 JAIST Shikida Lab.");
		
		NVCClientUtility.loadConfigurationFromSharedPreferences(this);
        
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        NVCClientUtility.brightness = lp.screenBrightness;
        NVCClient.setCurrentActivity(this);
    }
    
    /**
     * Called when the activity is about to start interacting with the user.
     */
    @Override
    protected void onResume() {
        super.onResume();
    }
    
    /**
     * Called when your activity's options menu needs to be created.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);


        return true;
    }

    /**
     * Called right before your activity's option menu is displayed.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);


        return true;
    }

    /**
     * Called when a menu item is selected.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }
    
    OnClickListener mConnectListener = new OnClickListener() {
    	public void onClick(View v) {
    		
//    		NVCClientUtility.startProgressDialog(
//    				"Status", "Connecting...", NVCClientActivity.this);
    		
    		CharSequence portText = editTextPort.getText();
    		String address = editTextAddress.getText().toString();
    		String name = editTextName.getText().toString();
    		int port = Integer.parseInt(portText.toString());
    		
    		// Check port field
    		if (port == 0) {
    			port = NVCClient.DEFAULT_PORT;
    		}
    		
    		// Check name field
    		if (name.length() == 0) {
    			NVCClientUtility.showAlertDialog(
    					"ERROR", "Fill \"name\" field", 
    					NVCClientActivity.this);
    			return;
    		} else {
    			NVCClient.name = name;
    		}
    		
    		// Check radio buttons
    		if (radioDebug.isChecked()) {
    			NVCClient.debug = true;
    		} else {
    			NVCClient.debug = false;
    		}

    		// Connect
    		NVCClient.setAddress(address, port);
    		NVCClient client = NVCClient.getInstance();
    		boolean result = client.connectServer();
    		if (!result) {
//    			NVCClientUtility.stopProgressDialog();
    			NVCClientUtility.showAlertDialog(
    					"ERROR", "Connect failed", NVCClientActivity.this);
    			return;
    		}
    		
    		NVCClient.waitForIntent = true;
    		client.sendMessage("GETD");
//    		NVCClientUtility.stopProgressDialog();
    		
    		// Save input parameters
    		setPreferences(address, port, name, NVCClient.debug);
    	}
    };
    
    
    OnClickListener mBacklightListener = new OnClickListener() {
    	public void onClick(View v) {
    		
    		/*
    		WindowManager.LayoutParams lp = getWindow().getAttributes();
    		
    		float prev = lp.screenBrightness;
    		
    		if (prev == NVCClientUtility.lowerBrightness) {
    			lp.screenBrightness = NVCClientUtility.upperBrightness;
    			getWindow().setAttributes(lp);
    		} else {
        		lp.screenBrightness = NVCClientUtility.lowerBrightness;
        		getWindow().setAttributes(lp);
    		}
    		*/
    		changeSettingsActivity();
    	}
    };


	public void changeToDiscussionActivity() {
		Intent intent = new Intent(
				NVCClientActivity.this, DiscussionActivity.class);
		startActivity(intent);
	}
	
	public void changeSettingsActivity() {
		Intent intent = new Intent(
				NVCClientActivity.this, SettingsActivity.class);
		startActivity(intent);
	}
	
	private void setPreferences(
			String address,
			int port, 
			String name,
			boolean debugable) {
		Editor editor = NVCClientUtility.getPreferences(this).edit();
		editor.putString("address", address);
		editor.putInt("port", port);
		editor.putString("name", name);
		editor.putBoolean("debugable", debugable);
		editor.commit();
	}
}