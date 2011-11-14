package jp.ac.jaist.skdlab.nvcsys;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class NVCClientUtility {
	
	private static final float DEFAULT_UPPER = 1.0f;
	private static final float DEFAULT_LOWER = 0.05f;
	private static final String PREF_NAME = "nvcclient";
	protected static float brightness;
	protected static float upperBrightness = DEFAULT_UPPER;
	protected static float lowerBrightness = DEFAULT_LOWER;
	private static ProgressDialog progressDialog = null;
	
	public static void loadConfigurationFromSharedPreferences(Context context) {
		upperBrightness = getPreference(
				context, "upper_brightness", DEFAULT_UPPER);
		lowerBrightness = getPreference(
				context, "lower_brightness", DEFAULT_LOWER);
	}
	
	public static void setPreference(Context context, String key, Object value){
		SharedPreferences pref = getPreferences(context);
		Editor editor = pref.edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof Float) {
			editor.putFloat(key, (Float) value);
		} else if (value instanceof Integer) {
			editor.putInt(key, (Integer) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		} else {
			editor.putString(key, value.toString());
		}
		editor.commit();
	}
	
	public static boolean getPreference(
			Context context, String key, boolean defaultValue) {
		SharedPreferences pref = getPreferences(context);
		return pref.getBoolean(key, defaultValue);
	}
	
	public static float getPreference(
			Context context, String key, float defaultValue) {
		SharedPreferences pref = getPreferences(context);
		return pref.getFloat(key, defaultValue);
	}
	
	public static int getPreference(
			Context context, String key, int defaultValue) {
		SharedPreferences pref = getPreferences(context);
		return pref.getInt(key, defaultValue);
	}
	
	public static long getPreference(
			Context context, String key, long defaultValue) {
		SharedPreferences pref = getPreferences(context);
		return pref.getLong(key, defaultValue);
	}
	
	public static String getPreference(
			Context context, String key, String defaultValue) {
		SharedPreferences pref = getPreferences(context);
		return pref.getString(key, defaultValue);
	}

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
	
	public static SharedPreferences getPreferences(Context context) {
		return new ContextWrapper(context).getSharedPreferences(
				PREF_NAME, Context.MODE_PRIVATE);
	}
}