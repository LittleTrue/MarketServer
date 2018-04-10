package manager;
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

public class ModifyWorker extends HttpServlet{
	private ResultSet r;
	private static Connection conn = null;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 int modifyResult;//方法变量定义
		 String input;
		 int flag=0;
		 
		String workerId;//输入
		String workerName;
		String workerTel;
		String workerPosition;
		String workerSex;
		String passwordOld;
		String password;
	 
	 
	 conn=login.Login.getCon();
	 
	 System.out.println("manager5被访问了");
	 
	 req.setCharacterEncoding("utf-8");
	 resp.setCharacterEncoding("utf-8"); 

	 input=login.Login.getRequestBody(req);//输入流获取
	 PrintWriter out=resp.getWriter();;//输出流获取
	  
	 System.out.println(input);

	 JSONObject get_obj=JSONObject.fromString(input);
	
	 workerId=get_obj.getString("no");
	 workerName=get_obj.getString("name");
	 workerTel=get_obj.getString("tel");
	 workerPosition=get_obj.getString("job");
	 workerSex=get_obj.getString("sex");
	 passwordOld=get_obj.getString("password_old");
	 password=get_obj.getString("password");
	  
	
	 JSONObject ret_obj = new JSONObject();
	 
	 String sqlupdate="UPDATE worker SET ";
     String where=" WHERE worker_id="+ workerId;
     
     if(!workerName.equals("")) {  
     if(flag==0)
  	  { sqlupdate=sqlupdate+"worker_name='"+workerName+"'";
  	   flag=1;}
     }
     
     if(!workerTel.equals("")){
  	   if(flag==0)
      	{ sqlupdate=sqlupdate+"worker_phone='"+workerTel+"'";
      	   flag=1;}
  	   else {
  		   sqlupdate=sqlupdate+","+"worker_phone='"+workerTel+"'"; 
  	   }
     }
     
     if(!workerPosition.equals("")){
    	   if(flag==0)
        	{ sqlupdate=sqlupdate+"worker_position='"+workerPosition+"'";
        	   flag=1;}
    	   else {
    		   sqlupdate=sqlupdate+","+"worker_position='"+workerPosition+"'"; 
    	   }
       }
     
     if(!workerSex.equals("")){
  	   if(flag==0)
      	{ sqlupdate=sqlupdate+"worker_sex='"+workerSex+"'";
      	   flag=1;}
  	   else {
  		   sqlupdate=sqlupdate+","+"worker_sex='"+workerSex+"'"; 
  	   }
     }
     

     try { 
     if(!passwordOld.equals("")){
    	 
     String managerModifyWorker_require_son= "select worker_password from worker where worker_id ="+workerId;
     PreparedStatement stmt1 = conn.prepareStatement(managerModifyWorker_require_son);     
     r= stmt1.executeQuery();
     
     if (!r.next()) {  	  
		  ret_obj.put("status", false);
		  ret_obj.put("message", "没有该员工信息");
		  out.print(ret_obj.toString());  
	      out.flush();
	      out.close();
	}else {

		if(r.getString(1).equals(passwordOld))
		{
			if(flag==0)
	      	{ sqlupdate=sqlupdate+"worker_password='"+password+"'";
	      	   flag=1;}
	  	   else {
	  		   sqlupdate=sqlupdate+","+"worker_password='"+password+"'"; 
	  	   }
		}else {
			ret_obj.put("status", false);
			ret_obj.put("message", "输入旧密码错误,密码未能成功修改");
			out.print(ret_obj.toString());  
		    out.flush();
		    out.close();
		}
	} 
     stmt1.close();
     }
     
      String managerModifyWorker_require =sqlupdate+where;
      System.out.println(managerModifyWorker_require);
      PreparedStatement stmt2 = conn.prepareStatement(managerModifyWorker_require);     
      modifyResult= stmt2.executeUpdate(); 
 
  		if(modifyResult==0) {  
		  ret_obj.put("status", false);
		  ret_obj.put("message", "删除员工失败");
  		}else {
	   	ret_obj.put("status",true);
    	} 
  		
      stmt2.close();
  
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
@Override  
protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
    this.doGet(req, resp);  
} 
@Override  
protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
    this.doGet(req, resp);  
} 
}
