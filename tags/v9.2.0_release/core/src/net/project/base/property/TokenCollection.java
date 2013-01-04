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
|   $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.base.property;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.project.base.RecordStatus;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * Provides a collection of tokens.
 * <p>
 * This is maintained by the TokenCollectionCache in the application scope such
 * that there is only one TokenCollection instance for each Context.
 * A TokenCollection is also referenced by a TokenSearchMap which is maintained
 * in the session scope.
 * </p>
 *
 * @author Vishwajeet Lohakarey
 * @author Tim Morrow
 * @since 7.4
 */
class TokenCollection implements Serializable {

    /**
     * The map of token names and tokens.
     * This is used to quickly lookup a token based on its name.
     * Each key is a <code>String</code> token name.  Each value is a <code>Token</code>.
     */
    private final Map tokenMap = new HashMap();

    /**
     * The date that the collection was loaded on.
     * This is used for determining if the collection is out-of-date.
     * It is updated when the collection is loaded.
     */
    private Date loadedDate = null;

    /**
     * Constructs an empty TokenCollection.
     */
    TokenCollection() {
        // Do nothing
    }

    /**
     * Adds a token to this collection.
     * If this token is already in the collection, it is replaced.
     * @param token the token to add
     * @return the added token
     */
    synchronized Token addToken(Token token) {
        return (Token) tokenMap.put(token.getName(), token);
    }

    /**
     * Gets the token with the specified name.
     * @param name the name of the token to get
     * @return the token with that name or null if there is no token with
     * that name
     */
    Token getToken(String name) {
        return (Token) this.tokenMap.get(name);
    }

    /**
     * Provides an iterator over the tokens in this collection.
     * The iterator supports element removal via the remove() method.
     * @return an iterator where each element is a <code>Token</code>.
     */
    Iterator iterator() {
        return this.tokenMap.values().iterator();
    }

    /**
     * Returns the date that this collection was loaded on.
     * @return the loaded date
     * @throws IllegalStateException if the date is null, which means the
     * collection has not been loaded
     */
    Date getLoadedDate() {

        if (this.loadedDate == null) {
            throw new IllegalStateException("TokenCollection has not been loaded");
        }

        return this.loadedDate;
    }

    /**
     * Loads tokens for the specified context from the database.
     * @param context the context for which to load tokens
     * @throws PersistenceException if there is a problem loading the tokens
     */
    void load(Context context) throws PersistenceException {

        Token token = null;
        String contextID = context.getID();
        String language = context.getLanguage();

        StringBuffer query = new StringBuffer();
        query.append("select property, property_value, property_value_clob, property_type, is_system_property, is_translatable_property ");
        query.append("from pn_property ");
        query.append("where record_status = 'A' and context_id = ").append(contextID).append(" ");
        query.append("and language = '").append(language).append("' ");

        int PROPERTY_COL = 1;
        int PROPERTY_VALUE_COL = 2;
        int PROPERTY_VALUE_CLOB_COL = 3;
        int PROPERTY_TYPE = 4;
        int IS_SYSTEM_PROPERTY = 5;
        int IS_TRANSLATABLE_PROPERTY = 6;

        DBBean db = new DBBean();

        try {
            db.createStatement();
            db.stmt.setFetchSize(250);
            db.executeQuery(query.toString());

            while (db.result.next()) {

                // Figure out which column to read the value from
                // based on the property type
                String value = null;
                PropertyType type = PropertyType.findByID(db.result.getString(PROPERTY_TYPE));

                if (type.isClobStorage()) {
                    // A clob storage type is read from the clob column
                    value = ClobHelper.read(db.result.getClob(PROPERTY_VALUE_CLOB_COL));
                } else {
                    // All other types read from the regular value column
                    value = db.result.getString(PROPERTY_VALUE_COL);
                }

                token = new Token(
                        contextID,
                        db.result.getString(PROPERTY_COL),
                        value,
                        type,
                        language,
                        RecordStatus.ACTIVE,
                        net.project.util.Conversion.toBoolean((db.result.getString(IS_SYSTEM_PROPERTY))),
                        net.project.util.Conversion.toBoolean((db.result.getString(IS_TRANSLATABLE_PROPERTY))));

                this.addToken(token);
            }

            // Indicate the time at which the token collection is loaded
            this.loadedDate = new Date();

        } catch (SQLException sqle) {
            throw new PersistenceException("TokenCollectionCache.loadGlossary() threw an SQLException: " + sqle, sqle);
        } catch (Throwable t) {
            throw new RuntimeException("error found", t);
        } finally {
            db.release();

        }
    }
}