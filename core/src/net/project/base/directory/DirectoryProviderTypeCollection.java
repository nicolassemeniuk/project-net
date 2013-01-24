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
+----------------------------------------------------------------------*/
package net.project.base.directory;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

/**
 * The collection of <code>DirectoryProviderType</code>s that are
 * available when defining a domain.
 *
 * @author Tim
 * @since Gecko Update 3
 */
public class DirectoryProviderTypeCollection extends java.util.ArrayList {

    //
    // Static members
    //

    /**
     * Gets a loaded instance of all the directory provider types
     * available.
     * @return the loaded colelction of directory provider types
     * @throws PersistenceException if there is a problem loading the
     * types
     */
    public static DirectoryProviderTypeCollection getInstance() 
            throws net.project.persistence.PersistenceException {
        
        DirectoryProviderTypeCollection types = new DirectoryProviderTypeCollection();
        types.load();
        
        return types;
    }

    //
    // Instance members
    //

    /**
     * Flag indicating whether this collection has been loaded from
     * persistent store.
     */
    private boolean isLoaded = false;


    /**
     * Clears this collection and sets the <code>loaded</code> flag
     * to false.
     * @see #isLoaded
     */
    public void clear() {
        super.clear();
        setLoaded(false);
    }

    /**
     * Loads all the directory provider types from persistent storage.
     * The collection is cleared first.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws net.project.persistence.PersistenceException {
        
        // Clear all entries
        clear();

        DBBean db = new DBBean();

        try {
            db.prepareStatement(DirectoryProviderType.getLoadQuery());
            db.executePrepared();
            
            while (db.result.next()) {
                DirectoryProviderType providerType = new DirectoryProviderType();
                DirectoryProviderType.populate(db.result, providerType);
                add(providerType);
            }

            setLoaded(true);
        
        } catch (java.sql.SQLException sqle) {
            throw new net.project.persistence.PersistenceException("Directory provider type load operation failed: " + sqle, sqle);
        
        } finally {
            db.release();

        }

    }

    /**
     * Specifies whether this collection has been loaded from persistent
     * store.
     * @param isLoaded true if the colleciton has been loaded; false if
     * it has not yet been loaded
     * @see #isLoaded
     */
    private void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    /**
     * Indicates whether this collection has been loaded from persistent
     * store.
     * @return true if this collection has been loaded from persistent
     * store; false if not
     * @see #load
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Returns an HTML option list of directory provider types.
     * Each value is the id of the provider type; the display
     * text is the name of the provider type.
     * @param selectedTypeID the id of the type to pre-select in
     * the option list
     * @return the HTML option list
     */
    public String getOptionList(String selectedTypeID) {
        StringBuffer optionBuffer = new StringBuffer();
        
        for (java.util.Iterator it = iterator(); it.hasNext(); ) {
            DirectoryProviderType nextType = (DirectoryProviderType) it.next();
            
            optionBuffer.append("<option value=\"").append(nextType.getID()).append("\"");
            if (nextType.getID().equals(selectedTypeID)) {
                optionBuffer.append(" selected");
            }
            optionBuffer.append(">").append(nextType.getName()).append("</option>");
        }

        return optionBuffer.toString();
    }

}
