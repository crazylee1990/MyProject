import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;


public class q4_HBase  extends HttpServlet {
	private static final long serialVersionUID = 156191561902L;
	//teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"	
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";
	private final static String TABLE_NAME = "q4TableScan";
	private final static String COL_FAMILY = "q4";
	private final static int HTABLE_POOL_SIZE = 20000; 
	
	private static String MASTER_DNS = "localhost";
	private static String MASTER_IP = "localhost";
	
	private static Map<String, String> cache = new HashMap<String, String>();
	private final static int CACHESIZE = 1000;
	
//	private static String MASTER_DNS = null;
//	private static String MASTER_IP = null;
//	
//	static{
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(new File(System.getProperty("catalina.base") + "/webapps/ROOT/WEB-INF/classes/master_ip.txt")));
//			String line;
//			while((line = br.readLine()) != null){
//				if(MASTER_IP == null){
//					MASTER_IP = line;
//				}else{
//					MASTER_DNS = line;
//				}
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if(br != null){
//				try {
//					br.close();
//				} catch (IOException e2) {
//					e2.printStackTrace();
//				}
//				
//			}
//		}
//	}

	static Configuration config = null;
	static HTablePool pool = null;
	static {
		config = HBaseConfiguration.create();
		config.clear();
    	config.set("hbase.master", MASTER_DNS + ":60000");
		config.set("fs.hdfs.impl", "emr.hbase.fs.BlockableFileSystem");
		config.set("hbase.regionserver.handler.count", "100");
		config.set("hbase.zookeeper.quorum", MASTER_IP);
		config.set("hbase.zookeeper.property.clientPort", "2181");
		config.set("hbase.rootdir", "hdfs://"+ MASTER_IP + ":9000/hbase");
		config.set("hbase.tmp.dir", "/mnt/var/lib/hbase/tmp-data");
		// create a HTablePool
		pool=new HTablePool(config, HTABLE_POOL_SIZE);
	}	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		
		// Set the response MIME type of the response message
		response.setContentType("text/html");
		// Allocate a output writers to write the response message into the
		// network socket
		PrintWriter out = null;
		try {
			out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8),true);
			String date = request.getParameter("date");
			String location = request.getParameter("location");
			location = location.replace('+', ' ');
			
			String minRank = request.getParameter("m");
			String maxRank = request.getParameter("n");
			
			StringBuilder key = new StringBuilder();
			key.append(date);
			key.append(location);
			key.append(minRank);
			key.append(maxRank);
			
			
			
//			String maxRank = "" + max;
			
//			//format the user id to 10 digit length
//			minRank = String.format("%1$5s", minRank).replace(' ','0');
//			maxRank = String.format("%1$5s", maxRank).replace(' ','0');
			
			out.print(TEAMINFO + "\n");
			if(cache.containsKey(key.toString())){
				out.print(cache.get(key.toString()));
			}else {
				try {
					HTableInterface table = null;
					try {
						table = pool.getTable(TABLE_NAME);
					} catch (Exception e) {
						out.print(e.getMessage());
					}
					
//					String rowKeyStart = date + location + " " + minRank;
//					String rowKeyEnd = date + location + " " + maxRank;
//					
//					Scan scan = new Scan(Bytes.toBytes(rowKeyStart),Bytes.toBytes(rowKeyEnd));
//					scan.addFamily(Bytes.toBytes(COL_FAMILY)); 
//					
//					ResultScanner rs = table.getScanner(scan);
//					
//					for (Result r = rs.next(); r != null; r = rs.next()) {
//						KeyValue[] kvs = r.raw();
//						StringBuffer sb = new StringBuffer();
//						sb.append(new String(kvs[0].getValue(),0,kvs[0].getValue().length, "UTF-8") + ":");
//						for(int i=1; i<kvs.length; i++){
//							sb.append(new String(kvs[i].getValue(),0,kvs[i].getValue().length, "UTF-8"));
//						}
//						sb.append("\n");
//						out.print(sb.toString());
//					}
//					rs.close();
////					
//					Scan scan = new Scan
//					Scan s = ...;
//					s.setStartRow("pets");
//					s.setStopRow("pets");
//					// get all columns for my pet "fluffy". 
//					Filter f = new ColumnRangeFilter(Bytes.toBytes("fluffy"), true,
//					                                 Bytes.toBytes("fluffz"), false);
//					s.setFilter(f);
//					s.setBatch(20); // avoid getting all columns for the HBase row 
//					ResultScanner rs = t.getScanner(s);
//					for (Result r = rs.next(); r != null; r = rs.next()) {
//					  // r will now have all HBase columns that start with "fluffy",
//					  // which would represent a single row
//					  for (KeyValue kv : r.raw()) {
//					    // each kv represent - the latest version of - a column
//					  }
//					}

					int min = Integer.parseInt(minRank);
					int max = Integer.parseInt(maxRank);
					List<Get> gets = new ArrayList<Get>();
					for (int i = min; i <= max; i++) {
						StringBuilder rowKey = new StringBuilder();
						
						rowKey.append(date);
						rowKey.append(location);
						rowKey.append(" ");
						rowKey.append(i);
						
						Get get = new Get(Bytes.toBytes(rowKey.toString()));
						get.addFamily(Bytes.toBytes(COL_FAMILY));
						gets.add(get);
					}
					
					Result[] tableResults= table.get(gets);
					StringBuilder result = new StringBuilder();
					for (Result tableResult: tableResults) {
						if (!tableResult.isEmpty()) {
							byte[] hashtagBytes = tableResult.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("hashtag"));
							byte[] tweetIdBytes = tableResult.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("tweetId"));
							
							String tweetId = Bytes.toString(tweetIdBytes);
							String hashtag = new String(hashtagBytes,0,hashtagBytes.length, "UTF-8");
							result.append(hashtag);
							result.append(":");
							result.append(tweetId);
//							result.append(tweetId);
							result.append("\n");
						}
					}	
					out.print(result.toString());
					if(cache.size() <= CACHESIZE){
						cache.put(key.toString(), result.toString());
					}
					table.close();
				} catch (Exception e) {
					e.printStackTrace();
				} 
			}
			
		} catch (IOException e) {
			
		}finally{
			out.close();
		}
	}
}

