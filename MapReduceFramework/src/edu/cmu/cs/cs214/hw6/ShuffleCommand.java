package edu.cmu.cs.cs214.hw6;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;

import edu.cmu.cs.cs214.hw6.util.KeyValuePair;
import edu.cmu.cs.cs214.hw6.util.Log;



public class ShuffleCommand extends WorkerCommand{
	private static final long serialVersionUID = -5314076333098679665L;
	private static final String TAG = "ShuffleCommand";
	
	private final WorkerInfo hostWorker;
	private final List<String> mapResults;
	private final int numOfReduceWorkers;
	private final int recipient;
    
	/**
	 * @param hostWorker : the worker who now has the data for shuffle
	 * @param mapResults : the map results from previous phase
	 * @param numOfReduceWorkers : total number of reduce workers
	 * @param recipient : the recipient who is going collect this map results
	 */
    public ShuffleCommand(WorkerInfo hostWorker, List<String> mapResults, int numOfReduceWorkers, int recipient) {
		this.hostWorker = hostWorker;
		this.mapResults = mapResults;
    	this.numOfReduceWorkers = numOfReduceWorkers;
    	this.recipient = recipient;
	}
    
    @Override
    public void run() {
    	Socket socket = getSocket();
    	ObjectOutputStream output = null;
    	Scanner scanner = null;
    	try {
    		output = new ObjectOutputStream(socket.getOutputStream());
			for(String result: mapResults){
				try {
					FileInputStream input = new FileInputStream(result);
					scanner = new Scanner(input); 
					scanner.useDelimiter("\\W+");
					while (scanner.hasNext()) {
						String key = scanner.next().trim().toLowerCase();
						String value = scanner.next().trim();
						int hashValue = key.hashCode();
						/**handle the negative hash value**/
						if(hashValue < 0) hashValue = -hashValue;
						if(hashValue % numOfReduceWorkers == recipient){
							output.writeObject(new KeyValuePair(key, value));
						}
					}
				} catch (FileNotFoundException e) {
					Log.e(TAG,"Cannot find this file",e);
				}
			}
		} catch (IOException e) {
			Log.e(TAG, "Lost connection with reduce worker", e);
		} finally{
			if(scanner != null){
				scanner.close();
			}
			try {
				if(output != null){
					output.close();
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
    }
    
    public WorkerInfo getHostWorker() {
		return hostWorker;
	}
    
    public int getRecipiant() {
		return recipient;
	}
    
}
