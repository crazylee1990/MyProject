import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;

public class q5_MySQL extends HttpServlet {
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
				sql.append("SELECT score_1, score_2, score_3, total_score FROM tweets_q5 WHERE user_id=");
				sql.append(userA+" ");
				sql.append("OR ruser_id=" + userB);
				
				ResultSet rs = stmt.executeQuery(sql.toString());

				List<Integer> user_A_scores = new ArrayList<Integer>();
				List<Integer> user_B_scores = new ArrayList<Integer>();
				StringBuffer sb = new StringBuffer();
				while (rs.next()) {
					// Retrieve by column name
					String score_1 = rs.getString("score_1");
					String score_2 = rs.getString("score_2");
					String score_3 = rs.getString("score_1");
					String total_score = rs.getString("total_score");
					if(user_A_scores.size() == 0){
						user_A_scores.add(Integer.parseInt(score_1));
						user_A_scores.add(Integer.parseInt(score_2));
						user_A_scores.add(Integer.parseInt(score_3));
						user_A_scores.add(Integer.parseInt(total_score));
					}else{
						user_B_scores.add(Integer.parseInt(score_1));
						user_B_scores.add(Integer.parseInt(score_2));
						user_B_scores.add(Integer.parseInt(score_3));
						user_B_scores.add(Integer.parseInt(total_score));
					}
				}
				
				sb.append(userA + "\t" + userB + "\tWINNER\n");
				for(int i=0; i<user_A_scores.size(); i++){
					int A = user_A_scores.get(i);
					int B = user_B_scores.get(i);
					sb.append(A + "\t");
					sb.append(B + "\t");
					sb.append(A>B?A:B + "\n");
				}
				out.print(sb.toString());
				cache.put(key, sb.toString());
				rs.close();
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
