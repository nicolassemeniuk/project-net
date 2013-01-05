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

import java.util.Comparator;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.persistence.PersistenceExceptionPropagator;
import net.project.util.VisitException;

/**
 * Class representing a "group" of results returned from a finder.  The grouping
 * classes allow a report designer (or other finder user) to display results
 * with a similar group value to be shown with a different header.
 *
 * It is worthwhile to note that the results of a Finder are not automatically
 * applied to result set.  To make this happen, create a new instance of a
 * {@link net.project.base.finder.GroupingIterator} class.
 *
 * @see net.project.base.finder.GroupingIterator
 * @see net.project.base.finder.EmptyFinderGrouping
 * @see net.project.base.finder.FinderGroupingList
 * @author Matthew Flower
 * @since Version 7.4
 */
public abstract class FinderGrouping implements Comparator {
    /**
     * A unique identifier for this finder grouping.  This can be any value,
     * but it is usual an integer stored in a string value, such as "10", "20", etc.
     */
    private String id;
    /**
     * A <code>String</code> value which contains a property (aka token) which
     * can be used to look up a human readable name for this finder grouping.
     * This is most often used to display a list of grouping options to the user
     * in an HTML form.
     */
    private String nameToken;
    /**
     * Indicates whether the user has "selected" this grouping so that it should
     * be applied tot he results of a finder "find-by" method.
     */
    private boolean selected = false;

    /**
     * Public constructor.
     *
     * @param id a <code>String</code> value that uniquely identifies this finder
     * grouping.  This is especially important when a grouping is going to be
     * rendered to the screen or stored in a list.
     * @param nameToken a <code>String</code> value containing a token that points
     * to a human-readable version of the grouping.
     * @param isDefaultGrouping a <code>boolean</code> value that indicates if
     * this grouping should be selected as the default when it is in a list of
     * groupings.
     */
    public FinderGrouping(String id, String nameToken, boolean isDefaultGrouping) {
        super();
        this.id = id;
        this.nameToken = nameToken;
        this.selected = isDefaultGrouping;
    }

    /**
     * Gets a unique identifier for this finder grouping.  This can be any value,
     * but it is usual an integer stored in a string value, such as "10", "20",
     * etc.
     *
     * @return a unique identifier for this finder grouping.
     */
    public String getID() {
        return id;
    }

    /**
     * Indicates whether the user has "selected" this grouping so that it should
     * be applied to the results of a finder "find-by" method.
     *
     * @return a <code>boolean</code> value which indicates whether this grouping
     * should be applied to the results of a finder "find-by" method.
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Indicate whether the user has "selected" this grouping so that it should
     * be applied to the results of a finder "find-by" method.
     *
     * @param selected a <code>boolean</code> value which indicates whether this
     * grouping should be applied to the results of a finder "find-by" method.
     */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * Get the human-readable name for this finder grouping class.  This is
     * looked up based on the token that was passed to the constructor of this
     * class.
     *
     * @return a <code>String</code> value which contains the human-readable
     * name of this grouping.
     */
    public String getName() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * This method indicates whether the group has changed between the <code>
     * currentObject</code> parameter and the <code>lastObject</code> parameter.
     *
     * By default, this method will indicate a group change between a null object
     * and an object with any value.  This allows us to signal that there is a
     * change when a {@link net.project.base.finder.GroupingIterator} object
     * returns the first item in the list.
     *
     * @param currentObject a <code>Object</code> value that we will use to test
     * the grouping of the current selected object in a
     * {@link net.project.base.finder.GroupingIterator}.
     * @param lastObject a <code>Object</code> value that we will use to test
     * the grouping of the previously selected object in a
     * {@link net.project.base.finder.GroupingIterator}.
     * @return a <code>boolean</code> value indicating whether or not the group
     * has changed between the two objects passed into this method.
     * @throws PersistenceException if there is a difficulty looking up the value
     * of the objects in the database.  Not all grouping values can throw a
     * <code>PersistenceException</code>, but it has been included to prevent a
     * lot of spurrious catch statements.
     */
    protected boolean hasGroupChanged(Object currentObject, Object lastObject) throws PersistenceException {
        Object currentValue = getGroupingValue(currentObject);
        Object lastValue = getGroupingValue(lastObject);
        boolean toReturn = false;

        if (lastObject == null) {
            toReturn = true;
        } else if (lastValue == null) {
            toReturn = (currentValue != null);
        } else if (lastValue.equals(currentValue)) {
            toReturn = false;
        } else {
            toReturn = true;
        }

        return toReturn;
    }

    /**
     * Get the "value" associated with this grouping.  For example, if you were
     * grouping based on assignees, a group value for a certain task might be
     * "John Smith" whereas the grouping value for another task might be
     * "David Jones".  These values are used to indicate when a {@link net.project.base.finder.GroupingIterator}
     * has crossed the boundary of one group and has gone into another.
     *
     * @param currentObject a <code>Object</code> value from which the
     * implementer will determine the grouping value.
     * @return a <code>String</code> representation of the current value of the
     * object.
     * @throws PersistenceException if there is a database error thrown while
     * fetching the Grouping Value.  Not all implementation of getGroupingValue
     * will require this exception.
     */
    public abstract Object getGroupingValue(Object currentObject) throws PersistenceException;
    /**
     * Get the human readable name of this grouping that can be used to signal
     * the user what the current grouping is.  Often, this value can be identical
     * to get grouping value.  This method differs in that it can also be used
     * to add additional text to the group for display purposes.
     *
     * @param currentObject a <code>Object</code> value from which the
     * implementer will determine the grouping value.
     * @return a <code>String</code> value used to get the human-readable form
     * of the current group.
     * @throws PersistenceException if there is a database error thrown while
     * fetching the Grouping Value.  Not all implementation of getGroupingName
     * will require this exception.
     */
    public abstract String getGroupName(Object currentObject) throws PersistenceException;

    /**
     * This method returns a description of the group, including any special
     * parameters passed to it through the interface.  This method was designed
     * for displaying search parameters on a report.
     *
     * @return a <code>String</code> describing this group.
     */
    public String getGroupDescription() {
        return getName();
    }

    /**
     * Accepts an <code>IFinderIngredientVisitor</code> and visits this
     * FinderGrouping.
     *
     * @param visitor an <code>IFinderIngredientVisitor</code>
     */
    public void accept(IFinderIngredientVisitor visitor) throws VisitException {
        visitor.visitFinderGrouping(this);
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer,
     * zero, or a positive integer as the first argument is less than, equal
     * to, or greater than the second.<p>
     *
     * The implementor must ensure that <tt>sgn(compare(x, y)) ==
     * -sgn(compare(y, x))</tt> for all <tt>x</tt> and <tt>y</tt>.  (This
     * implies that <tt>compare(x, y)</tt> must throw an exception if and only
     * if <tt>compare(y, x)</tt> throws an exception.)<p>
     *
     * The implementor must also ensure that the relation is transitive:
     * <tt>((compare(x, y)&gt;0) &amp;&amp; (compare(y, z)&gt;0))</tt> implies
     * <tt>compare(x, z)&gt;0</tt>.<p>
     *
     * Finally, the implementer must ensure that <tt>compare(x, y)==0</tt>
     * implies that <tt>sgn(compare(x, z))==sgn(compare(y, z))</tt> for all
     * <tt>z</tt>.<p>
     *
     * It is generally the case, but <i>not</i> strictly required that
     * <tt>(compare(x, y)==0) == (x.equals(y))</tt>.  Generally speaking,
     * any comparator that violates this condition should clearly indicate
     * this fact.  The recommended language is "Note: this comparator
     * imposes orderings that are inconsistent with equals."
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the
     * 	       first argument is less than, equal to, or greater than the
     *	       second.
     * @throws ClassCastException if the arguments' types prevent them from
     * 	       being compared by this Comparator.
     */
    public int compare(Object o1, Object o2) {
        try {
            Object o1Value = getGroupingValue(o1);
            Object o2Value = getGroupingValue(o2);

            if ((o1Value == null) && (o2Value == null)) {
                //If both values are null, they are equal
                return 0;
            } else if (o1Value == null) {
                //If only the first value is null, it is less than the second value
                return -1;
            } else if (o2Value == null) {
                //If the second vault is null, the it is less than the first value
                return 1;
            } else if (o1 instanceof Comparable) {
                return ((Comparable)o1Value).compareTo(o2Value);
            } else {
                return o1Value.toString().compareTo(o2Value.toString());
            }
        } catch (PersistenceException pe) {
            throw new PersistenceExceptionPropagator(pe);
        }
    }
}
