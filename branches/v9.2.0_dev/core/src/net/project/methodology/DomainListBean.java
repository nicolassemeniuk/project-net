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

 package net.project.methodology;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.security.User;

import org.apache.log4j.Logger;

/**
 * @author Phil Dixon
 * @since 09/2000
 */
public class DomainListBean {
    public static String getIndustryOptionList(String industryID) {
        DBBean db = new DBBean();
        String qstrGetIndustries = "select industry_id, name from pn_industry_classification";
        StringBuffer options = new StringBuffer();

        try {
            db.executeQuery(qstrGetIndustries);

            while (db.result.next()) {
                if (db.result.getString("industry_id").equals(industryID)) {
                    options.append("<option SELECTED value=\"" + db.result.getString("industry_id") + "\">" +
                        db.result.getString("name") + "</option>");
                } else {
                    options.append("<option value=\"" + db.result.getString("industry_id") + "\">" +
                        db.result.getString("name") + "</option>");
                }
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).debug("methodology.DomainListBean.getIndustryOptionList(): to execute query: " +
                sqle);
        } finally {
            db.release();
        }

        return options.toString();
    }


    public static String getCategoryOptionList(String industryID) {
        DBBean db = new DBBean();
        String qstrGetCategories = "select c.category_id, c.name from pn_category c, " +
            "pn_industry_has_category ihc where ihc.industry_id = " + industryID +
            " and c.category_id = ihc.category_id";
        StringBuffer options = new StringBuffer();

        if (industryID != null) {
            try {
                db.executeQuery(qstrGetCategories);

                while (db.result.next()) {
                    options.append("<option value=\"" + db.result.getString("category_id") + "\">" +
                        db.result.getString("name") + "</option>");
                }
            } catch (SQLException sqle) {
            	Logger.getLogger(DomainListBean.class).debug("methodology.DomainListBean.getCategoryOptionList(): unable to execute query: " + sqle);
            } finally {
                db.release();
            }
        } // end if

        return options.toString();
    }

    public String getAvailableBusinessOptionList(String userID) {
        DBBean db = new DBBean();
        if (userID == null) {
            return null;
        }

        String qstrGetAvailableBusinessSpaces = "select b.business_id, b.business_name from pn_space_has_person shp," +
            "pn_business b where shp.person_id=" + userID + " and shp.record_status = 'A' and b.business_id = shp.space_id and b.record_status = 'A' " +
            "order by b.business_name asc ";
        StringBuffer options = new StringBuffer();

        try {
            db.executeQuery(qstrGetAvailableBusinessSpaces);

            while (db.result.next()) {
                options.append("<option value=\"" + db.result.getString("business_id") + "\">" + db.result.getString("business_name") + "</option>");
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).debug("project.DomainListBean.getAvailableBusinessOptionList threw an SQL exception: " + sqle);
        } finally {
            db.release();
        }

        return options.toString();
    }


    public String getAvailableBusinessOptionList(String userID, String defaultID) {
        DBBean db = new DBBean();
        if (userID == null)
            return null;

        StringBuffer options = new StringBuffer();
        String qstrGetAvailableBusinessSpaces = "select b.business_id, b.business_name from pn_space_has_person shp," +
            "pn_business b where shp.person_id=" + userID + " and shp.record_status = 'A' and b.business_id = shp.space_id and b.record_status = 'A' " +
            "order by b.business_name asc ";

        try {
            db.executeQuery(qstrGetAvailableBusinessSpaces);

            while (db.result.next())

                if (defaultID != null) {
                    if (defaultID.equals(db.result.getString("business_id"))) {
                        options.append("<option SELECTED value=\"" + db.result.getString("business_id") + "\">" +
                            db.result.getString("business_name") + "</option>");
                    } else {
                        options.append("<option value=\"" + db.result.getString("business_id") + "\">" +
                            db.result.getString("business_name") + "</option>");
                    }
                } else {
                    options.append("<option value=\"" + db.result.getString("business_id") + "\">" +
                        db.result.getString("business_name") + "</option>");
                }
        } catch (SQLException sqle) {
        	Logger.getLogger(DomainListBean.class).error("project.DomainListBean.getAvailableBusinessOptionList threw an SQL exception: " + sqle);
        } finally {
            db.release();
        }

        return options.toString();
    }


    public String getAvailableBusinessOptionList(User user) {
        return getAvailableBusinessOptionList(user.getID());
    }
}
