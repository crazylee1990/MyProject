import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;


public class q2_HBase  extends HttpServlet {
	private static final long serialVersionUID = 156191561902L;
	//teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"	
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";
	
	private final static String TABLE_NAME = "q2Table";
	private final static String QUALIFIER_ID = "tweetId";
	private final static String QUALIFIER_SOCRE = "score";
	private final static String QUALIFIER_TEXT = "text";
	private final static String COL_FAMILY = "q2";
	private final static int HTABLE_POOL_SIZE = 10; 
	
	private static String MASTER_DNS = "localhost";
	private static String MASTER_IP = "localhost";
	
//	private static String MASTER_DNS = null;
//	private static String MASTER_IP = null;
//	
	private static Map<String, String> cache = new HashMap<String, String>();
	private final static int CACHESIZE = 2000;
	
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
		config.set("hbase.rootdir", "hdfs://"+ MASTER_IP+":9000/hbase");
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
			String userId = request.getParameter("userid");
	        String time = request.getParameter("tweet_time");
			time = time.replace('+', ' ');
			out.print(TEAMINFO + "\n");
			String rowKey = userId + " " + time;
			
			if(cache.containsKey(rowKey)){
				out.print(cache.get(rowKey));
			}else{
				HTableInterface table = null;
				try {
					table = pool.getTable(TABLE_NAME);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				Get getTweetId = new Get(rowKey.getBytes());
				getTweetId.addFamily(Bytes.toBytes(COL_FAMILY));
				Result tableResult = table.get(getTweetId);
				if (!tableResult.isEmpty()) {
					byte[] tweetIdBytes = tableResult.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes(QUALIFIER_ID));
					byte[] scoreBytes = tableResult.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes(QUALIFIER_SOCRE));
					byte[] textBytes = tableResult.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes(QUALIFIER_TEXT));
					
					String tweetId = Bytes.toString(tweetIdBytes);
					String score = Bytes.toString(scoreBytes);
					String text = substituteChar(new String(textBytes,0,textBytes.length,"UTF-8"));
					
					String result = tweetId + ":" + score + ":" + text + "\n";
					out.print(result);
					if(cache.size() <= CACHESIZE){
						cache.put(rowKey, result);
					}
				}
				table.close();
			}
		}catch (Exception e) {
			out.print(e.getMessage());
		} finally{
			out.close();
		}
		
	}
	
	private String substituteChar(String text){
		if(text.contains("``#n#``")){
			text = text.replace("``#n#``", "\n");
		}
		if(text.contains("``#r#``")){
			text = text.replace("``#r#``", "\r");
		}
		if(text.contains("``#t#``")){
			text = text.replace("``#t#``", "\t");
		}
		return text;
	}

}
