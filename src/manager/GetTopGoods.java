package manager;
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

public class GetTopGoods extends HttpServlet{
	private ResultSet r;
	private static Connection conn = null;
	private HashMap<String,String> workerInfo;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 int  goodId;
		 String  goodName;
		 int  goodNum;
		 LocalDate prevWeek0 = LocalDate.now();
		 String type; 
		 String target;
		 
		 LocalDate prevWeek1 = LocalDate.now().minus(1, ChronoUnit.WEEKS);
		 
		 LocalDate prevWeek2 = LocalDate.now().minus(4, ChronoUnit.WEEKS);
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("manager被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 

		 type = req.getParameter("type");////key -value get方式获取url的键值对 
		 if(type==null) {
			 type="all" ;
		 }
		 
		 target = req.getParameter("target");
		 if(!target.equals("")) {
			 target = URLDecoder.decode(target,"UTF-8");
			 }else {
			target ="all";
		 }
		 PrintWriter out=resp.getWriter();;//输出流获取
		
		 JSONObject ret_obj = new JSONObject();
		 JSONArray ret_obj_array_goodName = new JSONArray();
		 JSONArray ret_obj_array_goodNum = new JSONArray();
	       try {    
	    	   String managerGetGoodsPrice_require;
	    	   
	    	   if(type.equals("week")) {
	    		   if(target.equals(null)||target==""||target.equals("all")) {  
	    			   managerGetGoodsPrice_require = "SELECT SUM(good_number) AS maxGood,o.good_name from market.order o LEFT OUTER JOIN market.goods g ON o.good_id=g.good_id WHERE DATE_FORMAT(o.create_time, '%Y-%m-%d') BETWEEN DATE_SUB(NOW(),INTERVAL 7 day) AND  NOW()  GROUP BY o.good_id,o.good_name ORDER BY SUM(good_number) DESC LIMIT 7"; 
	    		   }else {
	    			   managerGetGoodsPrice_require = "SELECT SUM(good_number) AS maxGood,o.good_name from market.order o LEFT OUTER JOIN market.goods g ON o.good_id=g.good_id WHERE DATE_FORMAT(o.create_time, '%Y-%m-%d') BETWEEN DATE_SUB(NOW(),INTERVAL 7 day) AND  NOW() AND g.good_divide='"+target+"' GROUP BY o.good_id,o.good_name ORDER BY SUM(good_number) DESC LIMIT 7"; 
	    			   
	    		   }	    		   	    	   
	    		   
	    	}else if(type.equals("month")){
	    		   if(target.equals(null)||target==""||target.equals("all")) {  
	    			   managerGetGoodsPrice_require = "SELECT SUM(good_number) AS maxGood,o.good_name from market.order o LEFT OUTER JOIN market.goods g ON o.good_id=g.good_id WHERE DATE_FORMAT(o.create_time, '%Y-%m-%d') BETWEEN DATE_SUB(NOW(),INTERVAL 30 day) AND  NOW()  GROUP BY o.good_id,o.good_name ORDER BY SUM(good_number) DESC LIMIT 7"; 
	    	   
	    		   }else {
	    			   managerGetGoodsPrice_require = "SELECT SUM(good_number) AS maxGood,o.good_name from market.order o LEFT OUTER JOIN market.goods g ON o.good_id=g.good_id WHERE DATE_FORMAT(o.create_time, '%Y-%m-%d') BETWEEN DATE_SUB(NOW(),INTERVAL 30 day) AND  NOW() AND g.good_divide='"+target+"' GROUP BY o.good_id,o.good_name ORDER BY SUM(good_number) DESC LIMIT 7"; 
	    			   
	    		   }	  
	    		   }else {
	    			   if(target.equals(null)||target==""||target.equals("all")) {  
	    		   managerGetGoodsPrice_require = "select SUM(good_number) as maxGood,good_name from market.order  GROUP BY good_id ORDER BY SUM(good_number) DESC LIMIT 7";   
	    			   }else {
		    			   managerGetGoodsPrice_require = "SELECT SUM(good_number) AS maxGood,o.good_name from market.order o LEFT OUTER JOIN market.goods g ON o.good_id=g.good_id WHERE g.good_divide='"+target+"' GROUP BY o.good_id,o.good_name ORDER BY SUM(good_number) DESC LIMIT 7"; 
		    			   
		    		   }	 
	    			   }
	    	   System.out.println(managerGetGoodsPrice_require);
	       PreparedStatement stmt = conn.prepareStatement(managerGetGoodsPrice_require);     
	       r= stmt.executeQuery(); 

	       if (!r.next()) {  	  
	    		  ret_obj.put("status", false);
	    		  ret_obj.put("message", "没有足够订单");
	    	}else {
	    		
	    		do {
	    			goodNum=r.getInt(1);
	    			goodName=r.getString(2);
	    			
	    			ret_obj_array_goodName.put(goodName);
	    		    ret_obj_array_goodNum.put(goodNum);
	    		}while(r.next());
	  
        		ret_obj.put("status",true);
        		ret_obj.put("goodName",ret_obj_array_goodName);
        		ret_obj.put("goodNum", ret_obj_array_goodNum);
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
