package net.project.servlet;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.project.hibernate.service.IReportService;
import net.project.hibernate.service.ServiceFactory;

@SuppressWarnings("serial")
public class UserActivityServlet extends HttpServlet {

	public void doGet ( HttpServletRequest request, HttpServletResponse response ) throws ServletException{	
		try{
			String view = request.getParameter("view");	
			OutputStream output = response.getOutputStream() ; 
			IReportService service = ServiceFactory.getInstance().getReportService();
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", view + "; filename=UserActivity.pdf");
			ByteArrayOutputStream out = (ByteArrayOutputStream)service.createUserActivityReport();
				
			output.write(out.toByteArray());
			output.close(); 
		}catch (Exception e) {
			e.printStackTrace();
		}
    }	
	
	public void doPost ( HttpServletRequest request, HttpServletResponse response ) throws ServletException{
		doGet(request, response);
    }	
	
}
