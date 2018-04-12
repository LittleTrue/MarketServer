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

public class AddLoss extends HttpServlet{

	private static Connection conn = null;
	private ResultSet r1;
	private ResultSet r2;
	private HashMap<String,String> workerInfo;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 String goods;
		  int insertResult;//方法变量定义
		  JSONObject value;
		  PreparedStatement stmt1;
		 PreparedStatement stmt2; 
		 int updateResult;
		 int autoIncKey=0;
			 
		 String workerId="";//进行操作中的员工session控制
		 
		 try {
			workerInfo=login.Login.getWorkerInfoFromToken(req);
			} catch (Exception e1) {
			e1.printStackTrace();
		}
		 workerId=workerInfo.get("workerId");
		
		 
		 System.out.println("manager被访问了");
		
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 PrintWriter out=resp.getWriter();;//输出流获取
		  
		 System.out.println(input);
	
		 JSONObject get_obj=JSONObject.fromString(input);
		 
		 goods=get_obj.getString("goods");
		 JSONArray get_obj_array=JSONArray.fromObject(goods);
		 
		 System.out.println(goods);
		
		 
		 JSONObject ret_obj = new JSONObject();
		 
	      conn=login.Login.getCon();
	      
	      for (int i = 0; i < get_obj_array.length(); i++) {
				value=(JSONObject) get_obj_array.get(i); 
				try {    
				    		if(autoIncKey==0) {
				    		String stockAddLoss_insert = "insert into market.loss(good_id,good_number,loss_case,loss_time,worker_id)"
					          		+ "values ('"+value.getInt("goodId")+"','"+value.getInt("lossNum")+"','"+value.getInt("lossCase")+"','"+ System.currentTimeMillis()+"','"+workerId+"')";
				    	
				    		System.out.println(stockAddLoss_insert);
					        stmt2 = (PreparedStatement)conn.prepareStatement(stockAddLoss_insert,Statement.RETURN_GENERATED_KEYS);   

					       stmt2.executeUpdate(); 
					       
					       r2=stmt2.getGeneratedKeys();
					       
					       if (!r2.next()) {  	  
					    		  ret_obj.put("status", false);
					    		  ret_obj.put("message", "插入损耗表失败");
					    		  break;	  
					    	}else {
					    		autoIncKey=r2.getInt(1);//取得ID
					    		System.out.println("zhujian:"+autoIncKey);
					    	}
					       }else {
					    	   String stockAddLoss_insert = "insert into market.loss(loss_id,good_id,good_number,loss_case,loss_time,worker_id)"
						          		+ "values ('"+autoIncKey+"','"+value.getInt("goodId")+"','"+value.getInt("lossNum")+"','"+value.getInt("lossCase")+"','"+ System.currentTimeMillis()+"','"+workerId+"')";
					    
						        stmt2 = conn.prepareStatement(stockAddLoss_insert);   
						       
						       insertResult=stmt2.executeUpdate(); 
						       if (insertResult == 0) {
						    	   ret_obj.put("status", false);
						    	   ret_obj.put("message", "插入损耗表失败");
						    	   break;  
						          }
						       
					       }
				    		if(ret_obj.length()==0) {//检测是否出错
				    			
				    			 String stockAddLoss_update="UPDATE goods SET good_stock = good_stock -"+value.getInt("lossNum")+
				    		          		" where good_id ="+value.getInt("goodId");
				    			 
				    			 stmt1 = conn.prepareStatement(stockAddLoss_update);
				    			
				    			 updateResult=stmt1.executeUpdate(); 
				    			 
							       if (updateResult == 0) {
							    	   ret_obj.put("status", false);
							    	   ret_obj.put("message", "更新库存失败");
							    	   break;  
							          }
				    			
				    		}else {
				    			ret_obj.put("status", false);
						    	ret_obj.put("message", "出错,不对库存进行更新,请删除这次对应的损耗记录并重试");
				    		}
				       }catch (SQLException e) {  	
				        	  e.printStackTrace();
			  
				          } 
		        }//for
	          
	      	//更新商品库存
	      
	      
	      
	      
	      if(ret_obj.length()==0) {
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
