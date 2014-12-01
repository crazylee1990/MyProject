package hBaseQ5;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;

public class Q5Reducer extends TableReducer<Text, Text, NullWritable> {

	private final byte[] CL_FAMILY = Bytes.toBytes("q");
	private final byte[] SCORE_1_NAME = Bytes.toBytes("1");
	private final byte[] SCORE_2_NAME =  Bytes.toBytes("2");
	private final byte[] SCORE_3_NAME =  Bytes.toBytes("3");
	private final byte[] TOTAL_SCORE_NAME =  Bytes.toBytes("t");
	
	
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {

		String value = "";
		while (values.iterator().hasNext()) {
			// add the row key
			String keyStr = new String(key.getBytes(), 0, key.getLength(), "UTF-8");
			Put put = new Put(Bytes.toBytes(Long.parseLong(keyStr)));
			
			Text valueText = values.iterator().next();
			value = new String(valueText.getBytes(), 0, valueText.getLength(), "UTF-8");

			if (value != null && !"".equals(value)) {
				// parse the String into value string into three tokens
				//String[] tokens = value.toString().split("\t");
				String[] tokens = value.split("\t");
				if (tokens.length == 4) {
					String score_1 = tokens[0];
					String score_2 = tokens[1];
					String score_3 = tokens[2];
					String total_score = tokens[3];
//					String hashtag = tokens[0];
//					String tweetId = tokens[1];
					//String score = tokens[2];

					// add the columns
					put.add(CL_FAMILY, SCORE_1_NAME, Bytes.toBytes(Integer.parseInt(score_1)));
					put.add(CL_FAMILY, SCORE_2_NAME, Bytes.toBytes(Integer.parseInt(score_2)));
					put.add(CL_FAMILY, SCORE_3_NAME, Bytes.toBytes(Integer.parseInt(score_3)));
					put.add(CL_FAMILY, TOTAL_SCORE_NAME, Bytes.toBytes(Integer.parseInt(total_score)));
					//put.add(CL_FAMILY, SCORE_COL_NAME, Bytes.toBytes(score));

					context.write(NullWritable.get(), put);

				} else {
					System.out.println(value.toString());
				}

			}
		}
		
	}

	
}