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

 package net.project.base.property;

import java.io.Serializable;
import java.text.Format;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Locale;

import net.project.brand.BrandManager;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.util.Conversion;
import net.project.base.compatibility.Compatibility;

/**
 * Helper class for initializing and fetching individual property values.
 * <p>
 * Most common usage is accessing properties by any of the {@link #get} methods.
 * </p>
 *
 * @author Unascribed
 * @since Version 2
 */
public class PropertyProvider implements Serializable {

    /** The prefix used to indicate that a string value denotes a token
     * name.  All keys specified as arguments may include the token prefix;
     * it will be removed prior to looking up the property. */
    public static final String TOKEN_PREFIX = "@";

    /* -------------------------------  Search Methods  ------------------------------- */

    /**
     * Retuns the value of the property for the specified key.
     * @param key the property key (or name)
     * @return the value of property
     */
    public static String get(String key) {
        return (String) lookup(key);
    }

    /**
     * Returns the value of the property for the specified key substituting
     * the zeroth placeholder <code>{0}</code> with the specified argument.
     * @param key the property key (or name)
     * @param args1 the value of the first placeholder
     * @return the value of property
     */
    public static String get(String key, String args1) {
        return get(key, new Object[]{args1});
    }

    /**
     * Returns the value of the property for the specified key substituting
     * placeholder zero and one <code>{0}, {1}</code> with the specified arguments.
     * @param key the property key (or name)
     * @param args1 the value of the first placeholder
     * @param args2 the value of the second placeholder
     * @return the value of property
     */
    public static String get(String key, String args1, String args2) {
        return get(key, new Object[]{args1, args2});
    }


    /**
     * Returns the value of the property for the specified key substituting
     * the placeholders in the key with the arguments in the specified collection.
     * @param key the property key (or name)
     * @param argumentCollection the list of objects to format based on property value pattern;
     * if the collection is ordered, the ordering is preserved with replacing
     * the placeholders
     * @return the value of property
     */
    public static String get(String key, Collection argumentCollection) {
        return get(key, argumentCollection.toArray());
    }

    /**
     * Returns the value of the property for the specified key substituting
     * the placeholders in the key with the arguments in the specified array.
     * Equivalent to calling <code>get(key, arguments, null)</code>.
     * @param key the property key (or name)
     * @param arguments the array of objects to format based on property value pattern
     * @see #get(String, Object[], Format[])
     */
    public static String get(String key, Object[] arguments) {
        return (String) lookup(key, arguments, null);
    }

    /**
     * Returns the value of the property for specified key with formatted objects
     * inserted, formatting using specified formats (if supplied).
     * Uses the value of the property as a pattern and formats specified arguments
     * using {@link java.text.MessageFormat}.  If <code>formats</code> parameter
     * is null, the default formats are used.
     * @param key the property key (or name)
     * @param arguments the list of objects to format based on property value pattern
     * @param formats the list of formats to use when formatting objects
     * @return the property value with formatted object values
     */
    public static String get(String key, Object[] arguments, Format[] formats) {
        return (String) lookup(key, arguments, formats);
    }

    /**
     * Returns the value of the property for specified key, converting value
     * to boolean.
     * The conversion process is described in {@link net.project.util.Conversion#toBoolean(String)}.
     * @param key the property key (or name)
     * @return the property value as a boolean
     */
    public static boolean getBoolean(String key) {
        return Conversion.toBoolean(get(key));
    }

    /**
     * Returns the value of the property for specified key, converting value
     * to boolean.
     * The conversion process is described in {@link net.project.util.Conversion#toBoolean(String)}.
     * @param key the property key (or name)
     * @param ifUndefined a <code>boolean</code> defining what the return value
     * should be if the property is not found in the database.
     * @return the property value as a boolean
     */
    public static boolean getBoolean(String key, boolean ifUndefined) {
        boolean toReturn;

        if (isDefined(key)) {
            toReturn = getBoolean(key);
        } else {
            toReturn = ifUndefined;
        }

        return toReturn;
    }

    /**
     * Returns the value of the property for specified key, converting value
     * to an int.
     * @param key the property key (or name)
     * @return the property value as an int or -1 if the value cannot be
     * parsed
     */
    public static int getInt(String key) {
        int intValue;

        try {
            intValue = Integer.parseInt(get(key));
        } catch (NumberFormatException e) {
            intValue = -1;
        }

        return intValue;
    }


    /**
     * Returns all the values for the specified property found at each level
     * of the search hierarchy.  This method is typically used when the property
     * represents something that may itself may be searched hierarchically.
     * For example, consider a property that contains a directory name.  By
     * receiving all the directory names, the contents of each directory may
     * be searched in a hierarchical manner.<br>
     * The first element in the collection is the most relevant value for
     * the current context.  The last element in the collection is the application
     * default value.  The number of elements in the collection is equal to
     * the number of levels in the search hierarchy
     * @param key the property key (or name)
     * @return the Collection of property values where each element is of type
     * <code>String</code>
     */
    public static Collection getAll(String key) {
        ArrayList stringList = new ArrayList();

        // Cast all Objects to Strings
        Iterator it = lookupAll(key).iterator();
        while (it.hasNext()) {
            stringList.add(it.next());
        }

        return stringList;
    }

    /**
     * Parses the specified value, replacing any other tokens within that value.
     * Uses the same algorithm that parses the value of a token when loaded.
     * @param value the to parse
     * @return the value with any referenced tokens parsed
     */
    public static String parse(String value) {
        return TokenParser.getParsedValue(value);
    }

    /**
     * Parses the specified value, with formatted objects inserted.
     * @param value the value to parse
     * @param arguments the objects to insert into the parsed value
     * @see #parse(String, Object[], Format[])
     */
    public static String parse(String value, Object[] arguments) {
        return parse(value, arguments, null);
    }

    /**
     * Parses the specified value, with formatted objects
     * inserted, formatting using specified formats (if supplied).
     * Uses the value as a pattern and formats specified arguments
     * using {@link java.text.MessageFormat}.  If <code>formats</code> parameter
     * is null, the default formats are used.
     * @param value the value to parse
     * @param arguments the list of objects to format based on property value pattern
     * @param formats the list of formats to use when formatting objects
     * @return the property value with formatted object values
     */
    private static String parse(String value, Object[] arguments, Format[] formats) {
        return format(parse(value), arguments, formats);
    }

    /* ------------------------------- Private datasource methods  ------------------------------- */

    /**
     * Looks up the value of the property for specified key.
     * If the key begins with {@link #TOKEN_PREFIX}, it is stripped off before
     * using the remainder of the key.  Any dereferenced properties in the found property
     * value are replaced with their values.
     * If no value is found for the key, the key is returned as it is.
     * If the key is null, null is returned.
     * @param key the property key
     * @return the property value with dereferenced properties replaced
     */
    private static Object lookup(String key) {

        Object value = null;

        if (key != null) {

            PropertyBundle bundle = PropertyBundle.getInstance();
            value = bundle.lookup(stripTokenPrefix(key));
        }

        if (value != null) {
            return (value instanceof String) ? TokenParser.getParsedValue((String) value) : value;
        }

        return key;
    }

    /**
     * Looks up the value of the property for specified key, formatting specified
     * arguments using value as a pattern.
     * @see #format
     */
    private static Object lookup(String key, Object[] arguments, Format[] formats) {

        Object value = null;

        value = lookup(key);

        if (value instanceof String) {
            return format((String) value, arguments, formats);

        } else {
            return value;

        }
    }

    /**
     * Looks up the values of the property for specified key.
     * It returns a collection of property values which are the values of
     * the property returned by each level in the search hierarchy.  If a particular
     * level does not have a value of the property, then no value is returned
     * for that level.<br>
     * If the key begins with {@link #TOKEN_PREFIX}, it is stripped off before
     * using the remainder of the key.  Any dereferenced properties in the found property
     * value are replaced with their values.
     * @param key the property key
     * @return the Collection of property values with dereferenced properties replaced,
     * where each element is of type <code>Object</code>
     */
    private static Collection lookupAll(String key) {

        Collection values = null;

        if (key != null) {
            PropertyBundle bundle = PropertyBundle.getInstance();
            values = bundle.lookupAllResolved(stripTokenPrefix(key));
        }

        return values;
    }


    /**
     * Formats specified arguments using pattern.  Effectively replaces numbered
     * variables in specified pattern with formatted object values from corresponding
     * array indices.
     * @param pattern the string pattern containing numbered variables
     * @param arguments the array of objects to be formatted based on pattern
     * @param formats the formats to use; null if default formats are to be used
     * @return the formatted value with formatted objects in place
     * @see java.text.MessageFormat
     */
    private static String format(String pattern, Object[] arguments, Format[] formats) {

        // Construct a new message format and set the locale to the user's locale
        MessageFormat formatter = new MessageFormat("");
        if(SessionManager.getUser() != null)
        	formatter.setLocale(SessionManager.getUser().getLocale());
        else 
        	formatter.setLocale(Locale.US);

        // Set the formats if some were specified
        if (formats != null) {
            formatter.setFormats(formats);
        }

        formatter.applyPattern(pattern);

        return formatter.format(arguments);
    }

    /**
     * Sets the brand context based on the request.
     * This looks for a language parameter and sets the language bsed on that.
     *
     * @param request the request from which to get the context
     * @param application the application scope required for locating the
     * tokens from the token cache for the loaded context
     * @throws PropertyException if there is a problem loading the context from the
     * request
     */
    public static void setContextFromRequest(javax.servlet.http.HttpServletRequest request, javax.servlet.ServletContext application)
        throws PropertyException {

        try {

            // first load brand context
            setBrandContextFromRequest(request);

            // then load all of the other necessary contexts
            // Note:  There are no contexts other than brand yet

            // now initialize the property bundle
            initializePropertyBundle(application);

        } catch (PersistenceException pe) {
            throw new PropertyException("Error setting context from request: " + pe, pe);

        }

    }

    /**
     * Public accessor method to force a reload of the current application token cache for the active context
     * <p>
     * This method will reload the token cache for *all* logged in users.
     * Note:  This method expects that the token cache is already loaded.
     * @param application the application scope required for locating the
     * tokens from the token cache for the loaded context
     * @throws PersistenceException if there is a problem loading bundle
     */
    public static void forceReloadTokenCache (javax.servlet.ServletContext application) throws PersistenceException {

        TokenCollectionCache cache = TokenCollectionCache.getInstance(application);
        cache.forceReloadCache(new Context(getActiveBrandID(), getActiveLanguage()));

    }

    /**
     * Sets the brand context based on the specified brand and language.
     * <p>
     * If the specified brand does not exist or is null, try to load the specified default.  Otherwise, load the system default.
     * <br>Note: will only switch contexts if the specified brand is different from the current active brand.
     * @param brandID the brandID to load
     * @param language the language to load
     * @param defaultBrandID the brand to attempt to load if the specified user's brand is not available.
     * @throws PropertyException if there is a problem loading the context
     */
    public static void switchContext (String brandID, String language, String defaultBrandID, String defaultLanguageID) throws PropertyException {

        try {

            // load the brand context.
            // Note: if no brand is available for the requested brand, the system default brand will be loaded
            setBrandContext(brandID, language, defaultBrandID, defaultLanguageID);

            // now initialize the property bundle
            initializePropertyBundle(PropertyBundle.getInstance().servletContext);

        } catch (PersistenceException pe) {
            throw new PropertyException("Error switching context: " + pe, pe);
        }
    }

    /**
     * Loads the default system brand and properties 
     * @throws PropertyException
     */
    public static void loadDefaultContext() throws PropertyException {
    	switchContext(null, null, null, null);
    }
    
    /**
     * Sets the brand context based on the specified language.
     * Brand is initialized even if the language is already loaded.  This ensures
     * that properties are reloaded if they have changed in the database.
     * @param language the language to switch the context to
     * @param application the application scope required for locating the
     * tokens from the token cache for the loaded context
     * @throws PropertyException if there is a problem loading the context for
     * the specified language
     */
    public static void setLanguage(String language, javax.servlet.ServletContext application) throws PropertyException {

        net.project.brand.BrandManager brandManager = net.project.brand.BrandManager.getInstance();

        try {
            brandManager.setRequestedLanguage(language);
            initializePropertyBundle(application);

        } catch (PersistenceException pe) {
            throw new PropertyException("Error setting context from language: " + pe, pe);
        }

    }

    /**
     * Convenince method for switching the brand context without a handle
     * to the application scope.
     * <p>
     * This is useful when a context switch is required in deep code where
     * no application scope handle is available.  However, it is recommended
     * to always try to pass in the application scope where possible.
     * </p>
     * <p>
     * The PropertyBundle should have already been initialized with the
     * application scope by calling {@link #setContextFromRequest}.
     * </p>
     * @param language the language to switch to
     * @throws IllegalStateException if the brand has not been set with the
     * application scope
     */
    public static void setLanguage(String language) throws PropertyException {
        PropertyBundle propertyBundle = PropertyBundle.getInstance();

        if (!propertyBundle.isLoaded()) {
            // PropertyBundle has not been initialzed with the application scope yet
            // We cannot switch the language with backdoor access until it has
            throw new IllegalStateException("Attempt to switch brand context without application scope failed");
        }

        setLanguage(language, propertyBundle.servletContext);
    }

    /**
     * Initializes the property bundle in the session scope.
     * Uses "back-door" session access.
     * Updates the property bundle in session scope if it is already there; if
     * there is no bundle in session scope, one is added if this method succeeds
     * @param application the application scope required for obtaining handles
     * to tokens
     * @throws PersistenceException if there is a problem loading the property
     * bundle; if no property bundle existed in session scope, none was added
     */
    private static void initializePropertyBundle(javax.servlet.ServletContext application) throws PersistenceException {
        PropertyBundle propertyBundle = PropertyBundle.getInstance();
        propertyBundle.load(application);

        SessionManager.setPropertyBundle(propertyBundle);
    }

    /**
     * Sets the brand context.
     */
    private static void setBrandContext(String brandID, String language, String defaultBrandID, String defaultLanguageID) throws PropertyException {
        BrandManager brandManager = net.project.brand.BrandManager.getInstance();

        brandManager.clear();
        brandManager.setID(brandID);
        brandManager.setRequestedLanguage(language);

        if (brandManager.isLoadable()) {

            try {
                brandManager.load();
            } catch (PersistenceException e) {
                // Ignore; we'll load the default brand below
            }
        }

        if (!brandManager.isLoaded()) {

            // next, if the specified brand didn't load, try and load the specified default
            brandManager.clear();
            brandManager.setID(defaultBrandID);
            brandManager.setRequestedLanguage(defaultLanguageID);

            if (brandManager.isLoadable()) {

                try {
                    brandManager.load();
                } catch (PersistenceException e) {
                    // Ignore; we'll load the default brand below
                }
            }
        }

        // Try and load the default brand if we were unable to load
        // a brand for any reason both the user brand and the specified default wouldn't load.
        if (!brandManager.isLoaded()) {
            try {
                brandManager.setDefaultBrand();
                brandManager.load();
            } catch (PersistenceException e) {
                // Now this is a serious error
                throw new PropertyException("Unable to load any specified brand or the default brand: " + e, e);
            }
        }

        // finally put it back into the session
        Compatibility.getSessionProvider().setAttribute(BrandManager.BRAND_MANAGER_SESSION_OBJECT_NAME, brandManager);
    }

    /**
     * Loads the brand context based on the brand and language information in
     * the specified request.  If a brand context cannot be loaded based on
     * the request parameters, the system default brand is loaded.
     * @param request the request from which to get brand information
     * @throws PropertyException if there is a problem loading the system
     * default brand
     */
    private static void setBrandContextFromRequest(javax.servlet.http.HttpServletRequest request)
        throws PropertyException {

        BrandManager brandManager = net.project.brand.BrandManager.getInstance();

        // first clear the brand, in case it was loaded
        brandManager.clear();
        brandManager.setBrandFromRequest(request);
        brandManager.setLanguageFromRequest(request);

        if (brandManager.isLoadable()) {
            // We got brandID or abbreviation from request or hostname
            try {
                brandManager.load();
            } catch (PersistenceException e) {
                // Ignore; we'll load the default brand below
            }
        }

        // Try and load the default brand if we were unable to load
        // a brand for any reason (either request had no information or
        // there was a problem loading
        if (!brandManager.isLoaded()) {
            try {
                brandManager.setDefaultBrand();
                brandManager.load();
            } catch (PersistenceException e) {
                // Now this is a serious error
                throw new PropertyException("Unable to load any specified brand or the default brand: " + e, e);
            }
        }

        // finally put it back into the session
        request.getSession().setAttribute(BrandManager.BRAND_MANAGER_SESSION_OBJECT_NAME, brandManager);
    }



    /* ------------------------------- Brand Convenience Methods  ------------------------------- */

    public static String getActiveBrandID() {
        BrandManager brandManager = BrandManager.getInstance();
        return brandManager.getID();
    }


    public static String getActiveConfigurationID() {
        BrandManager brandManager = BrandManager.getInstance();
        return brandManager.getConfigurationID();
    }

    public static String getActiveLanguage() {
        BrandManager brandManager = BrandManager.getInstance();
        return brandManager.getActiveLanguage();
    }

    /* ------------------------------- Utility Methods / Display Methods  ------------------------------- */

    /**
     * Returns the name of the session attribute that is the session-singleton
     * PropertyBundle.
     * @return the the session attribute name
     */
    public static String getPropertyBundleSessionName() {
        return PropertyBundle.PROPERTY_BUNDLE_SESSION_OBJECT_NAME;
    }

    /**
     * Indicates whether properties are loaded.
     * @return true if properties are loaded; false otherwise
     */
    public static boolean isLoaded() {
        PropertyBundle propertyBundle = PropertyBundle.getInstance();
        return propertyBundle.isLoaded();
    }

    /**
     * Removes the token prefix from the token name, if one is present.
     * @param token the token name from which to strip the prefix
     * @return the token with the prefix removed, or the original value if
     * it has no token
     */
    public static String stripTokenPrefix(String token) {
        return ((token.startsWith(TOKEN_PREFIX)) ? token.substring(TOKEN_PREFIX.length()) : token);
    }

    /**
     * Indicates whether the specified token has a value.
     * A token is defined if a non-null value is found for the token; since
     * all default brand tokens must have a value a token must not be defined
     * if its value is null.
     * @param token the token to check
     * @return true if the token is defined; false if not
     */
    public static boolean isDefined(String token) {
        PropertyBundle propertyBundle = PropertyBundle.getInstance();
        return propertyBundle.isTokenDefined(token);
    }

    /**
     * Indicates whether the specified token starts with the token prefix.
     * @param token the token to check
     * @return true if the token starts with the token prefix, false if not.
     * false is returned if the token is null
     */
    public static boolean isToken(String token) {
        return (token != null) ? token.startsWith(TOKEN_PREFIX) : false;
    }

    //
    // Instance Methods
    //

    /**
     * Private constructor to prevent instantiation.
     */
    private PropertyProvider() {
        // Do nothing
    }

}
