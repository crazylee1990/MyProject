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

import java.nio.charset.StandardCharsets;

public class q2_MySQL extends HttpServlet {
	private static final long serialVersionUID = 156191561901L;

	// teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";
	
	// JDBC driver name and database URL
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/Q2";

	// mySql Credential files
	static final String USER = "root";
	static final String PASS = "";
	
	//add a cache
	private Map<String, String> cache = new HashMap<String, String>();

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		// Set the response MIME type of the response message
		response.setContentType("text/html");
		// Allocate a output writers to write the response message into the
		// network socket
		PrintWriter out = new PrintWriter(new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8),true);
		String userId = request.getParameter("userid");
        String time = request.getParameter("tweet_time");

		out.print(TEAMINFO + "\n");
		
		String key = userId + time;
		
		try {
			if(cache.containsKey(key)){
				out.print(cache.get(key));
			}else{
				Class.forName(JDBC_DRIVER);

				// STEP 3: Open a connection
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				// STEP 4: Execute a query
				stmt = conn.createStatement();
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT tweet_id, score, text FROM tweets WHERE user_id=");
				sql.append(userId);
				sql.append(" AND date_time='");
				sql.append(time+"'");
				rs = stmt.executeQuery(sql.toString());

				// STEP 5: Extract data from result set
				StringBuffer sb = new StringBuffer();
				while (rs.next()) {
					// Retrieve by column name
					String tweetId = rs.getString("tweet_id");
					int score = rs.getInt("score");
					String tweetText = rs.getString("text");
					sb.append(tweetId+":"+score+":"+tweetText+"\n");
				}
				out.print(sb.toString());
				cache.put(key, sb.toString());
				
				rs.close();
				stmt.close();
				conn.close();
				out.close();
			}
		} catch (SQLException se) {
			// Handle errors for JDBC
			se.printStackTrace();
		} catch (Exception e) {
			// Handle errors for Class.forName
			out.println(e.getMessage());
			e.printStackTrace();
		} finally {
			// finally block used to close resources
			try {
				if (stmt != null)
					conn.close();
			} catch (SQLException se2) {
			}// nothing we can do
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}// end finally try
			if(out != null){
				out.close();
			}
		}// end try
	}
}
