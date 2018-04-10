package login;

import java.io.BufferedReader;
import java.io.IOException;  
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;  
  
import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONException;

public class LoginOut extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;
	private HashMap<String,String> workerInfo;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		
		 
		  int updateResult;//方法变量定义
		 
		 String workerId="";//进行操作中的员工session控制
		 
		 try {
			workerInfo=login.Login.getWorkerInfoFromToken(req);
			} catch (Exception e1) {
			e1.printStackTrace();
		}
		  
		 System.out.println("loginout被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
		 
		 workerId=workerInfo.get("workerId");
	
		PrintWriter out=resp.getWriter();;//输出流获取
		  
	
		
		 JSONObject ret_obj = new JSONObject();
		 
	      conn=login.Login.getCon();
	      
	      System.out.println(workerId);
	       try {    
	       String loginLoginOut_update = "UPDATE market.worker SET worker_token = null"
	          		+ " where worker_id ="+workerId;
	       PreparedStatement stmt = conn.prepareStatement(loginLoginOut_update);     
	       updateResult= stmt.executeUpdate(); 
	     
	       if ( updateResult==0) {  	  
	    		  ret_obj.put("status", false);
	    		  ret_obj.put("message", "退出失败");
	    	}else {
	    	   	ret_obj.put("status",true);
	          }  
	       }catch (SQLException e) {  	
	        	  e.printStackTrace();
  
	          } finally { 
	               try {
	                   conn.close(); 
	              } catch(SQLException e) {
	                   e.printStackTrace();
	              }
	          }
	          
	         
	        out.print(ret_obj.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  

}
