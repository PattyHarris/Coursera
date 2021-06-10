package vandy.mooc;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

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
        super.onCreate(savedInstanceState);

        // Get the URL associated with the Intent data.
        final Uri uri = getIntent().getData();

        // Download the image in the background, create an Intent that
        // contains the path to the image file, and set this as the
        // result of the Activity.

        final Handler handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Uri uri = (Uri) msg.obj;

                if (uri == null) {
                    setResult(RESULT_CANCELED);
                } else {
                    setResult(RESULT_OK, new Intent(null, uri));
                }

                finish();
                return true;
            }
        });

        new Thread() {
            public void run() {
                Uri downloadedUri = DownloadUtils.downloadImage(DownloadImageActivity.this, uri);
                Message message = new Message();
                message.obj = downloadedUri;
                handler.sendMessage(message);
            }
        }.start();
    }
}
