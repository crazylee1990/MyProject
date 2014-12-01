package hBaseQ4;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Mapper.Context;

/**
 * Use the date and loc as the row key, and rank as qualifier
 * @author Ruixi Deng
 *
 */
public class Q4Mapper extends Mapper<LongWritable, Text, Text, Text> {


	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
			
			// my table
			String[] tokens = new String(value.getBytes(), 0, value.getLength(), "UTF-8").split("\t");
			
			// the row key is the user_id plus the tweet_time
			if (tokens.length == 4) {
				// ignore the records that does not have 5 records
				String dateLoc = tokens[0];
				int rankInt = Integer.parseInt(tokens[3]);
				String rank = String.format("%05d", rankInt);
				
				String rowKeyStr = dateLoc +" "+ rank;
				Text rowKey = new Text(rowKeyStr);
				
				String valueStr = tokens[1]+"\t"+tokens[2];
				Text outputVal = new Text(valueStr);
				context.write(rowKey, outputVal);
		}
		
	}

}