package manager;
import java.io.BufferedReader;
import java.io.IOException;  
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;  
  
import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;
import javax.sound.midi.SysexMessage;

import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import java.time.*;
import java.time.temporal.ChronoUnit;

public class GetProfitInfo extends HttpServlet{
	private ResultSet r5;
	private ResultSet r;
	private static Connection conn = null;
	private HashMap<String,String> workerInfo;
	private ResultSet r1;
	private ResultSet r2;
	private ResultSet r3;
	private ResultSet r4;
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		//方法变量定义
		 String type;//输入as
		 String date;
		 float totalPay =0;
		 float profit= 0;
		 LocalDate prevWeek0 = LocalDate.now();
		 int flag=0;
		 float all = 0;
		 int order = 0;
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("manager被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 

		  type = req.getParameter("type");////key -value get方式获取url的键值对 
		  
		  System.out.println(type);
	
		 PrintWriter out=resp.getWriter();;//输出流获取
		
		 JSONObject ret_obj = new JSONObject();
		 JSONObject ret_obj_father = new JSONObject();
		 JSONArray ret_obj_array_time = new JSONArray();
		 
		 JSONArray ret_obj_array_profit = new JSONArray();
		 
		 JSONArray ret_obj_array_revenue = new JSONArray();
		 
	       try {  
	    	   System.out.println(prevWeek0);
	    	   String sql_for_day_pro="SELECT  SUM(total_pay),COUNT(DISTINCT order_id),DATE_FORMAT(a.create_time, '%Y-%m-%d') AS time FROM  market.order a "+
	    				"WHERE DATE_FORMAT(a.create_time, '%Y-%m-%d') = '"+prevWeek0+"'  GROUP BY  time ";
   		   System.out.println(sql_for_day_pro);
   	       PreparedStatement stmt5 = conn.prepareStatement(sql_for_day_pro);  
   	       
   	       r5= stmt5.executeQuery(); 
   	       if(!r5.next()) { 
   	    	   all=0;
   	    	  order=0;
   	    	   }else {
   	    		all=r5.getFloat(1);
   	    	    order=r5.getInt(2);
   	    	   }
 
	    	   if(type.equals("week")) {
	    		   
	    		   LocalDate prevWeek1 = LocalDate.now().minus(1, ChronoUnit.WEEKS);
	    		  
	    		  
	    		   
	    		   String sql_for_week_pro="SELECT  SUM(total_pay) as revenue,SUM((b.good_price-instock_price)*good_number) as profit,DATE_FORMAT(a.create_time, '%Y-%m-%d') AS time FROM  market.order a JOIN market.goods b ON a.good_id=b.good_id "+
		    				"WHERE DATE_FORMAT(a.create_time, '%Y-%m-%d') >= '"+prevWeek1+"' AND DATE_FORMAT(a.create_time, '%Y-%m-%d') < '"+prevWeek0+"' GROUP BY  time ";
	    		   System.out.println(sql_for_week_pro);
	    	       PreparedStatement stmt = conn.prepareStatement(sql_for_week_pro);  
	    	       
	    	       r= stmt.executeQuery(); 
	    	       if(!r.next()) {
	    	    	   ret_obj.put("status", false);
	    	    	   ret_obj.put("message", "统计查询出错");
	    	    	   out.print(ret_obj.toString());  
	    		       out.flush();
	    	       }else {
	    	    	   LocalDate prevday = LocalDate.now();
	    	    	   
	    	    	   for(int i=0;i<=6;i++) {
	    	    		   prevday=prevday.minus(1, ChronoUnit.DAYS);
	    	    		   flag=0;
	    	    		   System.out.println("hha"+prevday);
	    	    		   do{ 
	    	    	    	   date=r.getString(3);
	    	    	    	   System.out.println(date);
	    	    	    	if(date.equals(prevday.toString())) {
	    	    	    		flag=1;
	    	    	    		totalPay=r.getFloat(1);
	    	    	    		 profit=r.getFloat(2);
	    	    	    		System.out.println("YES");
	    	    	    	}
	    	    	    	   }while(r.next());
	    	    		   
	    	    		   if(flag==1) {
	    	    			   ret_obj_array_time.put(prevday.toString());
	    	    			   ret_obj_array_revenue.put(totalPay);
	    	    			   ret_obj_array_profit.put(profit);
	    	    		   }else {
	    	    			   ret_obj_array_time.put(prevday.toString());
	    	    			   ret_obj_array_revenue.put(0);
	    	    			   ret_obj_array_profit.put(0);
	    	    		   }
	    	    		   r.first();
	    	    	   }
	    	    	  
	    	    	   }
	    	    	   
	    	       }else {
	    	    	   LocalDate prevWeek1 = LocalDate.now().minus(1, ChronoUnit.WEEKS);
	    	    	   LocalDate prevWeek2 = LocalDate.now().minus(2, ChronoUnit.WEEKS);
		    		   LocalDate prevWeek3 = LocalDate.now().minus(3, ChronoUnit.WEEKS);
		    		   LocalDate prevWeek4 = LocalDate.now().minus(4, ChronoUnit.WEEKS);
		    		   
	    	     String sql_for_month_pro1="SELECT  SUM(total_pay) as revenue,SUM((b.good_price-instock_price)*good_number) as profit FROM  market.order a JOIN market.goods b ON a.good_id=b.good_id "+
			    				"WHERE DATE_FORMAT(a.create_time, '%Y-%m-%d') >= '"+prevWeek1+"' AND DATE_FORMAT(a.create_time, '%Y-%m-%d') < '"+prevWeek0+"'";
	    	     String sql_for_month_pro2="SELECT  SUM(total_pay) as revenue,SUM((b.good_price-instock_price)*good_number) as profit FROM  market.order a JOIN market.goods b ON a.good_id=b.good_id "+
		    				"WHERE DATE_FORMAT(a.create_time, '%Y-%m-%d') >= '"+prevWeek2 +"' AND DATE_FORMAT(a.create_time, '%Y-%m-%d') < '"+prevWeek1+"'";
	    	     String sql_for_month_pro3="SELECT  SUM(total_pay) as revenue,SUM((b.good_price-instock_price)*good_number) as profit FROM  market.order a JOIN market.goods b ON a.good_id=b.good_id "+
		    				"WHERE DATE_FORMAT(a.create_time, '%Y-%m-%d') >= '"+prevWeek3+"' AND DATE_FORMAT(a.create_time, '%Y-%m-%d') < '"+prevWeek2+"'";
	    	     String sql_for_month_pro4="SELECT  SUM(total_pay) as revenue,SUM((b.good_price-instock_price)*good_number) as profit FROM  market.order a JOIN market.goods b ON a.good_id=b.good_id "+
		    				"WHERE DATE_FORMAT(a.create_time, '%Y-%m-%d') >= '"+prevWeek4+"' AND DATE_FORMAT(a.create_time, '%Y-%m-%d') < '"+prevWeek3+"'";
		         
		    	 PreparedStatement stmt1 = conn.prepareStatement(sql_for_month_pro1);  
		    	 PreparedStatement stmt2 = conn.prepareStatement(sql_for_month_pro2);
	    		 PreparedStatement stmt3 = conn.prepareStatement(sql_for_month_pro3);
	    		 PreparedStatement stmt4 = conn.prepareStatement(sql_for_month_pro4);
	    		 System.out.println(sql_for_month_pro2);
	    		 System.out.println(sql_for_month_pro3);
	    		   r1= stmt1.executeQuery(); 
	    		   r2= stmt2.executeQuery(); 
	    		   r3= stmt3.executeQuery(); 
	    		   r4= stmt4.executeQuery(); 
	    		   r1.next();
	    		   r2.next();
	    		   r3.next();
	    		   r4.next();
	    		   ret_obj_array_time.put(prevWeek1.toString());
	    		   ret_obj_array_time.put(prevWeek2.toString());
	    		   ret_obj_array_time.put(prevWeek3.toString());
	    		   ret_obj_array_time.put(prevWeek4.toString());
	    		   
    			   ret_obj_array_revenue.put(r1.getFloat(1));
    			   ret_obj_array_revenue.put(r2.getFloat(1));
    			   ret_obj_array_revenue.put(r3.getFloat(1));
    			   ret_obj_array_revenue.put(r4.getFloat(1));
    			   
    			   ret_obj_array_profit.put(r1.getFloat(2));
    			   ret_obj_array_profit.put(r2.getFloat(2));
    			   ret_obj_array_profit.put(r3.getFloat(2));
    			   ret_obj_array_profit.put(r4.getFloat(2));
	    		
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
	       ret_obj_father.put("time",ret_obj_array_time);
    	   ret_obj_father.put("revenue",ret_obj_array_revenue);
    	   ret_obj_father.put("profit",ret_obj_array_profit);
    	   ret_obj_father.put("all",all);
    	   ret_obj_father.put("orders",order);
    	
    	
    	   
	       ret_obj.put("status",true);
	       ret_obj.put("info",ret_obj_father);
	        out.print(ret_obj.toString());  
	        out.flush();  
	       
	    }  
	 
	 @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  

}
