/* 
 * Copyright 2000-2009 Project.net Inc.
 *
 * This file is part of Project.net.
 * Project.net is free software: you can redistribute it and/or modify it under the terms of 
 * the GNU General Public License as published by the Free Software Foundation, version 3 of the License.
 * 
 * Project.net is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Project.net.
 * If not, see http://www.gnu.org/licenses/gpl-3.0.html
*/
package net.project.css;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;

public class CSSDbValues implements Filter {

	private FilterConfig config;
	
	public void destroy() {
		// Do nothing
	}

	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		res.setContentType("text/css");
		PrintWriter pw = res.getWriter();
		CSSResponseWrapper cssRes = new CSSResponseWrapper((HttpServletResponse) res);
		chain.doFilter(req, cssRes);
		String content = cssRes.getContentAsString();
		int indexFrom = 0;
		int index = content.indexOf("$$", indexFrom);
		//ArrayList<String> tokens = new ArrayList<String>();
		ArrayList tokens = new ArrayList();
		while (index > -1) {
			indexFrom = index + 2;
			int indexEnd = content.indexOf("$$", indexFrom);
			if (indexEnd > -1) {
				String token = content.substring(indexFrom, indexEnd);
				indexFrom = indexEnd + 2;
				index = content.indexOf("$$", indexFrom);
				if (!tokens.contains(token)) {
					tokens.add( token );
				}
			} else {
				//no more tokens
				break;
			}
		}
		//now check each token from database
		//for (String token: tokens) {
		Iterator iter = tokens.iterator();
		while (iter.hasNext()) {
			String token = (String) iter.next();
			String value = null;
			try {
				value = PropertyProvider.get(token);
				//if property is empty, check init param for filter
				if (value.equals(token)&&(config.getInitParameter(token) != null)) {
					value = config.getInitParameter(token);
				}
			} catch (Exception e) {
				//do nothing, exception is possible only if CSS is called before any other page after server start
				if (config.getInitParameter(token) != null) {
					value = config.getInitParameter(token);
				} else {
					value = token;
				}
			}
			//make replace
			content = content.replace("$$" + token + "$$", value);
		}
		content = content.replaceAll("jspRootUrl", SessionManager.getJSPRootURL());
		res.setContentLength(content.length());
		pw.write(content);
	}
	
	

	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}

}
