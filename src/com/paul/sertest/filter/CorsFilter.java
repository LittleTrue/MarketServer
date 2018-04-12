package com.paul.sertest.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paul.sertest.ResponseMgr;
import com.paul.sertest.TokenMgr;
import com.paul.sertest.config.Constant;
import com.paul.sertest.model.CheckResult;
import com.paul.sertest.model.SubjectModel;
import com.paul.sertest.utils.GsonUtil;

import net.sf.json.JSONObject;

public class CorsFilter implements Filter {
	
	private static Connection conn = null;
	private ResultSet r;
	private JSONObject get_obj;
	
	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1,
			FilterChain arg2) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;
		String token;
//		 System.out.println("---------获取请求数据-------------");  
//		 Enumeration es = request.getParameterNames();  
//	        while (es.hasMoreElements()) {  
//	            String paramName = (String) es.nextElement();  
//	            String value2 = request.getParameter(paramName);  
//	            System.out.println(paramName + "=" + value2);  
//	        }  
		
		response.setContentType("text/json");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Access-Control-Max-Age","3600");
		response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept, token");
		
//		if(request.getRequestURI().equals("/MarketServer/user/info")){
//		 token = request.getParameter("gdufe-shop");
//		 System.out.println("get");
//		}else {
		
		token = request.getHeader("gdufe-shop");
		//}
		
		System.out.println(token);
		if(token ==null||token =="") {
			PrintWriter printWriter = response.getWriter();
			printWriter.print(ResponseMgr.noLogin());
			printWriter.flush();
			printWriter.close();
		}
		
		CheckResult checkResult = TokenMgr.validateJWT(token);
		System.out.println("token是否成功:"+checkResult.isSuccess());
		
		
		if (checkResult.isSuccess()) {
			conn=login.Login.getCon();
			
			try {
				get_obj = JSONObject.fromString(TokenMgr.parseJWT(token).getSubject());
			

				 String filter_require = "select worker_token from worker"
			          		+ " where worker_id = "+get_obj.getString("workerId");
				 
			     PreparedStatement stmt = conn.prepareStatement(filter_require);  
			     
			     r=stmt.executeQuery();  
			     
			     if (!r.next()){ 
			    	 System.out.println("hehe");
			        PrintWriter printWriter2 = response.getWriter();
					printWriter2.print(ResponseMgr.noLogin());
					printWriter2.flush();
					printWriter2.close();
		    	}else {

		    		if(r.getString(1).equals(token)){
		    			
		    			System.out.println("token有效");
		    			arg2.doFilter(arg0,arg1);
		    			
		    		}else{
		    			if(request.getRequestURI().equals("/MarketServer/login/logout")){
		    				System.out.println("登出");
		    				arg2.doFilter(arg0,arg1);
		    			}else{	
		    				
		    			PrintWriter printWriter = response.getWriter();
						printWriter.print(ResponseMgr.loginExpire());
						printWriter.flush();
						printWriter.close();
						
		    			}
		    		}
		          } 
				
				
			} catch (Exception e) {
				e.printStackTrace();
			}	
		} else {
			switch (checkResult.getErrCode()) {
			
			case Constant.JWT_ERRCODE_EXPIRE:
				PrintWriter printWriter = response.getWriter();
				printWriter.print(ResponseMgr.loginExpire());
				printWriter.flush();
				printWriter.close();
				break;
		
			case Constant.JWT_ERRCODE_FAIL:
				PrintWriter printWriter2 = response.getWriter();
				printWriter2.print(ResponseMgr.noAuth());
				printWriter2.flush();
				printWriter2.close();
				break;
			default:
				break;
			}
		}
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
