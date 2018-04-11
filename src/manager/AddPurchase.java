package manager;
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

public class AddPurchase extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r1;
	private ResultSet r2;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 JSONObject value;
		 PreparedStatement stmt1;
		 PreparedStatement stmt2; 
		 String goodName;
		 long time=System.currentTimeMillis();
		 
		 int autoIncKey=0;
		 int insertResult;//方法变量定义
		 
	
		 String goods;//输入
		 String importance;
		 String note;
		 
		 System.out.println("manager被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 PrintWriter out=resp.getWriter(); //输出流获取
		  
		 System.out.println(input);
	
		 JSONObject get_obj=JSONObject.fromString(input);
		 
		 goods=get_obj.getString("goods");
		 importance=get_obj.getString("importance");
		 note=get_obj.getString("note");
		 
		 System.out.println(goods);
		 
		 JSONArray get_obj_array=JSONArray.fromObject(goods);//需要对改数组再进行一次json数组的转码
		
		 JSONObject ret_obj = new JSONObject();
		 
	      conn=login.Login.getCon();
	      
	      
	      for (int i = 0; i < get_obj_array.length(); i++) {
				value=(JSONObject) get_obj_array.get(i); 
				try {    
				       String managerAddPurchase_require = "select good_name from goods"
				          		+ " where good_id ='"+value.getString("goodId")+"'";
				       
				       stmt1 = conn.prepareStatement(managerAddPurchase_require);     
				       r1= stmt1.executeQuery(); 
				      
				       if (!r1.next()) {  	  
				    		  ret_obj.put("status", false);
				    		  ret_obj.put("message", "商品"+value.getString("goodId")+"不存在或信息出错");
				    		  break;  
				    	}else {
				    		goodName=r1.getString(1);

				    		if(autoIncKey==0) {
				    			
				    		String managerAddPurchase_insert = "insert into purchase(good_id,good_name,purchase_number,purchase_note,purchase_status,importance,create_time)"
					          		+ "values ('"+value.getInt("goodId")+"','"+goodName+"','"+value.getInt("purchaseNum")+"','"+note+"','"+0+"','"+importance+"','"+time+"')";
				    	
				    		
					       stmt2 = (PreparedStatement)conn.prepareStatement(managerAddPurchase_insert,Statement.RETURN_GENERATED_KEYS);   

					       stmt2.executeUpdate(); 
					       
					       r2=stmt2.getGeneratedKeys();
					       
					       if (!r2.next()){  	  
					    		  ret_obj.put("status", false);
					    		  ret_obj.put("message", "插入采购表失败");
					    		  break;	  
					    	}else {
					    		autoIncKey=r2.getInt(1);//取得ID
					    		System.out.println("zhujian:"+autoIncKey);
					    	}
					       }else {
					    	   String managerAddPurchase_insert = "insert into purchase(purchase_id,good_id,good_name,purchase_number,purchase_note,purchase_status,importance,create_time)"
						          		+ "values ('"+autoIncKey+"','"+value.getInt("goodId")+"','"+goodName+"','"+value.getInt("purchaseNum")+"','"+note+"','"+0+"','"+importance+"','"+time+"')";
					    	
						        stmt2 = conn.prepareStatement(managerAddPurchase_insert);   
						       
						       insertResult=stmt2.executeUpdate(); 
						       if (insertResult == 0) {
						    	   ret_obj.put("status", false);
						    	   ret_obj.put("message", "插入采购表失败");
						    	   break;  
						          }
						       
					       }
				          }
				       }catch (SQLException e) {  	
				        	  e.printStackTrace();
			  
				          } 
		        }//for
	          if(ret_obj.length()!=0) {
	        		ret_obj.put("status",true);
	          }

	        out.print(ret_obj.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  

}
