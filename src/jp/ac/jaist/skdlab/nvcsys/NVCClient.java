package jp.ac.jaist.skdlab.nvcsys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import android.app.Activity;

/**
 * The non-verbal communication support system - Client program
 * 
 * @author Yutaka Kato
 * @version 0.2.1
 */
public class NVCClient implements Runnable {

	public static final int DEFAULT_PORT = 300001;
	
	protected static String name = null;
	protected static String title = null;
	protected static String location = null;
	protected static float brightness;
	
	private static NVCClient instance = null;
	private static int port;
	private static String address = null;
	private Socket socket = null;
	private Thread thread = null;
	private static Activity activity = null;
	private List<String> discussionList = null;
	
	/**
	 * Singleton constructor
	 */
	private NVCClient() {
		// Do nothing
	}
	
	public static void setAddress(String address, int port) {
		NVCClient.address = address;
		NVCClient.port = port;
	}
	
	public static void setCurrentActivity(Activity activityInstance) {
		NVCClient.activity = activityInstance;
	}	
	
	public static NVCClient getInstance() {
		if (instance == null) {
			instance = new NVCClient();
		}
		return instance;
	}
	
	public boolean connectServer() {
		try {
			socket = new Socket(address, port);
			System.out.println("Connected with " + address + ":" + port);
			
			thread = new Thread(this);
			thread.start();
			
			return true;
		} catch (IOException e) {
			System.err.println("IO Error at connectServer()");
			return false;
		}
	}

	public void close() throws IOException {
		sendMessage("CLOSE");
		socket.close();
	}
	
	/**
	 * Send message to NVCServer
	 * 
	 * @param message Message string
	 */
	public void sendMessage(String message) {
		System.out.println("Sending message: " + message);
		try {
			PrintWriter writer = new PrintWriter(
					socket.getOutputStream());
			writer.println(message);
			writer.flush();
		} catch (IOException e) {
			System.err.println("IO Error at sendMessage()");
		}
	}
	
	/**
	 * Process messages from server
	 * 
	 * @param name Command name
	 * @param value Command value
	 */
	public void reachedMessage(String name, String value) {
		
		System.out.println("Reached: " + name + " " + value);
		
		if (name.equals("GETD_R")) {
			discussionList = new ArrayList<String>();
			StringTokenizer token = new StringTokenizer(value, ",");
			while (token.hasMoreTokens()) {
				discussionList.add(token.nextToken());
			}
			
			sendMessage("CHANGE " + NVCClient.name);
			
			// Change trigger of NVCClientActivity
			if (activity instanceof NVCClientActivity) {
				((NVCClientActivity) activity).changeToDiscussionActivity();				
			}
		}
		
		else if (name.equals("ADDD_R")) {
			
			// Change trigger of DiscussionActivity
			if (activity instanceof DiscussionActivity) {
	    		enteredDiscussion(title);
				((DiscussionActivity) activity).changeToActionAcvtivity();
			}
		}
		
		else if (name.equals("ENTER")) {
			System.out.println("Entered:" + value);
		}
		
		else if (name.equals("LEAVE")) {
			System.out.println("Leaved:  + value");			
		}
		
		else if (name.equals("MESSAGE")) {
			System.out.println("Message: " + value);
			
			if (activity instanceof ActionActivity) {
				((ActionActivity) activity).setCurrentStatus(value);
			}
		}
		
		else if (name.equals("OK")) {
//			System.out.println("OK reached");
		}
		
		else if (name.equals("ERROR")) {
			System.err.println("Error message: " + value);
//			NVCClientUtility.showAlertDialog("ERROR", value, activity);
		}
	}
	
	@Override
	public void run() {
		try {
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			while (!socket.isClosed()) {
				String message = reader.readLine();
				String[] messageArray = message.split(" ", 2);
				String name = messageArray[0];
				String value = messageArray.length < 2 ? "" : messageArray[1];
				reachedMessage(name, value);
			}
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Discussing state
	public void enteredDiscussion(String title) {
		NVCClient.title = title;
		System.out.println("entered discussion");
	}
	
	// Non-discussing state
	public void exitedDiscussion() {
		NVCClient.title = null;
		System.out.println("exited discussion");
	}
	
	
}
