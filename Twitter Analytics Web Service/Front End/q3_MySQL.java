import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

public class q3_MySQL extends HttpServlet {
	private static final long serialVersionUID = 15619156191561901L;
	Connection conn = null;
	Statement stmt = null;
	// teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";

	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/Q2";

	// mySql Credential files
	static final String USER = "root";
	static final String PASS = "";
	private Map<String, String> cache = new HashMap<String, String>();
	
	@Override
	public synchronized void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// Set the response MIME type of the response message
		response.setContentType("text/html");
		// Allocate a output writers to write the response message into the
		// network socket
		PrintWriter out = response.getWriter();
		System.setProperty("file.encoding", "UTF8");
		String userId = request.getParameter("userid");
		out.println(TEAMINFO);
		
		if(cache.containsKey(userId)){
			out.print(cache.get(userId));
			out.close();
			return;
		}else{
			try {
				// STEP 2: Register JDBC driver
				Class.forName(JDBC_DRIVER);

				// STEP 3: Open a connection
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				stmt = conn.createStatement();
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT retweet FROM tweets_q3 WHERE origin=");
				sql.append(userId);
				ResultSet rs = stmt.executeQuery(sql.toString());

				// STEP 5: Extract data from result set
				while (rs.next()) {
					// Retrieve by column name
					String retweetId = rs.getString("retweet");
					String[] retweetIds = retweetId.split(",");
					StringBuffer sb = new StringBuffer();
					for(int i=0; i<retweetIds.length; i++){
						sb.append(retweetIds[i] + '\n');
					}
					out.print(sb.toString());
					cache.put(userId, sb.toString());
				}
				rs.close();
				stmt.close();
				conn.close();
			} catch (SQLException se) {
//				out.append(se.getMessage());
				// Handle errors for JDBC
				se.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				// finally block used to close resources
				try {
					if (stmt != null){
						//conn.close();
						stmt.close();
					}
				} catch (SQLException se2) {
				}// nothing we can do
				try {
					if (conn != null)
						conn.close();
				} catch (SQLException se) {
					se.printStackTrace();
				}// end finally try
				out.close();
			}// end try
		}
	}
}
