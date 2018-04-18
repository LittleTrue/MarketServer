package front;

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

public class AddGoods extends HttpServlet {
	private static Connection conn = null;
	private ResultSet r1;
	private ResultSet r2;
	private ResultSet r3;
	private ResultSet r4;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 String input;
		 String goodId;
		 String good;
		 int goodNum;
		 String cart;
		 JSONObject value;
		 int total;
		 int isBind;
		 String cartGoodId;
		 int cartGoodNum;
		 int flag=0;
		 int breakOut=0;
		 String good_price;
		 String good_id;
		 String good_name;
		 float discount;
		 float maxDiscount=0;
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("front被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
		 
		  
		  input=login.Login.getRequestBody(req);
		  
		  JSONObject get_obj=JSONObject.fromString(input);
		  good= get_obj.getString("good");
		  cart= get_obj.getString("cart");
		  discount= Float.parseFloat(get_obj.getString("discount"));
		  
		  JSONObject get_obj_good=JSONObject.fromString(good);
		  JSONArray get_obj_array_cart=JSONArray.fromObject(cart);
		  
		  PrintWriter out=resp.getWriter(); 
		  
		  goodId = get_obj_good.getString("goodId");
		  goodNum = Integer.parseInt(get_obj_good.getString("goodNum"));
		  
		  
		 JSONObject ret_obj = new JSONObject();
		 JSONObject ret_obj_father = new JSONObject();
		 
		 for (int i = 0; i < get_obj_array_cart.length(); i++) {
				value=(JSONObject) get_obj_array_cart.get(i); 
				if(goodId.equals(value.get("goodId"))) {
					 goodNum=goodNum+Integer.parseInt((String) value.get("goodNum"));
				}
				}
	       try {    
	    	   String frontAddGoods_require_price = "select good_name,good_price,good_stock from goods"
		          		+ " where good_id ="+goodId+" AND good_stock -"+goodNum+">= 0";
	    	
		       PreparedStatement stmt1 = conn.prepareStatement(frontAddGoods_require_price);    
		       
		       r1 = stmt1.executeQuery(); 
		       
		       if (!r1.next()){ 
		    	   ret_obj_father.put("message","没有该商品ID或库存");  
		    	   ret_obj_father.put("status",false);
		       }else {
		    	   good_name=r1.getString(1);
		    	   good_id=goodId;
		    	   good_price=r1.getString(2);
		    	   ret_obj.put("good_price",good_price);  
		    		ret_obj.put("good_name",good_name);
		    		ret_obj.put("good_id",good_id);
		    		ret_obj_father.put("info",ret_obj);
		    		ret_obj_father.put("status",true);
		    		
		    	   String frontAddGoods_require = "select activity_id,good_id,is_bind,discount_num from activity"
		 	          		+ " where good_id ="+goodId+" AND good_number <="+goodNum;
		    	 
		    	   PreparedStatement stmt2 = conn.prepareStatement(frontAddGoods_require);    
		 	       
		 	      r2= stmt2.executeQuery();
		 	      r2.last();// 移动到最后  	    		
	    		  total=r2.getRow();// 获得结果集长度  
		    	  r2.beforeFirst();
		    	  
		    	   if (!r2.next()) {  
			    	   ret_obj_father.put("discount",discount);
		    	   }else {
		    		 
			    		 
			    			  isBind=r2.getInt(3);
			    			  if(total==1) {//只有捆绑或单个商品优惠
			    				  if(isBind==0) {
			    					  ret_obj_father.put("discount",discount+r2.getFloat(4));
						    	   }else {
						    		 String frontAddGoods_require_activity = "select activity_id,good_id,good_number,is_bind,discount_num from activity"
							 	         + " where activity_id ="+r2.getFloat(1)+" AND good_id <>"+goodId;
						    		 PreparedStatement stmt3 = conn.prepareStatement(frontAddGoods_require_activity); 
						    		 r3= stmt3.executeQuery();
						    		 if (!r3.next()) {  
								    	   ret_obj_father.put("discount",discount);
							    	   }else {
							    		   do {
							    			  cartGoodId=r3.getString(2);
							    			  cartGoodNum=r3.getInt(3);
							    			  
							    		for (int i = 0; i < get_obj_array_cart.length(); i++) {
							   			  value=(JSONObject) get_obj_array_cart.get(i); 
							   				if(cartGoodId.equals(value.get("goodId"))&&cartGoodNum<=Integer.parseInt((String)value.get("goodNum"))) {
							   					flag=1;//有优惠成立
							   				}
							   				
							   				}
							    		if(flag==0) {
						   					//所需绑定物在购物车中没有,优惠不成立
							    			r3.last();
							    			breakOut=1;
						   				}
							    	   }while(r3.next());
							    		   if(breakOut==1) {
							    			   ret_obj_father.put("discount",discount);
							    		   }else {
							    			   ret_obj_father.put("discount",discount+r2.getFloat(4));
							    		   }
							    		   }
						    	   }
			    			  }else {//多个优惠叠加与一个商品
			    				 
			    				  do { //优惠活动进行循环
			    					  breakOut=0;
			    					  System.out.println(r2.getDouble(1));
			    					  isBind=r2.getInt(4);
					    			   if(isBind==0) {
					    				   if(maxDiscount==0) {
						   						maxDiscount=r2.getFloat(4);
						   					}else if(maxDiscount<r2.getFloat(4)){
						   						maxDiscount=r2.getFloat(4);
						   					}
					    			   }else {
					    				   System.out.println("g");
			    					  String frontAddGoods_require_activity = "select activity_id,good_id,good_number,is_bind,discount_num from activity"
									 	         + " where activity_id ="+r2.getFloat(1)+" AND good_id <>"+goodId;
								     PreparedStatement stmt4 = conn.prepareStatement(frontAddGoods_require_activity); 
								    	r3= stmt4.executeQuery();
								    	 if (!r3.next()) {  
									    	   ret_obj_father.put("discount",discount);
								    	   }else { //查出该优惠所有商品进行循环
								    		   do {
								    			   flag=0;
								    			  cartGoodId=r3.getString(2);
								    			  cartGoodNum=r3.getInt(3);
								    			  System.out.println("ID"+r3.getString(1));
								    			  System.out.println("cartGoodNum:"+cartGoodNum);
								    		for (int i = 0; i < get_obj_array_cart.length(); i++) {
								   			  value=(JSONObject) get_obj_array_cart.get(i); 
								   				if(cartGoodId.equals(value.get("goodId"))&&cartGoodNum<=Integer.parseInt((String)value.get("goodNum"))) {
								   					flag=1;//这个商品在购物车满足,优惠成立
								   					System.out.println("haha");
								   				}
								   				
								   				}
								    		if(flag==0) {
							   					//所需绑定物在购物车中没有,这次优惠不成立
								    		    r3.last();
								    			breakOut=1;
							   				}
								    	   }while(r3.next());
								    		 if(breakOut!=1) {
								    		   if(maxDiscount==0) {
							   						maxDiscount=r2.getFloat(4);
							   					}else if(maxDiscount<r2.getFloat(4)){
							   						maxDiscount=r2.getFloat(4);
							   					}
								    		   }
								    		 }
					    			   }//isbind=1
			    				  }while(r2.next());
			    				  
					    	 ret_obj_father.put("discount",discount+maxDiscount);
 
		       }
		    	   }
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
		 	
		 	
	        out.print(ret_obj_father.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  
}
