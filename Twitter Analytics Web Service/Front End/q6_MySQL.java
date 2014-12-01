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

public class q6_MySQL extends HttpServlet {
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
		String userA = request.getParameter("m");
		String userB = request.getParameter("n");
		
		out.println(TEAMINFO);
		String key = userA + userB;
		if(cache.containsKey(key)){
			out.print(cache.get(key));
			out.close();
			return;
		}else{
			try {
				Class.forName(JDBC_DRIVER);
				conn = DriverManager.getConnection(DB_URL, USER, PASS);
				stmt = conn.createStatement();
				StringBuffer sql = new StringBuffer();
				sql.append("SELECT photo_total FROM tweets_q6 WHERE user_id>=");
				sql.append(userA+" limit 1");
				
				ResultSet rs1 = stmt.executeQuery(sql.toString());
				
				sql = new StringBuffer();
				sql.append("SELECT photo_total FROM tweets_q6 WHERE user_id<=");
				sql.append(userB+" limit 1");
				
				ResultSet rs2 = stmt.executeQuery(sql.toString());
				
				
				StringBuffer sb = new StringBuffer();
				while (rs1.next()) {
					sb.append(Integer.parseInt(rs2.getString(1)) - Integer.parseInt(rs1.getString(1)) + "\n");
					break;
				}
				
				out.print(sb.toString());
				cache.put(key, sb.toString());
				rs1.close();
				rs2.close();
				stmt.close();
				conn.close();
			} catch (SQLException se) {
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

