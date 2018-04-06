package manager;
import java.sql.Statement;
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

public class AddWorker extends HttpServlet {
	private static Connection conn = null;
	private ResultSet r;
	
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 String input;

		 int insertResult;//方法变量定义
		 
		 String name;
		 String sex;
		 String account;
		 String password;
		 String job;
		 String tel;//输入
		 
		 conn=login.Login.getCon();
		 
		 System.out.println("front被访问了1");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 


		 input=login.Login.getRequestBody(req);//输入流获取
		 
		  PrintWriter out=resp.getWriter(); //输出流获取
		  JSONObject ret_obj = new JSONObject();
		  
		 System.out.println(input);
	
		 JSONObject get_obj=JSONObject.fromString(input);
		 
		 name=get_obj.getString("name");
		 sex=get_obj.getString("sex");
		 account=get_obj.getString("account");
		 password=get_obj.getString("password");
		 job=get_obj.getString("job");
		 tel=get_obj.getString("tel");
		 
		 System.out.println(name);
		 
		 String sql = "insert into worker(worker_account,worker_password,worker_position,worker_phone,worker_name,worker_sex) values('"+account+"','"+password+"','"+job+"','"+tel+"','"+name+"','"+sex+"')";
		    try{
		    	// 建立Statement对象
	        	Statement stmt = conn.createStatement();
	        	//执行数据库查询语句
	        	ResultSet rs = stmt.executeQuery("SELECT * FROM worker WHERE worker_phone='"+tel+"'");
	        	if(rs.next()) {
	        		ret_obj.put("status", "false");
	        		ret_obj.put("message", "员工电话已存在");
	        	}
	        	else {
	        		 // 执行数据库语句
	        		insertResult=stmt.executeUpdate(sql);
	            	
	        		if(insertResult==0) {
	        			ret_obj.put("status", "false");
		        		ret_obj.put("message", "员工添加失败");
	        		}else {
	        			ret_obj.put("status", "true");
	        		}
	        	}
	        } catch (SQLException e) {
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
