package vandy.mooc;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.net.Uri;
import android.util.Log;

/**
 * Assignment 3
 * FilterImageFragment.java
 *
 */

/**
* This retained fragment manages the background filter image task.  It is
* responsible for starting the AsyncTask that does the actual filter work.
* It is expected that the activity which contains this fragment implements the
* FilterImageListener (see below).
*/

public class FilterImageFragment  extends Fragment {

	/**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Our spinner for showing that something is happening.
     */
    private ProgressDialog mSpinner;
    boolean mRunning = false;

    /**
     * The public interface to be implemented by the activity
     * which owns this fragment.
     *
     */
	public interface FilterImageListener {
		void onFilterImageComplete(Uri fileUri);
	}

	/**
	 * The AsyncTask used to filter the image in background.
	 */
	private FilterImageAsyncTask mFilterImageAsyncTask;
	
	/**
	 * Connects the listening activity to this fragment.
	 */
	private FilterImageListener mFilterImageListener;

	/**
	* Hold a reference to the parent Activity so we can report the
	* task's current progress and results. The Android framework 
	* will pass us a reference to the newly created Activity after 
	* each configuration change.
	*/
	@Override
	public void onAttach(Activity activity) {

        Log.i(TAG, "Entered onAttach()" );

        super.onAttach(activity);
		
		// Connects the containing class' implementation of the 
		// interface to this fragment. 

		try {

			// Set the FilterImageListener for communicating with the containing activity.
			mFilterImageListener = (FilterImageListener) activity;
		
		} 
		catch (ClassCastException ex) {
			throw new ClassCastException(activity.toString()
					+ " must implement FilterImageListener");
		}
	}

	/**
	* This method will only be called once when the retained
	* fragment is first created.
	*/
	@Override
	public void onCreate(Bundle savedInstanceState) {

        Log.i(TAG, "Entered onCreate()" );

        super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);

		// Create and execute the background filter image task.
		runFilterImageTask();
	}

	/**
	 * Run the filter image task
	 * 
	 * Note to self: this has some additional checks since I initially had the
	 * MainActivity as the containing activity.
	 */
	public void runFilterImageTask()
	{
		FilterImageActivity parentActivity = (FilterImageActivity) getActivity();

		if (mFilterImageAsyncTask != null && mFilterImageAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
			
	        if (mFilterImageAsyncTask.isCancelled()) {
	        	mFilterImageAsyncTask = new FilterImageAsyncTask();

	        	mFilterImageAsyncTask.execute( parentActivity.getDownloadedImageUri() );
	        }
	        else {
	            // Do nothing.....
	        }
		}

		if (mFilterImageAsyncTask != null && mFilterImageAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
			
	        mFilterImageAsyncTask.execute( parentActivity.getDownloadedImageUri() );
		}

		if (mFilterImageAsyncTask != null && mFilterImageAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
			
			mFilterImageAsyncTask = new FilterImageAsyncTask();	
			mFilterImageAsyncTask.execute( parentActivity.getDownloadedImageUri() );
		}

		if (mFilterImageAsyncTask == null) {

			mFilterImageAsyncTask = new FilterImageAsyncTask();	
			mFilterImageAsyncTask.execute( parentActivity.getDownloadedImageUri() );
		}
	}

	/**
	 * The parent activity is canceling this task, perhaps due
	 * to the back button pressed.
	 */
	public void cancelFilterImageTask()
	{
		Log.i(TAG, "Entered the cancelFilterImageTask() method");
		
		// We need to then cancel the AsyncTask
		if (mFilterImageAsyncTask != null) {

			mFilterImageAsyncTask.cancel(true);
		}
	}

	/**
	 * 
	 */
	@Override 
	public void onStart() { 
		
		Log.i(TAG, "Entered the onStart() method");

		super.onStart(); 
		
		// Create and execute the background filter image task.  Need to check
		// if it's already running...
		// runFilterImageTask();

	} 

	/**
	 * 
	 */
	@Override 
	public void onResume() { 
		
		Log.i(TAG, "Entered the onResume() method");

		super.onResume(); 
	} 


	/**
	 * 
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		Log.i(TAG, "Entered the onActivityCreated() method");
		
		super.onActivityCreated(savedInstanceState);

		// If we've been detached due to orientation changes,
		// the spinner will go away.  
		if (mRunning) {
			
			// In a normal startup, onPreExecute is called followed by
			// onActivityCreated - which means this spinner may be created twice
			// and which means we'll leak a window.
			FilterImageActivity parentActivity = (FilterImageActivity) getActivity();

			if (mSpinner == null) {
				mSpinner = parentActivity.createProgressDialog();
			}

			if (mSpinner != null) {
				mSpinner.show();
			}
		}
	}

	/**
	* The fragment is detached, but it's state should be
	* maintained....
	*/
	@Override
	public void onDetach() {
		
		Log.i(TAG, "Entered the onDetach() method");

		if (mSpinner != null && mSpinner.isShowing() ) {

			mSpinner.dismiss();
			mSpinner = null;
		}
		
		mFilterImageListener = null;
        mRunning = false;

		super.onDetach();
	}

	/**
	 * 
	 */
	@Override 
	public void onDestroy() { 
		
		Log.i(TAG, "Entered the onDestroy() method");

		super.onDestroy(); 
		mRunning = false;
	} 

	/**
	 * Pass the data back to the parent activity.
	 * @param fileUri
	 */
	public void filterImageComplete(Uri fileUri) {
		
		if (mFilterImageListener != null) {	
			mFilterImageListener.onFilterImageComplete(fileUri);
		}
	}


    /******************************************************************
	 * 
	 * Filter image AsyncTask for filtering the image.
	 * @author Patty
	 *
	 *****************************************************************/
	
	public class FilterImageAsyncTask extends AsyncTask<Object, Integer, Uri> {
	
		/**
	     * Debugging tag used by the Android logger.
	     */
	    private final String TAG = getClass().getSimpleName();
	    	    
	    /**
		 */
		public FilterImageAsyncTask() {
		}
	
		/**
		 * Start up the spinner
		 */
		@Override
		protected void onPreExecute() {
			
			Log.i(TAG, "Entered the onPreExecute() method");

			mRunning = true;

			FilterImageActivity parentActivity = (FilterImageActivity) getActivity();
			
			if (mSpinner == null) {
				mSpinner = parentActivity.createProgressDialog();
			}
			
			if (mSpinner != null) {
				mSpinner.show();
			}
						
		}
	
	    /**
		 * Here's where the filtering happens.
		 */
		@Override
		protected Uri doInBackground(Object... params) {
			
			Log.i(TAG, "Entered the doInBackground() method");
			
			Uri filterImageUrl = (Uri) params[0];
		
			FilterImageActivity parentActivity = (FilterImageActivity) getActivity();

			final Uri fileUri = Utils.grayScaleFilter(parentActivity.getApplicationContext(), 
	    			filterImageUrl);
	
	    	return fileUri;
		}
	
	    /**
		 * And here we notify our parent activity that the
		 * filter is completed.
		 */
		@Override
		protected void onPostExecute(Uri result) {
			
			Log.i(TAG, "Entered the onPostExecute() method");
				
			// Notify the parent activity that the filter
			// is complete.
			if (isCancelled() == false) {
				filterImageComplete(result);
			}

			if (mSpinner != null && mSpinner.isShowing() ) {

				 mSpinner.dismiss();
				 mSpinner = null;
			}

		}
		
	    /**
		 * The task has been cancelled.  Cleanup!
		 */
		@Override
		protected void onCancelled() {

			Log.i(TAG, "Entered the onCancelled() method");

			// Do not call super.onCancelled() according to the
			// Android documentation.

			if ( mSpinner != null && mSpinner.isShowing() ) {

				mSpinner.dismiss();
				mSpinner = null;
			}
					    
		}
	}

}
