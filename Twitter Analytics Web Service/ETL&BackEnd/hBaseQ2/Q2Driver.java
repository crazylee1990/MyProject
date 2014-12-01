package hBaseQ2;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q2Driver {
	
	private final static String TABLE_NAME = "q2Table";
	private final static String MASTER_IP = "172.31.39.25";

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("mapred.job.tracker", MASTER_IP+":9001");
		conf.set("mapred.task.timeout", "600000"); // enlarge the time out time
		conf.set("hbase.zookeeper.quorum", MASTER_IP);
		conf.set("hbase.zookeeper.property.clientPort", "2181");
		
		
		conf.set(TableOutputFormat.OUTPUT_TABLE, TABLE_NAME);
		Job job = new Job(conf, "HBaseImport");
		job.setJarByClass(Q2Driver.class);

		job.setMapperClass(Q2Mapper.class);
		job.setReducerClass(Q2Reducer.class);

		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);

		job.setInputFormatClass(TextInputFormat.class);
		job.setOutputFormatClass(TableOutputFormat.class);

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
