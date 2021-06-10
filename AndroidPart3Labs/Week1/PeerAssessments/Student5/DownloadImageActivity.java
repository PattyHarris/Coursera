package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
    
    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ DONE -- you fill in here.
    	super.onCreate(savedInstanceState);

    	final Context appContext = getApplicationContext();
    	
        // Get the URL associated with the Intent data.
        // @@ DONE -- you fill in here.
    	final Uri uri = getIntent().getData();

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        // @@ DONE -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.
    	new Thread(new Runnable(){
			public void run() {
				Uri result = DownloadUtils.downloadImage(appContext, uri);
				setResultOnUiThread(result);
			}
		}).start();

    }
    
    
    private void setResultOnUiThread(final Uri result) {
		DownloadImageActivity.this.runOnUiThread(new Runnable() {
			public void run() {
				
		    	Intent intent = new Intent();
		    	intent.setFlags( Intent.FLAG_GRANT_READ_URI_PERMISSION);

		    	if ( sanityCheck(result) ) {
		        	setResult(Activity.RESULT_CANCELED);    	
		    	}
		    	else {
		        	intent.setData(result);
		        	setResult(Activity.RESULT_OK, intent);    	
		    	}
		        finish();
			}
		});
    }
    
    private boolean sanityCheck(final Uri uri) {
    	return uri == null || uri.toString() == "";
    }
}

