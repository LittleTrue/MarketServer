package login;
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;  
  
import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;

import com.mysql.jdbc.ResultSetMetaData;
import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import com.paul.sertest.TokenMgr;
import com.paul.sertest.config.Constant;
import com.paul.sertest.model.SubjectModel;


public class Islogin  extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;
	private HashMap<String,String> workerInfo;
	
	//命名 接受及会话控制取出 数据:驼峰法  返回数据:间隔法
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
			//接受数据定义

			 System.out.println("被访问了");
			 
			 req.setCharacterEncoding("utf-8");
			 resp.setCharacterEncoding("utf-8");  //设置输入与返回文本编码格式
			 
			 try {
				workerInfo=login.Login.getWorkerInfoFromToken(req);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			 
			 PrintWriter out=resp.getWriter();  
			
			 JSONObject ret_obj = new JSONObject();
			
		
		     ret_obj.put("status",true);
		     ret_obj.put("role", workerInfo.get("workerPosition"));
		     ret_obj.put("name", workerInfo.get("workerName"));
		       
		     out.print(ret_obj.toString());  
		     out.flush();  
		      out.close();
}
		 @Override  
		    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		        this.doGet(req, resp);  
		    }  
}
