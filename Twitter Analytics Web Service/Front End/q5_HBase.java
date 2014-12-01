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


public class q5_HBase  extends HttpServlet {
	private static final long serialVersionUID = 156191561902L;
	//teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"	
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";
	private final static String TABLE_NAME = "q5Table";
//	private final static String TABLE_NAME = "5";
	private final static String COL_FAMILY = "q5";
//	private final static String COL_FAMILY = "q";
	private final static int HTABLE_POOL_SIZE = 20000; 
	
	private static String MASTER_DNS = "localhost";
	private static String MASTER_IP = "localhost";
	
	private static Map<String, String> cache = new HashMap<String, String>();
	private final static int CACHESIZE = 4000;
	
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
			String userA = request.getParameter("m");
//			long userA = Long.parseLong(request.getParameter("m"));
			String userB = request.getParameter("n");
//			long userB = Long.parseLong(request.getParameter("n"));
			
			out.print(TEAMINFO + "\n");
			StringBuilder key = new StringBuilder();
			key.append(userA);
			key.append(" ");
			key.append(userB);
			
			if(cache.containsKey(key.toString())){
				out.print(cache.get(key.toString()));
			}else{
				try {
					HTableInterface table = null;
					try {
						table = pool.getTable(TABLE_NAME);
					} catch (Exception e) {
						out.print(e.getMessage());
					}
					
					List<Get> gets = new ArrayList<Get>();
					Get getA = new Get(Bytes.toBytes(userA));
					Get getB = new Get(Bytes.toBytes(userB));
					getA.addFamily(Bytes.toBytes(COL_FAMILY));
					getB.addFamily(Bytes.toBytes(COL_FAMILY));
					gets.add(getA);
					gets.add(getB);
					
					Result[] tableResults= table.get(gets);
					
					List<Integer> user_A_scores = new ArrayList<Integer>();
					List<Integer> user_B_scores = new ArrayList<Integer>();
					StringBuffer sb = new StringBuffer();
					
					for(Result r : tableResults){
						if(!r.isEmpty()){
							
							byte[] score_1Bytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("score_1"));
							byte[] score_2Bytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("score_2"));
							byte[] score_3Bytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("score_3"));
							byte[] total_scoreBytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("total_score"));
							
							int score_1 = Integer.parseInt(Bytes.toString(score_1Bytes));
							int score_2 = Integer.parseInt(Bytes.toString(score_2Bytes));
							int score_3 = Integer.parseInt(Bytes.toString(score_3Bytes));
							int total_score = Integer.parseInt(Bytes.toString(total_scoreBytes));
							
//							byte[] score_1Bytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("1"));
//							byte[] score_2Bytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("2"));
//							byte[] score_3Bytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("3"));
//							byte[] total_scoreBytes = r.getValue(Bytes.toBytes(COL_FAMILY), Bytes.toBytes("t"));
//							
//							int score_1 = Integer.parseInt(Bytes.toString(score_1Bytes));
//							int score_2 = Integer.parseInt(Bytes.toString(score_2Bytes));
//							int score_3 = Integer.parseInt(Bytes.toString(score_3Bytes));
//							int total_score = Integer.parseInt(Bytes.toString(total_scoreBytes));
								
							if(user_A_scores.size() == 0){
								user_A_scores.add(score_1);
								user_A_scores.add(score_2);
								user_A_scores.add(score_3);
								user_A_scores.add(total_score);
							}else{
								user_B_scores.add(score_1);
								user_B_scores.add(score_2);
								user_B_scores.add(score_3);
								user_B_scores.add(total_score);
							}
						}
					}
					
					sb.append(userA + "\t" + userB + "\tWINNER\n");
					for(int i=0; i<user_A_scores.size(); i++){
						int A = user_A_scores.get(i);
						int B = user_B_scores.get(i);
						sb.append(A + "\t");
						sb.append(B + "\t");
						if(A == B){
							sb.append("X");
						}else{
							sb.append(A>B? userA:userB);
						}
						sb.append("\n");
					}
					out.print(sb.toString());
					if(cache.size() <= CACHESIZE){
						cache.put(key.toString(), sb.toString());
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

