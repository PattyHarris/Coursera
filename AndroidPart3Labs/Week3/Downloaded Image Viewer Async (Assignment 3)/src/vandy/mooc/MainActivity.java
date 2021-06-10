package vandy.mooc;

import java.io.File;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Assignment 3
 * MainActivity.java
 *
 */

/**
 * A main Activity that prompts the user for a URL to an image and
 * then uses Intents and other Activities to download the image and
 * view it.
 * 
 * This uses the assignment 1 DownloadImageActivity as the activity
 * which manages the 2 AsyncTasks.  This file is nearly identical to 
 * that of assignment 1 with the exception that I've used an explicit
 * intent for bring up the DownloadImageActivity.
 */
public class MainActivity extends LifecycleLoggingActivity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * A value that uniquely identifies the request to download an
     * image.
     */
    private static final int DOWNLOAD_IMAGE_REQUEST = 1;

    /**
     * A value that uniquely identifies the request to filter an
     * image.
     */
    private static final int FILTER_IMAGE_REQUEST = 2;
    
    /**
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUrl =
        Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    /**
     * URL returned from the filter image activity.
     */
    private Uri mFilterUri;
    

    /**
     * If we're paused, wait until the MainActivity has resumed before we launch
     * the gallery.
     */
    boolean mPaused;


     /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	
    	Log.i(TAG, "Entered onCreate()");
    	
       // Always call super class for necessary initialization/implementation.
    	super.onCreate(savedInstanceState);

        // Set the default layout.
    	setContentView(R.layout.main_activity);

        // Cache the EditText that holds the urls entered by the user
        // (if any).
		mUrlEditText = (EditText) findViewById(R.id.url);
		
		mPaused = false;
 
    	
    }

     
    /**
     * Called by the Android Activity framework when the user clicks
     * the "Download Image" button.
     * 
     * See the handler specified in main_activity.xml.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
        	        	
        	// Initialized to null to start.  It will be set once
        	// the filtered image task has returned.
         	mFilterUri = null;
        	
            // Hide the keyboard.
            hideKeyboard(this, mUrlEditText.getWindowToken());

            // Call the makeDownloadImageIntent() factory method to
            // create a new Intent to an Activity that can download an
            // image from the URL given by the user.  In this case
            // it's an Intent that's implemented by the
            // DownloadImageActivity.

			Uri downloadUri = getUrl();
            
            if ( downloadUri != null ) {
            	
	            Intent downLoadIntent = makeDownloadImageIntent(downloadUri);
	            
	            // Start the Activity associated with the Intent, which
	            // will download the image and then return the Uri for the
	            // downloaded image file via the onActivityResult() hook
	            // method.
	
	            if ( downLoadIntent != null ) {
	            	startActivityForResult(downLoadIntent, DOWNLOAD_IMAGE_REQUEST);
	            }
	            else {
	                Toast toast = Toast.makeText(getBaseContext(),
	                        "Failed to create download image intent!",
	                        Toast.LENGTH_SHORT);
	                toast.show();
	            }
            }

        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     * 
     * This will be called when the DownloadImageActivity completes.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
    	
    	Log.i(TAG, "Entered onActivityResult()");
    	
        // Check if the started Activity completed successfully.

    	if (resultCode == Activity.RESULT_OK)  {
    		
    		Uri downloadUri = data.getData();
    		
    		// Check whether the user has cancelled, e.g. hit the
    		// back button.  
    		
    		if (downloadUri == null) {
    			
    			Log.i(TAG, "User cancelled.");
    			
    			int extra = data.getIntExtra("user_cancelled", -1);
    			
    			if (extra == 1) {
    				
    		        onBackPressed();

    		        return;
    			}
    		}

            // Check if the request code is what we're expecting.
    		// If the request was a download image request, we'll kick off
    		// the filter image activity.
    		if ( requestCode == DOWNLOAD_IMAGE_REQUEST ) {
    			                
                if ( downloadUri != null ) {

    	            Intent filterImageIntent = makeFilterImageIntent(downloadUri);
    	            
    	            // Start the Activity associated with the Intent, which
    	            // will download the image and then return the Uri for the
    	            // downloaded image file via the onActivityResult() hook
    	            // method.
    	
    	            if ( filterImageIntent != null ) {
    	            	startActivityForResult(filterImageIntent, FILTER_IMAGE_REQUEST);
    	            }
    	            else {
    	                Toast toast = Toast.makeText(getBaseContext(),
    	                        "Failed to create filter image intent!",
    	                        Toast.LENGTH_SHORT);
    	                toast.show();
       	            }
                }
    			
    		}
 
            if ( requestCode == FILTER_IMAGE_REQUEST ) {
            	
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" application
                // by passing in the path to the downloaded image
                // file.
            	mFilterUri = downloadUri;
            	
            	if (mPaused == false) {
            	
            		// Prevents accidentally launching the gallery for other Resume
            		// events.
            		mFilterUri = null;
            		
	            	Intent galleryIntent = makeGalleryIntent( downloadUri.toString() );
	
	                // Start the Gallery Activity.
	
	            	if (galleryIntent != null) {
	                    startActivity( galleryIntent );
	            	}
	                else {
	                    Toast toast = Toast.makeText(getBaseContext(),
	                            "Failed to create Gallery intent!",
	                            Toast.LENGTH_SHORT);
	                    toast.show();
	                }
            	}
            }
        }
    	
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.

        else {
        	// If it didn't complete successfully, show a toast
        	// message indicating we had a problem.

            Toast toast = Toast.makeText(getBaseContext(),
                    "Sorry, could not download the requested image!",
                    Toast.LENGTH_SHORT);
            toast.show();
        	
        }
    }    


    /**
     * Factory method that returns an implicit Intent for viewing the
     * downloaded image in the Gallery application.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
    	
        // Create an intent that will start the Gallery app to view
        // the image.

    	// Gallery expects a file://path url - so here we need to create a file uri
    	// from the file path.
        if (pathToImageFile != null && !pathToImageFile.isEmpty() ) {

            Intent intent = new Intent(Intent.ACTION_VIEW);
            
            intent.setDataAndType (Uri.fromFile( new File(pathToImageFile)), "image/*");
            
            // Or this is another way.........................
            // intent.setDataAndType(Uri.parse("file://" + pathToImageFile), "image/*");
            
            return intent;
        } 
        else {
            return null;
        }

    }

    /**
     * Factory method that returns an explicit Intent for downloading
     * an image.  The implicit intent was used in assignment 1.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        // Create an intent that will download the image from the web.
    	
    	// Create the download intent using the give url
    	if (url == null) {
    		return null;
    	}
    	
    	Intent downloadImageIntent = new Intent(MainActivity.this, DownloadImageActivity.class);
    	downloadImageIntent.putExtra( SearchManager.QUERY, url.toString() );
    	
        return downloadImageIntent;
    }

    /**
     * Factory method that returns an explicit Intent for filtering
     * an image.  
     */
    private Intent makeFilterImageIntent(Uri url) {
    	
        // Create an intent that will download the image from the web.
    	
    	if (url == null) {
    		return null;
    	}
    	
    	Intent filterImageIntent = new Intent(MainActivity.this, FilterImageActivity.class);
    	filterImageIntent.putExtra( SearchManager.QUERY, url.toString() );
    	
        return filterImageIntent;
    }

    /**
     * Tracking so we know when to show the gallery
     */
    @Override
    protected void onPause(){
  
		Log.i(TAG, "Entered the onPause() method");
		mPaused = true;
		super.onPause();
    }

    /**
     * If we're resuming and we have a filter URL,
     * show the gallery for the image.
     */
    @Override
    protected void onResume() {
        // Always call super class for necessary
        // initialization/implementation and then log which lifecycle
        // hook method is being called.
    	
		Log.i(TAG, "Entered the MainActivity::onResume() method");
		super.onResume();

		mPaused = false;

		if ( mFilterUri != null ) {

        	Intent galleryIntent = makeGalleryIntent( mFilterUri.toString() );
        	
            // Start the Gallery Activity.

        	if (galleryIntent != null) {
                startActivity( galleryIntent );
        	}
            else {
                Toast toast = Toast.makeText(getBaseContext(),
                        "Failed to create Gallery intent!",
                        Toast.LENGTH_SHORT);
                toast.show();
            }
			
			mFilterUri = null;
			
		}
    }

    /**
     * Detect the back button to cancel the running tasks.
     */
     
    @Override
    public void onBackPressed() {
    	
    	Log.i(TAG, "Entered the MainActivity::onBackPressed() method");
    	
        super.onBackPressed();
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {

        Uri url = null;

        // Get the text the user typed in the edit text (if anything).
        url = Uri.parse(mUrlEditText.getText().toString());

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();

        if (uri == null || uri.equals("")) {
            url = mDefaultUrl;
        }

        // Do a sanity check to ensure the URL is valid, popping up a
        // toast if the URL is invalid.
        
        // Make sure the URL is formatted properly here
        
        if ( url.getScheme() != null && 
        		Patterns.WEB_URL.matcher( url.toString() ).find() ) {
            return url;
        }
        else {
            Toast.makeText(this,
                           "Invalid URL entered.  Please try again.",
                           Toast.LENGTH_SHORT).show();
            return null;
        } 
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public void hideKeyboard(Activity activity,
                             IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
    
}

