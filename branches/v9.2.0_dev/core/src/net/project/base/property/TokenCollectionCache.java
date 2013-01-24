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
|   $Revision: 19579 $
|       $Date: 2009-07-27 06:45:23 -0300 (lun, 27 jul 2009) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.base.property;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import net.project.persistence.PersistenceException;

/**
 * This class maintains a cache of tokens by context in the application scope.
 * All tokens are stored here.
 *
 * @author Vishwajeet
 * @since 7.4
 */
class TokenCollectionCache implements Serializable {

    //
    // Static Members
    //

    /**
     * Gets an instance of this TokenCollectionCache.
     * It first tries to retrieve the tokenCollectionCache from the Application Scope,
     * if not found there, then it creates a new instance and adds the new instance
     * back into the application scope.
     * <p>
     * This method is synchronized to ensure there is only every one cache
     * in the application scope.
     * </p>
     *
     * @param application the application scope which maintains the manager
     * @return the application-scope singleton instance of this class
     */
    synchronized static TokenCollectionCache getInstance(javax.servlet.ServletContext application) {
        
    	TokenCollectionCache tokenCollectionCache = null;
    	if (application != null ) 
    		tokenCollectionCache = (TokenCollectionCache) application.getAttribute(TOKEN_COLLECTION_MANAGER_APPLICATION_OBJECT_NAME);

        if (tokenCollectionCache == null) {
            tokenCollectionCache = new TokenCollectionCache();
            if (application != null ) 
            	application.setAttribute(TOKEN_COLLECTION_MANAGER_APPLICATION_OBJECT_NAME, tokenCollectionCache);
        }

        return tokenCollectionCache;
    }

    //
    // Instance Members
    //

    /**
     * The token collection manager objects identity in the application scope.
     */
    private static final String TOKEN_COLLECTION_MANAGER_APPLICATION_OBJECT_NAME = "tokenCollectionCache";

    /**
     * A map where each key is a Context and each value is a TokenCollection.
     * This is the master collection of all loaded token collections.
     */
    private final Map tokenCollectionMap;

    /**
     * Constructs a TokenCollectionCache.
     * The access has been intentionally restricted so that the only access to it would be through
     * the @link TokenCollectionCache#getInstance method.
     */
    private TokenCollectionCache() {
        this.tokenCollectionMap = new HashMap();
    }

    /**
     * Gets the token collection for the given context.
     * If there is no loaded token collection for the context then a new
     * token collection for the context is loaded and added to this manager.
     * <p>
     * This method is synchronized to prevent simultaneous loading of
     * tokens for the same context.
     * </p>
     *
     * @param context the context for which to get the token collection.
     * @param changeManager the loaded PropertyChangeManger that determines the date that
     * properties last changed; properties are reloaded if this cache's date
     * is earlier than the last update date.  This is passed into the method
     * to avoid synchronized database access and also to allow clients to
     * load the date information once rather than hit the database each time
     * a token collection is needed
     * @return the token collection for the given context
     * @throws PersistenceException if there is a problem loading a token
     * collection for a context
     */
    synchronized TokenCollection getTokenCollection(Context context, TokenChangeManager changeManager) throws PersistenceException {

        TokenCollection tokenCollection = (TokenCollection) this.tokenCollectionMap.get(context);

        // Load is required if no collection is loaded for the context
        // OR if the properties for the context have changed since the load date
        boolean isLoadRequired = (tokenCollection == null ||
                changeManager.hasChanged(context, tokenCollection.getLoadedDate()));

        if (isLoadRequired) {
            tokenCollection = new TokenCollection();
            tokenCollection.load(context);
            this.tokenCollectionMap.put(context, tokenCollection);
        }

        // Return the token collection from map
        return tokenCollection;
    }

    /**
     * Reloads a tokenCollection for the given context.
     * It only reloads the token collection if it has been previously been
     * loaded.
     * Synchronized access is required to avoid simultaneous reloading
     * of tokens for the same context.
     * @param context the context to reload tokens for
     */
    synchronized void reloadTokenCollection(Context context) throws PersistenceException {

        TokenCollection tokenCollection = (TokenCollection) this.tokenCollectionMap.get(context);

        // Reload the tokencollection for this context only if it is active.
        if (tokenCollection != null) {
            tokenCollection = new TokenCollection();
            tokenCollection.load(context);
            this.tokenCollectionMap.put(context, tokenCollection);
        }
    }

    /**
     * Reloads the application token cache for a given context. It only reloads the token collection if it has been
     * previously been loaded. Synchronized access is required to avoid simultaneous reloading of tokens for the
     * same context.
     * @param context the context to reload tokens for
     */
    synchronized void forceReloadCache(Context context) throws PersistenceException {

        TokenCollection tokenCollection = (TokenCollection) this.tokenCollectionMap.get(context);

        // Reload the tokencollection for this context only if it is active.
        if (tokenCollection != null) {
            tokenCollection.load(context);
        }
    }

}