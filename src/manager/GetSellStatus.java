
package manager;
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
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

public class GetSellStatus extends HttpServlet{
	private ResultSet r;
	private static Connection conn = null;
	private HashMap<String,String> workerInfo;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 int insertResult;//方法变量定义
	
		 
		 String goodId;//输入
		 Date time;
		 float price;
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("manager被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 

		  goodId = req.getParameter("goodId");////key -value get方式获取url的键值对 
		  System.out.println(goodId);
	
		PrintWriter out=resp.getWriter();;//输出流获取
		
		 JSONObject ret_obj = new JSONObject();
		 JSONArray ret_obj_array_time = new JSONArray();
		 JSONArray ret_obj_array_price = new JSONArray();
	       try {    
	       String managerGetGoodsPrice_require = "select good_number,create_time from market.order"
	    		   + " where good_id ="+goodId +" ORDER BY create_time ASC"; 
	       
	       System.out.println(managerGetGoodsPrice_require);
	       PreparedStatement stmt = conn.prepareStatement(managerGetGoodsPrice_require); 
	       
	       r= stmt.executeQuery(); 

	       if (!r.next()) {  	  
	    		  ret_obj.put("status", false);
	    		  ret_obj.put("message", "该商品价格没有销售记录");
	    	}else {
	    		
	    		do {
	    			price=r.getFloat(1);
	    			time=r.getDate(2);
	    			
	    			ret_obj_array_time.put(time.toString());
	    		    ret_obj_array_price.put(price);
	    		}while(r.next());
	  
        		ret_obj.put("status",true);
        		ret_obj.put("time",ret_obj_array_time);
        		ret_obj.put("sale",ret_obj_array_price);
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
