package accountant;
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

public class SetActivityBind extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;

	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 JSONObject value;
		 long time=System.currentTimeMillis();
		 
		 int insertResult;//方法变量定义
		 int autoIncKey=0;
		String price;
		 
		 String goods;//输入
		 System.out.println("acountant被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 PrintWriter out=resp.getWriter();;//输出流获取
		  
		 System.out.println(input);
	
		 JSONObject get_obj=JSONObject.fromString(input);
		
		 goods=get_obj.getString("goodsList");
		 price=get_obj.getString("discount");
		 JSONObject ret_obj = new JSONObject();
		 
		 JSONArray get_obj_array=JSONArray.fromObject(goods);//需要对改数组再进行一次json数组的转码
		 try { 
			 conn=login.Login.getCon();
				 
		 for (int i = 0; i < get_obj_array.length(); i++) {
			value=(JSONObject) get_obj_array.get(i); 
			  
			
			if(autoIncKey==0) {
				   String acountantSetActivity_insert = "INSERT INTO activity(good_id,is_bind,discount_num,good_number,create_time)"
		 	          		+ " value("+value.getInt("goodId")+","+1+","+price+","+value.getInt("num")+","+time+")";
	    	
	    		
			 PreparedStatement stmt1 = (PreparedStatement)conn.prepareStatement(acountantSetActivity_insert,Statement.RETURN_GENERATED_KEYS);   

			 	stmt1.executeUpdate(); 
		       
		       r=stmt1.getGeneratedKeys();
		       
		       if (!r.next()) {  	  
		    		  ret_obj.put("status", false);
		    		  ret_obj.put("message", "插入活动表失败");
		    		  break;	  
		    	}else {
		    		autoIncKey=r.getInt(1);//取得ID
		    		System.out.println("zhujian:"+autoIncKey);
		    	}
		       }else {
		    	   String acountantSetActivity_insert = "INSERT INTO activity(activity_id,good_id,is_bind,discount_num,good_number,create_time)"
		 	          		+ " value("+autoIncKey+","+value.getInt("goodId")+","+1+","+price+","+value.getInt("num")+","+time+")";
		    
		    	   PreparedStatement stmt1 = conn.prepareStatement(acountantSetActivity_insert);   
			       
			       insertResult=stmt1.executeUpdate(); 
			       if (insertResult == 0) {
			    	   ret_obj.put("status", false);
			    	   ret_obj.put("message", "商品"+value.getInt("goodId")+"插入活动失败");
			    	   break;  
			       	}
		       }
			    	}//FOR
	
	       }catch (SQLException e) {  	
	        	  e.printStackTrace();
  
	          } finally { 
	               try {
	                   conn.close(); 
	              } catch(SQLException e) {
	                   e.printStackTrace();
	              }
	          }
	         if(ret_obj.length()==0) {
	        	  ret_obj.put("status", true);
	         }

	        out.print(ret_obj.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  
}
