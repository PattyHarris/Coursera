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
         */
        private PingPong mMyType;
 
        /**
         * Number of iterations completed thus far.
         */
        private int mIterationsCompleted;

        
        /**
         * Constructor initializes the superclass and type field
         * (which is either PING or PONG).
         */
        public PingPongThread(PingPong myType) {
        	super(myType.toString());
        	
            // @@ TODO - you fill in here.
        	
        	// PH: Initialize the type field.

        	mMyType = myType;
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
    		Log.i(TAG, String.format("handleMessage() mMyType is %s count %d", mMyType.name(), 
    				mIterationsCompleted) );

        	Handler reqHandler =  mArrayHandlers[ mMyType.ordinal() ];
        	
            if (mIterationsCompleted < mMaxIterations) {
            	
            	// PH: The number of iterations is zero-based - we'll increment it after
            	// we've sent our message - that allows us to send one last message even
            	// though we've quit.
            	
            	mOutputStrategy.print(mMyType.name() + String.format(" (%d)", mIterationsCompleted + 1) );

            } 
            else {
           
                // Shutdown the HandlerThread to the main PingPong
                // thread can join with it.
                // @@ TODO - you fill in here.
            	
            	// PH: We need to tell the looper to quit which causes the
            	// thread to quit.  

            	Log.i(TAG, "handleMessage() Time to quit.");
                
            	getLooper().quit();
                                
            }

            // Create a Message that contains the Handler as the
            // reqMsg "target" and our Handler as the "obj" to use for
            // the reply.
            // @@ TODO - you fill in here.
            
            // PH: New target is the message's object.  Ideally, this bit
            // would be included in the "if" below, but this keeps code
            // with the TODO.  The target "may" be null.  The first sender
            // to hit the limit quits and sends an empty message to the
            // receiver.
            
            Handler target = (Handler)reqMsg.obj;
            Message msg = null;
            
            if ( target != null ) {
	            msg = target.obtainMessage();
	            msg.obj = reqHandler;
            }
            
            // Return control to the Handler in the other
            // HandlerThread, which is the "target" of the msg
            // parameter.
            // @@ TODO - you fill in here.
            
            // PH: Make sure the target still exists and that this 
            // handler is allowed to respond.  An empty message is used
            // to signal to the recipient, that the sender is done.
            // There should be a way to uses the CyclicBarrier here - see
            // commented out code below.

            if ( msg != null ) {
            	
				// PH: Make sure the target is around.
            	if ( target.getLooper().getThread().isAlive() ) {
            	
                    if (mIterationsCompleted >= mMaxIterations) {
                    	
                    	// PH: Using "1" here to indicate success - forums indicate one should
                    	// use 0 for failure, 1 for success.  Fine.
                    	Log.i(TAG, "handleMessage() Sending empty message.");
                    	target.sendEmptyMessage(1);
                    }
                    else {
                    	target.sendMessage(msg);
                    }
            	}
                else {
            		Log.i(TAG, "handleMessage() No reply message sent since target is dead.");
                }
            }
            
            // PH: Incremented the count now.  This location helps keep the if/else checks
            // a little cleaner and the skeleton generally intact.
            
        	mIterationsCompleted ++;
            return true;

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
        mOutputStrategy.print("Ready...Set...Go!");
       
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
            pongThread.join();
        }
        catch (java.lang.InterruptedException ex ) {
        	
        	Log.i(TAG, ex.getMessage());
        }
        

        // Let the user know we're done.
        mOutputStrategy.print("Done!");
    }
}
