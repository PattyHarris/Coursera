/**
 * 
 */
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
 * DownloadTaskFragment.java
 *
 */

/**
* This retained fragment manages the background download image task.  It is
* responsible for starting the AsyncTask that does the actual image download.
* It is expected that the activity which contains this fragment implements the
* DownloadImageListener (see below).
*/
public class DownloadTaskFragment extends Fragment {

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
	public interface DownloadImageListener {

		void onDownloadComplete(Uri fileUri);
	}

	/**
	 * The AsyncTask used to download the image in background.
	 */
	private DownloadImageAsyncTask mDownloadImageAsyncTask;
	
	/**
	 * Connects the listening activity to this fragment.
	 */
	private DownloadImageListener mDownloadImageListener;

	/**
	* Hold a reference to the parent Activity so we can report the
	* task's current progress and results. The Android framework 
	* will pass us a reference to the newly created Activity after 
	* each configuration change.
	*/
	@Override
	public void onAttach(Activity activity) {

		Log.i(TAG, "Entered the onAttach() method");

		super.onAttach(activity);
		
		// Connect the containing class' implementation of the 
		// interface to this fragment. 
		
		// If you want to keep a copy of the parent activity, here's where
		// is should be done...

		try {

			// Set the DownloadImageListener for communicating with the containing activity.
			mDownloadImageListener = (DownloadImageListener) activity;
		
		} 
		catch (ClassCastException ex) {
			throw new ClassCastException(activity.toString()
					+ " must implement DownloadImageListener");
		}
	}

	/**
	* This method will only be called once when the retained
	* fragment is first created.
	*/
	@Override
	public void onCreate(Bundle savedInstanceState) {

		Log.i(TAG, "Entered the onCreate() method");

		super.onCreate(savedInstanceState);

		// Retain this fragment across configuration changes.
		setRetainInstance(true);

		// Create and execute the background download image task.  Dr. Schmidt starts
		// this in onStart..
		runDownloadImageTask();
	}
	
	/**
	 * Run the download image task.  
	 * 
	 * Note to self: this has some additional checks since I initially had the
	 * MainActivity as the containing activity.
	 */
	public void runDownloadImageTask()
	{
		Log.i(TAG, "Entered the runDownloadImageTask() method");

		DownloadImageActivity parentActivity = (DownloadImageActivity) getActivity();
		
		if (mDownloadImageAsyncTask != null && mDownloadImageAsyncTask.getStatus() == AsyncTask.Status.RUNNING) {
			
	        if (mDownloadImageAsyncTask.isCancelled()) {
	        	mDownloadImageAsyncTask = new DownloadImageAsyncTask();

	        	mDownloadImageAsyncTask.execute( parentActivity.getDownloadUri() );
	        }
	        else {
	            // Do nothing.....
	        }
		}

		if (mDownloadImageAsyncTask != null && mDownloadImageAsyncTask.getStatus() == AsyncTask.Status.PENDING) {
			
	        mDownloadImageAsyncTask.execute( parentActivity.getDownloadUri() );
		}

		if (mDownloadImageAsyncTask != null && mDownloadImageAsyncTask.getStatus() == AsyncTask.Status.FINISHED) {
			
			mDownloadImageAsyncTask = new DownloadImageAsyncTask();	
			mDownloadImageAsyncTask.execute( parentActivity.getDownloadUri() );
		}

		if (mDownloadImageAsyncTask == null) {

			mDownloadImageAsyncTask = new DownloadImageAsyncTask();	
			mDownloadImageAsyncTask.execute( parentActivity.getDownloadUri() );
		}
		
	}
	
	/**
	 * The parent activity is canceling this task, perhaps due
	 * to the back button pressed.
	 */
	public void cancelDownloadTask()
	{
		Log.i(TAG, "Entered the cancelDownloadTask() method");

		// We need to then cancel the AsyncTask
		if (mDownloadImageAsyncTask != null) {

			mDownloadImageAsyncTask.cancel(true);
		}
	}

	/**
	 * 
	 */
	@Override 
	public void onStart() { 
		
		Log.i(TAG, "Entered the onStart() method");
		super.onStart(); 

		// Create and execute the background download image task.  Need to check if it's
		// already running.
		// runDownloadImageTask();

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

		super.onActivityCreated(savedInstanceState);
		
		Log.i(TAG, "Entered the onActivityCreated() method");

		// If we've been detached due to orientation changes,
		// the spinner will go away.  
		if (mRunning) {
			
			// In a normal startup, onPreExecute is called followed by
			// onActivityCreated - which means this spinner may be created twice
			// and which means we'll leak a window.
			DownloadImageActivity parentActivity = (DownloadImageActivity) getActivity();

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

		if (mSpinner != null && mSpinner.isShowing()) {
			        	
			mSpinner.dismiss();
			mSpinner = null;
		}

		mDownloadImageListener = null;
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
	public void downloadImageComplete(Uri fileUri) {
		
		if (mDownloadImageListener != null) {
			mDownloadImageListener.onDownloadComplete(fileUri);
		}
	}


	/******************************************************************
	 * 
	 * Download image AsyncTask for downloading the image.
	 *
	 *****************************************************************/
	
	public class DownloadImageAsyncTask extends AsyncTask<Object, Integer, Uri> {
	
		/**
	     * Debugging tag used by the Android logger.
	     */
	    private final String TAG = getClass().getSimpleName();
	    	    
	    /**
		 * TBD: We can get this from our fragment....
		 */
		public DownloadImageAsyncTask() {
		}
	

	    /**
		 * Start up the spinner
		 */
		@Override
		protected void onPreExecute() {
			
			Log.i(TAG, "Entered the onPreExecute() method");

			mRunning = true;
			
			DownloadImageActivity parentActivity = (DownloadImageActivity) getActivity();

			if (mSpinner == null) {
				mSpinner = parentActivity.createProgressDialog();
			}
			
			if (mSpinner != null) {
				mSpinner.show();
			}
			
		}
	
	    /**
		 * Here's where the download happens.
		 */
		@Override
		protected Uri doInBackground(Object... params) {
			
			Log.i(TAG, "Entered the doInBackground() method");
			
			Uri downloadImageUrl = (Uri) params[0];
			
			if (downloadImageUrl == null) {
				return null;
			}
		
			DownloadImageActivity parentActivity = (DownloadImageActivity) getActivity();

			final Uri fileUri = Utils.downloadImage(parentActivity.getApplicationContext(), 
	    			downloadImageUrl);
	
	    	return fileUri;
		}
		
	    /**
		 * And here we notify our parent activity that the
		 * download is completed.
		 */
		@Override
		protected void onPostExecute(Uri result) {
			
			Log.i(TAG, "Entered the onPostExecute() method");
	
			if (mSpinner != null && mSpinner.isShowing()) {

				mSpinner.dismiss();
				mSpinner = null;
			}
			
			// Notify the parent activity that the download
			// is complete or cancelled.
			if (isCancelled() == false) {
				downloadImageComplete(result);
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
			
			if (mSpinner != null && mSpinner.isShowing()) {

				mSpinner.dismiss();
				mSpinner = null;
			}
						
		}
	}

}
