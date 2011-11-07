package jp.ac.jaist.skdlab.nvcsys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

public class NVCClientUtility {
	
	public static final float BRIGHTNESS_HIGHEST = 1.0f;
	public static final float BRIGHTNESS_LOWEST = 0.05f;
	protected static float brightness;
	private static ProgressDialog progressDialog = null;

	public static void showAlertDialog(
			String title, String message, Context context) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context);
		dialog.setTitle(title);
		dialog.setMessage(message);
		dialog.show();
	}
	
	public static void startProgressDialog(
		String title, String message, Context context) {

		progressDialog = new ProgressDialog(context);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.show();
	}
	
	public static void stopProgressDialog() {
		progressDialog.dismiss();
	}
}