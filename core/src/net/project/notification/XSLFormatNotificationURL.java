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

package net.project.notification;

import net.project.base.property.PropertyProvider;
import net.project.security.SessionManager;

/**
 * <<Description>>
 * 
 * @author Philip Dixon
 * @since Version 7.7
 */
public class XSLFormatNotificationURL implements java.io.Serializable {

    public static String getSiteScheme() {
        return (PropertyProvider.get("prm.global.brand.defaultsitescheme"));
    }

    public static String getSiteHost() {
        return (PropertyProvider.get("prm.global.brand.defaulthost"));
    }

    public static String getJSPRootURL() {
        //bfd-5127
        //return (PropertyProvider.get("prm.global.brand.defaultjsprooturl"));
        return SessionManager.getJSPRootURL();
    }

    public static String getSiteURL() {
        StringBuffer siteURL = new StringBuffer();

        siteURL.append(getSiteScheme());
        siteURL.append(getSiteHost());

        return siteURL.toString();
    }

    public static String getAppURL() {
        StringBuffer url = new StringBuffer();

        url.append(getSiteURL());
        url.append(getJSPRootURL());

        return url.toString();
    }

    public static String formatSiteURL(String extraPath) {
        StringBuffer url = new StringBuffer();

        url.append(getSiteURL());
        url.append(extraPath);

        return url.toString();
    }

    public static String formatAppURL(String extraPath) {
        StringBuffer url = new StringBuffer();

        url.append(getAppURL());
        url.append(extraPath);

        return url.toString();
    }

}
