package stock;

import java.io.BufferedReader;
import java.io.IOException;  
import java.io.PrintWriter;
import java.net.URLDecoder;
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

import com.mysql.jdbc.ResultSetMetaData;
import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;



public class GetGoodsStock  extends HttpServlet {
	private static Connection conn = null;
	private ResultSet r;

	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 //方法变量定义
		 int depage[]= {0,1};
	
	
		 
		 int page;//输入 
		 int size;
		 String type="";
		 
		 int total=0;//输出

		 
		 conn=login.Login.getCon();
		 
		 System.out.println("stock被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
		 
		 
		 page = Integer.parseInt(req.getParameter("page"));
		 size = Integer.parseInt(req.getParameter("size"));////key -value get方式获取url的键值对 
		 type = req.getParameter("type");
		 type=URLDecoder.decode(type, "UTF-8");
		 
		 System.out.println(type);
		 depage=login.Login.getSplitPageInfo(page,size);
		 
		  PrintWriter out=resp.getWriter();   //输出流获取
		  
		 JSONObject ret_obj = new JSONObject();

		 JSONArray ret_obj_array = new JSONArray();
	
	       try {   
	    	   String stockGetGoodsStock_require_count;
	    	  String stockGetGoodsStock_require;
	    	  if(type.equals(null)||type==""||type.equals("全部")) {
	    		  
	    	  stockGetGoodsStock_require = "select * from goods "
	    			 +" ORDER BY good_id ASC"+" LIMIT "+depage[0]+","+depage[1];
	    	  
	    	  stockGetGoodsStock_require_count = "select count(*) from goods "
		    			 +" ORDER BY good_id ASC";
	    	   }else {
	    	  stockGetGoodsStock_require = "select * from goods WHERE good_divide = '"
	    			 +type+"' ORDER BY good_id ASC"+" LIMIT "+depage[0]+","+depage[1];   
	    	  
	    	  stockGetGoodsStock_require_count = "select count(*) from goods WHERE good_divide = '"
		    			 +type+"' ORDER BY good_id ASC"; 
	    	   }
	    	  
		      PreparedStatement stmt = conn.prepareStatement(stockGetGoodsStock_require_count); 
		      
		      r= stmt.executeQuery();
		      r.next();
		      total=r.getInt(1);
		      
		      stmt = conn.prepareStatement(stockGetGoodsStock_require); 
		      r= stmt.executeQuery();
	          ret_obj_array =login.Login.resultSetToJsonArry(r);
	        	
	         r.beforeFirst();// 返回第一个（记住不是rs.frist()）,不写的话下面的循环里面没值  
	        	
	        	if(!r.next()) {
	        		ret_obj.put("status",true);
	        		ret_obj.put("info","");
	        		ret_obj.put("total",0);
	        	}
	        	else {
	        		ret_obj.put("status",true);
	        		ret_obj.put("info",ret_obj_array);
	        		ret_obj.put("total",total);
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


