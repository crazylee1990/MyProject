package edu.cmu.cs.cs214.hw6;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.Iterator;

import edu.cmu.cs.cs214.hw6.util.Log;
import edu.cmu.cs.cs214.hw6.util.WorkerStorage;

/**
 * This class execute the map-task and output the results to corresponding file
 * @author Chao
 *
 */
public class ExecuteMapCommand extends WorkerCommand implements Serializable{
	private static final long serialVersionUID = 1235885302124332953L;
	private static final String TAG = "ExecuteMapCommand";

	/**the path of the this partition**/
	private final String partPath;
	private final MapTask mapTask;
	private final Partition partition;

	/**
	 * Constructor
	 * @param mapTask - the task that are going to be executed
	 * @param partition - the partition this task is going to handle
	 */
	public ExecuteMapCommand(MapTask mapTask, Partition partition) {
		this.mapTask = mapTask;
		this.partition = partition;
		this.partPath = getPartPath();
	}
	
	private String getPartPath() {
		StringBuilder sb = new StringBuilder();
		String pathString = WorkerStorage.getIntermediateResultsDirectory
				(partition.getWorkerName());
		sb.append(pathString);
		sb.append("/intermidiate_partition");
		sb.append(partition.getPartitionName());
		sb.append(".txt");
		return sb.toString();
	}

	@Override
	public void run() {
		// Get the socket to use to send results back to the client. Note that
        // the WorkerServer will close this socket for us, so we don't need to
        // worry about that.
        Socket socket = getSocket();
        Iterator<File> allIterator = partition.iterator();
        FileInputStream in = null;
        Emitter emitter = null;
        while(allIterator.hasNext()){
        	try {
        		emitter = new EmitterImpl(partPath,",");
        		// Opens a FileInputStream for the specified file, execute the task,
                // and close the input stream once we've calculated the result.
				in = new FileInputStream(allIterator.next());
				mapTask.execute(in, emitter);
			} catch (IOException e) {
				Log.e(TAG, "I/O error while executing task.", e);			
			} finally{
				try {
					if(in != null){
						in.close();
					}
				} catch (IOException e2) {
					Log.e(TAG, "Can not close the input stream");
				}
			}
        }
        try {
        	emitter.close();
		} catch (IOException e) {
			Log.e(TAG, "Cannot close the emiiter.", e);	
		}
        
        // Open an ObjectOutputStream to use to communicate with the client
        // that sent this command, and write the result back to the client.
        ObjectOutputStream out = null;
        try {
        	out = new ObjectOutputStream(socket.getOutputStream());
        	out.writeObject(partPath);
		} catch (IOException e) {
			Log.e(TAG, "Lost connection with master server", e);
		} finally{
			try {
				if(out != null){
					out.close();
				}
			} catch (IOException e2) {
				Log.e(TAG, "cannot close output stream", e2);
			}
		} 
	}
}
