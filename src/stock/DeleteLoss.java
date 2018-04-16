package stock;

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
import net.sf.json.JSONArray;
import net.sf.json.JSONException;

public class DeleteLoss extends HttpServlet{
	private static Connection conn = null;
	private HashMap<String,String> workerInfo;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		//方法变量定义
		 int deleteResult;
		 String lossId;//输入
		 
		 int total;//输出
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("accountant被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 

		 lossId = req.getParameter("loss_id");////key -value get方式获取url的键值对 
		  
		PrintWriter out=resp.getWriter();//输出流获取
		
		 JSONObject ret_obj = new JSONObject();
		 
		 JSONArray ret_obj_array = new JSONArray();
		 
	       try {    
	       String stockGetPurchaseListDetail_require = "Delete FROM loss"+
	          		 " where loss_id = "+lossId;
	       System.out.println(stockGetPurchaseListDetail_require);
	       PreparedStatement stmt = conn.prepareStatement(stockGetPurchaseListDetail_require);     
	       deleteResult= stmt.executeUpdate(); 
	       
	   	if(deleteResult==0) {  
			  ret_obj.put("status", false);
			  ret_obj.put("message", "删除损耗失败");
		}else {
		   	ret_obj.put("status",true);
	     } 
	       stmt.close();
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
