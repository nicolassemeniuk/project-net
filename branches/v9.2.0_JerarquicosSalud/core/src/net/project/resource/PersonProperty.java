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

 package net.project.resource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * This class is a utility for obtaining properties for a person within a particular space.
 * <p>
 * Depending on the scope, the properties may be stored and loaded independent of a space.
 * </p>
 * <p>
 * A property key is identified by the property context and property name.
 * eg: <br>
 *   The context for properties about a particular channel is <br>
 *       net.project.channel.channel_name<br>
 *   The properties may be<br>
 *       bgcolor<br>
 *       state<br>
 *       customTitle<br>
 * </p>
 * <p>
 * Property keys can have one or many values associated with them.  Before a
 * PersonProperty object can be used to get, post or remove properties you
 * must set the scope by calling <code>setScope</code>.
 * </p>
 * @author Adam Klatzkin
 * @author Matthew Flower
 * @author Tim Morrow
 * @since 03/00
 */
public class PersonProperty implements java.io.Serializable {

    //
    // Static Members
    //

    /**
     * Returns the PersonProperty from the session or creates one and puts it in
     * the session if there is none.
     * @param session the session from which to get the PersonProperty
     * @return the PersonProperty
     */
    public static PersonProperty getFromSession(HttpSession session) {
        PersonProperty settings = (PersonProperty) session.getAttribute("PR_user_properties");
        if (settings == null) {
            settings = new PersonProperty();
            session.setAttribute("PR_user_properties", settings);
        }
        return settings;
    }

    //
    // Instance Members
    //

    /** The scope from which to load settings. */
    private IPersonPropertyScope scope = null;

    /**
     * The cache is a map where each key is a "context" (a named location
     * in code for which property values are stored) and each value
     * is a Map.
     * <p>
     * The value Map is a map where each key is a property name and each value
     * is a list of String property values.
     * </p>
     */
    private final Map cache = new HashMap();

    private boolean matchExactContext = false;
    
    /**
     * Person property has facility to prefech and cache the tokens
     * Prefect can be done for extact contect match or wildcard matching such as %context%
     * Set this to true when it is required to load properties for exact context such as 'prm.schedule.main'
     */
    public void setMatchExactContext(boolean matchExactContext){
    	this.matchExactContext = matchExactContext;
    }
    
  
    /**
     * Specifies the scope against which person properties are
     * to be loaded and stored.
     * <p>
     * If the specified scope is different from the current scope
     * then the cache is cleared.
     * </p>
     *
     * @param scope the scope against which to load and store
     * person properties
     */
    public void setScope(IPersonPropertyScope scope) {
        if (this.scope == null || !this.scope.isSameScope(scope)) {
            cache.clear();
        }
        this.scope = scope;
    }

    /**
     * Returns the current scope.
     * @return the current scope
     */
    public IPersonPropertyScope getScope() {
        return this.scope;
    }

    /**
     * Indicates whether the specified scope is currently managed
     * by this PersonalProperty manager.
     * <p>
     * If not current, then the cache would be cleared if the scope were set.
     * </p>
     * @param scope the scope to check
     * @return true if the current settings are loaded for that scope;
     * false if the scope would require a reload of settings
     */
    public boolean isCurrent(IPersonPropertyScope scope) {
        return (this.scope != null && this.scope.isSameScope(scope));
    }

    /**
     * Gets the values for the specified properties assigned to the
     * current scope.
     *
     * @param context property context
     * @param property property name
     * @param useCache should property be fetched from cache only or not
     *
     * @return  String[]    an array of values assigned to the property
     */
    public String[] get(String context, String property, boolean useCache) {

        // attempt to retrieve the property from the cache
        Map props = (Map) cache.get(context);
        if (props != null) {

            List values = (List) props.get(property);
            if (values != null) {
                return (String[]) values.toArray(new String[]{});
            }
        }
        if(useCache)
        	return new String[]{};

        String query;

        if (scope == null || context == null || property == null) {
            return null;
        }

        query = "SELECT value " +
                "FROM pn_person_properties " +
                "WHERE space_id = ? " +
                "and person_id = ? " +
                "and context = ? " +
                "and property = ? ";

        String[] values = null;
        final DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query);
            db.pstmt.setString(++index, this.scope.getSpaceID());
            db.pstmt.setString(++index, this.scope.getUserID());
            db.pstmt.setString(++index, context);
            db.pstmt.setString(++index, property);
            db.executePrepared();
            List oValues = new ArrayList();

            while (db.result.next()) {
                oValues.add(db.result.getString(1));
            }

            // add the property values to the cache
            values = (String[]) oValues.toArray(new String[]{});
            cacheReplace(context, property, oValues);

        } catch (SQLException sqle) {
        	Logger.getLogger(PersonProperty.class).debug("PersonProperty.get() failed " + sqle);

        } finally {
            db.release();
        }

        return values;
    }
    
    /**
     * Gets the values for the specified properties assigned to the
     * current scope.
     *
     * @param context property context
     * @param property property name
     *
     * @return  String[]    an array of values assigned to the property
     */
    public String[] get(String context, String property) {
    	return get(context, property, false);
    }


    public void replace(String context, String property, String value) throws PersistenceException {
        removeAllValues(context, property);
        put(context, property, value);
    }

    /**
     * store a property value pair.
     * the value is added to existing values if they already exist
     *
     * @param context        property context
     * @param property       property name
     * @param value          property value
     *
     * @throws PersistenceException
     *         thrown if the property-value pair already exists
     */
    public void put(String context, String property, String value) throws PersistenceException {

        if (scope == null || context == null || property == null || value == null) {
            return;
        }

        final DBBean db = new DBBean();

        try {
            db.prepareCall("INSERT INTO pn_person_properties VALUES (?, ?, ?, ?, ?)");
            db.cstmt.setInt(1, Integer.parseInt(scope.getSpaceID()));
            db.cstmt.setInt(2, Integer.parseInt(scope.getUserID()));
            db.cstmt.setString(3, context);
            db.cstmt.setString(4, property);
            db.cstmt.setString(5, value);
            db.executeCallable();

            cachePut(context, property, value);
        } catch (SQLException sqle) {
            throw new PersistenceException("Error storing Person Property: " + sqle, sqle);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in PersonProperty.put(): " + nfe, nfe);
        } finally {
            db.release();
        }
    }

    /**
     * remove all values for a context.property.
     *
     * @param context        property context
     * @param property       property name
     * @throws PersistenceException
     */
    public void removeAllValues(String context, String property) throws PersistenceException {

        if (scope == null) {
            throw new PersistenceException("Scope must be set");
        }

        final DBBean db = new DBBean();

        try {
            db.prepareCall("DELETE FROM pn_person_properties WHERE space_id=? AND person_id=? AND context=? AND property=?");
            db.cstmt.setInt(1, Integer.parseInt(scope.getSpaceID()));
            db.cstmt.setInt(2, Integer.parseInt(scope.getUserID()));
            db.cstmt.setString(3, context);
            db.cstmt.setString(4, property);
            db.executeCallable();

            // remove the cached property
            Map props = (Map) cache.get(context);
            if (props != null) {
                props.remove(property);
            }
        } catch (SQLException sqle) {
            throw new PersistenceException("Error removing property: " + sqle, sqle);
        } catch (NumberFormatException nfe) {
            throw new PersistenceException("ParseInt Failed in PersonProperty.removeAllValues(): " + nfe, nfe);
        } finally {
            db.release();
        }

    }

    /**
     * Puts a value in the cache.
     * If the specified context is not in the cache or the specified property
     * is not found for the context then a new cache entry is created.
     * If the property is already present then the value is added to the
     * property's list of values.
     * <b>Note:</b> This may not be expected behavior; the property value is
     * not replaced.  To replace a property's value, first remove the property
     * from the cache.
     * @param context the context in which to store the property value
     * @param property the property
     * @param value the value for the property
     */
    private void cachePut(String context, String property, String value) {

        // cache the property
        Map props = (Map) cache.get(context);
        if (props == null) {
            props = new HashMap();
            cache.put(context, props);
        }
        List values = (List) props.get(property);
        if (values == null) {
            values = new ArrayList();
            props.put(property, values);
        }
        values.add(value);
    }

    /**
     * Replaces the values for the specified property.
     * If the properties are not found for the context then a new cache entry
     * is created.  The property's values are replaced with the list of
     * values.
     * @param context the context for whcih to replace the property values
     * @param property the property whose values to replace
     * @param values the list of values where each element is a <code>String</code>
     */
    private void cacheReplace(String context, String property, List values) {

        // cache the property
        Map props = (Map) cache.get(context);
        if (props == null) {
            props = new HashMap();
            cache.put(context, props);
        }

        props.put(property, values);
    }

    /**
     * Pre-fetches settings for the current scope where the context
     * starts with the specified prefix.
     * <p>
     * This is mostly a convenience when it is known in advance that a number
     * of property values are required for multiple related contexts.
     * </p>
     * <p>
     * The cache values are always replaced.
     * </p>
     * @param contextPrefix the value to match with the start of the
     * context
     */
    public void prefetchForContextPrefix(String contextPrefix) {

        if (scope == null) {
            return;
        }

        String query = "SELECT context, property, value " +
                "FROM pn_person_properties " +
                "WHERE space_id = ? " +
                "and person_id = ? ";
        if(matchExactContext)
        	query += "and context = ? ";
        else
        	query += "and context like ? ";

        final DBBean db = new DBBean();

        try {
            int index = 0;
            db.prepareStatement(query);
            db.pstmt.setString(++index, scope.getSpaceID());
            db.pstmt.setString(++index, scope.getUserID());
            if(matchExactContext)
            	db.pstmt.setString(++index, contextPrefix);
            else
            	db.pstmt.setString(++index, "%" + contextPrefix + "%");
            
            db.executePrepared();

            String context = null;
            String property = null;
            List values = new ArrayList();

            while (db.result.next()) {
                String nextContext = db.result.getString("context");
                String nextProperty = db.result.getString("property");

                if (!nextContext.equals(context) || !nextProperty.equals(property)) {
                    // Context or property has changed
                    // Replace the cached values (assuming this isn't the first loop)
                    if (context != null && property != null) {
                        cacheReplace(context, property, values);
                    }
                    context = nextContext;
                    property = nextProperty;
                    values = new ArrayList();
                }

                values.add(db.result.getString("value"));
            }
            //call cacheReplace again for the last context, propery and value, otherwise the lase record will always be missed.
            if (context != null && property != null) {
            	cacheReplace(context, property, values);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(PersonProperty.class).debug("Error loading properties: " + sqle);

        } finally {
            db.release();
        }

    }

}
