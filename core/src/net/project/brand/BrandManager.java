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
|    $RCSfile$
|   $Revision: 20762 $
|       $Date: 2010-04-28 11:17:20 -0300 (mié, 28 abr 2010) $
|     $Author: dpatil $
|
+----------------------------------------------------------------------*/
package net.project.brand;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.security.SessionManager;
import net.project.util.StringUtils;

import org.apache.log4j.Logger;

/**
 * Provides application brand management for configuring the application look&feel.
 * A single copy of BrandManager is stored in each session.
 */
public class BrandManager extends Brand implements Serializable {

    /**
     * The name for the session-singleton BrandManager object.
     */
    public static final String BRAND_MANAGER_SESSION_OBJECT_NAME = "brandManager";

    /**
     * Creates an empty BrandManager.
     */
    public BrandManager() {
        // Do nothing
    }


    /**
     * Gets the active instance of the brand manager from the session.
     * If there is no brand manager currently in the session, makes a new one.  However,
     * it does NOT put the new brand manager in the session
     * @return the brand manager that was in the session or a new brand manager
     */
    public static BrandManager getInstance() {
        Object brand = SessionManager.getBrandManager();
        return (brand != null) ? (BrandManager) brand : new BrandManager();
    }

    /**
     * Set the brand from the user request URL.
     * Also saves the hostname and protocol from the request.
     * The SessionManager will preserve this hostname and protocol in future requests.
     * @param request the user's request.
     */
    public void setBrandFromRequest(javax.servlet.http.HttpServletRequest request) {
        String hostname = net.project.util.HttpUtils.getRequestHostname(request);

        // Check the URL brand parameter.
        if (StringUtils.isNotEmpty(request.getParameter("brand"))) {
            setAbbreviation(request.getParameter("brand"));
        } else {
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            String userBrandValue = null;
            // Check the cookie for brand
            if (cookies != null) {
                for (int i=0; i<cookies.length; i++) {
                    if (cookies[i].getName().equals("prm.login.userBrand")) {
                    	userBrandValue =  cookies[i].getValue();
                    }
                }
            }
            if (StringUtils.isNotEmpty(userBrandValue)) // if brand found in cookie then set the brand
                setAbbreviation(userBrandValue);
            else										// else set brand from host name
                setID(getBrandIDForHostname(hostname));
        }
    }


    /**
     * Sets the language from the request.
     * This looks for a parameter named <code>language</code>.  If that parameter
     * is not found, it looks for a cookie named <code>prm.login.userLanguage</code>.
     * If neither are found, the language is not changed
     * @param request the user's request.
     */
    public void setLanguageFromRequest(javax.servlet.http.HttpServletRequest request) {

        String foundLanguage = null;

        String languageParameterValue = request.getParameter("language");
        if (languageParameterValue != null && languageParameterValue.trim().length() > 0) {
            // We found a language parameter
            foundLanguage = languageParameterValue;

        } else {
            // No language parameter
            // We must deduce the language from elsewhere

            // Try to get the language from a cookie
            javax.servlet.http.Cookie[] cookies = request.getCookies();
            String userLanguageValue = null;
            if (cookies != null) {
                for (int i=0; i<cookies.length; i++) {
                    if (cookies[i].getName().equals("prm.login.userLanguage")) {
                        userLanguageValue =  cookies[i].getValue();
                    }
                }
            }

            if (userLanguageValue != null) {
                // We found a language in the cookie; use it
                foundLanguage = userLanguageValue;

            }

         }

        // If we found a language, set it
        if (foundLanguage != null) {
            setRequestedLanguage(foundLanguage);
        }

    }

    /**
     * Indicates whether this brand is loadable, meaning one of
     * brand ID or abbreviation has been set.
     * <p>
     * This is useful to determine whether the request actually contained
     * values.
     * Calling <code>load</code> when the brand is not loadable will throw
     * a runtime exception.
     * </p>
     * @return true if the brand is loadable; false otherwise
     */
    public boolean isLoadable() {
        return (getID() != null || getAbbreviation() != null);
    }

    public String getSupportedLanguageOptionList() {
        return net.project.base.property.PropertyManager.getSupportedLanguageOptionList(getID(), getActiveLanguage());
    }

    public String getSupportedLanguageOptionList(String selectedLanguage) {
        return net.project.base.property.PropertyManager.getSupportedLanguageOptionList(getID(), selectedLanguage);
    }

    private String getBrandIDForHostname(String hostname) {

        DBBean db = new DBBean();
        String qstrGetBrandForHostname = "select brand_id from pn_brand_has_host where host_name = '" + hostname + "'";
        String brandID = null;

        try {

            db.executeQuery(qstrGetBrandForHostname);

            if (db.result.next())
                brandID = db.result.getString("brand_id");
        } catch (SQLException sqle) {
        	Logger.getLogger(BrandManager.class).info("BrandManager.getBrandForHostname() threw an SQLException: " + sqle);
        } finally {
            db.release();
        }

        return brandID;
    }


}

