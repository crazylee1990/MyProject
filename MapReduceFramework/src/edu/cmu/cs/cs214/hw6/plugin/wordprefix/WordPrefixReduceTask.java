package edu.cmu.cs.cs214.hw6.plugin.wordprefix;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.cmu.cs.cs214.hw6.Emitter;
import edu.cmu.cs.cs214.hw6.ReduceTask;

/**
 * The reduce task for a word-prefix map/reduce computation.
 */
public class WordPrefixReduceTask implements ReduceTask {
    private static final long serialVersionUID = 123246578654535698L;

    @Override
    public void execute(String key, Iterator<String> values, Emitter emitter) throws IOException {
    	/**init the values**/
    	String keyword = "";
        int max = 0;
        Map<String, Integer> counts = new HashMap<String, Integer>();
        // collect data
        while(values.hasNext()){
        	String word = values.next();
        	if(counts.containsKey(word))
        		counts.put(word, counts.get(word) + 1);
        	else
        		counts.put(word, 1);
        }
        
       
        for(Map.Entry<String, Integer> entry : counts.entrySet()){
        	if( entry.getValue() > max) {
        		max = entry.getValue();
        		keyword = entry.getKey();
        	}
        }
        emitter.emit(key, keyword);
    }

}
