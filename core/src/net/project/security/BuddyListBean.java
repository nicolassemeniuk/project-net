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

 package net.project.security;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.resource.PersonList;
import net.project.resource.UserActivity;
import net.project.resource.UserActivityStatus;
import net.project.space.Space;
import net.project.xml.XMLFormatter;

import org.apache.log4j.Logger;

/**  
    A list of teammates in a Space that have logged into the application recently.  
    @see net.project.resource.PersonList
    @see java.util.ArrayList    
*/
public class BuddyListBean extends PersonList implements java.io.Serializable
{
    /** Space context */
    protected Space m_space = null;

    /** User context */
    protected User m_user = null;

    /** db access bean  */
    protected DBBean db = new DBBean();

    /** Contains XML formatting information and utilities specific to this object **/
    protected XMLFormatter  m_formatter = new XMLFormatter();

        
    /** 
     Constructor.
   */
    public BuddyListBean()
    {
        super(10);
    }


    /** 
         Set the User context
    */
    public void setUser(User user)
    {
        m_user = user;
    }


/** 
    Set the space context for the buddyList. 
    If the Space context is not set, buddies (team members) from all spaces will be returned.
*/
    public void setSpace(Space space)
    {
        m_space = space;
    }


    /**
     * Converts the object to XML representation
     * This method returns the object as XML text. 
     *
     * @return XML representation
     */
    public String getXML()
    {
        StringBuffer xml = new StringBuffer();

        xml.append("<?xml version=\"1.0\" ?>\n");
        xml.append(getXMLBody());

        return xml.toString();
    }


    public String getXMLBody() {

        if (this.size() < 1)
            return "";

        StringBuffer xml = new StringBuffer();

        xml.append("<buddy_list>\n");
        xml.append("<securedConnection>"+ SessionManager.getSiteScheme().toLowerCase().contains("https") +"</securedConnection>");

        for (int i=0; i<this.size(); i++){
            xml.append( ((User)this.get(i)).getXMLBody() );
        }

        xml.append("</buddy_list>\n");
        return xml.toString();

    }


    /** get the current active buddlist for the Space context */
    public void load()
    {
        User user = null;
        String query = null;

        // clear the old items in the list.
        this.clear();

        if (m_space == null)
            throw new NullPointerException(" m_space can't be null when loading buddyList");

	query = "select "+
            "  sah.person_id, "+
            "  p.display_name, "+
            "  max(access_date) as max_access_date, "+
            "  pp.skype "+
            "from "+
            "  pn_space_has_person shp, "+
            "  pn_space_access_history sah, "+
            "  pn_person p, "+
            "  pn_user u, "+
            "  pn_person_profile pp "+
            "where "+
            "  shp.space_id = ? "+
            "  and shp.person_id = sah.person_id "+
            "  and shp.person_id = u.user_id "+
            "  and u.user_id = p.person_id "+
            "  and p.person_id = pp.person_id "+
            "group by "+
            "  sah.person_id, "+
            "  p.display_name, "+
			"  pp.skype " +
			"order by upper(p.display_name)";

	try {
	    db.prepareStatement(query);
	    db.pstmt.setInt(1, Integer.parseInt(m_space.getID()));
	    db.executePrepared();

	    while (db.result.next()) {
				user = new User();
				user.setID(db.result.getString("person_id"));
				user.setDisplayName(db.result.getString("display_name"));
				user.setSkype(db.result.getString("skype"));
				java.sql.Timestamp lastRecorded = db.result.getTimestamp("max_access_date");

				if (System.currentTimeMillis() - lastRecorded.getTime() > UserActivity.ASSUME_LOG_OUT_MILLIS) {
					// Assume that he is logged out
				} else if (System.currentTimeMillis() - lastRecorded.getTime() > UserActivity.INACTIVE_MILLIS) {
					user.getUserActivityStatusInstance().setStatus(UserActivityStatus.INACTIVE);
				} else {
					user.getUserActivityStatusInstance().setStatus(UserActivityStatus.ACTIVE);
				}
				this.add(user);
			}
	} catch (SQLException sqle) {
		Logger.getLogger(BuddyListBean.class).error("BuddyListBean.load threw an SQL exception: " + sqle);
	} finally {
            db.release();
	}

    }



    /**
    * Gets the presentation of the component
    * This method will apply the stylesheet to the XML representation of the component and
    * return the resulting text
    * 
    * @return presetation of the component
    */
    public String getPresentation()
    {
        return m_formatter.getPresentation(getXML());
    }


    /**
     * Sets the stylesheet file name used to render this component.
     * This method accepts the name of the stylesheet used to convert the XML representation of the component
     * to a presentation form.
     *
     * @param styleSheetFileName name of the stylesheet used to render the XML representation of the component
     */
    public void setStylesheet(String styleSheetFileName)
    {
        m_formatter.setStylesheet(styleSheetFileName);
    }



}

