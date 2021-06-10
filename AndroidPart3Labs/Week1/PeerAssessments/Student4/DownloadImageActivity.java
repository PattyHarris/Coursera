package vandy.mooc;

import java.io.InputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();
    private Uri url;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * some class scope variable initialization.
     *
     * @param Bundle object that contains saved state information.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        // @@ TODO -- you fill in here.
    	super.onCreate(savedInstanceState);
    	
        // Get the URL associated with the Intent data.
        // @@ TODO -- you fill in here.
    	url = getIntent().getData();
//    	url = getIntent().getData().toString();

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        // @@ TODO -- you fill in here using the Android "HaMeR"
        // concurrency framework.  Note that the finish() method
        // should be called in the UI thread, whereas the other
        // methods should be called in the background thread.
    	DownloadThread downloadThread = new DownloadThread();
        downloadThread.start();
    	
    }
    
    public class DownloadThread extends Thread {
		
		public void run() {
			
			Uri fileName = vandy.mooc.DownloadUtils.downloadImage(getBaseContext(), url);

            Intent returnIntent = new Intent();
            returnIntent.setData(fileName);
            setResult(RESULT_OK, returnIntent);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
		}


	}
}
