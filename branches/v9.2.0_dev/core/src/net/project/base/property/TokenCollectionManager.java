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
package net.project.base.property;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletContext;

import net.project.persistence.PersistenceException;

/**
 * Provides utility methods for managing token collections.
 */
public class TokenCollectionManager {

    /**
     * Reload the tokens for the specified context ID and language.
     * The tokens are actually only reloaded if they are currently loaded.
     * If the tokens are not currently loaded, they will be on their next access.
     *
     * @param application the application scope required for locating the
     * token collection to reload
     * @param contextID the ID of the context for which to load tokens
     * @param language the language for which to load tokens
     * @throws PersistenceException if there is a problem loading the tokens
     */
    public static void reloadTokens(ServletContext application, String contextID, String language)
            throws PersistenceException {

        TokenCollectionCache tokenCollectionCache = TokenCollectionCache.getInstance(application);
        tokenCollectionCache.reloadTokenCollection(new Context(contextID, language));
    }

    /**
     * Gets a map of contexts to token collections for each context in the
     * specified collection always loading from the database.
     * @param contexts the collection of contexts for which tokens are required
     * @return the map where each key is a <code>Context</code> and each value
     * is a <code>TokenCollection</code>.  The keys are not in any specified
     * order
     * @throws PersistenceException if there is a problem loading for any
     * context
     */
    static Map loadTokensForContexts(Collection contexts) throws PersistenceException {
        Map tokenMap = new HashMap();
        TokenCollection tokenCollection;

        for (Iterator it = contexts.iterator(); it.hasNext(); ) {
            Context context = (Context) it.next();

            tokenCollection = new TokenCollection();
            tokenCollection.load(context);
            tokenMap.put(context, tokenCollection);
        }

        return tokenMap;
    }

    /**
     * Loads the tokens for the specified contexts.
     * @param application the application scope from which to get tokens
     * @param contexts the contexts for which tokens are required
     * @return the map where each key is a <code>Context</code> and each value
     * is a <code>TokenCollection</code>.  The keys are not in any specified
     * order
     * @throws PersistenceException if tokens have to be loaded and there is
     * a problem loading
     */
    static Map getTokensForContexts(ServletContext application, Collection contexts)
            throws PersistenceException {

        // First load the information about whether properties have changed
        // This will tell the token collection cache whether properties have
        // changed for a particular context since it last loaded the context
        // We load it here to avoid database access during a synchronized block
        TokenChangeManager changeManager = TokenChangeManager.getLoadedInstance();

        TokenCollectionCache tokenCollectionCache = TokenCollectionCache.getInstance(application);
        Map tokenMap = new HashMap();

        // For each context, get the token collection
        // The collection cache will load or reload the tokens for the context
        // if they have not yet been loaded or have changed since the last
        // load
        for (Iterator it = contexts.iterator(); it.hasNext(); ) {
            Context context = (Context) it.next();
            TokenCollection tokenCollection = tokenCollectionCache.getTokenCollection(context, changeManager);
            tokenMap.put(context, tokenCollection);
        }

        return tokenMap;
    }

}