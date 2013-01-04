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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 19160 $
|       $Date: 2009-05-05 18:45:21 -0300 (mar, 05 may 2009) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.base.directory.search;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.AbstractSequentialList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Provides a convenient implementation of <code>ISearchResults</code>.
 * Subclasses need only implement <code>listIterator</code> and <code>size</code>.
 * It is implmented as a "read-only sequential access" list, meaning that
 * random access methods such as <code>get(int)</code> are implemented
 * on top of the <code>iterator</code> methods.
 */
public abstract class AbstractSearchResults 
        extends AbstractSequentialList 
        implements ISearchResults,Serializable {
    
    /**
     * Returns the _current_ size of the search results.
     * This may NOT actually represent the total of matching results, if not
     * all results have been read.
     * @return the current size
     */
    public abstract int size();
    
    /**
     * Returns a list iterator over the Search Results.
     * Each element is of type <code>ISearchResult</code>.
     * It is an unmodifiable ListIterator: <code>add</code>, <code>remove</code>
     * and <code>set</code> will throw <code>UnsupportedOperationException</code>s.
     * <p>
     * <b>Note:</b> This always returns a FULL iterator, never a page iterator.
     * </p>
     * @throws IndexOutOfBoundsException if the specified index is out of
     * range (<tt>index &lt; 0 || index &gt;= size()</tt>).
     */
    public abstract java.util.ListIterator listIterator(int index);
    
    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @throws SQLException 
     * @see net.project.persistence.IXMLPersistence#getXMLBody
     * @see net.project.persistence.IXMLPersistence#XML_VERSION
     */
    public String getXML() throws SQLException {
        return net.project.persistence.IXMLPersistence.XML_VERSION +
                getXMLBody();
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     * A subset of XML will be returned if paging is on.
     * @return XML representation of this object
     * @throws SQLException 
     * @see net.project.persistence.IXMLPersistence#getXML
     * @see #setPagingOn
     */
    public String getXMLBody() throws SQLException {
        StringBuffer xml = new StringBuffer();
        xml.append("<SearchResults>");

        for (Iterator it = iterator(); it.hasNext(); ) {
            ISearchResult nextResult = (ISearchResult) it.next();
            xml.append(nextResult.getXMLBody());
        }

        xml.append("</SearchResults>");
        return xml.toString();
    }

    /**
     * Returns a list containing <code>ISearchResult</code> objects
     * with the specified IDs.
     * The size of the returned list will only be the same as the length of
     * the array if <code>ISearchResult</code> objects were found
     * for each of the IDs.
     * <p>
     * <b>Note:</b> This will look for matching results by iterating
     * over all results; thus, if an ID is not found in these results,
     * the effect will have been to iterate over every result.
     * Subclasses may choose to provide a more efficient implementation.
     * </p>
     * @param resultIDs the ID values for the <code>ISearchResult</code>s
     * to return
     * @return the <code>ISearchResult</code>s with matching IDs
     */
    public java.util.List getForIDs(String[] resultIDs) {

        List resultIDList = java.util.Arrays.asList(resultIDs);
        List returnList = new ArrayList();

        // Iterate over all search results finding results with
        // matching IDs
        // Terminates at end of results or when we've found results
        // for all the IDs
        for (Iterator it = iterator(); it.hasNext() && returnList.size() < resultIDs.length; ) {
            ISearchResult nextResult = (ISearchResult) it.next();

            if (resultIDList.contains(nextResult.getID())) {
                returnList.add(nextResult);
            }

        }

        return returnList;
    }


    /**
     * Returns the iterator over the search results.
     * If paging is set on, returns a page iterator for the current page.
     * @return a pageIterator or full iterator
     * @see #setPagingOn
     */
    public Iterator iterator() {
        return (this.isPagingOn ? pageIterator() : super.iterator());
    }

    //
    // Implement IPageable
    //

    boolean isPagingOn = false;
    int pageStart = 0;
    int pageSize = 0;

    /**
     * Specifies that paging is on or off.
     * When paging is off the implementing class should behave identically
     * to the same class not implementing paging.
     * <p>
     * <b>Note:</b> Changes the behavior of <code>iterator()</code>
     * </p>
     * @param isPagingOn true if paging is on; false if paging is off
     * @see #iterator
     * @see #setPageRange
     */
    public void setPagingOn(boolean isPagingOn) {
        this.isPagingOn = isPagingOn;
    }

    /**
     * Specifies the range of results to operate over when paging is on.
     * @param start the first result number
     * @param pageSize the size of a page
     */
    public void setPageRange(int start, int pageSize) {
        this.pageStart = start;
        this.pageSize = pageSize;
    }

    /**
     * Returns an iterator over the objects supported by the IPageable
     * implmentor.
     * @return the Iterator for a single page of results
     */
    public java.util.Iterator pageIterator() {
        // The PageListIterator is made from the search result's listIterator
        // beginning at the correct starting position
        return new net.project.gui.pager.PageHelper.PageListIterator(listIterator(this.pageStart), this.pageStart, this.pageSize);
    }

    /**
     * Indicates whether there is a search result at the specified index.
     * Subclasses should override this method to provide an optimal solution.
     */
    public abstract boolean hasElement(int index);

    //
    // Inner classes
    //

    /**
     * Provides an iterator over the search results.
     * Each element is an <code>ISearchResult</code>.
     * This is a convenience class that implements some methods on
     * behalf of a subclass.
     * Subclasses need only implement <code>hasPrevious</code>, <code>previous</code>, <code>previousIndex</code>,
     * <code>hasNext</code>, <code>next</code>, <code>nextIndex</code>.
     * All other methods throw <code>java.util.UnsupportedOperationException</code>s.
     */
    protected static abstract class ListItr implements java.util.ListIterator {
        
        protected ListItr() {
            // Do nothing
        }

        public void set(Object obj) {
            throw new UnsupportedOperationException();
        }

        public void add(Object obj) {
            throw new UnsupportedOperationException();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

}
