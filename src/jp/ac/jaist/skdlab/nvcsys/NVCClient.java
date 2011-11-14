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
 * @version 0.3.2
 */
public class NVCClient implements Runnable {

	public static final String VERSION = "0.3.2";
	public static final int DEFAULT_PORT = 30001;
	public static final String DEFAULT_ADDRESS = "150.65.227.109";
	
	protected static boolean waitForIntent = false;
	protected static String name = null;
	protected static String title = null;
	protected static boolean debug = false;
	protected static boolean hosted = false;
	
	private static NVCClient instance = null;
	private static int port;
	private static String address = null;
	private Socket socket = null;
	private Thread thread = null;
	private static Activity activity = null;
	private List<String> discussionList = null;
	private List<String> discussionUserList = null;
	
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
		System.out.println("Connecting to " + address + ":" + port);
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
		thread.interrupt();
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
	public void reachedMessage(final String name, final String value) {
		
		System.out.println("Reached: " + name + " " + value);
		
		// Update status string at ActionActivity
		if (activity instanceof ActionActivity) {
			((ActionActivity) activity).mHandler.post(new Runnable() {
				@Override public void run() {
					try {
						((ActionActivity) activity).setCurrentStatus(
								"[Reached] " + name + " " + value);
					} catch (Exception e) { /*Maybe catch ClassCastException*/ }
				}
			});
		}
		
		if (name.equals("GETD_R")) {
			discussionList = new ArrayList<String>();
			StringTokenizer token = new StringTokenizer(value, ",");
			while (token.hasMoreTokens()) {
				discussionList.add(token.nextToken());
			}
			
			sendMessage("CHANGE " + NVCClient.name);
			
			// Change trigger of NVCClientActivity
			if (activity instanceof NVCClientActivity && waitForIntent) {
				waitForIntent = false;
				((NVCClientActivity) activity).changeToDiscussionActivity();
			}
			// Update status string at ActionActivity
			else if (activity instanceof ActionActivity) {
				((ActionActivity) activity).mHandler.post(new Runnable() {
					@Override public void run() {
						((ActionActivity) activity).setCurrentStatus(
								"[Change] " + value);
					}
				});
			}
		}
		
		else if (name.equals("GETU_R")) {
			discussionUserList = new ArrayList<String>();
			StringTokenizer token = new StringTokenizer(value, ",");
			while (token.hasMoreTokens()) {
				discussionUserList.add(token.nextToken());
			}
		}
		
		else if (name.equals("ADDD_R")) {
			
			// Change trigger of DiscussionActivity
			if (activity instanceof DiscussionActivity) {
				sendMessage("GETU " + title);
	    		enteredDiscussion(title);
				((DiscussionActivity) activity).changeToActionAcvtivity();
			}
		}
		
		else if (name.equals("ENTER_R")) {
			
			// Change trigger of DiscussionActivity
			if (activity instanceof DiscussionActivity) {
				sendMessage("GETU " + title);
	    		enteredDiscussion(title);
				((DiscussionActivity) activity).changeToActionAcvtivity();
			}
		}
		
		else if (name.equals("ENTER")) {
			System.out.println("Entered: " + value);
			
			if (value != null && discussionUserList != null) {
				if (!discussionUserList.contains(value)) {
					discussionUserList.add(value);					
				}
			}
			
			// Update status string at ActionActivity
			if (activity instanceof ActionActivity) {
				((ActionActivity) activity).mHandler.post(new Runnable() {
					@Override public void run() {
						((ActionActivity) activity).setCurrentStatus(
								"[Entered] " + value);
						((ActionActivity) activity).setDiscussionMemberList(
								discussionUserList);
					}
				});
			}
		}
		
		else if (name.equals("LEAVE")) {
			System.out.println("Leaved: " + value);
			
			if (value != null && discussionUserList != null) {
				if (discussionUserList.contains(value)) {
					discussionUserList.remove(value);									
				}
			}

			// Update status string at ActionActivity
			if (activity instanceof ActionActivity) {
				((ActionActivity) activity).mHandler.post(new Runnable() {
					@Override public void run() {
						((ActionActivity) activity).setCurrentStatus(
								"[Leaved] " + value);
						((ActionActivity) activity).setDiscussionMemberList(
								discussionUserList);
					}
				});
			}
		}
		
		else if (name.equals("MESSAGE")) {
			System.out.println("Message: " + value);
			
			// Update status string at ActionActivity
			if (activity instanceof ActionActivity) {
				((ActionActivity) activity).setCurrentStatus(
						"[Message] " + value);
			}			// Update status string at ActionActivity
			if (activity instanceof ActionActivity) {
				((ActionActivity) activity).setCurrentStatus(
						"[Message] " + value);
			}
		}
		
		else if (name.equals("UP_ALL")) {
			if (activity instanceof ActionActivity) {
				((ActionActivity) activity).mHandler.post(new Runnable() {
					@Override public void run() {
						((ActionActivity) activity).upBrightness();
					}
				});
			}
		}
		
		else if (name.equals("DOWN_ALL")) {
			if (activity instanceof ActionActivity) {
				((ActionActivity) activity).mHandler.post(new Runnable() {
					@Override public void run() {
						((ActionActivity) activity).downBrightness();
					}
				});
			}
		}
		
		else if (name.equals("UP")) {
			if (activity instanceof ActionActivity) {
				
				((ActionActivity) activity).mHandler.post(new Runnable() {
					@Override public void run() {
						((ActionActivity) activity).downBrightness();
					}
				});
				
				// Target is me
				if (value.equals(NVCClient.name)) {
					((ActionActivity) activity).mHandler.post(new Runnable() {
						@Override public void run() {
							((ActionActivity) activity).upBrightness();
						}
					});
				}
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
				if (message == null) {
					continue;
				}
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
	
	public List<String> getDiscussionList() {
		return discussionList;
	}
	
	public List<String> getDiscussionUserList() {
		return discussionUserList;
	}
}