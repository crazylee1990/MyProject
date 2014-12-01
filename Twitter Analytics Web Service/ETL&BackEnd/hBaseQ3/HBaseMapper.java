package hBaseQ3;

import java.io.IOException;
import java.util.Scanner;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class HBaseMapper extends Mapper<LongWritable, Text, Text, Text> {

	public void map(LongWritable key, Text value, Context context)
			throws IOException, InterruptedException {
		
		// my table
		
		String rowkeyStr = value.toString().split("\t")[0];
		Text rowkey = new Text(rowkeyStr);
		context.write(rowkey, value);
		
	}

}
