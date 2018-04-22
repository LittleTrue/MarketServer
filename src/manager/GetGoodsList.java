package manager;
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

import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;

public class GetGoodsList extends HttpServlet{
	private ResultSet r1;
	private ResultSet r2;
	private static Connection conn = null;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 int total=0;//方法变量定义
	     int depage[]= {0,1};
		 String target;
		//输入
		 int page; 
		 int size;
		 String type="";
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("manager被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
		 
		 page = Integer.parseInt(req.getParameter("page"));////key -value get方式获取url的键值对 
		 size = Integer.parseInt(req.getParameter("size"));
		  System.out.println(size);
		  type = req.getParameter("type");
		 type=URLDecoder.decode(type, "UTF-8");
			 
		 target = req.getParameter("target");
		 if(!target.equals("")) {
		 target = URLDecoder.decode(target,"UTF-8");
		 }
		 
		  depage=login.Login.getSplitPageInfo(page,size);
		  
		  PrintWriter out=resp.getWriter();   //输出流获取
		  
		 JSONObject ret_obj = new JSONObject();
		 JSONArray ret_obj_array = new JSONArray();
		 
		 String managerGetGoodsList_query;
		 String managerGetGoodsList_query_count;
		  if(type.equals(null)||type==""||type.equals("全部")) {
    		  if(!target.equals("")) {
			  managerGetGoodsList_query= "SELECT * from market.goods WHERE good_name LIKE '%"+target+"%'"+
						 " ORDER BY good_id ASC"+" LIMIT "+depage[0]+","+depage[1];
			  
			  managerGetGoodsList_query_count="SELECT count(*) from market.goods WHERE good_name LIKE '%"+target+"%'";
    		  }else {
    			 managerGetGoodsList_query= "SELECT * from market.goods "+
 						 " ORDER BY good_id ASC"+" LIMIT "+depage[0]+","+depage[1];
 			  
 			  managerGetGoodsList_query_count="SELECT count(*) from market.goods";
    		  }
	    	   }else {
	    		   if(!target.equals("")) {   
	    	managerGetGoodsList_query= "select * from goods WHERE good_divide = '"
	    			 +type+"' AND  good_name LIKE '%"+target+"%' ORDER BY good_id ASC"+" LIMIT "+depage[0]+","+depage[1];
	    	
	    	managerGetGoodsList_query_count="SELECT count(*) from market.goods WHERE good_divide ='"+type+"' AND  good_name LIKE '%"+target+"%'";
	    	   }else {
	    		   managerGetGoodsList_query= "select * from goods WHERE good_divide = '"
	  	    			 +type+"'  ORDER BY good_id ASC"+" LIMIT "+depage[0]+","+depage[1];
	  	    	
	  	    	managerGetGoodsList_query_count="SELECT count(*) from market.goods WHERE good_divide ='"+type+"'"; 		   
	    	   }
	    	   }
		  System.out.println( managerGetGoodsList_query);
    
		    try{
		    	
	        	// 建立查询对象
	        	PreparedStatement pstm = conn.prepareStatement(managerGetGoodsList_query);
	        	PreparedStatement pstm1 = conn.prepareStatement(managerGetGoodsList_query_count);
	        	//执行查询
	        	 r1 = pstm.executeQuery();
	        	 
	        	 r2 = pstm1.executeQuery();
	        	 r2.next();
	        	ret_obj_array =login.Login.resultSetToJsonArry(r1);
	        	
	        	r1.beforeFirst();// 返回第一个（记住不是rs.frist()）,不写的话下面的循环里面没值  
	        	
	        	if(!r1.next()) {
	        		ret_obj.put("status",true);
	        		ret_obj.put("goods","");
	        		ret_obj.put("total",0);
	        	}
	        	else {
	        		total=r2.getInt(1);
	        		
	        		ret_obj.put("goods",ret_obj_array);
	        		ret_obj.put("total",total);
	        		ret_obj.put("status",true);
	        	}
	        
	        } catch (SQLException e) {
	        	
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
