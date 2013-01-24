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
| Singleton PropertyProvider
+----------------------------------------------------------------------*/
package net.project.base.property;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;

/**
 * Provides access to properties in a session.
 * <p>
 * All access to properties goes through the PropertyBundle.  Each user's session
 * maintains a property bundle for accessing properties.
 * The property bundle maintains the lookup hierarchy, but does not store
 * the properties itself.
 * </p>
 * <p>
 * When loaded, the PropertyBundle builds a search map by combining the tokens
 * for each level (user, space, brand) in order and for each context/language
 * combination in the correct order.
 * </p>
 *
 * @author Unascrbied
 * @since Version 2
 */
public class PropertyBundle implements java.io.Serializable {

    /**
     * The session attribute name used for storing the session-singleton
     * property bundle.
     */
    public static final String PROPERTY_BUNDLE_SESSION_OBJECT_NAME = "propertyBundle";

    static final String PROPERTY_BRAND_CONTEXT = "brand_context";
    static final String PROPERTY_SPACE_CONTEXT = "space_context";
    static final String PROPERTY_USER_CONTEXT = "user_context";

    /**
     * The order to load glossaries when initializing the search map.
     */
    private static final String[] SEARCH_ORDER =
            {PROPERTY_USER_CONTEXT, PROPERTY_SPACE_CONTEXT, PROPERTY_BRAND_CONTEXT};


    private final PropertyCategoryManager categories = new PropertyCategoryManager();

    /**
     * The map that maintains access to the tokens in the application scope.
     * It is a linked collection of TokenSearchMap built in context-order.
     */
    private TokenSearchMap searchMap = null;

    /**
     * A handle on the servlet context, used for back-door access to the
     * application scope.  This is needed so that the properties can be
     * switched and loaded into the token cache at any time.
     */
    ServletContext servletContext = null;

    private boolean isLoaded = false;

    /* -------------------------------  Constructors  ------------------------------- */

    /**
     * Creates a new PropertyBundle.
     */
    protected PropertyBundle() {
        // Do nothing
    }


    /**
     * Gets the active instance of the property bundle from the session.
     * If there is no brand currently in the session, instantiates a new one,
     * however the PropertyBundle IS NOT ADDED TO THE SESSION.
     * @return the existing session property bundle or a new property bundle
     */
    static PropertyBundle getInstance() {
        Object propertyBundle = SessionManager.getPropertyBundle();
        return (propertyBundle != null) ? (PropertyBundle) propertyBundle : PropertyBundleType.findType().getPropertyBundle();
    }

    /* -------------------------------  PropertyBundle Search Methods  ------------------------------- */

    /**
     * Lookup a property name in the glossary.
     * @param property the name of the property value to get
     * @return the property value
     */
    Object lookup(String property) {
        return this.searchMap.getTokenValue(property);
    }


    /**
     * Lookup a property name in the glossary, returning the values at each
     * level in the search hierarchy.
     * @param property the name of the property to lookup
     * @return the Collection of values where each element is an <code>Object</code>
     */
    Collection lookupAllResolved(String property) {
        return this.searchMap.getTokenValuesResolved(property);
    }


    /**
     * Indicates whether the specified token is defined.
     * A token is defined if its value is not null.  Since all default tokens
     * must have a value, this logic works.
     * @param token the token to check
     * @return true if the token is not defined; false otherwise
     */
    boolean isTokenDefined(String token) {
        return (lookup(token) != null) ? true : false;
    }

    /* -------------------------------  PropertyBundle Persistence Methods  ------------------------------- */

    /**
     * Loads the PropertyBundle based on the settings in the session-scoped
     * BrandManager.
     * This method is undefined if no BrandManager exists.
     * @param application the application scope required for accessing cached
     * tokens
     * @throws PersistenceException if there is a problem loading
     * any glossaries
     */
    void load(javax.servlet.ServletContext application) throws PersistenceException {

        try {

            // Save the servlet context
            // This allows us to switch configuration context at any time
            this.servletContext = application;

            // load all the glossaries for the supported contexts.
            Map glossaryMap = loadGlossarys(application);

            // now that we've loaded the glossaries, set the searchmap
            initializeSearchMap(glossaryMap);

            // now load the category list and associated properties for this glossary
            this.categories.loadCategories();

            setIsLoaded();

        } catch (PersistenceException pe) {
            setIsLoaded(false);
            throw new PersistenceException("PropertyBundle.load() threw a PersistenceException: " + pe, pe);

        } catch (PropertyException pe) {
            setIsLoaded(false);
            throw new PersistenceException("PropertyBundle.load() threw a PropertyException: " + pe, pe);
        }

    }

    PropertyCategoryManager getCategories() {
        return this.categories;
    }


    boolean isLoaded() {
        return this.isLoaded;
    }


    /**
     * Loads all supported glossaries.
     * @param application
     * @return the map of String context to Glossary
     * @throws PersistenceException
     * @throws PropertyException
     */
    private Map loadGlossarys(javax.servlet.ServletContext application) throws PersistenceException, PropertyException {
        Map glossaryMap = new HashMap();

        for (int i = 0; i < SEARCH_ORDER.length; i++) {
            loadContextGlossary(SEARCH_ORDER[i], glossaryMap, application);
        }

        return glossaryMap;
    }

    /**
     * Loads the glossary for the specified context.
     * @param context
     * @param glossaryMap
     * @param application
     * @throws PersistenceException
     * @throws PropertyException if the context was loaded but is not supported
     * by this property bundle
     */
    private void loadContextGlossary(String context, Map glossaryMap, javax.servlet.ServletContext application) throws PersistenceException, PropertyException {
        Glossary glossary = null;

        if (context.equals(PROPERTY_BRAND_CONTEXT)) {
            BrandGlossary brandGlossary = new BrandGlossary(net.project.brand.BrandManager.getInstance());
            brandGlossary.load(application);
            glossary = brandGlossary;

        } else if (context.equals(PROPERTY_SPACE_CONTEXT)) {
            // TBC

        } else if (context.equals(PROPERTY_USER_CONTEXT)) {
            // TBC
        }

        if (glossary != null) {

            if (!isSupportedContext(context)) {
                throw new PropertyException("Context not supported.  Can not set context: " + context);
            }

            glossaryMap.put(context, glossary);

        }

    }


    /**
     * Initializes the token search map based on the current search order.
     */
    private void initializeSearchMap(Map glossaryMap) {

        TokenSearchMap tokenSearchMap = null;
        Glossary glossary = null;
        int glossaryAvailable = 0;

        for (int i = 0; i < SEARCH_ORDER.length; i++) {

            glossary = (Glossary) glossaryMap.get(SEARCH_ORDER[i]);

            if (glossary != null) {

                glossaryAvailable++;

                // if this is the first trip through
                if (glossaryAvailable == 1) {
                    tokenSearchMap = glossary.getTokenSearchMap();
                } else {
                    tokenSearchMap.setTailDefaults(glossary.getTokenSearchMap());
                }

            }
        } // end for

        this.searchMap = tokenSearchMap;
    }


    private boolean isSupportedContext(String context) {

        boolean flag = false;

        for (int i = 0; i < SEARCH_ORDER.length; i++) {

            if (context.equalsIgnoreCase(SEARCH_ORDER[i])) {
                flag = true;
                break;
            }
        }

        return flag;
    }


    private void setIsLoaded(boolean flag) {
        this.isLoaded = flag;
    }

    private void setIsLoaded() {
        setIsLoaded(true);
    }


}
