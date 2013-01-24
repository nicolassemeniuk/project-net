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
+----------------------------------------------------------------------*/
package net.project.gui.pager;

/**
 * Allows objects to indicate that they support the ability to render
 * themselves in a paged-list fashion.
 * That is, they can display a subset of the objects that they maintain.
 * <p>
 * The most common scenario is for a <code>Collection</code> object to
 * implement this interface and define the <code>iterator()</code> method
 * to return <code>pageIterator()</code> if paging is on.
 * </p>
 * <p>
 * It is up to the implementing class to ensure that any methods that
 * produce a collection of results (regardless of the actual object type:
 * may be Collection, array, String, XMLDocument etc.) take into consideration
 * that paging may be on; given the above scenario, this will automatically
 * be handled if <code>iterator()</code> is always used.
 * </p>
 */
public interface IPageable {

    /**
     * Specifies that paging is on or off.
     * When paging is off the implementing class should behave identically
     * to the same class not implementing this interface.
     * <p>
     * <b>Note:</b> This method will be called to turn paging on PRIOR to
     * actually setting page range.  Any previously passed page range becomes
     * invalid as soon as paging is turned OFF.
     * </p>
     * @param isPagingOn true if paging is on; false if paging is off
     * @see #setPageRange
     */
    public void setPagingOn(boolean isPagingOn);

    /**
     * Specifies the range of results to operate over when paging is on.
     * @param start the first result number
     * @param pageSize the maximum number of results needed for a
     * page
     * @see #setPagingOn
     */
    public void setPageRange(int start, int pageSize);

    /**
     * Returns an iterator over the objects supported by the IPageable
     * implementor.
     * The number of items returned by the pageIterator must be less than
     * or equal to the maximumResultCount.  If less, this means that the
     * object provides no more items and that further calls to pageIterator
     * would return no results.
     * @return the Iterator for a single page of results
     * @see PageHelper#PageIterator
     * @see PageHelper#PageListIterator
     */
    public java.util.Iterator pageIterator();

    /**
     * Indicates whether this IPageable has an element at the specified index.
     * This may be used for determining whether there is a next page, or
     * for determining index links.
     * <p>
     * <b>Note:</b> The index number specified here may be much greater than
     * the current position of the page iterator; implementors should take
     * care to implement this method optimally.
     * </p>
     * @param index the index number to check for
     */
    public boolean hasElement(int index);

}
