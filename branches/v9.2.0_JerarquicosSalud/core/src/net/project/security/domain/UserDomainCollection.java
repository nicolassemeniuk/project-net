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

 /*--------------------------------------------------------------------------------------+
|
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|
+--------------------------------------------------------------------------------------*/

package net.project.security.domain;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.database.DBBean;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

/**
 * This class represents a collection of domains.  Specifically for use for display.
 * 
 * @author Philip Dixon
 * @see net.project.security.domain.UserDomain
 * @since Gecko
 */
public class UserDomainCollection extends java.util.ArrayList implements IXMLPersistence, java.io.Serializable {


    /** Database access bean */
    private DBBean db = new DBBean();

    /** isLoaded flag */
    private boolean isLoaded = false;


    /* ------------------------------- Constructor(s)  ------------------------------- */

    public UserDomainCollection() {
        // do nothing
    }



    /**
     * Load the collection with Instantiated UserDomain objects from the database.
     * 
     * @exception PersistenceException
     *                   If the database load operation fails.
     * @since Gecko
     */
    public void load() throws PersistenceException {

        StringBuffer loadQuery = new StringBuffer(UserDomain.getLoadQuery());
        loadQuery.append("where ud.record_status = 'A' ");

        load(loadQuery.toString(), java.util.Collections.EMPTY_LIST);

    }

    public void loadForConfigurationID(String configurationID) throws PersistenceException {

        StringBuffer loadQuery = new StringBuffer(UserDomain.getLoadForConfigurationQuery());
        loadQuery.append("and ud.record_status = 'A' ");

        ArrayList bindVariables = new ArrayList();
        bindVariables.add(configurationID);

        load(loadQuery.toString(), bindVariables);
        
    }

    /**
     * Loads this UserDomainCollection based on the specified query
     * with the specified bindVariables.
     * Assumes bindVariables are all strings.
     * @param query the query to execute
     * @param bindVariables the string variables to bind to the query
     * @throws PersistenceException if there is a problem loading
     */
    private void load(String query, List bindVariables) throws PersistenceException {

        DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query);
            for (Iterator it = bindVariables.iterator(); it.hasNext(); ) {
                db.pstmt.setString(++index, (String) it.next());
            }
            db.executePrepared();

            while (db.result.next()) {

                UserDomain domain = new UserDomain();

                UserDomain.populate(db.result, domain);
                domain.setLoaded (true);

                this.add (domain);
            }

            this.isLoaded = true;
        
        } catch (SQLException sqle) {
            this.isLoaded = false;
            throw new PersistenceException ("UserDomainCollection.load() threw an SQLException: " + sqle, sqle);
        
        } finally {
            db.release();
        
        }

    }
    /* ------------------------------- XML Methods  ------------------------------- */    


    /**
     * Returns the properties of this UserDomain in XML format.
     * 
     * @return a <code>String</code> containing XML which describes this
     * UserDomain.
     * @since Gecko
     */
    public String getXML() {

        StringBuffer xml = new StringBuffer();

        xml.append (XML_VERSION);
        xml.append ( getXMLBody() );

        return xml.toString();
    }


    /**
     * Returns the properties of this UserDomain in XML format -- without the version string
     * 
     * @return a <code>String</code> value containing XML which describes this
     * user format.
     * @since Gecko
     */
    public String getXMLBody() {
        
        StringBuffer xml = new StringBuffer();
        java.util.Iterator list = this.iterator();
        UserDomain domain = null;

        xml.append ("<UserDomainCollection>");

        while ( list.hasNext() ) {

            domain = (UserDomain) list.next();
            xml.append ( domain.getXMLBody() );
        }

        xml.append ("</UserDomainCollection>");

        return xml.toString();
    }


}
