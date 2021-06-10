package vandy.mooc;

import java.util.concurrent.CyclicBarrier;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

/**
 * @class PlayPingPong
 *
 * @brief This class uses elements of the Android HaMeR framework to
 *        create two Threads that alternately print "Ping" and "Pong",
 *        respectively, on the display.
 */
public class PlayPingPong implements Runnable {

    /**
     * PH: Debugging tag used by the Android logger.
     */
    private final String TAG = getClass().getSimpleName();

    /**
     * Keep track of whether a Thread is printing "ping" or "pong".
     */
    private enum PingPong {
        PING, PONG
    };

    /**
     * PH: Helper enum so that "this" knows what it's supposed to print.
     */
    private enum PingPongStrings {
 
    	STRING_PING("PING"), 
        STRING_PONG("PONG");
        
        private final String typeString;
        
        /**
         * @param typeString
         */
        private PingPongStrings(final String type) {
            this.typeString = type;
        }

        /**
         * Override to access the string value.
         */
        @Override
        public String toString() {
            return typeString;
        }

    };

    /**
     * Number of iterations to run the ping-pong algorithm.
     */
    private final int mMaxIterations;

    /**
     * The strategy for outputting strings to the display.
     */
    private final OutputStrategy mOutputStrategy;

    /**
     * Define a pair of Handlers used to send/handle Messages via the
     * HandlerThreads.
     */
    // @@ TODO - you fill in here.
    
    // PH: Use an array of Handlers based on the size of the enum.
    Handler[] mArrayHandlers = new Handler[PingPong.values().length];

    /**
     * Define a CyclicBarrier synchronizer that ensures the
     * HandlerThreads are fully initialized before the ping-pong
     * algorithm begins.
     */
    // @@ TODO - you fill in here.
    
    // PH: Create our barrier using the size of the array of Handlers.
    CyclicBarrier mBarrier = new CyclicBarrier( mArrayHandlers.length );
    
    /**
     * Implements the concurrent ping/pong algorithm using a pair of
     * Android Handlers (which are defined as an array field in the
     * enclosing PlayPingPong class so they can be shared by the ping
     * and pong objects).  The class (1) extends the HandlerThread
     * superclass to enable it to run in the background and (2)
     * implements the Handler.Callback interface so its
     * handleMessage() method can be dispatched without requiring
     * additional subclassing.
     */
    class PingPongThread extends HandlerThread implements Handler.Callback {
        /**
         * Keeps track of whether this Thread handles "pings" or
         * "pongs".
         * 
         * PH: Added the extra string type
         */
        private PingPong mMyType;
        private String mMyTypeString;

        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        /**
         * PH: Can this thread send the message?  This is a clumsy
         * way to prevent the PONG thread from sending an 
         * unnecessary last message.
         */
        private boolean sendMessageOK;

        
        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
        	
            // @@ TODO - you fill in here.
        	
        	// PH: Initialize the type field and what it needs to print.
        	mMyType = myType;
        	
        	if (mMyType == PingPong.PING ) {
        		mMyTypeString = PingPongStrings.STRING_PING.toString();
        	}
        	else {
        		mMyTypeString = PingPongStrings.STRING_PONG.toString();        		
        	}
        }

        /**
         * This hook method is dispatched after the HandlerThread has
         * been started.  It performs ping-pong initialization prior
         * to the HandlerThread running its event loop.
         */
        @Override    
        protected void onLooperPrepared() {
            // Create the Handler that will service this type of
            // Handler, i.e., either PING or PONG.
            // @@ TODO - you fill in here.
        	
        	// PH: Use the given type to set the new Handler
        	// at the correct index.
        	mArrayHandlers[ mMyType.ordinal() ] = new Handler(this);

            try {
            	// PH: Changed wording here as suggested in the forums...
            	// Use the CyclicBarrier defined in the enclosing PlayPingPong class to
                // wait for both Threads to initialize their Handlers.
                // @@ TODO - you fill in here.
            	
            	mBarrier.await();

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Start the PING_THREAD first by (1) creating a Message
            // where the PING Handler is the "target" and the PONG
            // Handler is the "obj" to use for the reply and (2)
            // sending the Message to the PING_THREAD's Handler.
            
            // @@ TODO - you fill in here.

            if (mMyType == PingPong.PING) {
            	
            	Message pingMsg = mArrayHandlers[ mMyType.ordinal() ].obtainMessage();

            	pingMsg.obj = mArrayHandlers[PingPong.PONG.ordinal()];
            	
            	mArrayHandlers[ mMyType.ordinal() ].sendMessage(pingMsg);
            }
        }

        /**
         * Hook method called back by HandlerThread to perform the
         * ping-pong protocol concurrently.
         */
        @Override
        public boolean handleMessage(Message reqMsg) {
            // Print the appropriate string if this thread isn't done
            // with all its iterations yet.
            // @@ TODO - you fill in here, replacing "true" with the
            // appropriate code.

    		Log.i(TAG, "Entered the handleMessage() method");
    		Log.i(TAG, String.format("handleMessage() mMyType%d count %d", mMyType.ordinal(), 
    				mIterationsCompleted) );

        	Handler reqHandler =  mArrayHandlers[ mMyType.ordinal() ];
        	
            if (mIterationsCompleted < mMaxIterations) {
            	            	
            	mIterationsCompleted ++;
            	mOutputStrategy.print(mMyTypeString + String.format(" (%d)\n", mIterationsCompleted) );
            	
            	sendMessageOK = true;

            } 
            else {
                // Shutdown the HandlerThread to the main PingPong
                // thread can join with it.
                // @@ TODO - you fill in here.
            	
            	// PH: We need to tell the looper to quit which causes the
            	// thread to quit.  

            	Log.i(TAG, "handleMessage() Time to quit.");
                
            	getLooper().quit();
                
                // PH: Check here prevents the PONG message
                // from sending an unnecessary response.

                if ( mMyType == PingPong.PING ) {
                	sendMessageOK = true;
                }
                else {
                	sendMessageOK = false;
                }
                
            }

            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            // @@ TODO - you fill in here.
            
            // PH: New target is the message's object.  Ideally, this bit
            // would be included in the "if" below, but this keeps code
            // with the TODO.
            
            Handler target = (Handler)reqMsg.obj;
            Message msg = target.obtainMessage();
            msg.obj = reqHandler;
            
            // Return control to the Handler in the other
            // HandlerThread, which is the "target" of the msg
            // parameter.
            // @@ TODO - you fill in here.
            
            // PH: Make sure the target still exists and that this 
            // handler is allowed to respond.  The check for
            // sendMessageOK eliminates the PONG threading sending
            // a message when the PING thread is gone ahead and died.
            // There should be a way to uses the CyclicBarrier here - see
            // commented out code below.

            if (target.getLooper().getThread().isAlive() && sendMessageOK ) {
            	
         		target.sendMessage(msg);
            }
            else {
            	if (!sendMessageOK) {
            		Log.i(TAG, "handleMessage() no reply message sent since we're done.");
            	}
            	else {
            		Log.i(TAG, "handleMessage() no reply message sent since target is dead.");
            	}
            }
            
            /**
             * PH: This has been a suggested solution which I've commented out
             * since it still means we're sending a message to a dead
             * thread - which might be fine, but I wanted to eliminate that.
             * Keeping this here for my reference only.....
             *             
            if (mIterationsCompleted >= mMaxIterations) {
            	
                try {
                	
                	Log.i(TAG, "handleMessage() Waiting at barrier.");
                	mBarrier.await();

                } 
                catch (Exception e) {
                    e.printStackTrace();
                }
            }*/

            return true;
        }

    }

    /**
     * Constructor initializes the data members.
     */
    public PlayPingPong(int maxIterations,
                        OutputStrategy outputStrategy) {
        // Number of iterations to perform pings and pongs.
        mMaxIterations = maxIterations;

        // Strategy that controls how output is displayed to the user.
        mOutputStrategy = outputStrategy;
    }

    /**
     * Start running the ping/pong code, which can be called from a
     * main() method in a Java class, an Android Activity, etc.
     */
    public void run() {
        // Let the user know we're starting. 
        mOutputStrategy.print("Ready...Set...Go!\n");
       
        // Create the ping and pong threads.
        // @@ TODO - you fill in here.

        PingPongThread pingThread = new PingPongThread(PingPong.PING);
        PingPongThread pongThread = new PingPongThread(PingPong.PONG);

        // Start ping and pong threads, which cause their Looper to
        // loop.
        // @@ TODO - you fill in here.
        
        pingThread.start();
        pongThread.start();

        // Use barrier synchronizer(s) to wait for all work to be done
        // before exiting play().
        
        // PH: And more clearly, use come kind of synchronization mechanism 
        // to wait for all work to be done before exiting play().
        
        // @@ TODO - you fill in here with some type of barrier
        // synchronization mechanism (which needn't be a
        // CyclicBarrier..)
        
        try {
            Log.i(TAG, "run() Waiting for threads to finish...");
            pingThread.join();
        }
        catch (java.lang.InterruptedException ex ) {
        	
        	Log.i(TAG, ex.getMessage());
        }
        

        // Let the user know we're done.
        mOutputStrategy.print("Done!");
    }
}
