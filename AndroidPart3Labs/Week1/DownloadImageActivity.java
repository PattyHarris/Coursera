package vandy.mooc;

import android.app.Activity;
import android.app.SearchManager;
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
    	
    	// PH: Grab the URL associated with the intent data which we
    	// will fill in, and then use the HaMeR framework to create
    	// a thread, have that thread download the image in the background.
    	// When it's done, we will create an intent, set the result of this
    	// activity to that intent, indicating the path name where it's been
    	// downloaded.  Then we will complete the activity with finish() which needs
    	// be called in the UI thread.  Key here is to pass something from 
    	// a background thread to the UI thread...
    	
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
    	super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
    	
    	Intent downloadImageIntent = getIntent();
    	final String intentUrl = downloadImageIntent.getStringExtra(SearchManager.QUERY);
    	
    	Log.i(TAG, "DownloadImageActivity: URL = " + intentUrl);

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.
    	
    	// PH: Check for a null here DownloadUtils will return a null
    	// on 404

        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.  See
        // http://stackoverflow.com/questions/20412871/is-it-safe-to-finish-an-android-activity-from-a-background-thread
        // for more discussion about this topic.
    	
	    new Thread() {
	    	
	        public void run() {

	        	// PH: Download the image for the URL
	        	final Uri fileUri = DownloadUtils.downloadImage(getApplicationContext(), 
	        			Uri.parse(intentUrl));
	        	
	        	if (fileUri != null )
	        	{
	        		// PH: Set the result with data
                	Log.i(TAG, "DownloadImageActivity: We have an image..." + fileUri.toString() );

                	Intent imageResultIntent = new Intent();
	        		imageResultIntent.setData(fileUri);

	        		setResult(Activity.RESULT_OK, imageResultIntent);
	        		
	        	}
	        	else {
	        		// PH: Else, we failed to return an image.
                	Log.i(TAG, "DownloadImageActivity: No image returned...");
 
	        		setResult( Activity.RESULT_CANCELED );
	        		
	        	}

	        	// PH: Finish in the UI thread...note that testing with
	        	// a sleep shows that the above indeed is completed before
	        	// this is called.  So, if we have a long wait, there "should"
	        	// be some indication on the UI that it's taking awhile....

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                     	// Call finish in the UI thread.
                    	Log.i(TAG, "DownloadImageActivity: Finish this activity.");
            			finish();
                       
                    }
                });
            }

	    }.start();

    }

}
