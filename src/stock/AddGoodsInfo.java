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
import net.sf.json.JSONException;


public class AddGoodsInfo extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;
		 
		 int insertResult;//方法变量定义
		 
		 String goodDescribe;
		 String goodAttr;
		 String goodDivide;
		 String cost;
		 String goodName;
		 String supplier;
		 String warmNum;//输入
		 String good;
		 String goodPrice;
		 
		 System.out.println("front被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 
		  PrintWriter out=resp.getWriter(); //输出流获取
		  
		 System.out.println(input);
	
		 JSONObject get_obj=JSONObject.fromString(input);
		 good=get_obj.getString("good");
		 
		  get_obj=JSONObject.fromString(good);
		 
		 
		 goodDescribe=get_obj.getString("goodDescribe");
		 goodAttr=get_obj.getString("goodAttr");
		 goodDivide=get_obj.getString("goodDivide");
		 cost=get_obj.getString("cost");
		 goodName=get_obj.getString("goodName");
		 supplier=get_obj.getString("supplier");
		 warmNum=get_obj.getString("warmNum");
		 goodPrice=get_obj.getString("goodPrice");
		 System.out.println(warmNum);
		 
		 JSONObject ret_obj = new JSONObject();
		 
	      conn=login.Login.getCon();
	      
	      
	       try {    
	       String stockAddGoodsInfo_require = "select * from market.goods"
	          		+ " where good_name ='"+goodName+"' AND good_attr ='"+goodAttr+"'";
	       System.out.println( stockAddGoodsInfo_require);
	       
	       PreparedStatement stmt1 = conn.prepareStatement(stockAddGoodsInfo_require);     
	       
	       r= stmt1.executeQuery(); 
	     
	       if (r.next()) {  	  
	    		  ret_obj.put("status", false);
	    		  ret_obj.put("message", "商品已存在");
	    	}else {	
	    		String stockAddGoodsInfo_insert = "insert into market.goods(good_name,good_attr,good_divide,good_supplier,warn_stock,good_describe,good_price,instock_price)"
	    				+ "values ('"+goodName+"','"+goodAttr+"','"+goodDivide+"','"+supplier+"','"+warmNum+"','"+goodDescribe+"','"+goodPrice+"','"+cost+"')";
	    		System.out.println(stockAddGoodsInfo_insert);
	    		PreparedStatement stmt2 = conn.prepareStatement(stockAddGoodsInfo_insert);     
	    		insertResult= stmt2.executeUpdate(); 
	    		 if (insertResult==0) {  	  
		    		  ret_obj.put("status", false);
		    		  ret_obj.put("message", "添加商品失败");
		    	}else {
		    	   	ret_obj.put("status",true);
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
	          
	         
	        out.print(ret_obj.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  

}
