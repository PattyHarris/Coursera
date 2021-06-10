package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import java.util.logging.Handler;

/**
 * An Activity that downloads an image, stores it in a local file on
 * the local device, and returns a Uri to the image file.
 */
public class DownloadImageActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    private Uri mUrl;

    private Handler mHandler;

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
        Intent dwnloadIntent = getIntent();

        // Get the URL associated with the Intent data.
        mUrl = dwnloadIntent.getData();
        Log.d(TAG, "Got URL '" + mUrl + "'");

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        new Thread( new Runnable() {
            Uri mUri;
            @Override
            public void run() {
                mUri = DownloadUtils.downloadImage(DownloadImageActivity.this, DownloadImageActivity.this.mUrl);

                Log.d(TAG, "Got image!");

                DownloadImageActivity.this.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        Intent imgIntent = new Intent();
                        if( mUri  != null ) {
                            imgIntent.setData(mUri);
                            DownloadImageActivity.this.setResult(Activity.RESULT_OK, imgIntent);
                        }
                        else {
                            DownloadImageActivity.this.setResult(Activity.RESULT_CANCELED, imgIntent);
                        }
                        finish();
                    }

                });
            }
        }).start();



    }
}
