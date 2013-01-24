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
/**
 * 
 */
package net.project.view.components;

import net.project.base.property.PropertyProvider;
import net.project.view.pages.resource.management.AllocateByProject;

import org.apache.log4j.Logger;

/**
 * @author
 */
public class Footer {
	private static Logger log;

	private String footerHrefUrl;

	public Footer() {
		try {
			log = Logger.getLogger(AllocateByProject.class);
			footerHrefUrl = PropertyProvider.get("prm.global.footer.copyright.href");
		} catch (Exception ex) {
			log.error("Error occured while getting Footer page property values.");
		}
	}

	/**
	 * @return the footerHrefUrl
	 */
	public String getFooterHrefUrl() {
		return footerHrefUrl;
	}
}
