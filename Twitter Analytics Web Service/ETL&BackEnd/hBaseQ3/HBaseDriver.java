package hBaseQ3;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class HBaseDriver {
	
	private final static String TABLE_NAME = "q3Table";
	private static String MASTER_IP;

	public static void main(String[] args) throws Exception {
		MASTER_IP = args[2];
		Configuration conf = new Configuration();
		conf.set("mapred.job.tracker", MASTER_IP+":9001");
		conf.set("mapred.task.timeout", "1200000");
		conf.set("hbase.zookeeper.quorum", MASTER_IP);
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		
		
		conf.set(TableOutputFormat.OUTPUT_TABLE, TABLE_NAME);
		
		Job job = new Job(conf, "HBaseImportQ3");
		job.setJarByClass(HBaseDriver.class);

		job.setMapperClass(HBaseMapper.class);
		job.setReducerClass(HBaseReducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TableOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
