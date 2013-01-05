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

/*----------------------------------------------------------------------+
 |                                                                       
 |     $RCSfile$
 |    $Revision: 14865 $
 |        $Date: 2006-03-31 01:19:17 -0300 (Vie, 31 Mar 2006) $
 |      $Author: avinash $
 |                                                                       
 +----------------------------------------------------------------------*/
package net.project.taglibs;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.property.PropertyProvider;

/**
 * PropertyProviderTag is the taglib tag for property provider.
 * 
 * @author Martin Liguori
 * @since Version 1
 */
public class PropertyProviderTag extends TagSupport {

	private String value = null;

	private String args1 = null;

	private String args2 = null;

	/**
	 * Start of tag. Called after the attributes are set. Create new property
	 * Providers manager and set all the attributes.
	 * 
	 * @return {@link #EVAL_BODY_INCLUDE}
	 */
	public int doStartTag() throws JspTagException {

		String realValue = "";

		/*
		 * I am going to check which method of the class PropertyProvider we
		 * must call.
		 */
		if (getValue() != null) {
			if (getArgs1() != null) {
				if (getArgs2() != null) {
					realValue = PropertyProvider.get(value, args1, args2);
				} else {
					realValue = PropertyProvider.get(value, args1);
				}
			} else {
				realValue = PropertyProvider.get(value);
			}

			try {
				pageContext.getOut().print(realValue);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return EVAL_BODY_INCLUDE;
	}

	/**
	 * End of tag. Write out presentation of Property Provider manager
	 * 
	 * @return {@link #EVAL_PAGE}
	 */
	public int doEndTag() throws JspTagException {
		clear();
		return EVAL_PAGE;
	}

	/**
	 * Clear all properties.
	 */
	private void clear() {
		value = null;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return the args1
	 */
	public String getArgs1() {
		return args1;
	}

	/**
	 * @param args1
	 *            the args1 to set
	 */
	public void setArgs1(String args1) {
		this.args1 = args1;
	}

	/**
	 * @return the args2
	 */
	public String getArgs2() {
		return args2;
	}

	/**
	 * @param args2
	 *            the args2 to set
	 */
	public void setArgs2(String args2) {
		this.args2 = args2;
	}

}