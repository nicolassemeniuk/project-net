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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.base.finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import net.project.persistence.PersistenceException;
import net.project.persistence.PersistenceExceptionPropagator;

/**
 * This class extends Iterator to expose methods to determine if the result set
 * has crossed a group boundary.  If so, it also provides the name of the
 * current group.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class GroupingIterator implements Iterator {
    /** The data that this grouping iterator is iterating. */
    private List dataToIterate;
    /**
     * An iterator that is doing the actual work of iterating.  We are just a
     * wrapper class to provide additional functionality.
     */
    private Iterator dataIterator;
    /**
     * The previous object that we iterated.  This is set to null if we haven't
     * had a "previous object" yet.
     */
    private Object lastObject = null;
    /**
     * The current object that the iterator is pointing at.
     */
    private Object currentObject = null;
    /**
     * This list of all <code>FinderGrouping</code> objects that we are using
     * in this GroupingIterator.  These objects will let us know when we have
     * crossed a group boundary.
     */
    private ArrayList finderGroupings = new ArrayList();
    /**
     * This is the FinderGrouping which has signalled that a group change has
     * occurred.  This value is used when we have to get the group name -- this
     * object knows what that group name is.
     */
    private FinderGrouping signalledFinderGrouping = null;

    /**
     * Standard constructor which initializes private member variables.
     *
     * @param dataToIterate a <code>List</code> object which contains data that
     * this Iterate is going to iterate through.
     * @throws PersistenceException if an error occurs while trying to sort the
     * data.  We need to sort the data before we put it into groups -- otherwise
     * we would have multiple groups with the same name.
     */
    public GroupingIterator(List dataToIterate) throws PersistenceException {
        this.dataToIterate = dataToIterate;
        reset();
    }

    /**
     * Reset the iterator so that it points to the beginning of the data that
     * it was provided to iterate.
     *
     * @throws PersistenceException if an error occurs while trying to sort the
     * data.  We need to sort the data before we put it into groups -- otherwise
     * we would have multiple groups with the same name.
     */
    public void reset() throws PersistenceException {
        try {
            for (Iterator it = finderGroupings.iterator(); it.hasNext(); ) {
                Collections.sort(dataToIterate, (FinderGrouping)it.next());
            }

            dataIterator = dataToIterate.iterator();
        } catch (PersistenceExceptionPropagator pep) {
            throw pep.getOriginalException();
        }
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return an element
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return dataIterator.hasNext();
    }



    /**
     * Returns the next element in the iteration.
     *
     * @return the next element in the iteration.
     * @exception NoSuchElementException iteration has no more elements.
     */
    public Object next() {
        lastObject = currentObject;
        currentObject = dataIterator.next();

        return currentObject;
    }

    /**
     * Add a new finder grouping to this <code>GroupingIterator</code>.  If a
     * call to {@link net.project.base.finder.FinderGrouping#getGroupingValue}
     * differs on the current item versus the previous item, this will signal a
     * new group, which will cause the result of {@link #isGroupChanged} to be
     * <code>true</code>.
     *
     * Please note that although we can accept multiple grouping objects right
     * now, there is no way to differentiate which <code>FinderGrouping</code>
     * signalled that we have crossed the boundary.  Perhaps this feature will
     * be needed and implemented in the future.
     *
     * @param newFinderGrouping a <code>FinderGrouping</code> object that will
     * be used to determine if we have crossed a group boundary.
     */
    public void addFinderGrouping(FinderGrouping newFinderGrouping) {
        finderGroupings.add(newFinderGrouping);
    }


    /**
     * Add new finder groupings to this <code>GroupingIterator</code>.  If a
     * call to {@link net.project.base.finder.FinderGrouping#getGroupingValue}
     * differs on the current item versus the previous item, this will signal a
     * new group, which will cause the result of {@link #isGroupChanged} to be
     * <code>true</code>.
     *
     * Please note that although we can accept multiple grouping objects right
     * now, there is no way to differentiate which <code>FinderGrouping</code>
     * signalled that we have crossed the boundary.  Perhaps this feature will
     * be needed and implemented in the future.
     *
     * @param finderGroupingList a <code>FinderGroupingList</code> object which
     * contains one or more <code>FinderGrouping</code> items which we want to
     * add to the internal list of finder groupings.  These objects that will
     * be used to determine if we have crossed a group boundary.
     */
    public void addFinderGroupingList(FinderGroupingList finderGroupingList) {
        finderGroupings.addAll(finderGroupingList.getSelectedGroupings());
    }

    /**
     * Clear out any <code>FinderGrouping</code> objects that are being stored
     * internally.
     */
    public void clearFinderGroupingList() {
        finderGroupings.clear();
    }

    /**
     * The return value of this method indicates whether the most recent call to
     * the {@link #next} method return an object that crossed a group boundary.
     * That is, if the previous object was a member of group 1 and the new object
     * was a member of group 2, this method would return true.
     *
     * Group membership is determined by a {@link net.project.base.finder.FinderGrouping}
     * object which has been added to this iterator prior to calling <code>next()</code>.
     *
     * @return a <code>boolean</code> value which is true if last call to the
     * {@link #next} method caused this method to cross grouping boundaries.
     * @throws PersistenceException if there is a database exception trying to
     * determine the group value of an object.
     */
    public boolean isGroupChanged() throws PersistenceException {
        return checkGroupChanged(currentObject, lastObject);
    }

    /**
     * This method determines if the group value of an object has changed between
     * the current object and the previous object.
     *
     * @param currentObject an <code>Object</code> value which was returned the
     * last time the {@link #next} method was called.
     * @param lastObject an <code>Object</code> value which was returned the time
     * before last that {@link #next} was called.
     * @return a <code>boolean</code> value indicating whether the group values
     * of the two object parameters are the same.
     * @throws PersistenceException if there is a database exception while looking
     * up the database values of the two objects.
     */
    private boolean checkGroupChanged(Object currentObject, Object lastObject) throws PersistenceException {
        boolean toReturn = false;
        signalledFinderGrouping = null;

        for (Iterator it = finderGroupings.iterator(); it.hasNext(); ) {
            FinderGrouping fg = (FinderGrouping)it.next();
            if (fg.hasGroupChanged(currentObject, lastObject)) {
                signalledFinderGrouping = fg;
                toReturn = true;
            }
        }

        return toReturn;
    }

    /**
     * Get a human-readable name of the group that the previous call to {@link #next}
     * belongs to.
     *
     * @return a <code>String</code> value which contains a human-readable
     * version of the group name that the object returned by the last call to
     * {@link #next} belongs to.
     * @throws PersistenceException if there is a database exception thrown
     * while retrieving the group name.
     */
    public String getGroupName() throws PersistenceException {
        String toReturn = null;

        if (signalledFinderGrouping != null) {
            return signalledFinderGrouping.getGroupName(currentObject);
        }

        return toReturn;
    }

    /**
     *
     * Removes from the underlying collection the last element returned by the
     * iterator (optional operation).  This method can be called only once per
     * call to <tt>next</tt>.  The behavior of an iterator is unspecified if
     * the underlying collection is modified while the iteration is in
     * progress in any way other than by calling this method.
     *
     * @exception UnsupportedOperationException if the <tt>remove</tt>
     *		  operation is not supported by this Iterator.

     * @exception IllegalStateException if the <tt>next</tt> method has not
     *		  yet been called, or the <tt>remove</tt> method has already
     *		  been called after the last call to the <tt>next</tt>
     *		  method.
     */
    public void remove() {
        throw new IllegalStateException("Remove operation not supported");
    }
}
