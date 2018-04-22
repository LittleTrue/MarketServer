package stock;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
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
import java.util.ArrayList;

import javax.servlet.ServletException;  
import javax.servlet.http.HttpServlet;  
import javax.servlet.http.HttpServletRequest;  
import javax.servlet.http.HttpServletResponse;  

import com.sun.javafx.collections.MappingChange.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONString;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class AddGoodsType extends HttpServlet{
	private static Connection conn = null;
	private ResultSet r;
	
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { 
		 String input;//方法变量定义
		 int insertResult;
		 
		 String typeName;
		 int total;//输出
		 String url = null;
		// long time=System.currentTimeMillis();
		 conn=login.Login.getCon();
		 
		 System.out.println("stock被访问了");
		 
		 req.setCharacterEncoding("utf-8");
		 resp.setCharacterEncoding("utf-8"); 
	
		 typeName=req.getParameter("typeName");
		 System.out.println(typeName);
		 
		 PrintWriter out=resp.getWriter();;//输出流获取
		
		 JSONObject ret_obj = new JSONObject();
		 JSONArray ret_obj_array = new JSONArray();
		 
		 
		 //得到绝对文件夹路径，比如"D:\\Tomcat6\\webapps\\test\\upload"

         String path = this.getServletContext().getRealPath("/upload");
        
         //临时文件夹路径
         DiskFileItemFactory factory =new DiskFileItemFactory();
         
         String repositoryPath =this.getServletContext().getRealPath("/upload/temp");        

         // 设定临时文件夹为repositoryPath
         factory.setRepository(new File(repositoryPath)); 
         // 设定上传文件的阈值，如果上传文件大于1M，就可能在repository

         // 所代 表的文件夹中产生临时文件，否则直接在内存中进行处理
         factory.setSizeThreshold(1024* 1024);
         // 创建一个ServletFileUpload对象

         ServletFileUpload uploader =new ServletFileUpload(factory);
         try
         {
                  // 调用uploader中的parseRequest方法，可以获得请求中的相关内容，
                  // 即一个FileItem类型的ArrayList。FileItem是在
                  // org.apache.commons.fileupload中定义的，它可以代表一个文件，
                  // 也可以代表一个普通的form field
                  ArrayList<FileItem>list = (ArrayList<FileItem>)uploader.parseRequest(req);

                  System.out.println("输入流长度:"+list.size());
                  
                  for(FileItem fileItem : list)
                  {

                           if(fileItem.isFormField())      // 如果是普通的form field
                           {

                                     String name = fileItem.getFieldName();
                                     System.out.println(name);
                                     String value = fileItem.getString();
                                     System.out.println(name+ " = " + value);

                           }
                           else   // 如果是文件
                           {       	   
                                     String value = fileItem.getName();
                                     int start = value.lastIndexOf("\\");
                                     String fileName = value.substring(start + 1); 
                                     
                                     // 将其中包含的内容写到path(即upload目录)下，
                                     // 名为fileName的文件中
                                     fileItem.write(new File(path, fileName));
                                     url="upload/"+fileName;
                                     System.out.println(url);
                           }

                  }
                  
       	       String stockGetGoodsType_require = "select * from type where typeName = '"+typeName+"'";
         		
    	       PreparedStatement stmt1 = conn.prepareStatement(stockGetGoodsType_require);     
    	       r= stmt1.executeQuery(); 
    	       
    	       if (r.next()) {  	  
    	
    	    	   ret_obj.put("status",false);
    	    	   ret_obj.put("message","分类已存在");
    	          }else {
    	        	  
    	       String stockGetGoodsType_insert = "INSERT INTO market.type(typeName,typePic) VALUES('"+typeName+"','"+url+"')";	  
    	       PreparedStatement stmt2 = conn.prepareStatement(stockGetGoodsType_insert);     
    	       System.out.println(stockGetGoodsType_insert);
    	      insertResult = stmt2.executeUpdate(); 
    	      if (insertResult==0) {  
    	    	  ret_obj.put("status",false);
   	    	   ret_obj.put("message","增加失败");
    	    	  
    	      }else {
    	    ret_obj.put("status",true);
   	    	   ret_obj.put("url",url);
    	      }
    	        	  
    	          }
    	       stmt1.close();
         }
         catch(Exception e)
         {

                  e.printStackTrace();
          }finally{
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
