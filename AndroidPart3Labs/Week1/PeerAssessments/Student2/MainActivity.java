package vandy.mooc;

import android.app.Activity;
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
    private Uri mDefaultUrl =
        Uri.parse("http://www.dre.vanderbilt.edu/~schmidt/robot.png");


    private Button mGetImageForUrlButton;


    /**
     * Intent type for images
     */
    private static final String IMAGE_TYPE = "image/*";

    /**
     * File protocol to prepend to file paths like:</br>
     * file:///disk/dir/files/images/foo.jpg
     *
     */
    private static final String FILE_PROTOCOL = "file://";


    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the default layout.
        this.setContentView(R.layout.main_activity);

        // Cache the EditText that holds the urls entered by the user
        // (if any).
        mUrlEditText = (EditText) findViewById(R.id.url);

        mGetImageForUrlButton = (Button) findViewById(R.id.button1);
        mGetImageForUrlButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Got click, getting URL");
                downloadImage(view);
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

            Uri uri = getUrl();
            if ( uri == null ) {
                return;
            }
            // Call the makeDownloadImageIntent() factory method to
            // create a new Intent to an Activity that can download an
            // image from the URL given by the user.  In this case
            // it's an Intent that's implemented by the
            // DownloadImageActivity.
            Intent downloadImageIntent = makeDownloadImageIntent( uri );

            // Start the Activity associated with the Intent, which
            // will download the image and then return the Uri for the
            // downloaded image file via the onActivityResult() hook
            // method.
            startActivityForResult(downloadImageIntent, MainActivity.DOWNLOAD_IMAGE_REQUEST);
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
        // Check if the started Activity completed successfully.
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "Got result OK");
            // Check if the request code is what we're expecting.
            if (requestCode == MainActivity.DOWNLOAD_IMAGE_REQUEST) {
                Log.d(TAG, "Got download img req code");
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" app
                // by passing in the path to the downloaded image
                // file.
                Log.d(TAG, "Activity Result image URI: " + data.getData());
                String imgPath = data.getDataString();
                Intent gi = this.makeGalleryIntent(imgPath);


                // Start the Gallery Activity.
                startActivity(gi);
            }
        }
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.
        // code.
        else {
            Toast.makeText(this,
                    "Failed to Download!",
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
    	// code.
        Log.d(TAG, "Path to image file: " + pathToImageFile);
        Intent i = new Intent( Intent.ACTION_VIEW ,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        i.setDataAndType(Uri.parse(FILE_PROTOCOL+pathToImageFile), IMAGE_TYPE);

        return i;
    }

    /**
     * Factory method that returns an Intent for downloading an image.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        // Create an intent that will download the image from the web.
    	Intent downloadImgIntent = new Intent( this, DownloadImageActivity.class );
        downloadImgIntent.setData(url);
        return downloadImgIntent;
    }

    /**
     * Get the URL to download based on user input.
     */
    protected Uri getUrl() {
        Uri url = null;

        // Get the text the user typed in the edit text (if anything).
        url = Uri.parse(mUrlEditText.getText().toString());

        Log.d(TAG, "Got URL:'" + url + "' from edit text field");

        // If the user didn't provide a URL then use the default.
        String uri = url.toString();
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;

        // Do a sanity check to ensure the URL is valid, popping up a
        // toast if the URL is invalid.
        if(URLUtil.isValidUrl(url.toString())) {
            return url;
        }
        else {
            Toast.makeText(this,
                           "Invalid URL",
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
