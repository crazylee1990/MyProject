package edu.cmu.cs.cs214.hw6.plugin.wordprefix;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import edu.cmu.cs.cs214.hw6.Emitter;
import edu.cmu.cs.cs214.hw6.MapTask;

/**
 * The map task for a word-prefix map/reduce computation.
 */
public class WordPrefixMapTask implements MapTask {
    private static final long serialVersionUID = 1243656546543345632L;

    @Override
    public void execute(InputStream in, Emitter emitter) throws IOException{
    	Scanner scanner = new Scanner(in);
        scanner.useDelimiter("\\W+");
        while (scanner.hasNext()) {
            String value = scanner.next().trim().toLowerCase();
            for(int i = 0; i < value.length(); ++i){
            	emitter.emit(value.substring(0, i+1), value);
            }
        }
        scanner.close();
    }

}
