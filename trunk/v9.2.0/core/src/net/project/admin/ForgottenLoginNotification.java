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

 package net.project.admin;

import net.project.base.property.PropertyProvider;
import net.project.notification.ImmediateNotification;

/**
 * Notification object used for sending information when user forgets login name.
 */
public class ForgottenLoginNotification extends ImmediateNotification {

    /** Stylesheet to use to format forgotten login email */
    public static final String FORGOTTENLOGIN_NOTIFICATION_STYLE_SHEET = "/registration/nativedir/xsl/ForgottenLogin.xsl";

    public ForgottenLoginNotification() {
        super();
    }

    /**
     * Initialize Notification object from registration bean
     * @param regBean registraion bean to get notification body from
     */
    public void init(RegistrationBean regBean) {

        StringBuffer xml = new StringBuffer();

        // set the sender in this case because we do not have a user in session for notification on registration
        setSenderID(regBean.getID());
        // in this special case, we want the "From" address of the notification to be the brand default
        // rather than the sender (who in this case is the recipeient).
        setFromAddress(PropertyProvider.get("prm.global.default.email.fromaddress"));
                

        // special case.  all registration notifications must be sent via email
        setDeliveryTypeID(net.project.notification.IDeliverable.EMAIL_DELIVERABLE);
        setDeliveryAddress(regBean.getEmail());
        setCustomizationUserID(regBean.getID());
        setXSLStylesheetPath(FORGOTTENLOGIN_NOTIFICATION_STYLE_SHEET);


        xml.append(net.project.xml.IXMLTags.XML_VERSION_STRING);

        xml.append("<forgotten-login>");
        xml.append(regBean.getXMLBody());
        xml.append("</forgotten-login>");

        setNotificationXML(xml.toString());

    }

}	//  ForgottenLoginNotification
