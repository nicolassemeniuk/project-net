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
package net.project.form;

import net.project.notification.ImmediateNotification;
import net.project.notification.NotificationException;
import net.project.xml.XMLUtils;

public class FormDataNotification extends ImmediateNotification{

	public static final String NOTIFICATION_STYLESHEET = "/form/xsl/ExternalFormNotification.xsl";
	
	public FormDataNotification() {
		super();
	}

	public void initialize(FormData formData) throws NotificationException {
		
		StringBuffer xml = new StringBuffer();
		
        setDeliveryTypeID("100");
        setXSLStylesheetPath(NOTIFICATION_STYLESHEET);
        setDeliveryAddress(formData.getCreatorEmail());		

        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);
        
        xml.append("<notification>");
        xml.append("<form_name>");
        xml.append(formData.getForm().getName());
        xml.append("</form_name>");
        xml.append("<form_data>"); 
        xml.append(formData.getForm().writeHtml());
        xml.append("</form_data>");
        xml.append("</notification>");
                
        setNotificationXML(xml.toString());
		        
	}	
	
}
