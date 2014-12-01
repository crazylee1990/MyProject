package hBaseQ3;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Reporter;

public class HBaseReducer extends TableReducer<Text, Text, NullWritable> {

	private final byte[] COL_FAMILY = Bytes.toBytes("q3");
	private final byte[] QUALIFIER = Bytes.toBytes("retweetId");
	
	
	public void reduce(Text key, Iterable<Text> values, Context context)
			throws IOException, InterruptedException {
		
		String value = "";
		while (values.iterator().hasNext()) {  
			Put put = new Put(Bytes.toBytes(key.toString()));
            value = values.iterator().next().toString();  
            if (value != null && !"".equals(value)) {  
                String kv = createKeyValue(value.toString());  
                if (kv != null){  
                	put.add(COL_FAMILY, QUALIFIER, Bytes.toBytes(kv.toString()));
                	context.write(NullWritable.get(), put);
                }
            }  
        }		
		
	}
	
	private String createKeyValue(String str) {

		String[] strs = str.split("\t");
		if (strs.length < 2)
			return null;
		String row = strs[0];
		String value = strs[1];
		return value;

	}

}
