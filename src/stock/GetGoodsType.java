package stock;
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
public class GetGoodsType extends HttpServlet {
	private static Connection conn = null;
	private ResultSet r;
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 //方法变量定义

	
		 
		
		 int total;//输出
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("stock3被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 

		PrintWriter out=resp.getWriter();;//输出流获取
		
		 JSONObject ret_obj = new JSONObject();
		 JSONArray ret_obj_array = new JSONArray();
 
	       try {    
	       String stockGetGoodsType_require = "select distinct good_divide from goods";
	          		
	       PreparedStatement stmt = conn.prepareStatement(stockGetGoodsType_require);     
	       r= stmt.executeQuery(); 
   
	       ret_obj_array =login.Login.resultSetToJsonArry(r);
       	
	       r.beforeFirst();// 返回第一个（记住不是rs.frist()）,不写的话下面的循环里面没值 
	       
	       if (!r.next()) {  	  
	    	   ret_obj.put("status",true);
       		ret_obj.put("info","");
       		ret_obj.put("total",0);
	    	}else {
	    		r.last();// 移动到最后  	    		
	    		total=r.getRow();// 获得结果集长度  
	    		
        		ret_obj.put("status",true);
        		ret_obj.put("info",ret_obj_array);
        		ret_obj.put("total",total);
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
