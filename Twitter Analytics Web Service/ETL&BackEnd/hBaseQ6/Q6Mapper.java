package hBaseQ6;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Q6Mapper extends Mapper<LongWritable, Text, Text, Text> {


	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
			
			// my table
			String[] tokens = new String(value.getBytes(), 0, value.getLength(), "UTF-8").split("\t", 2);
			
			// the row key is the user_id, following by photos and total photos
			if (tokens.length == 2) {
				// ignore the records that does not have 5 records
				String userId = tokens[0];
				String userId10 = String.format("%010d", Long.parseLong(userId));
				String valueStr = tokens[1];
				
				Text rowKey = new Text(userId10);
				
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