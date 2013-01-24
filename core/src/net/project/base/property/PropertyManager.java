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

 package net.project.base.property;


import java.sql.SQLException;
import java.util.Arrays;

import net.project.database.DBBean;
import net.project.gui.html.HTMLOptionList;
import net.project.persistence.PersistenceException;
import net.project.resource.ProfileCodes;

import org.apache.log4j.Logger;

public class PropertyManager {


    /* ------------------------------- Constructor(s)  ------------------------------- */


    /* ------------------------------- Properties Convenience Methods  ------------------------------- */




    /* ------------------------------- Static Domain Retrieval Methods  ------------------------------- */

    public static String getLanguageOptionList() {
        return ProfileCodes.getLanguageOptionList();
    }


    public static String getSupportedLanguageOptionList(String contextID, String activeLanguage) {

        DBBean db = new DBBean();
        String qstrGetLanguages = "select l.language_code, l.language_name from pn_language l, " +
                "pn_brand_supports_language bsl where bsl.brand_id = " + contextID +
                " and bsl.language_code = l.language_code";
        StringBuffer options = new StringBuffer();

        try {

            db.executeQuery(qstrGetLanguages);

            while (db.result.next()) {
                String languageCode = db.result.getString("language_code");

                if (activeLanguage != null && languageCode.equals(activeLanguage))
                    options.append("<option SELECTED value=\"" + languageCode +
                            "\">" + PropertyProvider.get(db.result.getString("language_name")) + "</option>");
                else
                    options.append("<option value=\"" + languageCode +
                            "\">" + PropertyProvider.get(db.result.getString("language_name")) + "</option>");
            }
        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(PropertyManager.class).debug("PropertyManager.getSupportedLanguageOptionList() threw an SQLException: " + sqle);
        } finally {
            db.release();
        }

        return options.toString();
    }


    public static String getPropertyTypeOptionList() {
        return getPropertyTypeOptionList(null);
    }

    public static String getPropertyTypeOptionList(String defaultPropertyType) {
        return HTMLOptionList.makeHtmlOptionList(PropertyType.getAllPropertyTypes(), defaultPropertyType);
    }

    public static String getCategoryOptionList(String[] defaults) {

        DBBean db = new DBBean();
        String qstrGetCategoryList = "select category_id, name from pn_property_category";
        StringBuffer options = new StringBuffer();

        try {

            db.executeQuery(qstrGetCategoryList);

            while (db.result.next()) {

                if (defaults != null && Arrays.asList(defaults).contains(db.result.getString("category_id"))) {
                    options.append("<option SELECTED value=\"" + db.result.getString("category_id") +
                            "\">" + db.result.getString("name") + "</option>");
                } else
                    options.append("<option value=\"" + db.result.getString("category_id") +
                            "\">" + db.result.getString("name") + "</option>");

            }
        } // end try
        catch (SQLException sqle) {
            Logger.getLogger(PropertyManager.class).debug("PropertyManager.getCategoryOptionList() threw an SQLException: " + sqle);
        } finally {
            db.release();
        }

        return options.toString();
    }


    /**
     * Copies all properties from one context id to another.  These are persisted.
     * @param fromContextID the context id whose properties are to be copied
     * @param toContextID the new context id for the properties
     * @throws PersistenceException if there is a problem copying the properties
     */
    public static void copyProperties(String fromContextID, String toContextID) throws PersistenceException {
        DBBean db = new DBBean();
        StringBuffer query = new StringBuffer();

        query.append("insert into pn_property ");
        query.append("(context_id, language, property_type, property, property_value, property_value_clob, record_status, is_system_property, is_translatable_property) ");
        query.append("select ?, language, property_type, property, property_value, property_value_clob, record_status, is_system_property, is_translatable_property ");
        query.append("from pn_property where context_id = ? ");

        try {
            db.prepareStatement(query.toString());
            int index = 0;
            db.pstmt.setString(++index, toContextID);
            db.pstmt.setString(++index, fromContextID);

            db.executePrepared();

        } catch (SQLException sqle) {
            throw new PersistenceException("Properties copy operation failed.", sqle);

        } finally {
            db.release();

        }

    }


}
