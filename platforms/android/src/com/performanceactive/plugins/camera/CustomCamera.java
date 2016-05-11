package com.performanceactive.plugins.camera;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.os.Environment;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONException;

import static com.performanceactive.plugins.camera.CustomCameraActivity.ERROR_MESSAGE;
import static com.performanceactive.plugins.camera.CustomCameraActivity.FILENAME;
import static com.performanceactive.plugins.camera.CustomCameraActivity.IMAGE_URI;
import static com.performanceactive.plugins.camera.CustomCameraActivity.QUALITY;
import static com.performanceactive.plugins.camera.CustomCameraActivity.RESULT_ERROR;
import static com.performanceactive.plugins.camera.CustomCameraActivity.TARGET_HEIGHT;
import static com.performanceactive.plugins.camera.CustomCameraActivity.TARGET_WIDTH;
import static com.performanceactive.plugins.camera.CustomCameraActivity.TOP_MESSAGE;
import static com.performanceactive.plugins.camera.CustomCameraActivity.TMP_PATH;


public class CustomCamera extends CordovaPlugin {

    private CallbackContext callbackContext;

	@Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
	    if (!hasRearFacingCamera()) {
	        callbackContext.error("No rear camera detected");
	        return false;
	    }
	    this.callbackContext = callbackContext;
	    Context context = cordova.getActivity().getApplicationContext();
	    Intent intent = new Intent(context, CustomCameraActivity.class);
	    intent.putExtra(FILENAME, args.getString(0));
	    intent.putExtra(QUALITY, args.getInt(1));
	    intent.putExtra(TARGET_WIDTH, args.getInt(2));
	    intent.putExtra(TARGET_HEIGHT, args.getInt(3));
		intent.putExtra(TOP_MESSAGE, args.getString(4));
		intent.putExtra(TMP_PATH, getTempDirectoryPath());
	    cordova.startActivityForResult(this, intent, 0);
        return true;
    }

	private String getTempDirectoryPath() {
        File cache = null;

        // SD Card Mounted
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cache = cordova.getActivity().getExternalCacheDir();
        }
        // Use internal storage
        else {
            cache = cordova.getActivity().getCacheDir();
        }

        // Create the cache directory if it doesn't exist
        cache.mkdirs();
        return cache.getAbsolutePath();
    }

	private boolean hasRearFacingCamera() {
	    Context context = cordova.getActivity().getApplicationContext();
	    return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
	}

	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
	    if (resultCode == Activity.RESULT_OK) {
	        callbackContext.success(intent.getExtras().getString(IMAGE_URI));
	    } else if (resultCode == RESULT_ERROR) {
	        String errorMessage = intent.getExtras().getString(ERROR_MESSAGE);
	        if (errorMessage != null) {
	            callbackContext.error(errorMessage);
	        } else {
	            callbackContext.error("Failed to take picture");
	        }
	    }
    }

}
