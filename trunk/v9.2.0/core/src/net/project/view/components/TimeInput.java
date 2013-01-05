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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import net.project.calendar.TimeBean;

import org.apache.log4j.Logger;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 *
 */
public class TimeInput {
	@Parameter
	private String name;

	@Parameter
	private Date time;
	
	@Parameter
	private boolean isOptional;

	@Parameter
	private String elementID;
	
	@Parameter
    private int minuteStyle = TimeBean.MINUTE_STYLE_NORMAL;
	
	@Parameter
    private boolean isIncludeTimeZone;
	
	@Parameter
	private boolean isDisabled;

    @Parameter
    private TimeZone timeZone;
    
	private Map attributeValueMap = new HashMap();
	
	private String htmlString;

	private static Logger log;
	
	@Inject
	private ComponentResources resources;
	
	/**
	 * Initializing Time tag,
	 */
	@SetupRender
	void constructInputElement() {

		// Use the TimeBean to generate the presentation
		TimeBean timeBean = new TimeBean();
		timeBean.setTag(this.name);
		if (this.elementID != null) {
			timeBean.setID(this.elementID);
		}
		timeBean.setDate(this.time);
		
		this.attributeValueMap.put("disabled", isDisabled);
		
		timeBean.setAttributes(this.attributeValueMap);
		timeBean.setOptional(this.isOptional);
		timeBean.setMinuteStyle(minuteStyle);
		timeBean.setIncludeTimeZone(isIncludeTimeZone);
		
		if (this.timeZone != null) {
			timeBean.setTimeZone(timeZone);
		}

		// Now construct the HTML
		StringBuffer elementText = new StringBuffer();
		elementText.append(timeBean.getPresentation());

		this.htmlString = elementText.toString();
		
	}
	

	/**
	 * @return the htmlString
	 */
	public String getHtmlString() {
		return this.htmlString;
	}

}
