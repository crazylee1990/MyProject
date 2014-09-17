package edu.cmu.cs.cs214.hw6;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cmu.cs.cs214.hw6.util.Log;
import edu.cmu.cs.cs214.hw6.util.StaffUtils;

/**
 * This class represents the "master server" in the distributed map/reduce
 * framework. The {@link MasterServer} is in charge of managing the entire
 * map/reduce computation from beginning to end. The {@link MasterServer}
 * listens for incoming client connections on a distinct host/port address, and
 * is passed an array of {@link WorkerInfo} objects when it is first initialized
 * that provides it with necessary information about each of the available
 * workers in the system (i.e. each worker's name, host address, port number,
 * and the set of {@link Partition}s it stores). A single map/reduce computation
 * managed by the {@link MasterServer} will typically behave as follows:
 * 
 * <ol>
 * <li>Wait for the client to submit a map/reduce task.</li>
 * <li>Distribute the {@link MapTask} across a set of "map-workers" and wait for
 * all map-workers to complete.</li>
 * <li>Distribute the {@link ReduceTask} across a set of "reduce-workers" and
 * wait for all reduce-workers to complete.</li>
 * <li>Write the final key/value pair results of the computation back to the
 * client.</li>
 * </ol>
 */
public class MasterServer extends Thread {
	private static final String TAG = "Master";

	private final ExecutorService masterExecutor;
	private final int mPort;
	private final List<WorkerInfo> mWorkers;
	private List<Socket> allClients;
	private Map<String, Boolean> isPartHandled;
	
	
	/** Create at most one thread per available processor on this machine. */
	private static final int MAX_POOL_SIZE = Runtime.getRuntime().availableProcessors();

	/**
	 * Constructor
	 * @param masterPort - the port master server to listen on
	 * @param workers - all available workers to finish this task
	 */
	public MasterServer(int masterPort, List<WorkerInfo> workers) {
		masterExecutor = Executors.newSingleThreadExecutor();
		mPort = masterPort;
		mWorkers = workers;
		isPartHandled = new HashMap<String, Boolean>();
		allClients = Collections.synchronizedList(new ArrayList<Socket>());
		init(this.mWorkers);
	}

	/**
	 * Collect all partitions in all workers
	 * @param workers
	 */
	private void init(List<WorkerInfo> workers) {
		Map<String, Partition> data = new HashMap<String, Partition>();

		for (WorkerInfo info : workers) {
			for (Partition partition : info.getPartitions()) {
				if (!data.containsKey(partition.getPartitionName())) {
					data.put(partition.getPartitionName(),partition);
				}
			}
		}
		setPartUnhandled(data.keySet());
	}
	
	private void setPartUnhandled(Set<String> partitions){
		for(String key : partitions){
			isPartHandled.put(key, false);
		}
	}
	
	private boolean checkMapState(List<Future<Void>> mapResults,List<MapCallable> mapCallables){
		boolean mapState = true;
		for(int i = 0; i < mapResults.size(); i++){
			try {
				mapResults.get(i).get();
			} catch (ExecutionException e) {
				mapState = false;
				// clear the work done before
				mapCallables.get(i).reset();
				Log.e(TAG, "Map worker throws an exception", e);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Log.e(TAG, "Thread interrupted", e);
			}
		}
		return mapState;
	}

	@Override
	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(mPort);
		} catch (IOException e) {
			Log.e(TAG, "Cannot construct server socket on " + mPort + ".", e);
			return;
		}
		
		Log.i(TAG, "Listening for incoming tasks on port " + mPort + ".");

		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				// Handle the client's request on a background thread,
				// partition data and receive the immediate result and
				// shuffle and send reduce task again
				Log.i(TAG, "Handling " + allClients.size() + " requests in total");
				allClients.add(clientSocket);
				masterExecutor.execute(new TaskHandler(clientSocket));
			} catch (IOException e) {
				Log.e(TAG, "Catch IOException when connecting socket.", e);
				break;
			}
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			Log.e(TAG, "Catch IOException when closing server socket.", e);
		}
		masterExecutor.shutdown();
	}


	/**
	 * Task Handler to create a new thread to handle the map/reduce task
	 * @author Chao
	 *
	 */
	private class TaskHandler implements Runnable {
		private Socket mSocket;

		public TaskHandler(Socket clientSocket) {
			this.mSocket = clientSocket;
		}

		@Override
		public void run() {
			int numOfThread = Math.min(mWorkers.size(), MAX_POOL_SIZE);
			final ExecutorService mExecutor = Executors.newFixedThreadPool(numOfThread);
			
			int numOfFailWorkers = 0;
			try {
				
				List<MapCallable> mapCallables = new ArrayList<MapCallable>();
				List<ReduceCallable> reduceCallables = new ArrayList<ReduceCallable>();
				List<Future<String>> finalResult = null;
				List<String> report = new ArrayList<String>();
				

			
				// receive map/reduce tasks from client server
				ObjectInputStream in = new ObjectInputStream(mSocket.getInputStream());
				final MapTask mapTask = (MapTask) in.readObject();
				final ReduceTask reduceTask = (ReduceTask) in.readObject();
			
				for (WorkerInfo w : mWorkers) {
					mapCallables.add(new MapCallable(mapTask, w));
				}

				boolean finishFlag = false;
				// keep doing the job until it is done
				while(!finishFlag){
					List<Future<Void>> mapResults = new ArrayList<Future<Void>>();
					// clear final result and reduce workers
					report.clear();
					reduceCallables.clear();
					int availableWorkers = 0, index = 0;
					for (MapCallable mapWorker : mapCallables)
						if(mapWorker.isAvailable())
							availableWorkers++;
					
					Log.i(TAG, "Available worker server: " + availableWorkers);
					
					// handle map failures
					while(true){
						try {
							// execute all the map tasks in background threads
							mapResults = mExecutor.invokeAll(mapCallables);
							
							// check the map state
							boolean mapState = checkMapState(mapResults,mapCallables);
							
							if(mapState) {
								// check if all partitions are handled
								for(Map.Entry<String, Boolean> entry : isPartHandled.entrySet()){
									// if found some job is not done, it means
									// too many worker server fails, you can't finish 
									// the task correctly anyway, just set finish to true
									if(!entry.getValue()) {
										finishFlag = true;
										Log.i(TAG, "Not enough worker to finish the task");
									}
								}
								break;
							}
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							Log.e(TAG, "Thread interrupted", e);
						} 
					}
					
					if(finishFlag)
						break; // break the outer loop

					Log.i(TAG, "Done map task, do shuffle and reduce task.");
					
					numOfFailWorkers = mWorkers.size() - availableWorkers;
					if(numOfFailWorkers > 0)
						Log.i(TAG, "Some worker fails: " + numOfFailWorkers);
					
					for (MapCallable mapWorker : mapCallables)
						if(mapWorker.isAvailable()){
							reduceCallables.add(new ReduceCallable(availableWorkers,
								index, reduceTask, mapWorker.getWorker(), mapCallables));
							index ++;
						}
					
					try {
						// execute all the reduce tasks in background threads
						finalResult = mExecutor.invokeAll(reduceCallables);
						
						// check the reduce job
						boolean reduce_flag = true;
						for(Future<String> fut : finalResult){
							try {
								fut.get();
							} catch (ExecutionException e) {
								reduce_flag = false;
								Log.e(TAG, "Reduce worker throws an exception", e);
							}
						}
						if(reduce_flag) // if all reduce jobs are done correctly
							finishFlag = true;
						else{
							// check connection
							for(MapCallable mapWorker : mapCallables){
								mapWorker.checkConnection();
							}
							Log.i(TAG, "Redo the task");
						}
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
						Log.e(TAG, "Thread interrupted", e);	
					}
				}
				
				
				// get the final result
				if(finalResult != null){
					for (Future<String> f : finalResult)
						try {
							report.add(f.get());
						} catch (InterruptedException e1) {
							// already handle before, ignore it here
						} catch (ExecutionException e2) {
							// TODO: handle exception
						}
				}
				
				Log.i(TAG, "Done reduce task, write report back to client.");
				
				// clear the job
				for(Map.Entry<String, Boolean> entry : isPartHandled.entrySet())
					isPartHandled.put(entry.getKey(), false);
				ObjectOutputStream out = new ObjectOutputStream(mSocket.getOutputStream());
				out.writeObject(report);

			} catch (IOException e) {
				Log.e(TAG, "Connection lost.", e);
			} catch (ClassNotFoundException e) {
				Log.e(TAG, "Received invalid task from client.", e);
			} finally {
				mExecutor.shutdown();
				try {
					allClients.remove(mSocket);
					mSocket.close();
				} catch (IOException e) {
					// Ignore because we're about to exit anyway.
				}
			}
		}
	}

	private class MapCallable implements Callable<Void> {
		private final MapTask mapTask;
		private final WorkerInfo mWorker;
		private boolean available;
		private List<String> mapResults;

		public MapCallable(MapTask task, WorkerInfo worker) {
			mapTask = task;
			mWorker = worker;
			available = true;
			mapResults = new ArrayList<String>();
		}

		public boolean isAvailable() {
			return available;
		}

		public List<String> getMapResults() {
			return mapResults;
		}
		
		public void reset() {
			Log.i(TAG, mWorker.getName() + "has been reset!");
			available = false;
			mapResults.clear();
		}

		public void checkConnection() {
			if(!available){
				return;
			}
			Socket socket = null;
			try {
				socket = new Socket(mWorker.getHost(),mWorker.getPort());
			} catch (IOException e) {
				reset();
			} finally{
				try {
					if(socket != null){
						socket.close();
					}
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
		/**
		 * Returns the {@link WorkerInfo} object that provides information
		 * about the worker that this callable task is responsible for
		 * interacting with.
		 */
		public WorkerInfo getWorker() {
			return mWorker;
		}

		@Override
		public Void call() throws Exception {
			if(!available){
				return null;
			}
			for(Partition p: mWorker.getPartitions()){
				String pName = p.getPartitionName();
				if(!isPartHandled.get(pName)){
					/**make sure that each partition is only handled once**/
					isPartHandled.put(pName,true);
					mapResults.add(handleSinglePartition(p));
				}
			}
			return null;
		}
		
		private String handleSinglePartition(Partition p) throws Exception{
			Socket socket = null;
			ObjectOutputStream out = null;
			ObjectInputStream in = null;
            try {
                // Establish a connection with the worker server.
                socket = new Socket(mWorker.getHost(), mWorker.getPort());

                // Create the ObjectOutputStream and write the WorkerCommand
                // over the network to be read and executed by a WorkerServer.
                out = new ObjectOutputStream(socket.getOutputStream());
                out.writeObject(new ExecuteMapCommand(mapTask, p));

                // Note that we instantiate the ObjectInputStream AFTER writing
                // the object over the objectOutputStream. Initializing it
                // immediately after initializing the ObjectOutputStream (but
                // before writing the object) will cause the entire program to
                // block, as described in this StackOverflow answer:
                // http://stackoverflow.com/q/5658089/844882:
                in = new ObjectInputStream(socket.getInputStream());

                // Read and return the worker's final result.
                return (String) in.readObject();
            } catch (Exception e) {
                // Catch, log, and re-throw the exception. Always make sure you
                // log your exceptions, or else debugging your code will be a
                // nightmare!
                Log.e(TAG, "Warning! Received exception while interacting with worker.", e);
                throw e;
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                    if (out != null){
                    	out.close();
                    }
                    if(in != null){
                    	in.close();
                    }
                } catch (IOException e) {
                    // Ignore because we're about to exit anyway.
                }
            }
		}
	}

	private static class ReduceCallable implements Callable<String> {
		private final ReduceTask reduceTask;
		private final WorkerInfo mWorker;
		private final List<ShuffleCommand> shuffleCommands;


		public ReduceCallable(int availableNum, int recipiant, ReduceTask task, WorkerInfo workerInfo, List<MapCallable> mapWorkers) {
			this.reduceTask = task;
			this.mWorker = workerInfo;
			shuffleCommands = new ArrayList<ShuffleCommand>();
			initShuffleCommand(mapWorkers,availableNum,recipiant);
           
        }
		
		private void initShuffleCommand(List<MapCallable> mapWorkers,int availableNum, int recipiant) {
			for(MapCallable m: mapWorkers){
				if(m.isAvailable()){
					ShuffleCommand sc = new ShuffleCommand(m.getWorker(), m.getMapResults(),availableNum, recipiant);
					shuffleCommands.add(sc);
				}
			}
		}

		@Override
		public String call() throws Exception {
			Socket socket = null;
			ObjectOutputStream out = null;
			ObjectInputStream in = null;
			try {
				// Establish a connection with the worker server.
				socket = new Socket(mWorker.getHost(), mWorker.getPort());

				// Create the ObjectOutputStream and write the WorkerCommand
				// over the network to be read and executed by a WorkerServer.
				out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(new ExecuteReduceCommand(reduceTask,mWorker,shuffleCommands));
			
				// Note that we instantiate the ObjectInputStream AFTER writing
				// the object over the objectOutputStream. Initializing it
				// immediately after initializing the ObjectOutputStream (but
				// before writing the object) will cause the entire program to
				// block, as described in this StackOverflow answer:
				// http://stackoverflow.com/q/5658089/844882:
				in = new ObjectInputStream(socket.getInputStream());

				// Read and return the worker's final result.
				return (String) in.readObject();
			} catch (Exception e) {
				// Catch, log, and re-throw the exception. Always make sure you
				// log your exceptions, or else debugging your code will be a
				// nightmare!
				Log.e(TAG,"Warning! Received exception while interacting with worker.",e);
				throw e;
			} finally {
				try {
					if (socket != null) {
                        socket.close();
                    }
                    if (out != null){
                    	out.close();
                    }
                    if(in != null){
                    	in.close();
                    }
				} catch (IOException e) {
					// Ignore because we're about to exit anyway.
				}
			}
		}
	}

	/********************************************************************/
	/***************** STAFF CODE BELOW. DO NOT MODIFY. *****************/
	/********************************************************************/

	/**
	 * Starts the master server on a distinct port. Information about each
	 * available worker in the distributed system is parsed and passed as an
	 * argument to the {@link MasterServer} constructor. This information can be
	 * either specified via command line arguments or via system properties
	 * specified in the <code>master.properties</code> and
	 * <code>workers.properties</code> file (if no command line arguments are
	 * specified).
	 */
	public static void main(String[] args) {
		StaffUtils.makeMasterServer(args).start();
	}

}
