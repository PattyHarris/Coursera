Assignment 3 Design Notes

There are 6 Java files included in this project:
1. MainActivity.java: the main entry into the application
2. DownloadImageActivity.java: The download image activity class.  This class instantiate the
DownloadTaskFragment which in turn starts the AsyncTask to download the image.
3. DownloadTaskFragment.java: A retrain fragment who's primary job is to start the DownloadImageAsyncTask, 
an inner class contained in this file.
4. FilterImageActivity.java: The filter image activity class.  This class instantiates the FilterImageFragment
which in turn starts the AsyncTask to filter the image.
5. FilterImageFragment.java: A retain fragment who's primary job is to start the FilterImageAsyncTask,
an inner class contained in this file.

Design Summary:

There are 2 activities that are started from MainActivity.  The first activity is the DownloadImageActivity which
is used to download the image. When it returns, MainActivity starts the filter process using the FilterImageActivity.
On successful return from the FilterImageActivity, MainActivity shows the image in the gallery.

Testing:

The application was tested using a Galaxy Nexus, API 22, with Memory Options: RAM 1024, VM Heap: 512

Orientation: Using CTRL+F12 to rotate the emulator.
Using setRetainInstance(true) maintains state during these changes.

Back button: 
I needed a way to cancel the spinner and relay this information back up the chain of activities.
This was problematic.  The solution used here was to catch the "cancel" from the ProgressDialog
in the containing activity and then set the result in the intent such that MainActivity could 
call it's super.onBackPressed.  I'm sure there's a better way.

Home button:
This was again using the setRetainInstance(true) mechanism to maintain state.  To make sure that
the MainActivity was "active" before showing the gallery, I track whether the MainActivity is 
paused, and if so, what until resume to show the gallery.

There is lots of logging.