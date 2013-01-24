package net.project.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class for Servlet: SecurityFilter
 * 
 */
public class SecurityFilter extends javax.servlet.http.HttpServlet implements Filter {

	public SecurityFilter() {
		super();
	}

	private FilterConfig config = null;

	private ServletContext ctx = null;

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
		this.ctx = config.getServletContext();
	}

	public void destroy() {
		config = null;
	}

	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		try {
			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) res;
			if (request.getSession().getAttribute("user") == null && request.getRequestURI().indexOf("login.html") == -1) {
				response.sendRedirect(request.getContextPath());
			} 
			request.setCharacterEncoding("UTF-8");
			chain.doFilter(req, res);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}