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

 package net.project.space;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.business.BusinessSpace;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * A Personal Workspace.
 */
public class PersonalSpace extends Space implements Serializable {

    /**
     * The user that this space is for.
     */
    protected User m_user = null;

    /**
     * Creates a new PersonalSpace.
     */
    public PersonalSpace() {
        setType(ISpaceTypes.PERSONAL_SPACE);
        this.spaceType = SpaceTypes.PERSONAL;
    }


    /**
     * Creates a new PersonalSpace for the specified id. The space is not
     * loaded.
     */
    public PersonalSpace(String space_id) {
        super(space_id);
        setType(ISpaceTypes.PERSONAL_SPACE);
        this.spaceType = SpaceTypes.PERSONAL;
    }


    /**
     * Creates a new PersonalSpace with a User context.
     */
    public PersonalSpace(User user) {
        super(user.getID());
        setType(ISpaceTypes.PERSONAL_SPACE);
    }

    /**
     * Set the User context.
     */
    public void setUser(User user) {
        m_user = user;
        setLoaded(false);

        if (user == null) {
            setID(null);
        } else {
            setID(user.getID());
        }
    }


    /**
     * Get the project space's User.
     */
    public User getUser() {
        return m_user;
    }


    /**
     * Get a list of all the Business Spaces that the user has access to (member
     * of).
     *
     * @return an SpaceList of BusinessSpaces.
     */
    public SpaceList getBusinessSpaces() {
        BusinessSpace space = null;
        SpaceList spaceList = new SpaceList();

        spaceList.setType(ISpaceTypes.BUSINESS_SPACE);

        String query = "select b.business_id, b.business_name, b.business_desc from pn_space_has_person shp, pn_business b " +
            "where shp.person_id=" + m_user.getID() + " and shp.record_status = 'A' and b.business_id = shp.space_id and b.record_status = 'A' " +
            "order by upper(b.business_name) asc ";

        DBBean db = new DBBean();
        try {
            db.executeQuery(query);

            while (db.result.next()) {
                space = new BusinessSpace();
                space.setID(db.result.getString("business_id"));
                space.setName(db.result.getString("business_name"));
                space.setDescription(db.result.getString("business_desc"));
                spaceList.add(space);
            } // end while
        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(PersonalSpace.class).error("PersonalSpace.getBusinessSpaces(): threw an SQL exception: " + sqle);
        } // end catch
        finally {
            db.release();
        }

        return spaceList;

    } // end getBusinessSpaces()


    /**************************************************************************************************
     *  Implementing IJDBCPersistence
     **************************************************************************************************/

    /**
     * Load this object from the persistence store.
     */
    public void load() {

        String query = "select personal_space_name from pn_person_view where person_id = ? ";

        if (getID() == null) {
            throw new NullPointerException("PersonalSpace: id not set.  Cannot load().");
        }

        DBBean db = new DBBean();
        try {
            int index = 0;

            db.prepareStatement(query);

            db.pstmt.setString(++index, getID());

            db.executePrepared();

            if (db.result.next()) {
                setLoaded(true);
                setName(db.result.getString("personal_space_name"));
                setRecordStatus("A");
            }
        } catch (SQLException sqle) {
            setName(null);
            setLoaded(false);
            Logger.getLogger(PersonalSpace.class).error("PersonalSpace.load(): threw an SQL exception: " + sqle);
        } // end catch
        finally {
            db.release();
        }

    } // end load()


    /**
     * Save this object to the database.
     */
    public void store() {
        if (getID() == null) {
            throw new NullPointerException("m_space_is is null.  Cannot store.");
        }

        DBBean db = new DBBean();
        try {
            db.executeQuery("update pn_personal_space set personal_space_name=" + DBFormat.varchar2(getName()) + " where person_id=" + getID());
        } catch (SQLException sqle) {
        	Logger.getLogger(PersonalSpace.class).error("PersonalSpace.store(): threw an SQL exception: " + sqle);
        } // end catch
        finally {
            db.release();
        }

    } // end store()


    /**
     * Removes the space from the database.
     *
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     */
    public void remove() throws PersistenceException {
        throw new PersistenceException("not implemented");
    }

    /**
     * Converts the object to XML representation This method returns the object
     * as XML text.
     *
     * @return XML representation of this object
     */
    public String getXML() {
        StringBuffer xml = new StringBuffer();
        xml.append("<?xml version=\"1.0\" ?>\n\n");
        xml.append(getXMLBody());
        return xml.toString();
    }


    /**
     * Get an XML representation of the Space without xml tag.
     *
     * @return an XML representation of this Space without the xml tag.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<PersonalSpace>\n");
        xml.append("<id>" + this.getID() + "</id>\n");
        xml.append("<name>" + XMLUtils.escape(this.getName()) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(this.getDescription()) + "</description>\n");
        xml.append("<type>" + XMLUtils.escape(this.getType()) + "</type>\n");
        xml.append("<userDefinedSubType>" + XMLUtils.escape(this.getUserDefinedSubtype()) + "</userDefinedSubType>\n");
        xml.append("<flavor>" + XMLUtils.escape(this.getFlavor()) + "</flavor>\n");
        xml.append("<recordStatus>" + getRecordStatus() + "</recordStatus>");
        xml.append("</PersonalSpace>\n");
        return xml.toString();
    }


}
