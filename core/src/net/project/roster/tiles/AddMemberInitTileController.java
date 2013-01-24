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
package net.project.roster.tiles;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.tiles.ComponentContext;
import org.apache.struts.tiles.ControllerSupport;

public class AddMemberInitTileController extends ControllerSupport {


	private Logger log = Logger.getLogger(AddMemberInitTileController.class);

	public AddMemberInitTileController() {
	}
	
	@Override
	public void perform(ComponentContext context, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) throws ServletException, IOException {
		try {
			System.out.println("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n 1. prolazi \n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
		} catch (Exception e) {
			e.printStackTrace();
			if (log.isDebugEnabled()) {
				log.debug(e.getMessage());
			}
			

		}
	}





}
