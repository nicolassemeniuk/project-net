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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

/**
 * A list of Spaces. Extends ArrayList.
 *
 * @author Roger Bly
 * @author Deepak
 * @see java.util.ArrayList
 */
public class SpaceList extends ArrayList implements IXMLPersistence {
    /** Used for filtering Space on the basis of Space Name */
    private String spaceNameFilter = null;
    /**
     * Filter for which types of space will be loaded.  These are Strings from
     * ISpaceTypes
     */
    private List spaceTypes = new LinkedList();
    /** Indicates if we only want to load active projects. */
    private boolean loadActiveOnly = false;

    /**
     * Ensures that all the Space objects in this collection are loaded.
     */
    public void loadSpaces() throws PersistenceException {
        // Iterate over all spaces, loading if not already loaded
        Iterator it = iterator();
        while (it.hasNext()) {
            Space space = (Space) it.next();
            if (!space.isLoaded()) {
                space.load();
            }
        }
    }

    /**
     * Loads all the spaces irrespective of users .
     *
     * @throws PersistenceException
     */
    public void loadAll()
        throws PersistenceException {

        loadActiveOnly = false;
        spaceNameFilter = null;
        spaceTypes.clear();
        loadData();
    }

    /**
     * Loads all the Active spaces irrespective of users .
     *
     * @throws PersistenceException
     */
    public void loadAllActive()
        throws PersistenceException {

        loadActiveOnly = true;
        spaceNameFilter = null;
        spaceTypes.clear();
        loadData();
    }

    /**
     * Loads the spaces irrespective of users  but as per  parameter String
     * which filters on the basis of space type .
     *
     * @throws PersistenceException
     */
    public void loadFiltered(String filter) throws PersistenceException {
        this.spaceNameFilter = filter.toUpperCase();
        this.loadActiveOnly = true;
        loadData();
    }


    /**
     * Loads all the filtered Spaces from the database irrespective of users of
     * type of Space
     *
     * @param type a <code>String</code> containing the type of Space on which
     * filter should be operated on
     * @throws PersistenceException
     */
    public void loadFilteredType(String type) throws PersistenceException {
        spaceTypes.clear();
        spaceTypes.add(type);

        loadActiveOnly = true;
        spaceNameFilter = null;

        loadData();
    }

    /**
     * Loads all the filtered Spaces from the database irrespective of users or
     * type of Space
     *
     * @param types a <code>String[]</code> containing the types of Space which
     * should be loaded.
     * @throws PersistenceException if there is an error while loading.
     */
    public void loadFilteredTypes(String[] types) throws PersistenceException {
        spaceTypes = new LinkedList(Arrays.asList(types));
        loadActiveOnly = true;
        spaceNameFilter = null;

        loadData();
    }

    /**
     * This method actually loads tha data & contructs the collection.
     *
     * @throws PersistenceException
     */
    private void loadData() throws PersistenceException {
        this.clear();

        StringBuffer query = new StringBuffer();

        DBBean db = new DBBean();
        try {
            // Query the database and determine the type for this space ID

            query.append("select space_id , space_name , space_type , record_status from pn_space_view ");

            if (spaceTypes.size() == 0 && spaceNameFilter == null) {
                query.append("where space_type in ( " + SpaceTypes.getSpaceTypesInClause() + " )");
            } else if (spaceTypes.size() == 0 && spaceNameFilter != null) {
                query.append("where space_name = '"+spaceNameFilter+"'");
            } else if (spaceTypes.size() > 0 && spaceNameFilter != null) {
                query.append("where space_type in ( " + DatabaseUtils.collectionToCSV(spaceTypes, true) + ")");
                query.append("  and UPPER(space_name) like '%" + spaceNameFilter.toUpperCase() + "%'");
            } else if (spaceTypes.size() > 0) {
                query.append("where space_type in ( " + DatabaseUtils.collectionToCSV(spaceTypes, true) + ")");
            } else {
                query.append("where 1=1");
            }
            if (this.loadActiveOnly) {
                query.append(" and record_status='A' ");
            }
            db.prepareStatement(query.toString());
            db.executePrepared();

            while (db.result.next()) {
                Space space = SpaceFactory.constructSpaceFromType(db.result.getString("space_type"));

                space.setID(db.result.getString("space_id"));
                space.setName(db.result.getString("space_name"));
                space.setRecordStatus(db.result.getString("record_status"));

                this.add(space);
            }
        } catch (java.sql.SQLException sqle) {
            throw new PersistenceException("error loading space from database for this spaceID", sqle);
        } catch (SpaceTypeException ste) {
            throw new PersistenceException("" + ste, ste);
        } finally {
            db.release();
        }
    }

    /**
     * Set the type of this list to one of the Space types defined in
     * ISpaceTypes. If the list contains a mixture of Space types, use the
     * setMixedType(true) method.
     *
     * @param spaceType one of the Space types defined in ISpaceTypes.
     * @see net.project.space.ISpaceTypes
     */
    public void setType(String spaceType) {
        spaceTypes.add(spaceType);
    }

    /**
     * Get the type of this list. If the list contains a mixture of Space types,
     * isMixedType() will return true.
     *
     * @return thes type of the Spaces in the list as defined in ISpaceTypes.
     * @see net.project.space.ISpaceTypes
     */
    public String getType() {
        return (spaceTypes.size() == 0 ? null : (String)spaceTypes.get(0));
    }

    /**
     * Converts the object to XML representation of the Space Collection. This
     * method returns the Space as XML text.
     *
     * @return XML representation of the Space
     */
    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
     * Get the XML for the list of Spaces.  This XML is different for each type
     * of space.
     *
     * @return XML representing the space list.
     */
    public String getXMLBody() {

        StringBuffer xml = new StringBuffer();
        xml.append("<SpaceList>\n");

        for (int i = 0; i < this.size(); i++) {
            xml.append(((Space) this.get(i)).getXMLBody());
        }

        xml.append("</SpaceList>\n");
        return xml.toString();
    }


    /**
     * Converts the object to XML representation of the Space Collection. This
     * method returns the generic (non-typed) properties about a space in XML
     * form
     *
     * @return XML representation of the Space
     */
    public String getXMLProperties() {
        return IXMLPersistence.XML_VERSION + getXMLBodyProperties();
    }

    /**
     * Converts the Form to XML representation without the XML version tag. This
     * represents the common Space properties of all spaces; that is, the XML is
     * not customized per Space type.
     *
     * @return XML representation
     * @see Space#getXMLProperties
     */
    public String getXMLBodyProperties() {

        StringBuffer xml = new StringBuffer(300);

        xml.append("<SpaceList>\n");
        Iterator itr = this.iterator();

        while (itr.hasNext()) {
            Space space = (Space) itr.next();
            xml.append(space.getXMLProperties());
        }
        xml.append("</SpaceList>\n");

        return xml.toString();
    }

    /**
     * get a comma separated list of objectIDs for the Spaces on the list.
     *
     * @return a String containing a comma-separated list of objectIDs, null if
     *         the list is empty.
     */
    public String getIdCSV() {
        if (this.size() < 1) {
            return null;
        }

        StringBuffer idList = new StringBuffer();
        Iterator iterator = this.iterator();
        while (iterator.hasNext()) {
            idList.append(((Space) iterator.next()).getID());
            if (iterator.hasNext()) {
                idList.append(",");
            }
        }
        return idList.toString();
    }
}

