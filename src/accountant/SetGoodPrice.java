package accountant;
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

public class SetGoodPrice extends HttpServlet{
	private static Connection conn = null;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 
		  int updateResult=0;//方法变量定义
		  int insertResult=0;
		  
		 String goodId;
		 String price;//输入
		 
		 
		 System.out.println("acountant被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 PrintWriter out=resp.getWriter();;//输出流获取
		  
	
		 JSONObject get_obj=JSONObject.fromString(input);
		
		 goodId=get_obj.getString("goodId");
		 price=get_obj.getString("price");
		  
		 JSONObject ret_obj = new JSONObject();
		 
	      conn=login.Login.getCon();
	      
	       try {    
	       String accountantSetGoodPrice_update = "UPDATE goods SET good_price ="+price+
	          		" where good_id ="+goodId;
	      
	       PreparedStatement stmt1 = conn.prepareStatement(accountantSetGoodPrice_update);     
	      
	       updateResult= stmt1.executeUpdate(); 
	     
	       if(updateResult==0) {
   			ret_obj.put("status", false);
       		ret_obj.put("message", "设置价格失败");
       		
   			}else {
   			String accountantSetGoodPrice_insert = "INSERT INTO market.price(good_id,good_price,create_time)"
   				 + "  values ("+goodId+","+price+","+System.currentTimeMillis()+")";
   			
   			PreparedStatement stmt2 = conn.prepareStatement(accountantSetGoodPrice_insert);
   			
   			
   			insertResult= stmt2.executeUpdate(); 
   			
   			if(insertResult==0) {
   	   			ret_obj.put("status", false);
   	       		ret_obj.put("message", "插入历史价格表失败");
   	       		
   	   			}else {
   	   			ret_obj.put("status",true);
   	   		    ret_obj.put("price",price);
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
