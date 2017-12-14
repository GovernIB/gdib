package es.rsits.ws.transport.http.servlet;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.xml.ws.transport.http.servlet.ServletAdapter;
import com.sun.xml.ws.transport.http.servlet.WSServletDelegate;

public class RSITSWSServletDelegate extends WSServletDelegate {

	
	public RSITSWSServletDelegate(List<ServletAdapter> adapters, ServletContext context) {
		super(adapters, context);
		// TODO Auto-generated constructor stub
	}
	
	public void doGet(HttpServletRequest request, HttpServletResponse response, ServletContext context)
	        throws ServletException {
		//(ServletScope) context.getAttribute("org.springframework.web.context.support.ServletContextScope")
		System.out.println("PREInterceptado!!");		
		super.doGet(request,response,context);		
		System.out.println("POSTInterceptado!!!");
	}

}
