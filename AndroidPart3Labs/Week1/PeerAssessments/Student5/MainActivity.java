package vandy.mooc;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * A main Activity that prompts the user for a URL to an image and
 * then uses Intents and other Activities to download the image and
 * view it.
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
     * EditText field for entering the desired URL to an image.
     */
    private EditText mUrlEditText;

    /**
     * URL for the image that's downloaded by default if the user
     * doesn't specify otherwise.
     */
    private Uri mDefaultUri =
        Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	//Log.i(TAG, "onCreate()");
        // Always call super class for necessary
        // initialization/implementation.
        // @@ DONE -- you fill in here.
    	super.onCreate(savedInstanceState);
    	
        // Set the default layout.
        // @@ DONE -- you fill in here.
    	setContentView(R.layout.main_activity);
    	
        // Cache the EditText that holds the urls entered by the user
        // (if any).
        // @@ DONE -- you fill in here.
    	mUrlEditText = (EditText)findViewById(R.id.url);
    	
    	final Button button1 = (Button)findViewById(R.id.button1);
    	button1.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				downloadImage(v);
			}
    		
    	});
    }

    /**
     * Called by the Android Activity framework when the user clicks
     * the "Find Address" button.
     *
     * @param view The view.
     */
    public void downloadImage(View view) {
        try {
            // Hide the keyboard.
            hideKeyboard(this,
                         mUrlEditText.getWindowToken());

            // Call the makeDownloadImageIntent() factory method to
            // create a new Intent to an Activity that can download an
            // image from the URL given by the user.  In this case
            // it's an Intent that's implemented by the
            // DownloadImageActivity.
            // @@ DONE - you fill in here.          
            Uri url = getUrl();
            if (url == null)
            	return;
            Intent downloadIntent = makeDownloadImageIntent(url);

            // Start the Activity associated with the Intent, which
            // will download the image and then return the Uri for the
            // downloaded image file via the onActivityResult() hook
            // method.
            // @@ DONE -- you fill in here.
            startActivityForResult(downloadIntent, MainActivity.DOWNLOAD_IMAGE_REQUEST);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Hook method called back by the Android Activity framework when
     * an Activity that's been launched exits, giving the requestCode
     * it was started with, the resultCode it returned, and any
     * additional data from it.
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
    	
    	Log.d(TAG, "onACtivityResult(" +
    				"int requestCode:" + requestCode +
    				", int resultCode:" + resultCode +
    				", Itent data)");
    				
        // Check if the started Activity completed successfully.
        // @@ DONE -- you fill in here, replacing true with the right
        // code.
        if (resultCode == Activity.RESULT_OK) {
            // Check if the request code is what we're expecting.
            // @@ DONE -- you fill in here, replacing true with the
            // right code.
            if (requestCode == MainActivity.DOWNLOAD_IMAGE_REQUEST) {
            	final String pathToImageFile = "file://" + data.getDataString();
            	
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" app
                // by passing in the path to the downloaded image
                // file.
                // @@ DONE -- you fill in here.
            	final Intent galleryIntent = this.makeGalleryIntent(pathToImageFile);            	
            	
                // Start the Gallery Activity.
                // @@ DONE -- you fill in here.
            	startActivity(galleryIntent);
            }
        }
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.
        // @@ DONE -- you fill in here, replacing true with the right
        // code.
        else if (resultCode == Activity.RESULT_CANCELED) {
            Toast.makeText(this,
            		"User Canceled the Download", //R.string.user_cancel_download,
            		Toast.LENGTH_SHORT).show();
        }
    }    

    /**
     * Factory method that returns an Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.
    	// @@ DONE -- you fill in here, replacing "null" with the proper
    	// code.
    	return new Intent(Intent.ACTION_VIEW)
    			.setDataAndType(Uri.parse(pathToImageFile),"image/*");
    }

    /**
     * Factory method that returns an Intent for downloading an image.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        // Create an intent that will download the image from the web.
    	// @@ DONE -- you fill in here, replacing "null" with the proper
    	// code.
    	
		return new Intent(Intent.ACTION_WEB_SEARCH, url)
			.putExtra(SearchManager.QUERY, url.toString());
		
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri uri = null;

        String url = mUrlEditText.getText().toString();
        // Get the text the user typed in the edit text (if anything).
        uri = Uri.parse(url);

        // If the user didn't provide a URL then use the default.
        if (url == null || url.equals(""))
    	{
        	uri = mDefaultUri;
            url = mDefaultUri.toString(); // also update url to validate below
    	}

        // Do a sanity check to ensure the URL is valid, popping up a
        // toast if the URL is invalid.
        // @@ DONE -- you fill in here, replacing "true" with the
        // proper code.      
        if (URLUtil.isValidUrl(url)) {
        	return uri;
        }
        else {
            Toast.makeText(this,
                           "Invalid url!", //R.string.invalid_url,
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
        mgr.hideSoftInputFromWindow(windowToken,
                                    0);
    }
}
