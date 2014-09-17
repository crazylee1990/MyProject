package edu.cmu.cs.cs214.hw6;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import edu.cmu.cs.cs214.hw6.util.Log;

/**
 * A helpful class that write a pair out to the file 
 * @author Chao
 *
 */
public class EmitterImpl implements Emitter{
    private static final String TAG ="Emitter";
	private BufferedWriter output;
	
    /**the format of the output*/
	private final String seperation;

	/**
	 * Constructor
	 * @param path : the path of the file to emit
	 * @param seperation : the format of the output. For example, the map output can be <Hello,1>
	 * @throws IOException : handle IOException for FileWriter
	 */
	public EmitterImpl(String path, String seperation) throws IOException {
		FileWriter file = new FileWriter(path);
		output = new BufferedWriter(file);
		this.seperation = seperation;
	}
	
	@Override
	public void emit(String key, String value) throws IOException {
		try{
			String context = key + seperation + value + "\n";
			output.write(context);
		} catch (IOException e){
			Log.e(TAG, "I/O error while emitting key-value pair.", e);
		}
	}

	@Override
	public void close() throws IOException {
		if(output != null){
			output.close();
		}
	}
	
}
 