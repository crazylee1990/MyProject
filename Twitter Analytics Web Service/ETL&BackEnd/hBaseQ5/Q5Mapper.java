package hBaseQ5;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Q5Mapper extends Mapper<LongWritable, Text, Text, Text> {


	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
			
			// my table
			String[] tokens = new String(value.getBytes(), 0, value.getLength(), "UTF-8").split("\t", 2);
			
			// the row key is the user_id, following by score1, score2, score3, total score
			if (tokens.length == 2) {
				// ignore the records that does not have 5 records
				String userId = tokens[0];
				String valueStr = tokens[1];
				
				Text rowKey = new Text(userId);
				
//				String dateLoc = tokens[0];
//				String rank = tokens[3];
//				
//				String rowKeyStr = dateLoc +" "+ rank;
//				Text rowKey = new Text(rowKeyStr);
//				
//				String valueStr = tokens[1]+"\t"+tokens[2];
				Text outputVal = new Text(valueStr);
				context.write(rowKey, outputVal);
		}
		
	}

}