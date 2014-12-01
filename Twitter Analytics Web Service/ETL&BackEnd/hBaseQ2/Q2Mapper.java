package hBaseQ2;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Q2Mapper extends Mapper<LongWritable, Text, Text, Text> {


	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		// my table
		//String[] tokens = value.toString().split("\t");
		String[] tokens = new String(value.getBytes(), 0, value.getLength(), "UTF-8").split("\t");
		
		// the row key is the user_id plus the tweet_time
		if (tokens.length == 5) {
			// ignore the records that does not have 5 records
			String userId = tokens[0];
			String time = tokens[1];
			
			String rowKeyStr = userId+" "+time;
			Text rowKey = new Text(rowKeyStr);
			
			String valueStr = tokens[2]+"\t"+tokens[3]+"\t"+tokens[4];
			Text outputVal = new Text(valueStr);
			context.write(rowKey, outputVal);
		}
		
	}

}