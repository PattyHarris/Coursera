package vandy.mooc;


import vandy.mooc.FilterImageFragment.FilterImageListener;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager.BadTokenException;

/**
 * Assignment 3
 * FilterImageActivity.java
 *
 */

/**
 * An Activity that filters an image.
 */
public class FilterImageActivity extends LifecycleLoggingActivity implements FilterImageListener, OnCancelListener {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
        
    private Uri mDownloadedImageUri;

    private FragmentManager mFragmentManager;
    private FilterImageFragment mFilterImageFragment;

    static final String FILTER_IMAGE_FRAGMENT_TAG = "filter_task";

    /**
     * Setter and getters
     */
    public Uri getDownloadedImageUri() {
    	return mDownloadedImageUri;
    }
    
    public void setDownloadedImageUri(Uri imageUri) {
    	mDownloadedImageUri = imageUri;
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

    	Log.i(TAG, "Entered FilterImageActivity::onCreate()");

    	super.onCreate(savedInstanceState);
			
    	mFragmentManager = getFragmentManager();
    	
        // Get the URL associated with the Intent data.
    	
    	Intent filterImageIntent = getIntent();
    	final String intentUrl = filterImageIntent.getStringExtra(SearchManager.QUERY);
    	
    	Log.i(TAG, "FilterImageActivity: URL = " + intentUrl);

    	
		// The filter fragment will start the filter AsyncTask.  
    	// Then when it's done, it will call the
		// implemented interface on below.
		
		setDownloadedImageUri( Uri.parse(intentUrl) );
		
		// Start the fragment to filter the image.
		mFilterImageFragment = (FilterImageFragment) mFragmentManager                
				.findFragmentByTag(FILTER_IMAGE_FRAGMENT_TAG);

		// If we find our filter fragment, then the fragment was retained from a
		// configuration change.
		if (mFilterImageFragment == null) {
			
			mFilterImageFragment = new FilterImageFragment();
			mFragmentManager.beginTransaction()
				.add(mFilterImageFragment, FILTER_IMAGE_FRAGMENT_TAG)
				.addToBackStack(null)
				.commit();
		}
		else  {
		
			mFilterImageFragment.runFilterImageTask();
		}
    	
    }
    
    /**
     * When the filter image is completed, this activity will
     * will send the results back to the MainActivity.  
     */
    @Override
    public void onFilterImageComplete(Uri fileUri) {
    	
        Log.i(TAG, "Entered onFilterImageComplete()" );
                
        if (fileUri != null ) {
        	
    		// Set the result with data.  We received a good result, so return
        	// the result back to the MainActivity and quit.
        	Log.i(TAG, "onFilterImageComplete() received file Uri: " + fileUri.toString() );

        	Intent imageResultIntent = new Intent();
    		imageResultIntent.setData(fileUri);

    		setResult(Activity.RESULT_OK, imageResultIntent);
    		
    		// Since this is called from onPostExecute, we're in the UI thread
    		// and can safely quit this activity.
    		finish();

        }
        else {
    		// Else, we failed to return an image.
        	Log.i(TAG, "onFilterImageComplete: No image returned...");

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
    	
    	Log.i(TAG, "Entered the FilterImageActivity::onBackPressed() method");
    	
    	// Cancel the AyncTasks.
    	
    	FilterImageFragment filterFragment = (FilterImageFragment) mFragmentManager                
                .findFragmentByTag(FILTER_IMAGE_FRAGMENT_TAG);

    	if (filterFragment != null) {
    		
    		filterFragment.cancelFilterImageTask();
    		
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
    	
    	Log.i(TAG, "Entered the FilterImageActivity::onCancel() method");

    	// Cancel the AyncTasks - they will respond with a null
    	// result which will force us to finish...
    	FilterImageFragment downloadFragment = 
    			(FilterImageFragment) mFragmentManager.findFragmentByTag(FILTER_IMAGE_FRAGMENT_TAG);

    	if (downloadFragment != null) {
    		
    		downloadFragment.cancelFilterImageTask();
    		
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
    	
    	System.out.println("Entered the FilterImageActivity::createProgressDialog() method");

    	ProgressDialog dialog = new ProgressDialog(FilterImageActivity.this);
    	
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
