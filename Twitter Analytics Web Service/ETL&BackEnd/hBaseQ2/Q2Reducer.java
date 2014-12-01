package hBaseQ2;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

public class Q2Reducer extends TableReducer<Text, Text, NullWritable> {

	private final byte[] CL_FAMILY = Bytes.toBytes("q2");
	private final byte[] TWEET_COL_NAME = Bytes.toBytes("tweetId");
	private final byte[] TEXT_COL_NAME =  Bytes.toBytes("text");
	private final byte[] SCORE_COL_NAME = Bytes.toBytes("score");
	
	private StringBuilder sb = new StringBuilder();

	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		String value = "";
		while (values.iterator().hasNext()) {
			// add the row key
			String keyStr = new String(key.getBytes(), 0, key.getLength(), "UTF-8");
			Put put = new Put(Bytes.toBytes(keyStr));
			
			Text valueText = values.iterator().next();
			value = new String(valueText.getBytes(), 0, valueText.getLength(), "UTF-8");
			
			//Put put = new Put(Bytes.toBytes(key.toString()));
			//value = values.iterator().next().toString();
			if (value != null && !"".equals(value)) {
				// parse the String into value string into three tokens
				String[] tokens = value.toString().split("\t");
				if (tokens.length == 3) {
					String tweetId = tokens[0];
					String text = tokens[1];
					String score = tokens[2];

					// add the columns
					put.add(CL_FAMILY, TWEET_COL_NAME, Bytes.toBytes(tweetId));
					put.add(CL_FAMILY, TEXT_COL_NAME, Bytes.toBytes(text));
					put.add(CL_FAMILY, SCORE_COL_NAME, Bytes.toBytes(score));

					context.write(NullWritable.get(), put);

				} else {
					System.out.println(value.toString());
				}

			}
		}

	}

}