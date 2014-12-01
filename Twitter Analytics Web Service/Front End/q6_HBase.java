import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;


public class q6_HBase  extends HttpServlet {
	private static final long serialVersionUID = 156191561902L;
	//teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"	
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";
	private final static String TABLE_NAME = "q6Table";
	private final static String COL_FAMILY = "q6";
	private final static int HTABLE_POOL_SIZE = 20000; 
	
	private Map<String, Integer> cache = new HashMap<String, Integer>();
	private final static int CACHESIZE = 5000;
	
	private static String MASTER_DNS = "localhost";
	private static String MASTER_IP = "localhost";
	
//	private static String MASTER_IP = null;
//	private static String MASTER_DNS = null;
//	
//	static{
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new FileReader(new File(System.getProperty("catalina.base") + "/webapps/ROOT/WEB-INF/classes/master_ip.txt")));
//			
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
		config.set("hbase.rootdir", "hdfs://" + MASTER_IP +":9000/hbase");
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
			String userA = request.getParameter("m");
			String userB = request.getParameter("n");
			
			
			//format the user id to 10 digit length
			userA = String.format("%1$10s", userA).replace(' ','0');
			userB = String.format("%1$10s", userB).replace(' ','0');
			
			int sum = 0;
			int first = 0;
			int second = 0;
			int firstNumOfPhotos = 0;
			int secondNumOfPhotos = 0;
			
			out.print(TEAMINFO + "\n");
			
			// if the user Id A is less than user Id B, then 0 images can be found
			if(userA.compareTo(userB) > 0){
				out.print(sum + "\n");
				out.close();
				return;
			}
			if(userB.compareTo("2594997268") > 0){
				userB = "2594997268";
			}
			
			StringBuilder sb = new StringBuilder();
			sb.append(userA);
			sb.append(userB);
			if(cache.containsKey(sb.toString())){
				out.print(cache.get(sb.toString()));
			}else{
				try {
					HTableInterface table = null;
					try {
						table = pool.getTable(TABLE_NAME);
					} catch (Exception e) {
						out.print(e.getMessage());
					}
					
					Scan s=new Scan();
					s.setStartRow(Bytes.toBytes(userA));
					s.setCaching(1);
					ResultScanner scanner = table.getScanner(s);
					for (Result rr = scanner.next(); rr != null;) {
						first = Integer.parseInt(Bytes.toString(rr.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("total_photos"))));
						firstNumOfPhotos =  Integer.parseInt(Bytes.toString(rr.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("photos_num"))));
						break;
					}
							
					s.setStartRow(Bytes.toBytes(userB));
					s.setCaching(1);
					scanner = table.getScanner(s);
					for (Result rr = scanner.next(); rr != null;) {
						if(!Bytes.toString(rr.getRow()).equals(userB)){
							secondNumOfPhotos =  Integer.parseInt(Bytes.toString(rr.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("photos_num"))));
						}
						second = Integer.parseInt(Bytes.toString(rr.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("total_photos"))));
						break;
					}
					
					sum = second - first - secondNumOfPhotos + firstNumOfPhotos;
					out.print(sum + "\n");
					if(cache.size() <= CACHESIZE){
						cache.put(sb.toString(), sum);
					}
					scanner.close();		
				}catch (IOException e) {
					e.printStackTrace();
				}	
			}
		}catch (Exception e){
			
		}finally{
			out.close();
		}
	}
}

