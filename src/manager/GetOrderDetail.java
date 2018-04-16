package manager;

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

public class GetOrderDetail  extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;
	private HashMap<String,String> workerInfo;

	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		//方法变量定义
		 
		 String orderId;//输入
		 
		 int total;//输出
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("manager被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 

		 orderId = req.getParameter("order_id");////key -value get方式获取url的键值对 
		  
		PrintWriter out=resp.getWriter();//输出流获取
		
		 JSONObject ret_obj = new JSONObject();
		 
		 JSONArray ret_obj_array = new JSONArray();
		 
	       try {    
	       String stockGetPurchaseListDetail_require = "select order_id,a.good_id,good_number,a.good_price,a.good_name,good_attr FROM market.order a JOIN goods b ON a.good_id=b.good_id"
	          +" where order_id = "+orderId;
	       System.out.println(stockGetPurchaseListDetail_require);
	       PreparedStatement stmt = conn.prepareStatement(stockGetPurchaseListDetail_require);     
	       r= stmt.executeQuery(); 
	       ret_obj_array =login.Login.resultSetToJsonArry(r);
       	
       	r.beforeFirst();// 返回第一个（记住不是rs.frist()）,不写的话下面的循环里面没值  
       	
       	if(!r.next()) {
       		ret_obj.put("status",false);
       		ret_obj.put("message","无order_id或当前id没有商品信息");
       	}
       	else {
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
