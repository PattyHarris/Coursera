package vandy.mooc;

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
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.net.URL;

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

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param savedInstanceState object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
        super.onCreate(savedInstanceState);

        // Set the default layout.
        // @@ TODO -- you fill in here.
        setContentView(R.layout.main_activity);

        // Cache the EditText that holds the urls entered by the user
        // (if any).
        // @@ TODO -- you fill in here.
        //  Few test image working URLs.
        //   http://goo.gl/tH44bS PNG:
        //   http://goo.gl/rtNhhP PNG:
        //   http://goo.gl/GxgXe1 PNG:
        //   http://goo.gl/1eHVga JPG:
        //   http://goo.gl/eztdzi JPG:
        // Capture the download button
        mUrlEditText = (EditText) findViewById(R.id.url);
        Button downloadImageButton = (Button) findViewById(R.id.button1);
        downloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadImage(view); // Kick off.
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
            // @@ TODO - you fill in here.
            Uri uri = getUrl();
            Intent downloadImageIntent = null;
            if ( uri != null) {
                downloadImageIntent = makeDownloadImageIntent(uri);
            }

            // Start the Activity associated with the Intent, which
            // will download the image and then return the Uri for the
            // downloaded image file via the onActivityResult() hook
            // method.
            // @@ TODO -- you fill in here.
            if (downloadImageIntent != null)
                startActivityForResult(downloadImageIntent, DOWNLOAD_IMAGE_REQUEST);
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
        // @@ TODO -- you fill in here, replacing true with the right
        // code.
        if (resultCode == RESULT_OK) {
            // Check if the request code is what we're expecting.
            // @@ TODO -- you fill in here, replacing true with the
            // right code.
            if (requestCode == DOWNLOAD_IMAGE_REQUEST) {
                // Call the makeGalleryIntent() factory method to
                // create an Intent that will launch the "Gallery" app
                // by passing in the path to the downloaded image
                // file.
                // @@ TODO -- you fill in here.
                Uri fileUrl = data.getData();
                if (fileUrl == null) {
                    Toast.makeText(this, "Image not downloaded NULL.", Toast.LENGTH_SHORT).show();
                    return;
                } else if (fileUrl != null) {
                    File file = new File(fileUrl.toString());
                    if (file.exists()) {
                        if (file.length() == 0 ) {
                            Toast.makeText(this, "Image not downloaded zero size.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } // file size is zero
                }

                // Start the Gallery Activity.
                // @@ TODO -- you fill in here.
                Intent gallery = makeGalleryIntent(fileUrl.toString());
                if (gallery != null) startActivity(gallery); // Don't start gallery in case of error
            }
        }
        // Check if the started Activity did not complete successfully
        // and inform the user a problem occurred when trying to
        // download contents at the given URL.
        // @@ TODO -- you fill in here, replacing true with the right
        // code.
        else /* if (true) */ {
            Toast.makeText(this, "Image not downloaded.", Toast.LENGTH_SHORT).show();
            Log.d(TAG,"resultCode:" + resultCode + " Req. code:" + requestCode);
        }
    }    

    /**
     * Factory method that returns an Intent for viewing the
     * downloaded image in the Gallery app.
     */
    private Intent makeGalleryIntent(String pathToImageFile) {
        // Create an intent that will start the Gallery app to view
        // the image.
    	// TODO -- you fill in here, replacing "null" with the proper
    	// code.
        // Error checking here due to different behavior seen in API 22 and API 18
        File file = new File(pathToImageFile);
        if (file.exists()) {
            if (file.length() > 0 ) {
                Log.d(TAG, "Image:" + file.length() + " bytes (" + pathToImageFile + ")");
                Toast.makeText(this, "Image size: " + +file.length() + " bytes.", Toast.LENGTH_SHORT).show();
                Intent baseIntent = new Intent(Intent.ACTION_VIEW);
                baseIntent.setDataAndType(Uri.fromFile(new File(pathToImageFile)), "image/*");
                return Intent.createChooser(baseIntent, "Show picture Using");
            } else { // API 18. File was created with zero size.
                Log.d(TAG,"Image EMPTY:" + pathToImageFile );
                Toast.makeText(this, "Image EMPTY: " + pathToImageFile,Toast.LENGTH_SHORT).show();
                return null;
            }
        } else { // API 22.
            Log.d(TAG,"No IMG_FILE:" + pathToImageFile );
            Toast.makeText(this, "No image: " + pathToImageFile,Toast.LENGTH_SHORT).show();
            return  null;
        }
    }

    /**
     * Factory method that returns an Intent for downloading an image.
     */
    private Intent makeDownloadImageIntent(Uri url) {
        // Create an intent that will download the image from the web.
    	// TODO -- you fill in here, replacing "null" with the proper
    	// code.
        Intent baseIntent = new Intent(Intent.ACTION_WEB_SEARCH, url);
        baseIntent.putExtra(SearchManager.QUERY,url.toString());
        return Intent.createChooser(baseIntent,"Complete Action Using");
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
        if (uri == null || uri.equals(""))
            url = mDefaultUrl;

        // Do a sanity check to ensure the URL is valid, popping up a
        // toast if the URL is invalid.
        // @@ TODO -- you fill in here, replacing "true" with the
        // proper code.
        if (URLUtil.isValidUrl(url.toString()))
            return url;
        else {
            Toast.makeText(this,
                           "Invalid URL:" + uri,
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
