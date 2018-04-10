package login;
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

import com.mysql.jdbc.ResultSetMetaData;
import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import com.paul.sertest.TokenMgr;
import com.paul.sertest.config.Constant;
import com.paul.sertest.model.SubjectModel;

public class Login extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;

	
	//命名 接受及会话控制取出 数据:驼峰法  返回数据:间隔法
	 protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
		 
		 int autoIncKey;//方法成员变量
		 int updateResult;
		 
		 String workerName;
		 String workerPosition;
		 
		 String input; //输入流定义
		 
		 String admin;
		 String password;//接受数据定义
		 String token;
	
		 
		 System.out.println("被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8");  //设置输入与返回文本编码格式

//		 resp.setContentType("text/html;charset=utf-8");
//		 resp.setHeader("content-type","text/html;charset=UTF-8");
		 
//json	 
		 input=getRequestBody(req);
		
		 PrintWriter out = new PrintWriter(resp.getOutputStream());
  
		 System.out.println(input);
	
	
		 
		 System.out.println("");
//json
		 JSONObject get_obj=JSONObject.fromString(input);
		 
		 JSONObject ret_obj = new JSONObject();
		 

	      conn=getCon();
	    
	
		 admin=get_obj.getString("username");
		 password=get_obj.getString("password");
	       try {    
	       String adminLogin_require = "select worker_id,worker_position,worker_name from worker"
	          		+ " where worker_account ='"+admin+"' AND worker_password ='"+ password+"'";
	       PreparedStatement stmt1 = conn.prepareStatement(adminLogin_require);     
	       
	       r=stmt1.executeQuery();   
	       if (!r.next()) {  	  
	    		  ret_obj.put("status", false);
	    		  ret_obj.put("message", "账号或密码错误");
	    	}else {
	    		autoIncKey=r.getInt(1);//取得ID	
	    		workerPosition=r.getString(2);
	    		workerName=r.getString(3);
	    		
	    		SubjectModel sub = new SubjectModel(autoIncKey,workerName,workerPosition);
	    		
	    		token = TokenMgr.createJWT(Constant.JWT_ID, TokenMgr.generalSubject(sub),Constant.JWT_TTL);			
				
				String adminLogin_update = "UPDATE worker SET worker_token = '"+token
		          		+ "' where worker_id ='"+autoIncKey+"'";
			
		       PreparedStatement stmt2 = conn.prepareStatement(adminLogin_update);     
		       
		       updateResult=stmt2.executeUpdate();  
		       
		       if (updateResult==0) {  	  
		    		  ret_obj.put("status", false);
		    		  ret_obj.put("message", "更新token失败,请重新登录");
		    	}else {
		    		ret_obj.put("status", true);
					ret_obj.put("token",token);
					
					
		    	}
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
	        out.close();
	    }  
	 
	 
	  @Override  
	    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {  
	        this.doGet(req, resp);  
	    }  
	  
	  
	 public static Connection getCon() {
         try {
             Class.forName("com.mysql.jdbc.Driver"); 
             String url = "jdbc:mysql://47.106.107.239:3306/market?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true";       
            conn = DriverManager.getConnection(url,"root","1120433guo*GUO");  
         	} catch (Exception e) {
             System.out.println("连接失败");
             e.printStackTrace();
         	}
         return conn;
       }
	 

	  
	    /**
	     * 获取请求头
	     * @param  int $page 	   页码
	     * @param  int $page_size  每页显示记录数
	     * @return array
	     */
	    public static String getRequestBody(HttpServletRequest req) throws IOException {
	    BufferedReader reader = req.getReader();
	    String input = null;
	    StringBuffer requestBody = new StringBuffer();
	    while((input = reader.readLine()) != null) {
	    requestBody.append(input);
	    }
	    return requestBody.toString();
	    }
	    
	    /**
	     * 获取分页信息
	     * @param  int $page 	   页码
	     * @param  int $page_size  每页显示记录数
	     * @return array
	     */
	    public static int[] getSplitPageInfo(int page,int page_size){
	        //参数为零则采用默认值
	    	int page_start;
	    	int ret[]= {1,20};
	        page = (page==0) || page < 1 ? 1 : page;
	        page_size = (page_size==0) ? 20 : page_size;
	        page_start = page_size * (page - 1);
	        ret[0]=page_start;
	        ret[1]=page_size;
	        return ret;
	    }
	    
	    /** 
	     * 将resultSet转化为JSON数组 
	     * resultSetToJsonArry方法需要从第头开始转化,之前不能使用next,并且使用之后指向最后一个记录
	     * @param rs 
	     * @return 
	     * @throws SQLException 
	     * @throws JSONException 
	     */  
	    public static JSONArray resultSetToJsonArry(ResultSet rs) throws SQLException,JSONException   
	    {   
	       // json数组   
	       JSONArray array = new JSONArray();   
	           
	       // 获取列数   
	       ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();   
	       int columnCount = metaData.getColumnCount();   
	           
	       // 遍历ResultSet中的每条数据   
	        while (rs.next()) {   
	            JSONObject jsonObj = new JSONObject();   
	             
	            // 遍历每一列   
	            for (int i = 1; i <= columnCount; i++) {   
	                String columnName =metaData.getColumnLabel(i);   
	                String value = rs.getString(columnName);  
//	                if (value==null){
//	                int value_change=rs.getInt(columnName);
//	                jsonObj.put(columnName,value_change); 
//	                }else {}
	                jsonObj.put(columnName,value);   
	                
	            }    
	            array.put(jsonObj);    
	        }         
	       return array;   
	    }  
	    
	    /** 
	     * resultSetToJsonArry方法需要从第头开始转化,之前不能使用next,并且使用之后指向最后一个记录
	     * @String token 
	     * @return workerId等
	     * @throws Exception 
	     * @throws SQLException 
	     */   public static HashMap<String,String> getWorkerInfoFromToken(HttpServletRequest req) throws  Exception      
		    {   
	    	 	HashMap<String,String> map=new HashMap<String, String>();
	    	 	String token = req.getHeader("gdufe-shop");
	    	 	//String token = req.getParameter("gdufe-shop");
	    	 	JSONObject get_obj = JSONObject.fromString(TokenMgr.parseJWT(token).getSubject());
	    	    map.put("workerId",get_obj.getString("workerId"));  
	            map.put("workerName",get_obj.getString("workerName"));  
	            map.put("workerPosition",get_obj.getString("workerPosition"));     
	    	 	  return map;   
		    }  
}
