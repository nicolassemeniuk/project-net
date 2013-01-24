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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.api.handler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.servlet.ServletContext;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * An application-scoped singleton provider with the capability
 * for checking out a person ID such that no other clients in the
 * same application scope can get the person ID.
 * <p/>
 * The personID returned is randomly selected from the set of person IDs.
 * </p>
 * 
 * @author Tim Morrow
 * @since Version 7.6.4
 */
class PersonProvider {

    private static final Logger logger = Logger.getLogger(PersonProvider.class);

    private static final String PERSON_PROVIDER_ATTRIBUTE_NAME = PersonProvider.class.getName();

    /** All personIDs in the database. */
    private final Set allPersonIDs = new HashSet();

    /** PersonIDs available for checkout. */
    private final List availablePersonIDs = new ArrayList();

    /**
     * Random number generator used for selecting a personID from the
     * set of available person IDs.
     */
    private final Random random = new Random();

    /**
     * Get the PersonProvider from the servlet context.
     * 
     * @param context 
     * @return 
     * @throws PersistenceException if there is an error loading person IDs
     */
    static synchronized PersonProvider getPersonProvider(ServletContext context) throws PersistenceException {
        PersonProvider provider = (PersonProvider) context.getAttribute(PERSON_PROVIDER_ATTRIBUTE_NAME);
        if (provider == null) {
            provider = new PersonProvider();
            context.setAttribute(PERSON_PROVIDER_ATTRIBUTE_NAME, provider);
        }
        return provider;
    }

    /**
     * Loads all available personIDs.
     */
    private PersonProvider() throws PersistenceException {

        String query = "select person_id from pn_person where person_id <> 1 and record_status = 'A'";
        DBBean db = new DBBean();
        try {
            db.executeQuery(query);
            while (db.result.next()) {
                allPersonIDs.add(db.result.getString("person_id"));
            }
        } catch (SQLException e) {
            throw new PersistenceException("Error loading person IDs: " + e, e);

        } finally {
            db.release();
        }

        reset();
    }

    /**
     * Resets the available personIDs to all personIDs.
     */
    public void reset() {
        synchronized (this) {
            availablePersonIDs.clear();
            availablePersonIDs.addAll(allPersonIDs);
        }
    }

    /**
     * Returns a random personID from the available set.
     * The returned personID will not be returned again
     * until {@link #reset()} is called.
     * 
     * @return the personID
     * @throws IllegalStateException if there are no more personIDs left
     */
    String checkOutPersonID() {
        if (logger.isDebugEnabled()) {
            logger.debug("Starting number of available person IDs: " + availablePersonIDs.size());
        }
        String personID;

        synchronized (this) {
            if (availablePersonIDs.size() == 0) {
                throw new IllegalStateException("No more person IDs available (" + allPersonIDs.size() + " in original set)");
            }
            int index = random.nextInt(availablePersonIDs.size());
            personID = (String) availablePersonIDs.remove(index);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("Ending number of available person IDs: " + availablePersonIDs.size());
        }

        return personID;
    }

}
