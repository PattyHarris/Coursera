package vandy.mooc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

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
        // @@ TODO -- you fill in here.
        super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
        final Uri uriImage = getIntent().getData();
        Log.i(TAG,"onCreate:" + uriImage.toString());
        Toast.makeText(this, "Download Image: " + uriImage,Toast.LENGTH_SHORT).show();

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.
        final android.os.Handler handler = new android.os.Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadUtils downloader = new DownloadUtils();
                try {
                    final Uri fileName = downloader.downloadImage(getApplicationContext(), uriImage);

                    Log.d(TAG,"Filename:" + fileName);
                    handler.post(new Runnable() { // UI Context
                        @Override
                        public void run() { // Post to UP Thread
                            Intent returnToIntent = new Intent();
                            if (fileName != null) {
                                returnToIntent.setData(fileName);
                                Log.d(TAG, "Downloader data:" + fileName);
                                setResult(RESULT_OK, returnToIntent);
                            } else {
                                returnToIntent.setData(Uri.parse("NoImage"));
                                setResult(RESULT_CANCELED, returnToIntent);
                                Log.d(TAG, "Downloader return NULL:");
                            }
                            finish();
                        } // End Handler runnable
                    }); // End post
                } catch (Exception ex) { // Catch for 404 Error.
                    // Example: for non-existent URL.
                    // java.io.FileNotFoundException: http://www.dre.vanderbilt.edu/~schmidt/robot.pn
                    Intent returnToIntent = new Intent();
                    returnToIntent.setData(Uri.parse("NoImage")); // Set this to avoid crash.
                    setResult(RESULT_CANCELED, returnToIntent);
                    Log.d(TAG, "Downloader Exception:");
                    finish();
                }
            } // end Thread run
        }).start(); // Thread start

    }

}
