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
|    $Revision: 18948 $
|        $Date: 2009-02-21 09:39:24 -0200 (sáb, 21 feb 2009) $
|      $Author: ritesh $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.xml;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap; // use jdk provided ConcurrentHashMap implementation

import org.apache.log4j.Logger;

/**
 * Provides a simple cache that supports expiry and reloading of items
 * in the cache.
 *
 * @author Tim Morrow
 * @since Version 7.6.3
 */
abstract class Cache {

    private static final Logger logger = Logger.getLogger(Cache.class);

    /**
     * The map of keys to entries.
     */
    private final Map cacheMap = new ConcurrentHashMap();

    /**
     * The loader used to load entries not already in the cache.
     */
    private final ICacheLoader cacheLoader;

    /**
     * Indicates whether we should check for expired entries.
     */
    private final boolean isCheckForExpiry;

    /**
     * Creates a new cache with the specified loader.
     * @param cacheLoader the loader to use to load missing entries
     * @param isCheckForExpiry true if every entry should be checked on a get
     * to see if it has expired
     */
    protected Cache(ICacheLoader cacheLoader, boolean isCheckForExpiry) {
        this.cacheLoader = cacheLoader;
        this.isCheckForExpiry = isCheckForExpiry;
    }

    /**
     * Returns the entry from the cache for the specified key,
     * adding it to the cache if it doesn't exist.
     * <p>
     * The <code>cacheLoader</code> is used to load the entry for
     * the key if it is not present in the cache or if it has changed.
     * </p>
     * @param key the key for which to get the entry
     * @return the entry for that key
     * @throws CacheLoadException if there is a problem loading the entry
     */ 
    protected IEntry getEntry(IKey key) throws CacheLoadException {
        if (logger.isDebugEnabled()) {
            logger.debug("Getting entry for key " + key.toString() + ", current size " + cacheMap.size());
        }
        IEntry entry = (IEntry) cacheMap.get(key);
        if (entry == null || (isCheckForExpiry && entry.isExpired(key))) {
            entry = cacheLoader.load(key);
            cacheMap.put(key, entry);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("After, current size " + cacheMap.size());
        }
        return entry;
    }

    //
    // Nested top-level classes & interfaces
    //

    /**
     * A cache loader is used to load an entry for a specified
     * key when the entry is not found or has expired.
     */
    static interface ICacheLoader {

        /**
         * Loads the entry for the specified key.
         * @param key the key for which to load the entry
         * @return the loaded entry
         */
        IEntry load(IKey key) throws CacheLoadException;

    }

    /**
     * Provides a key to a cache.
     * <p>
     * Implementing classes <b>MUST</b> override <code>equals</code> and <code>hashCode</code>.
     * </p>
     */
    static interface IKey {
    }

    /**
     * Provides an entry in the cache.
     */
    static interface IEntry {
        /**
         * Indicates whether this entry's content may have expired
         * given the key that represents the content.
         * <p>
         * It is up to the implementer to ensure that the key provides
         * whatever information is needed to test for expiration and
         * the entry maintains the original information.
         * For example, if the entry is the content of a file then the
         * entry should maintain the original modified date and the key provide
         * the new modified date.
         * </p>
         * @param cacheKey the key that may provide information to determine
         * whether the entry has expired
         * @return true if the entry has expired; false otherwise
         */
        boolean isExpired(IKey cacheKey);
    }
    
}
