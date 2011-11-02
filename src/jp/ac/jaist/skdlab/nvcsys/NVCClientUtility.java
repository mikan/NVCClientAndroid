package jp.ac.jaist.skdlab.nvcsys;

import android.app.AlertDialog;
import android.content.Context;

public class NVCClientUtility {
	
	protected static float brightness;

	public static void showAlertDialog(
			String title, String message, Context context) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
	}	
}
