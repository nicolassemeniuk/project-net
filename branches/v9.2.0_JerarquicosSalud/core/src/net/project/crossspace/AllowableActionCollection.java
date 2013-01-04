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

 package net.project.crossspace;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class AllowableActionCollection implements Collection {
	// mutable share (value=1) is the default.
    public static final AllowableActionCollection DEFAULT = AllowableActionCollection.construct(1);    
    private final Set actionSet = new HashSet();

    public static AllowableActionCollection construct(int allowableActionID) {
        AllowableActionCollection aac = new AllowableActionCollection();

        for (Iterator it = AllowableAction.getAll().iterator(); it.hasNext();) {
            AllowableAction aa = (AllowableAction) it.next();
            if ((aa.getID() & allowableActionID) > 0) {
                aac.add(aa);
            }
        }

        return aac;
    }

    /**
     * Get the database id that is associated with the sum of all of these
     * AllowableActions.
     * @return
     */
    public int getDatabaseID() {
        int dbID = 0;
        for (Iterator it = actionSet.iterator(); it.hasNext();) {
            AllowableAction aa = (AllowableAction) it.next();
            dbID = dbID | aa.getID();
        }
        return dbID;
    }

    public Collection getAll() {
        return new ArrayList(actionSet);
    }

    public int size() { return actionSet.size(); }
    public void clear() { actionSet.clear(); }
    public boolean isEmpty() { return actionSet.isEmpty(); }
    public Object[] toArray() { return actionSet.toArray(); }
    public boolean add(Object o) { return actionSet.add(o); }
    public boolean contains(Object o) { return actionSet.contains(o); }
    public boolean remove(Object o) { return actionSet.remove(o); }
    public String toString() { return actionSet.toString(); }
    public boolean addAll(Collection c) { return actionSet.addAll(c); }
    public boolean containsAll(Collection c) { return actionSet.containsAll(c); }
    public boolean removeAll(Collection c) { return actionSet.removeAll(c); }
    public boolean retainAll(Collection c) { return actionSet.retainAll(c); }
    public Iterator iterator() { return actionSet.iterator(); }
    public Object[] toArray(Object[] a) { return actionSet.toArray(a); }
}
