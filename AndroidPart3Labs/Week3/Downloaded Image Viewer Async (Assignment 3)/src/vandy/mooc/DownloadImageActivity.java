package vandy.mooc;


import vandy.mooc.DownloadTaskFragment.DownloadImageListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

/**
 * Assignment 3
 * DownloadImageActivity.java
 *
 */

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends LifecycleLoggingActivity implements DownloadImageListener, OnCancelListener  {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
    

    private Uri mDownloadUri;

    private FragmentManager mFragmentManager;
    private DownloadTaskFragment mDownloadFragment;

    static final String DOWNLOAD_IMAGE_FRAGMENT_TAG = "download_task";


    /**
     * Setter and getters
     */
    public Uri getDownloadUri() {
    	return mDownloadUri;
    }
    
    public void setDownloadUri(Uri imageUri) {
    	mDownloadUri = imageUri;
    }
        
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

    	super.onCreate(savedInstanceState);
			
    	mFragmentManager = getFragmentManager();
    	
        // Get the URL associated with the Intent data.
    	
    	Intent downloadImageIntent = getIntent();
    	final String intentUrl = downloadImageIntent.getStringExtra(SearchManager.QUERY);
    	
    	Log.i(TAG, "DownloadImageActivity: URL = " + intentUrl);

    	setDownloadUri( Uri.parse(intentUrl));

        // Download the image in the background using a retained fragment and
    	// AsynTask.  When this completes, it will call the implemented interface
    	// method to let this activity know that the download has completed.
    	// See onDownloadComplete below.
    	
    	mDownloadFragment = (DownloadTaskFragment) mFragmentManager                
                .findFragmentByTag(DOWNLOAD_IMAGE_FRAGMENT_TAG);

        // If we find our download fragment, then the fragment was retained from a
        // configuration change.
        if (mDownloadFragment == null) {
        	
        	mDownloadFragment = new DownloadTaskFragment();
        	mFragmentManager.beginTransaction()
        		.add(mDownloadFragment, DOWNLOAD_IMAGE_FRAGMENT_TAG)
        		.addToBackStack(null)
        		.commit();
        }
        else {
        
        	mDownloadFragment.runDownloadImageTask();
        }
    	
    }
    
    /**
     * When the image download is completed, this activity will
     * start the filter activity.
     */
    @Override
    public void onDownloadComplete(Uri fileUri) {
    	
        Log.i(TAG, "Entered onDownloadComplete()" );
        
        if (fileUri != null ) {
        	
        	Log.i(TAG, "onDownloadComplete() received file Uri: " + fileUri.toString() );
        	
        	// The image was downloaded successfully so this activity is complete.
        	
        	Intent imageResultIntent = new Intent();
    		imageResultIntent.setData(fileUri);

    		setResult(Activity.RESULT_OK, imageResultIntent);
    		
    		// Since this is called from onPostExecute, we're in the UI thread
    		// and can safely quit this activity.
    		finish();
        }
        else {
    		// Else, we failed to return an image.
        	Log.i(TAG, "DownloadImageActivity: No image returned...");

    		setResult( Activity.RESULT_CANCELED );
    		
    		// Since this is called from onPostExecute, we're in the UI thread
    		// and can safely quit this activity.
    		finish();
        	
        }
    }
    
    

    /**
     * Detect the back button to cancel the running tasks.
     */  
    @Override
    public void onBackPressed() {
    	
    	Log.i(TAG, "Entered the DownloadImageActivity::onBackPressed() method");

    	// Cancel the AyncTasks - they will respond with a null
    	// result which will force us to finish...
    	DownloadTaskFragment downloadFragment = 
    			(DownloadTaskFragment) mFragmentManager.findFragmentByTag(DOWNLOAD_IMAGE_FRAGMENT_TAG);

    	if (downloadFragment != null) {
    		
    		downloadFragment.cancelDownloadTask();
    		
    		if (mFragmentManager.getBackStackEntryCount() > 0 ) {
    			mFragmentManager.popBackStack();
    		}
    	}
    	
    	super.onBackPressed();
	
    }
    
    /**
     * Allows this activity to know when the user
     * has cancelled the progress dialog (e.g. back button
     * has been pressed.
     */
    @Override
    public void onCancel(DialogInterface dialog) {
    	
    	Log.i(TAG, "Entered the DownloadImageActivity::onCancel() method");

    	// Cancel the AyncTasks - they will respond with a null
    	// result which will force us to finish...
    	DownloadTaskFragment downloadFragment = 
    			(DownloadTaskFragment) mFragmentManager.findFragmentByTag(DOWNLOAD_IMAGE_FRAGMENT_TAG);

    	if (downloadFragment != null) {
    		
    		downloadFragment.cancelDownloadTask();
    		
    		if (mFragmentManager.getBackStackEntryCount() > 0 ) {
    			mFragmentManager.popBackStack();
    		}
    	}
    	
    	// This isn't an error scenario, but when the back-button
    	// is pressed.  To notify the parent activity, this is
    	// a hacky way....
    	Intent imageResultIntent = new Intent();
    	
		imageResultIntent.setData(null);
		imageResultIntent.putExtra("user_cancelled", 1);

		setResult(Activity.RESULT_OK, imageResultIntent);
		finish();

    }


	/******************************************************************
	 * 
	 * Helper progress dialog used by the background tasks.
	 *
	 *****************************************************************/

    public ProgressDialog createProgressDialog() {
    	  	
    	Log.i(TAG, "Entered the DownloadImageActivity::createProgressDialog() method");

    	ProgressDialog dialog = new ProgressDialog(DownloadImageActivity.this);

        try {
                dialog.show();
        } 
        catch (BadTokenException ex) {
        	ex.printStackTrace();
        	return null;

        }
        
    	dialog.setOnCancelListener(this);
        
        dialog.setCancelable(true);
        dialog.setIndeterminate(true);
        dialog.setContentView(R.layout.progress_dialog);

        return dialog;
    }
}
