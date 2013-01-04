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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.gui.navbar;

import java.sql.ResultSet;
import java.sql.SQLException;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.util.ErrorLogger;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * <code>FeaturedFormItems</code> builds XML that describes links for the left navigation
 * bar.  It is created by calling the {@net.project.navbar.NavBarObjectFactory} class.
 *
 * @author Matthew Flower (11/12/2001)
 * @since Gecko
 */
public class FeaturedFormItems implements NavBarItem {
    /**
     * Database connection used to find out which form objects to load and information
     * about them.
     */
    DBBean db = new DBBean();
    /**
     * We are only loading featured form items for a specific space.  This is the id of
     * that specific space
     */
    String spaceID;
    /**
     * Base URL that will be navigated to when a user clicks on this featured menu item.
     * we will be appending an id to this.
     */
    String url;
    
    /**
     * Constructor that creates a new <code>FeaturedFormItems</code> object.
     *
     * @param spaceID a <code>String</code> value which contains the spaceID that we
     * will use to load the property forms to display in the menu.  (We will only
     * display forms that are accessible from this form.)
     * @param url a <code>String</code> value which contains the base URL that
     * featured form items point to when clicked.
     */
    public FeaturedFormItems(String spaceID, String url) {
        this.spaceID = spaceID;
        this.url = url;
    }

    /**
     * Load the forms items that we are going to display on the menu from the database.
     *
     * @return a <code>ResultSet</code> value containing the result set of forms that
     * we are going to add to the NavBar XML.
     */
    private ResultSet loadForms() throws SQLException {
        StringBuffer query = new StringBuffer();

        //Construct the SQL statement which will look up the form items that
        //we'd like to display.
        query.append("select c.class_id form_id, c.class_name form_name ");
        query.append("from ");
        query.append("  pn_space_has_featured_menuitem sfm, ");
        query.append("  pn_space_has_class shc, ");
        query.append("  pn_class c ");
        query.append("where ");
        query.append("  sfm.space_id = ?");
        query.append("  and sfm.object_id = shc.class_id ");
        query.append("  and sfm.space_id = shc.space_id ");
        query.append("  and shc.class_id = c.class_id ");
        query.append("  and c.record_status = 'A'");

        try {
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, this.spaceID);

            //Run the query
            db.executePrepared();
        } catch (SQLException sqle) {
        	Logger.getLogger(FeaturedFormItems.class).error("Unexpected error while loading form " +
                                       "data in FeaturedFormItems.loadForms(): " +
                                       sqle);
            throw sqle;
        }

        //The result will get a live dataset.
        return db.result;
    }
    
    public String getNavBarXML(int depth) throws PnetException {
        //Padding might seem frivolous, but consider how difficult it is
        //to debug multiple levels of embedded menu tags - you just might change
        //your mind about removing this code.
        StringBuffer leftPad = new StringBuffer();
        for (int i=0; i<depth; i++)
            leftPad.append("  ");

        //This string buffer will hold the XML that we are going to return to the user.
        StringBuffer xml = new StringBuffer();

        try {
            ResultSet rs = loadForms();

            //Loop through the form list and add XML for each form we'd like to add to
            //the menu
            while (rs.next()) {
                xml.append(leftPad).append("<Menu>\n");
                xml.append(leftPad).append("  <URL>").append(url).append("&amp;id=").append(rs.getString("form_id")).append("</URL>\n");
                xml.append(leftPad).append("  <Label>").append(XMLUtils.escape(rs.getString("form_name"))).append("</Label>");
                xml.append(leftPad).append("  <Depth>").append(depth).append("</Depth>");
                xml.append(leftPad).append("</Menu>\n");
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FeaturedFormItems.class).error("Unexpected error while loading featured form items: " + sqle);
            throw new PersistenceException("Unable to load featured form items.", ErrorLogger.HIGH);
        } finally {
            //Free up the database connection
            db.release();
        }

        //Return our newly constructed XML.
        return xml.toString();
    }

    
    public String getNavBarHTML() throws PnetException {
        //This string buffer will hold the HTML that we are going to return to the user.
        StringBuffer html = new StringBuffer();

        try {
            ResultSet rs = loadForms();

            //Loop through the form list and add HTML for each form we'd like to add to
            //the menu
            while (rs.next()) {
            	html.append(" <a");
            	html.append(" href='").append(url).append("&amp;id=").append(rs.getString("form_id")).append("'");
            	html.append(" >").append(XMLUtils.escape(rs.getString("form_name")));
            	html.append("</a>");
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FeaturedFormItems.class).error("Unexpected error while loading featured form items: " + sqle);
            throw new PersistenceException("Unable to load featured form items.", ErrorLogger.HIGH);
        } finally {
            //Free up the database connection
            db.release();
        }

        //Return our newly constructed XML.
        return html.toString();
    }
    
    /**
     * Add a child object to this NavBarItem (!!!Child objects are not supported
     * by the FeaturedMenuItem, calling this will just raise an error.)  
     *
     * @param child a <code>NavBarItem</code> that would have became the child
     * object of this object.
     */
    public void addChild(NavBarItem child) throws PnetException {
        throw new PnetException("The featureditems tag does not support child objects");
    }
}

