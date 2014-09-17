package edu.cmu.cs.cs214.hw6;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cmu.cs.cs214.hw6.util.KeyValuePair;
import edu.cmu.cs.cs214.hw6.util.Log;
import edu.cmu.cs.cs214.hw6.util.WorkerStorage;


public class ExecuteReduceCommand extends WorkerCommand{

	private static final long serialVersionUID = -5314076333098679666L;
	private static final String TAG = "ExecuteReduceCommand";

	private final ReduceTask reduceTask;
	private final WorkerInfo mWorker;
	private final List<ShuffleCommand> shuffleCommands;
	private Map<String,List<String>> dataset;
	/** Create at most one thread per available processor on this machine. */
	private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();
	
	/**
	 * Constructor 
	 * @param reduceTask : the reduce that is going to execute
	 * @param mWorker : the worker who works on this task
	 * @param shuffleCommands : a list of shuffle commands
	 */
	public ExecuteReduceCommand(ReduceTask reduceTask,WorkerInfo mWorker,
			List<ShuffleCommand> shuffleCommands) {
		this.reduceTask = reduceTask;
		this.mWorker = mWorker;
		this.shuffleCommands = shuffleCommands;
		this.dataset = Collections.synchronizedMap
				(new HashMap<String, List<String>>());
	}
	
	private String getPath() {
		String path = WorkerStorage.getFinalResultsDirectory(mWorker.getName());
		path += "/" + mWorker.getName() + "_" + reduceTask.getClass().getSimpleName() + ".txt";
		return path;
	}
	
	@Override
	public void run() {
		ExecutorService mExecutor = Executors.newFixedThreadPool(MAX_POOL_SIZE);
		
		// do shuffle phase here, collect data
				List<ShuffleCallable> shuffleCallables = new ArrayList<ShuffleCallable>();
				for (ShuffleCommand cmd : shuffleCommands) {
					shuffleCallables.add(new ShuffleCallable(cmd));
				}
				
				try {
					// Executes shuffle tasks on background threads
					List<Future<Void>> shuffleResult = mExecutor.invokeAll(shuffleCallables);
					for(Future<Void> fut : shuffleResult){
						try {
							fut.get();
						}catch (ExecutionException e) {
							Log.e(TAG, "The shuffle phase fails",e);
							// just finish the reduce command
							// here we don't handle the shuffle failure
							// because since in this case the map workers are the same as
							// reduce workers, since the shuffle fails, a reduce worker
							// must also fail
							return; 
						}
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					Log.e(TAG, "Thread interrupted", e);
				}
				
				// record the path storing the intermediate results
				String path = getPath();

				Emitter emitter = null;
				try {
					emitter = new EmitterImpl(path, "/");

					// start executing reduce task on given data set
					for (Map.Entry<String, List<String>> entry : dataset.entrySet()) {
						Iterator<String> it = entry.getValue().iterator();
						reduceTask.execute(entry.getKey(), it, emitter);
					}
				} catch (IOException e) {
					Log.e(TAG, "Fails to create an buffered writer", e);
				} finally {
					// close the buffered writer
					try {
						if (emitter != null)
							emitter.close();
					} catch (IOException e) {
						// ignore it because we are about to exit
					}
				}

				
				Socket socket = getSocket();
				ObjectOutputStream out = null;
				try {
					// connect with master server
					out = new ObjectOutputStream(socket.getOutputStream());

					// send result back to master server
					out.writeObject(path);
				} catch (IOException e) {
					Log.e(TAG, "Lost connection with master server", e);
				} finally {
					mExecutor.shutdown();
					try {
						if (out != null)
							out.close();
					} catch (IOException e) {
						// ignore because we are about to exit
					}
				}
		
		
	}
	
	private class ShuffleCallable implements Callable<Void>,Serializable{
		private static final long serialVersionUID = 8698673242421527694L;
		private final WorkerInfo mapWorker;
		private final ShuffleCommand shuffleCommand;
		
		public ShuffleCallable(ShuffleCommand sc) {
			this.mapWorker = sc.getHostWorker();
			this.shuffleCommand = sc;
		}
		
		@Override
		public Void call() throws Exception {
			Socket socket = null;
			ObjectOutputStream outputStream = null;
			ObjectInputStream inputStream = null;
			
			try {
				socket = new Socket(mapWorker.getHost(),mapWorker.getPort());
				outputStream = new ObjectOutputStream(socket.getOutputStream());
				outputStream.writeObject(shuffleCommand);
				outputStream.flush();
				
				inputStream = new ObjectInputStream(socket.getInputStream());
				while(true){
					KeyValuePair pair = (KeyValuePair) inputStream.readObject();
					if(dataset.containsKey(pair.getKey())){
						dataset.get(pair.getKey()).add(pair.getValue());
					}else {
						List<String> newV = Collections.synchronizedList(new ArrayList<String>());
						newV.add(pair.getValue());
						dataset.put(pair.getKey(), newV);
					}
				}
				
			} catch (EOFException e) {
				return null;
			} finally {
				try {
					if(socket != null){
						socket.close();
					}
					if(outputStream != null){
						outputStream.close();
					}
					if(inputStream != null){
						inputStream.close();
					}
				} catch (IOException e2) {
					// ignore it because we are about to exit anyway
				}
				
			}
		}
	}
	
	
}
