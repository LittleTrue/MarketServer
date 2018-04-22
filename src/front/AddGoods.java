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
import java.util.Arrays;
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
		 String cart;
		 
		 JSONObject value;
		 JSONObject  valueForBind;
		 JSONObject  valueFordiscount;
		 int total;
		 int isBind;
		 
		 String cartGoodId = null;
		 int cartGoodNum = 0;
		 
		 int flag=0;
		 int breakOut=0;
		 
//		 String good_price;
		 String goodId;
		 int goodNum;
		 
		 float discount = 0;
		 float maxDiscount=0;
		 
		 String cartGoodIdForBind= null;
		 int cartGoodNumForBind = 0;
		 
		 JSONArray discountList =new JSONArray();
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("front被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
		 
		  
		  input=login.Login.getRequestBody(req);
		  
		  JSONObject get_obj=JSONObject.fromString(input);
		  //good= get_obj.getString("good");
		  cart= get_obj.getString("cartList");
		 // discount= Float.parseFloat(get_obj.getString("discount"));
		  
		  //JSONObject get_obj_good=JSONObject.fromString(good);
		  JSONArray get_obj_array_cart=JSONArray.fromObject(cart);
		  
		  PrintWriter out=resp.getWriter(); 
		  
		//  goodId = get_obj_good.getString("goodId");
		 // goodNum = Integer.parseInt(get_obj_good.getString("goodNum"));
		  //System.out.println(goodId);
		  
		 JSONObject ret_obj = new JSONObject();
		 JSONObject ret_obj_father = new JSONObject();
		 
//		 for (int i = 0; i < get_obj_array_cart.length(); i++) {
//				value=(JSONObject) get_obj_array_cart.get(i); 
//				System.out.println(value.getString("goodId"));
//				if(goodId.equals(value.getString("goodId"))) {
//					
//					 goodNum=goodNum+Integer.parseInt((String) value.getString("goodNum"));
//				}
//				}
		   try {  
			   
		 for (int i = 0; i < get_obj_array_cart.length(); i++) {//循环cart
  			  value=(JSONObject) get_obj_array_cart.get(i); 
  			 goodId=value.getString("goodId");
  			 goodNum=value.getInt("goodNum");
  			 
  			  System.out.println(goodId);

	    	   String frontAddGoods_require = "select activity_id,good_id,is_bind,discount_num from activity"
	 	          		+ " where good_id ="+goodId+" AND good_number <="+goodNum;
	    	   PreparedStatement stmt1 = conn.prepareStatement(frontAddGoods_require); 
	    	   
	    	   r1= stmt1.executeQuery();
	    	   
		 	   r1.last();// 移动到最后  	    		
	    	   total=r1.getRow();// 获得结果集长度  
		       r1.beforeFirst();
		    	  
		      if (r1.next()) {  //该商品有优惠成立
//		    	  int idArr[]={};
//		    	  int idArr_length = 0;
		    	  isBind=r1.getInt(3);
    			  if(total==1) {//只有一种捆绑或单个商品优惠
    				  if(isBind==0) {//只是单个商品优惠
    					 discount=discount+r1.getFloat(4);
			    	   }else {  //寻找当次购物车遍历当前商品捆绑的优惠商品
			    		   String frontAddGoods_require_activity = "select activity_id,good_id,good_number,is_bind,discount_num from activity"
						 	         + " where activity_id ="+r1.getFloat(1)+" AND good_id <>"+goodId;
			    		   
					    		 System.out.println(frontAddGoods_require_activity);
					    		 
					    		 PreparedStatement stmt2 = conn.prepareStatement(frontAddGoods_require_activity); 
					    		 r2= stmt2.executeQuery();
					    		 
					    		 if (r2.next()) {
					    			 do {//当前遍历商品的捆绑商品遍历
					    				
					    				 flag=0;
						    			  cartGoodId=r2.getString(2);
						    			  cartGoodNum=r2.getInt(3);
						    			 
						    		for (int j = i+1; j < get_obj_array_cart.length(); j++) {//在cart中寻找捆绑商品的存在 继承没有循环完的i
						    			
						    			valueForBind=(JSONObject)get_obj_array_cart.get(j); 
						    			cartGoodIdForBind=valueForBind.getString("goodId");
						    			cartGoodNumForBind=valueForBind.getInt("goodNum");
						    			  	   
						   			  value=(JSONObject) get_obj_array_cart.get(j); 
						   			  
//						   			  idArr[idArr_length] = Integer.parseInt(goodId);
//						   			idArr_length++;
						   			
						   				if(cartGoodIdForBind.equals(r2.getString(2))&& cartGoodNumForBind >=r2.getInt(3)) {
						   					System.out.println("开始内循环"+cartGoodIdForBind);
						   					flag=1;//有优惠成立
//						   					idArr[idArr_length] = Integer.parseInt(cartGoodIdForBind);
//						   					idArr_length++;
						   					System.out.println("HH");
						   				}
						   				
						    		}//for2
						    		
						    		if(flag==0) {
					   					//所需绑定物有一个都没有,优惠不成立
						    			r2.last();
						    			breakOut=1;
					   				}
						    			
						    	   }while(r2.next());  
						    	   
						    	   if(breakOut==0) {//优惠成立
						    		  
//						    		  for(int k = 0;k < discountList.length();k++) {
//						    			  valueFordiscount=(JSONObject) get_obj_array_cart.get(i); 
//						    			  Arrays.sort(valueFordiscount,idArr);
//					                        Arrays.sort(idArr);
//					                        if (Arrays.equals(array1, array2)) {
//					                                System.out.println("两个数组中的元素值相同");
//					                        } else {
//					                                System.out.println("两个数组中的元素值不相同");
//					                        }
//						    		  }
//						    		  if(discountList.length() == 0) {
//								    		  JSONObject discountInfo = new JSONObject();
//								    		  
//								    		  discountInfo.put("idArr", idArr);
//								    	
//								      }
						    		   
					    			  discount=discount+r1.getFloat(4);
					    		   }
					    			 	
			    	   		}//捆绑商品存在结果集	   
			                   }//捆绑商品
    			  }else {//多个活动
    			  
    				  do { //优惠活动进行循环
    					  breakOut=0;
    					 
    					  isBind=r1.getInt(3);
		    			   if(isBind==0) {
		    				   if(maxDiscount==0) {
			   						maxDiscount=r1.getFloat(4);
			   					}else if(maxDiscount<r1.getFloat(4)){
			   						maxDiscount=r1.getFloat(4);
			   					}
		    			   }else {
		    				   System.out.println("g");
    					  String frontAddGoods_require_activity = "select activity_id,good_id,good_number,is_bind,discount_num from activity"
						 	         + " where activity_id ="+r1.getFloat(1)+" AND good_id <>"+goodId;
					     PreparedStatement stmt3 = conn.prepareStatement(frontAddGoods_require_activity); 
					    	r3= stmt3.executeQuery();
					    	 if (r3.next()) {  
					    		   do {
					    			   flag=0;
					    			  cartGoodId=r3.getString(2);
					    			  cartGoodNum=r3.getInt(3);
					    			  
					    			  System.out.println("ID"+r3.getString(1));
					    			  System.out.println("cartGoodNum:"+cartGoodNum);
					    			  
					    		for (int j = i+1; j < get_obj_array_cart.length(); j++) {
					    			valueForBind=(JSONObject) get_obj_array_cart.get(j); 
					   			  cartGoodIdForBind=valueForBind.getString("goodId");
					   			  cartGoodNumForBind=valueForBind.getInt("goodNum");
				    			
					   				if(cartGoodId.equals(r3.getString(2))&&cartGoodNum>=r3.getInt(3)){
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
				   						maxDiscount=r1.getFloat(4);
				   					}else if(maxDiscount<r1.getFloat(4)){
				   						maxDiscount=r1.getFloat(4);
				   					}
					    		   }
					    		 }//存在结果集
		    			   }//isbind=1
    				  }while(r1.next());
    				  discount=discount+maxDiscount;
    			  }
		      }//for1
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
		   ret_obj_father.put("discount",discount);
		   ret_obj_father.put("status",true);
		   
	        out.print(ret_obj_father.toString());  
	        out.flush(); 		   
		    		   
		    	   
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  
}
