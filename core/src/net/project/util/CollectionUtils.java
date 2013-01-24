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

 package net.project.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Provides static utility methods that do work on collections.
 *
 * @author Matthew Flower
 * @since Version 7.7.0
 */
public class CollectionUtils {
    /**
     * A class that someone calling {@link CollectionUtils#listToMap} calls in
     * order to specify what the key is in an object. 
     */
    public static abstract class MapKeyLocator {
        /**
         * Used to find the key of an object in the list in the listToMap method.
         *
         * @param listObject a <code>Object</code> which was found in the list
         * for which we want to find the appropriate key.
         * @return an <code>Object</code> which we are going to use as the key
         * in the Map.
         */
        public abstract Object getKey(Object listObject);
    }

    /**
     * This method converts a list of objects into a map of objects using
     * @param list
     * @return
     */
    public static Map listToMap(List list, MapKeyLocator locator) {
        HashMap map = new HashMap();

        for (Iterator it = list.iterator(); it.hasNext();) {
            Object value = (Object) it.next();
            Object key = locator.getKey(value);

            map.put(key, value);
        }

        return map;
    }


    public static Collection createCollection(Object collectionItem) {
        //TODO: Once we have varargs in Java 1.5, we can replace these methods.  (Think varags)
        LinkedList list = new LinkedList();
        list.add(collectionItem);

        return list;
    }

    public static Collection createCollection(Object collectionItem1, Object collectionItem2) {
        //TODO: Once we have varargs in Java 1.5, we can replace these methods.  (Think varags)
        LinkedList list = new LinkedList();
        list.add(collectionItem1);
        list.add(collectionItem2);

        return list;
    }

    public static Set createSet(Object setItem1) {
        HashSet set = new HashSet();
        set.add(setItem1);
        return set;
    }

    public static Set createSet(Object setItem1, Object setItem2) {
        HashSet set = new HashSet();
        set.add(setItem1);
        set.add(setItem2);
        return set;
    }

    /**
     * This method iterates through a collection of objects it presumes to be
     * regular expressions.  It attempts to match each against the provided string
     * to see if there is a match.
     *
     * @param collection a <code>java.util.Collection</code> of <code>String</code>
     * objects presumed to be regular expressions.
     * @param toTest a <code>String</code> which we are going to try to run regular
     * expressions against to see if we can find a match.
     * @return a <code>boolean</code> indicating if any of the strings in the
     * collection match.
     */
    public static boolean stringCollectionMatches(Collection collection, String toTest) {
        boolean match = false;

        for (Iterator it = collection.iterator(); it.hasNext();) {
            String s = (String) it.next();
            match = match || toTest.matches(s);

            if (match) {
                break;
            }
        }

        return match;
    }
}
