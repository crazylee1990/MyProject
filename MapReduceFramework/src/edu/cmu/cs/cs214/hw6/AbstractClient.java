package edu.cmu.cs.cs214.hw6;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import edu.cmu.cs.cs214.hw6.plugin.wordcount.WordCountClient;
import edu.cmu.cs.cs214.hw6.plugin.wordprefix.WordPrefixClient;
import edu.cmu.cs.cs214.hw6.util.Log;

/**
 * An abstract client class used primarily for code reuse between the
 * {@link WordCountClient} and {@link WordPrefixClient}.
 */
public abstract class AbstractClient {
	private static final String TAG = "Client";
	
    private final String mMasterHost;
    private final int mMasterPort;

    /**
     * The {@link AbstractClient} constructor.
     *
     * @param masterHost The host name of the {@link MasterServer}.
     * @param masterPort The port that the {@link MasterServer} is listening on.
     */
    public AbstractClient(String masterHost, int masterPort) {
        mMasterHost = masterHost;
        mMasterPort = masterPort;
    }

    protected abstract MapTask getMapTask();

    protected abstract ReduceTask getReduceTask();

    public void execute() {
        final MapTask mapTask = getMapTask();
        final ReduceTask reduceTask = getReduceTask();

        // TODO: Submit the map/reduce task to the master and wait for the task
        // to complete.
        Socket socket = null;
        try{
        	// Establish a connection with the master server.
        	socket = new Socket(mMasterHost,mMasterPort);

            // Create the ObjectOutputStream and write the map/reduce task
            // over the network to be read and executed
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(mapTask);
            out.writeObject(reduceTask);
            // Note that we instantiate the ObjectInputStream AFTER writing
            // the object over the objectOutputStream. Initializing it
            // immediately after initializing the ObjectOutputStream (but
            // before writing the object) will cause the entire program to
            // block, as described in this StackOverflow answer:
            // http://stackoverflow.com/q/5658089/844882:
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            // here receive some result****************
            @SuppressWarnings("unchecked")
			List<String> report = (List<String>) in.readObject();
            System.out.println("The result is stored at location :");
            for(String res : report)
            	System.out.println(res);
        }catch(ClassNotFoundException e){
        	Log.e(TAG, "Master server send the wrong object", e);
    	}catch(IOException e){
        	Log.e(TAG, "Can't establish a connection to Master Server", e);
        }finally{
        	try{
        		if(socket != null)
        			socket.close();
        	}catch(IOException e){
        		// Ignore because we're about to exit anyway.
        	}
        }
    }

}
