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

public class ModifyGoods extends HttpServlet{
	private ResultSet r;
	private static Connection conn = null;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 int modifyResult;//方法变量定义
		 String input;
		 int flag=0;
		 
		 String goodDescribe;
		 String goodAttr;
		 String goodDivide;
		 String cost;
		 String goodName;
		 String supplier;
		 String warmNum;//输入
		 String good;
		 String goodId;
		 String goodStock;
		 
	 
	 conn=login.Login.getCon();
	 
	 System.out.println("stock被访问了");
	 
	 req.setCharacterEncoding("utf-8");
	 resp.setCharacterEncoding("utf-8"); 

	 input=login.Login.getRequestBody(req);//输入流获取
	 PrintWriter out=resp.getWriter();;//输出流获取
	  
	 System.out.println(input);

	 JSONObject get_obj=JSONObject.fromString(input);
	 good=get_obj.getString("good");
	 get_obj=JSONObject.fromString(good);
	 
	 goodId=get_obj.getString("good_id");
	 warmNum=get_obj.getString("warn_stock");
	 supplier=get_obj.getString("good_supplier");
	 goodName=get_obj.getString("good_name");
	 cost=get_obj.getString("instock_price");
	 goodDivide=get_obj.getString("good_divide");
	 goodAttr=get_obj.getString("good_attr");
	 goodDescribe=get_obj.getString("good_describe");
	 goodStock=get_obj.getString("good_stock");
	
	 JSONObject ret_obj = new JSONObject();
	 
	 String sqlupdate="UPDATE goods SET ";
     String where=" WHERE good_id="+ goodId;
     
     if(!warmNum.equals("")) {  
     if(flag==0)
  	  { sqlupdate=sqlupdate+"warn_stock='"+warmNum+"'";
  	   flag=1;}
     }
     
     if(!supplier.equals("")){
  	   if(flag==0)
      	{ sqlupdate=sqlupdate+"good_supplier='"+supplier+"'";
      	   flag=1;}
  	   else {
  		   sqlupdate=sqlupdate+","+"good_supplier='"+supplier+"'"; 
  	   }
     }
     
     if(!goodName.equals("")){
    	   if(flag==0)
        	{ sqlupdate=sqlupdate+"good_name='"+goodName+"'";
        	   flag=1;}
    	   else {
    		   sqlupdate=sqlupdate+","+"good_name='"+goodName+"'"; 
    	   }
       }
     
     if(!cost.equals("")){
  	   if(flag==0)
      	{ sqlupdate=sqlupdate+"instock_price='"+cost+"'";
      	   flag=1;}
  	   else {
  		   sqlupdate=sqlupdate+","+"instock_price='"+cost+"'"; 
  	   }
     }
     
     if(!goodDivide.equals("")){
    	   if(flag==0)
        	{ sqlupdate=sqlupdate+"good_divide='"+goodDivide+"'";
        	   flag=1;}
    	   else {
    		   sqlupdate=sqlupdate+","+"good_divide='"+goodDivide+"'"; 
    	   }
       }
     
     if(!goodAttr.equals("")){
  	   if(flag==0)
      	{ sqlupdate=sqlupdate+"good_attr='"+goodAttr+"'";
      	   flag=1;}
  	   else {
  		   sqlupdate=sqlupdate+","+"good_attr='"+goodAttr+"'"; 
  	   }
     }
     
     if(!goodDescribe.equals("")){
    	   if(flag==0)
        	{ sqlupdate=sqlupdate+"good_describe='"+goodDescribe+"'";
        	   flag=1;}
    	   else {
    		   sqlupdate=sqlupdate+","+"good_describe='"+goodDescribe+"'"; 
    	   }
       }
     if(!goodStock.equals("")){
  	   if(flag==0)
      	{ sqlupdate=sqlupdate+"good_stock='"+goodStock+"'";
      	   flag=1;}
  	   else {
  		   sqlupdate=sqlupdate+","+"good_stock='"+goodStock+"'"; 
  	   }
     }
     try { 
     
     
      String stockModifyGoods_update =sqlupdate+where;
      
      System.out.println(stockModifyGoods_update);
      PreparedStatement stmt1 = conn.prepareStatement(stockModifyGoods_update);     
      modifyResult= stmt1.executeUpdate(); 
 
  		if(modifyResult==0) {  
		  ret_obj.put("status", false);
		  ret_obj.put("message", "修改商品失败");
  		}else {
	   	ret_obj.put("status",true);
    	} 
  		
      stmt1.close();
  
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
