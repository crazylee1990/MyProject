// To save as "<TOMCAT_HOME>\webapps\hello\WEB-INF\classes\HelloServlet.java"
import java.io.*;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.*;
import javax.servlet.http.*;



public class q1 extends HttpServlet {
	
	private static final long serialVersionUID = 15619L;
	
	//teamInfo = "TeamName, Chao's AWS, Ruixi's AWS, Yang's AWS"	
	private static final String TEAMINFO = "Initializing,837142752372,169680956667,903553157929";
	private static final BigInteger X = new BigInteger("6876766832351765396496377534476050002970857483815262918450355869850085167053394672634315391224052153");
	private Map<BigInteger, BigInteger> cache = new HashMap<BigInteger, BigInteger>();
	private static final int CACHESIZE = 5000;
	
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		// Set the response MIME type of the response message
		response.setContentType("text/html");
		// Allocate a output writers to write the response message into the
		// network socket
		PrintWriter out = response.getWriter();
		// Write the response message
		try {
			String key = request.getParameter("key");
			BigInteger XY = new BigInteger(key);
			BigInteger Y = new BigInteger("0");
			if(cache.containsKey(XY)){
				Y = cache.get(XY);
			}else{
				Y = XY.divide(X);
				if(cache.size() <= CACHESIZE){
					cache.put(XY, Y);
				}
				
			}
			out.print(Y + "\n");
			out.print(TEAMINFO + "\n");
			out.print(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()) + "\n");
//			out.println();
			
		}finally {
			out.close(); // Always close the output writer
		}
	}
}