/**
 * 
 */
package net.project.util;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author 
 *
 */
public class TemplateFormatter {
	
	private ServletContext servletContext;
	
	private String templatePath;
	
	/**
	 * @param servletContext
	 * @param templatePath
	 */
	public TemplateFormatter(ServletContext servletContext, String templatePath){
		this.servletContext = servletContext;
		this.templatePath = templatePath;
	}
	
	/**
	 * @param theObject
	 * @return
	 */
	public String transForm(Object theObject){
		
		if (this.templatePath == null) {
			throw new NullPointerException(" Template path is undefined");
		}
		if (this.servletContext == null) {
			throw new NullPointerException(" ServletContext is null");
		}
		if (theObject == null) {
			throw new IllegalArgumentException(" null is not allowed to transform");
		}
		SimpleHash simpleHash = new SimpleHash();
		simpleHash.put("token", new TokenHelper());
		simpleHash.put("theObject", theObject);
		simpleHash.put("htmlUtils", new HTMLUtils());
		
		StringWriter writer = new StringWriter();
		
		Configuration configuration = new Configuration();
		configuration.setServletContextForTemplateLoading(this.servletContext, null);
		
		try {
			Template template = configuration.getTemplate(this.templatePath);
			template.process(simpleHash, writer);
		} catch (IOException pnetEx) {
			pnetEx.printStackTrace();
		} catch (TemplateException pnetEx) {
			pnetEx.printStackTrace();
		}
		return writer.toString();
	}
}
