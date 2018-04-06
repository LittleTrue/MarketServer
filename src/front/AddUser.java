package front;

import java.sql.Statement;
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

public class AddUser extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 int insertResult;
		 
		 String workerId;
		 String workerPosition;//进行操作中的员工session控制
		 
		 
		 String userName;
		 String userPhone;
		 String userSex;//输入
		 
		 
		 System.out.println("front被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 
		 PrintWriter out=resp.getWriter(); //输出流获取
		  
		 
		 JSONObject get_obj=JSONObject.fromString(input);
		 userName=get_obj.getString("name");
		 userPhone=get_obj.getString("tel");
		 userSex=get_obj.getString("sex");
		 
		
		 System.out.println(userSex);
		 
		 JSONObject ret_obj = new JSONObject();
		 
	      conn=login.Login.getCon();
	      
	      try {  
	      // 建立Statement对象
      		Statement stmt1 = conn.createStatement();
      		//执行数据库查询语句
      		 r = stmt1.executeQuery("SELECT * FROM market.user WHERE user_phone='"+userPhone+"'");
      		 
		  	if(r.next()) {
		  		ret_obj.put("status", "false");
	    		ret_obj.put("message", "该会员电话已存在");
		  	
		  	}else {
		  
	       String frontAddUser_insert = "insert into market.user(user_name,user_integral,user_phone,regist_time,discount,user_sex) "
	    		   + "  values ('"+userName+"','"+0+"','"+userPhone+"','"+System.currentTimeMillis()+"','"+1+"','"+userSex+"')";
	          	System.out.println(frontAddUser_insert);
	       PreparedStatement stmt2 = conn.prepareStatement(frontAddUser_insert);     
	       insertResult=stmt2.executeUpdate(); 
	     
	       if (insertResult==0) {  	  
	    		  ret_obj.put("status", "false");
	    		  ret_obj.put("message", "添加会员失败");
	    	}else {
	    	   	ret_obj.put("status", "true");
	          } 
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