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
//设置活动优惠
public class SetActivity extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;

	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 long time=System.currentTimeMillis();
		 int insertResult;//方法变量定义
		 
		 
		 String goodId;//输入
		 String goodNum;
		 String discountPrice;
		 
		 
		 System.out.println("acountant被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 PrintWriter out=resp.getWriter();;//输出流获取
		  
		 System.out.println(input);
	
		 JSONObject get_obj=JSONObject.fromString(input);
		
		 goodId=get_obj.getString("goodId");
		 goodNum=get_obj.getString("num");
		 discountPrice=get_obj.getString("discount");
		 
		 
		 JSONObject ret_obj = new JSONObject();
		 
	      conn=login.Login.getCon();
	      
	      
	       try {    
	       String acountantSetActivity_require = "select * from activity"
	          		+ " where good_id ="+goodId+" AND is_bind = 0";
	       PreparedStatement stmt1 = conn.prepareStatement(acountantSetActivity_require);     
	       r= stmt1.executeQuery(); 
	     
	       if (r.next()) {  	  
	    		  ret_obj.put("status", false);
	    		  ret_obj.put("message", "添加优惠失败");
	    	}else {
	    	   
	       String acountantSetActivity_insert = "INSERT INTO activity(good_id,is_bind,discount_num,good_number,create_time)"
	 	          		+ " value("+goodId+","+0+","+discountPrice+","+goodNum+","+time+")";
	       System.out.println(acountantSetActivity_insert);
	 	   PreparedStatement stmt2 = conn.prepareStatement(acountantSetActivity_insert);     
	 	   insertResult= stmt2.executeUpdate();
	 	   
	 	   if(insertResult==0) {
 	   			ret_obj.put("status", false);
 	       		ret_obj.put("message", "插入活动表失败");
 	       		
 	   			}else {
 	   			ret_obj.put("status",true);
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
