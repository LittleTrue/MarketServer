package manager;

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
import net.sf.json.JSONArray;
import net.sf.json.JSONException;

public class GetWorker extends HttpServlet  {
	private static Connection conn = null;
	private ResultSet r;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 int autoIncKey=0;
		 int total=0;
		 int insertResult;//方法变量定义
	     int depage[]= {0,1};
		 
		//输入
		 int page; 
		 int size;
		
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("front1被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
		 
		 page = Integer.parseInt(req.getParameter("page"));////key -value get方式获取url的键值对 
		 size = Integer.parseInt(req.getParameter("size"));
		  System.out.println(size);
		  
		  depage=login.Login.getSplitPageInfo(page,size);
		  
		  PrintWriter out=resp.getWriter();   //输出流获取
		  
		 JSONObject ret_obj = new JSONObject();
		 JSONArray ret_obj_array = new JSONArray();
		 
  
		 String sql = "SELECT worker_id as no,worker_name as name,worker_sex as sex,worker_position as job,worker_phone as tel FROM worker where worker_position <>'manager'"+
				 " ORDER BY worker_id ASC"+" LIMIT "+depage[0]+","+depage[1];
		    try{
		    	// 建立Statement对象
	        	Statement stmt = conn.createStatement();
	        	// 建立查询对象
	        	PreparedStatement pstm = conn.prepareStatement(sql);
	        	//执行查询
	        	 r = pstm.executeQuery();
	        	ret_obj_array =login.Login.resultSetToJsonArry(r);
	        	
	        	r.beforeFirst();// 返回第一个（记住不是rs.frist()）,不写的话下面的循环里面没值  
	        	
	        	if(!r.next()) {
	        		ret_obj.put("status",false);
	        		ret_obj.put("message","当前没有员工账号");
	        	}
	        	else {
	        		r.last();// 移动到最后  	    		
		    		total=r.getRow();// 获得结果集长度  
		    		
	        		ret_obj.put("status",true);
	        		ret_obj.put("info",ret_obj_array);
	        		ret_obj.put("total",total);
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
