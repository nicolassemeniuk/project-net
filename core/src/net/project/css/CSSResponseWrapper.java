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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CSSResponseWrapper extends HttpServletResponseWrapper {

	private ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	private PrintWriter pw = new PrintWriter(baos);
	
	private ByteArrayServletOutputStream basos = new ByteArrayServletOutputStream(baos);
	
	public CSSResponseWrapper(HttpServletResponse res) {
		super(res);
	}

	public PrintWriter getWriter() throws IOException {
		return pw;
	}
	
	public ServletOutputStream getOutputStream() throws IOException {
		return basos;
	}
	
	public String getContentAsString() {
		return baos.toString();
	}

	private static class ByteArrayServletOutputStream extends ServletOutputStream {
		ByteArrayOutputStream baos;
		
		ByteArrayServletOutputStream(ByteArrayOutputStream baos) {
			this.baos = baos;
		}
		
		public void write(int param) throws java.io.IOException {
			baos.write(param);
		}
	}
	
}
