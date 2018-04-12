package stock;
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

public class FinishPurchase extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r1;
	private ResultSet r2;
	private HashMap<String,String> workerInfo;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 JSONObject value;
		 int autoIncKey=0;
		 int insertResult;
		  int[] updateResult;//方法变量定义
		  
		 String list;
		PreparedStatement stmt2;
		String stockFinishPurchase_update;
		String stockFinishPurchase_update_for_stock;
		
		 
		 String workerId;
		 String workerPosition;//进行操作中的员工session控制
		 
		 try {
			workerInfo=login.Login.getWorkerInfoFromToken(req);
			} catch (Exception e1) {
			e1.printStackTrace();
		}
		 
		 //TOken,,,
		 String use_integral;//输出
		 
		 System.out.println("stock6被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		input=login.Login.getRequestBody(req);//输入流获取
		PrintWriter out=resp.getWriter();;//输出流获取
		
		 System.out.println(input);
		 
		 JSONObject get_obj=JSONObject.fromObject(input);
		 
		 list=get_obj.getString("list");
		 
		 JSONArray get_obj_array=JSONArray.fromObject(list);
		 
		 JSONObject ret_obj = new JSONObject();
		 	try { 
		 		 conn=login.Login.getCon();
			     conn.setAutoCommit(false); 
				 Statement stmt1 =conn.createStatement();
				
	      for (int i = 0; i < get_obj_array.length(); i++) {
				value=(JSONObject) get_obj_array.get(i); 
			  
				        stockFinishPurchase_update = "update purchase set purchase_status ="+value.getInt("purchase_status")
				          		+ " where purchase_id = "+value.getInt("purchase_id")+" AND good_id="+value.getInt("good_id");
//				       
				        stmt1.addBatch(stockFinishPurchase_update);
				        
				        stockFinishPurchase_update_for_stock = "update goods set good_stock = good_stock +"+value.getInt("purchase_number")
		          		+ " where good_id = "+value.getInt("good_id");
				        
				        if(value.getInt("purchase_status")==1) {
				        stmt1.addBatch( stockFinishPurchase_update_for_stock);
				      
				    	if(autoIncKey==0) {
				    		String stockFinishPurchase_insert = "insert into stock(good_id,instock_number,stock_time,worker_id)"
					          		+ "values ('"+value.getInt("good_id")+"','"+value.getInt("purchase_number")+"','"+System.currentTimeMillis()+"','"+workerInfo.get("workerId")+"')";
				    	System.out.println(stockFinishPurchase_insert);
					        stmt2 = (PreparedStatement)conn.prepareStatement(stockFinishPurchase_insert,Statement.RETURN_GENERATED_KEYS);   

					       stmt2.executeUpdate(); 
					       r2=stmt2.getGeneratedKeys();
					       
					       if (!r2.next()) {  	  
					    		  ret_obj.put("status", false);
					    		  ret_obj.put("message", "插入入库失败");
					    		  break;	  
					    	}else {
					    		autoIncKey=r2.getInt(1);//取得ID
					    		System.out.println("zhujian:"+autoIncKey);
					    	}
					       }else {
					    		String stockFinishPurchase_insert = "insert into stock(stock_id,good_id,instock_number,stock_time,worker_id)"
						          		+ "  values ('"+autoIncKey+"','"+value.getInt("good_id")+"','"+value.getInt("purchase_number")+"','"+ System.currentTimeMillis()+"','"+workerInfo.get("workerId")+"')";
					    		System.out.println(stockFinishPurchase_insert);
						        stmt2 = conn.prepareStatement(stockFinishPurchase_insert);   
						       
						       insertResult=stmt2.executeUpdate(); 
						       if (insertResult == 0) {
						    	   ret_obj.put("status", false);
						    	   ret_obj.put("message", "插入入库表失败");
						    	   break;  
						       }
						       
					       }
				        	}
		        }//for
	    	
			       updateResult=stmt1.executeBatch();
			     	conn.commit();
			       if (updateResult[0] == 0) {
			    	   ret_obj.put("status", false);
			    	   ret_obj.put("message", "更新采购表状态失败");
			          }else {
			        	  ret_obj.put("status",true);
				    	  ret_obj.put("message", "更新采购表状态,更新库存及入库成功"); 
			          }
			     
		          }catch (SQLException e) {  	
		        	  e.printStackTrace();
		          }
	          
	         
	 		out.print(ret_obj.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  

}
