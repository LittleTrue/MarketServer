package front;

import java.sql.Statement;
import java.time.LocalDateTime;
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
import java.util.Iterator;

import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;

public class GoodsClean extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r1;
	private ResultSet r2;
	private HashMap<String,String> workerInfo;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 JSONObject value;
		 int autoIncKey=0;
		 int insertResult;
		 int[]  updateResult;
		 float good_price;
		 float activity_price;
		 PreparedStatement stmt1;
		 PreparedStatement stmt2; 
		 
		 
		 LocalDateTime localtime=LocalDateTime.now();
		
		 String time=localtime.toString();
		 int userIntegral=0;
		 int newuserIntegral = 0;
		 
		 String frontGoodsClean_update;
		 
		 String workerId="";//进行操作中的员工session控制
		 
		 try {
			workerInfo=login.Login.getWorkerInfoFromToken(req);
			} catch (Exception e1) {
			e1.printStackTrace();
		}
		 
		  workerId=workerInfo.get("workerId");
		 
		 
		 
		 
		 float pay;
		 String order;
		 String userId;
		 
		 
		 String user_integral;
		 
		 System.out.println("front被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);
		 PrintWriter out=resp.getWriter();   
		 
		 System.out.println(input);
	
		 
		 JSONObject get_obj=JSONObject.fromString(input);
		 
		 pay=Float.parseFloat(get_obj.getString("pay"));
		 userId=get_obj.getString("userId");
		 System.out.println(userId);
		 
		 if(userId.equals("")) {
			 userId="0";
		 }
		 order=get_obj.getString("order");
		 
		 JSONObject ret_obj = new JSONObject();
		 
		 System.out.println(pay);
			
	      
		 JSONArray get_obj_array=JSONArray.fromObject(order);//需要对改数组再进行一次json数组的转码
		 try { 
			 conn=login.Login.getCon();
		     conn.setAutoCommit(false); 
			 Statement stmt3 =conn.createStatement();
		 
		 for (int i = 0; i < get_obj_array.length(); i++) {
			value=(JSONObject) get_obj_array.get(i); 
			  
			       String frontAddOrderGoods_require = "select good_price,good_name from goods"
			          		+ " where good_id ='"+value.getString("goodId")+"'";
			       
			       stmt1 = conn.prepareStatement(frontAddOrderGoods_require);     
			       r1= stmt1.executeQuery(); 
			      
			       if (!r1.next()) {  	  
			    		  ret_obj.put("status", false);
			    		  ret_obj.put("message", "商品不存在或信息出错");
			    		  break;  
			    	}else {
			    		
			    		frontGoodsClean_update="update goods set good_stock = good_stock -"+value.getInt("goodNum")
		          		+ " where good_id = "+value.getInt("goodId");
			    		
			    		stmt3.addBatch(frontGoodsClean_update);
			    		
			    		good_price=r1.getFloat(1);
//			    		activity_price=r1.getFloat(3);
//			    		
//			    		if(activity_price!=0) {
//			    			good_price=activity_price;
//			    		}
			    		
			    		if(autoIncKey==0) {
			    		String frontAddOrderGoods_insert = "insert into market.order(good_id,good_name,good_number,good_price,user_id,worker_id,create_time,total_pay)"
				          		+ "values ('"+value.getInt("goodId")+"','"+r1.getString(2)+"','"+value.getInt("goodNum")+"','"+good_price+"','"+userId+"','"+workerId+"','"+ time+"','"+pay+"')";
			    	
			    		
				        stmt2 = (PreparedStatement)conn.prepareStatement(frontAddOrderGoods_insert,Statement.RETURN_GENERATED_KEYS);   

				       stmt2.executeUpdate(); 
				       
				       r2=stmt2.getGeneratedKeys();
				       
				       if (!r2.next()) {  	  
				    		  ret_obj.put("status", false);
				    		  ret_obj.put("message", "插入订单表失败");
				    		  break;	  
				    	}else {
				    		autoIncKey=r2.getInt(1);//取得ID
				    		System.out.println("zhujian:"+autoIncKey);
				    	}
				       }else {
				    		String frontAddOrderGoods_insert = "insert into market.order(order_id,good_id,good_name,good_number,good_price,user_id,worker_id,create_time,total_pay)"
					          		+ "  values ('"+autoIncKey+"','"+value.getString("goodId")+"','"+r1.getString(2)+"','"+value.getString("goodNum")+"','"+good_price+"','"+userId+"','"+workerId+"','"+ time+"','"+pay+"')";
				    
					        stmt2 = conn.prepareStatement(frontAddOrderGoods_insert);   
					       
					       insertResult=stmt2.executeUpdate(); 
					       if (insertResult == 0) {
					    	   ret_obj.put("status", false);
					    	   ret_obj.put("message", "插入订单表失败");
					    	   break;  
					          }
					       
				       }
			    		
			          }     
	        }//for 
		 updateResult=stmt3.executeBatch();
	     	conn.commit();
	       if (updateResult[0] == 0) {
	    	   ret_obj.put("status", false);
	    	   ret_obj.put("message", "更新库存状态失败");
	          }
	       
		 }catch (SQLException e) {  	
			 e.printStackTrace();
	  
		 	} 
		 
		 if(userId.equals("")) {
		 try {
		 String frontUser_require = "select user_integral from market.user"
	          		+ " where user_id ='"+userId+"'";
		 
	     stmt1 = conn.prepareStatement(frontUser_require); 
	    
			r1= stmt1.executeQuery();
			
			if (!r1.next()) {  	  
		   		  ret_obj.put("status", false);
		   		  ret_obj.put("message", "会员信息异常");
		   		 	
	     	}else {
	     		 userIntegral = r1.getInt(1);
	     		 newuserIntegral=userIntegral+(int)pay;
	     		 System.out.println(newuserIntegral);
	     	}
			String frontUser_insert = "update market.user SET user_integral ="+newuserIntegral+" where user_id="+userId;
	          		
	    	 stmt2= conn.prepareStatement(frontUser_insert);  
	    	 
	    	 insertResult=stmt2.executeUpdate(); 
		       if (insertResult == 0) {
		    	   ret_obj.put("status", false);
		    	   ret_obj.put("message", "积分更新失败");  
		          }else {
		        	ret_obj.put("status",true);
			    	ret_obj.put("user_integral",newuserIntegral);  
		          }
	     stmt1.close(); 
     	 stmt2.close(); 
		 }
		  catch(SQLException e) {
              e.printStackTrace();
         }finally { 
             try {
            	
                 conn.close(); 
            } catch(SQLException e) {
                 e.printStackTrace();
            }
        }
		 }else {
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
