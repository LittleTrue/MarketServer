package front;

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

public class AddGoods extends HttpServlet {
	private static Connection conn = null;
	private ResultSet r;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 String goodId;
		 
		 String good_price;
		 String activity_price;
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("front被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
		 
		
//		 resp.setContentType("text/html;charset=utf-8");
//		 resp.setHeader("content-type","text/html;charset=UTF-8");


		  goodId = req.getParameter("goodNo");////key -value get方式获取url的键值对 
		  System.out.println(goodId);
		  PrintWriter out=resp.getWriter();   
		  
		  
		 JSONObject ret_obj = new JSONObject();
		 
  
	       try {    
	       String frontAddGoods_require = "select good_price,activity_price from goods"
	          		+ " where good_id ='"+goodId+"'";
	       PreparedStatement stmt = conn.prepareStatement(frontAddGoods_require);     
	       r= stmt.executeQuery(); 
	     
	       if (!r.next()) {  	  
	    		  ret_obj.put("status", false);
	    		  ret_obj.put("message", "添加商品失败");
	    	}else {
	    		good_price=r.getString(1);
	    		activity_price=r.getString(2);
	    		
	    		if(activity_price!=null) {
	    			good_price=activity_price;
	    		}
	    	   	ret_obj.put("status",true);
	    		ret_obj.put("good_price",good_price);  
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
