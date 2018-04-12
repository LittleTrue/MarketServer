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

public class GetUser extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		
		 String type;
		 String target;//输入
		 
		 
		 String userName;
		 String userId;//输出
		 String userSex;
		 String userPhone;
		 
		 System.out.println("front被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 

		 type = req.getParameter("type");////key -value get方式获取url的键值对 
		 target = req.getParameter("target");
		 
		 System.out.println(target);
		  
		 PrintWriter out=resp.getWriter();   //输出流获取
		  
		 JSONObject ret_obj = new JSONObject();
		 JSONObject ret_obj_father = new JSONObject();
	      conn=login.Login.getCon();
	      
	       try {    
	       String frontGetUser_require = "select user_name,user_id,user_sex,user_phone from user"
	          		+ " where user_phone ='"+target+"' or user_id="+target;
	       
	       System.out.println(frontGetUser_require);

	       PreparedStatement stmt = conn.prepareStatement(frontGetUser_require);     
	       r= stmt.executeQuery(); 
	     
	       if (!r.next()) {  	  
	    	   ret_obj_father.put("status", false);
	    	   ret_obj_father.put("message", "没有该会员");
	    	}else {
	    		userName=r.getString(1);
	    		userId=r.getString(2);
	    		userSex=r.getString(3);
	    		userPhone=r.getString(4);
	    		ret_obj_father.put("status",true);
	    		ret_obj.put("name",userName);  
	    		ret_obj.put("id",userId);
	    		ret_obj.put("sex",userSex);
	    		ret_obj.put("tel",userPhone);
	    		ret_obj_father.put("info",ret_obj);
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

	        out.print(ret_obj_father.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  
}
