import java.io.*;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;


public class q3_HBase  extends HttpServlet {
	private static final long serialVersionUID = 156191561902L;
	//teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"	
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";
	
	private final static String TABLE_NAME = "q3Table";
	private final static String QUALIFIER = "retweetId";
	private final static String COL_FAMILY = "q3";
	private final static int HTABLE_POOL_SIZE = 10; 
	
	private static String MASTER_DNS = "localhost";
	private static String MASTER_IP = "localhost";
	
	private static Map<String, String> cache = new HashMap<String, String>();
	private final static int CACHESIZE = 2000;
	
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
		config.set("hbase.rootdir", "hdfs://" + MASTER_IP + ":9000/hbase");
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
			out = response.getWriter();
			String userId = request.getParameter("userid");
			out.print(TEAMINFO + "\n");
			if(cache.containsKey(userId)){
				out.print(cache.get(userId) + "\n");
			}else{
				try {
					HTableInterface table = null;
					try {
						table = pool.getTable(TABLE_NAME);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//get retweet ids
					Get getTweetId = new Get(userId.getBytes());
					getTweetId.addFamily(Bytes.toBytes(COL_FAMILY));
					Result tableResult = table.get(getTweetId);
					if (!tableResult.isEmpty()) {
						byte[] retweetIdsBytes = tableResult.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes(QUALIFIER));
						String resultComma = Bytes.toString(retweetIdsBytes);
						String result = resultComma.replaceAll("[,]","\\\n");
						out.print(result);
						if(cache.size() >= CACHESIZE){
							cache = new HashMap<String, String>();
						}
						cache.put(userId,result+"\n");
					}
					out.print("\n");
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

