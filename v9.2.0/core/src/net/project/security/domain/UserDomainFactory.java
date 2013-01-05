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

import net.project.database.DBBean;
import net.project.security.SecurityException;

import org.apache.log4j.Logger;

/**
 * Simple factory class for creating <code>UserDomain</code> objects for specified object/ID's
 *
 * @author Philip Dixon
 * @see net.project.security.domain.UserDomain
 * @since Gecko
 */
class UserDomainFactory implements java.io.Serializable {
    /**
     * Return an instantiated and loaded <code>UserDomain</code> object for a specified domain.
     *
     * @param domainID The domainID of the person in question
     * @return An instantiated and loaded <code>UserDomain</code> object
     *
     * @exception SecurityException
     *                   Throws a <code>SecurityException</code> if no domain can be found for the specified id, or if the domainID is not found in the database.
     * @see net.project.security.domain.UserDomain
     * @since Gecko
     */
    protected static UserDomain makeDomainForDomainID(String domainID) throws SecurityException {
        UserDomain toReturn = null;

        DBBean db = new DBBean();
        try {
            db.setAutoCommit(false);
            db.openConnection();

            toReturn = makeDomainForDomainID(domainID, db);

            db.commit();
        } catch (Exception e) {
            try {
                db.rollback();
            } catch (SQLException e2) {
            	Logger.getLogger(UserDomainFactory.class).debug("Fatal error: unable to roll back transaction.");
            }

            if (e instanceof SecurityException) {
                throw (SecurityException)e;
            } else {
                throw new SecurityException("UserDomainFactory.makeDomainForDomainID() " +
                    "was unable to load the domain due to a PersistenceException.", e);
            }
        } finally {
            db.release();
        }

        return toReturn;
    }

    /**
     * Return an instantiated and loaded <code>UserDomain</code> object for a specified domain.
     *
     * @param domainID The domainID of the person in question
     * @return An instantiated and loaded <code>UserDomain</code> object
     *
     * @exception SecurityException
     *                   Throws a <code>SecurityException</code> if no domain can be found for the specified id, or if the domainID is not found in the database.
     * @see net.project.security.domain.UserDomain
     * @since Gecko
     */
    protected static UserDomain makeDomainForDomainID(String domainID, DBBean db) throws SecurityException {
        UserDomain domain = new UserDomain();

        if (domainID == null) {
            throw new SecurityException("UserDomainFactory.makeDomainForDomainID() the domainID is null");
        }

        try {
            domain.setID(domainID);
            domain.load(db);
        } catch (net.project.persistence.PersistenceException pe) {
            throw new SecurityException("UserDomainFactory.makeDomainForDomainID() was unable to load the domain " +
                "due to a PersistenceException.", pe);
        }

        return domain;
    }


}
