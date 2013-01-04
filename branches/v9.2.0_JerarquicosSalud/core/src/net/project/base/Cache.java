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

 package net.project.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.project.persistence.PersistenceException;

/**
 * This basically a fancy map of objects that also has the ability to load the
 * object if the object isn't currently in the map.
 *
 * @author Matthew Flower
 * @since Version 8.0.1
 */
public class Cache implements Map {
    /**
     * Map of the objects in the cache.
     */
    private Map cache = new HashMap();

    /**
     * Get an object the cache, loading from the database if necessary.
     *
     * @param key
     * @return
     */
    public Object getWithLoad(String key) throws PersistenceException {
        //Object object = ObjectFactory.load(key);
        Object object = null;

        if (object instanceof Identifiable) {
            cache.put(((Identifiable)object).getID(), object);
        }

        return object;
    }

    public Object put(Identifiable value) {
        return put(value.getID(), value);
    }

    //Pass-thru methods to an existing map implementation.
    public int size() { return cache.size(); }
    public void clear() { cache.clear(); }
    public boolean isEmpty() { return cache.isEmpty(); }
    public boolean containsKey(Object key) { return cache.containsKey(key); }
    public boolean containsValue(Object value) { return cache.containsValue(value); }
    public Collection values() { return cache.values(); }
    public void putAll(Map t) { cache.putAll(t); }
    public Set entrySet() { return cache.entrySet(); }
    public Set keySet() { return cache.keySet(); }
    public Object get(Object key) { return cache.get(key); }
    public Object remove(Object key) { return cache.remove(key); }
    public Object put(Object key, Object value) { return cache.put(key, value); }
}
