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

import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Provides helper classes for objects implementing IPageable.
 */
public class PageHelper {

    /**
     * Provides an iterator over a single page of results.
     * Note: This should only be used when a <code>ListIterator</code>
     * is not available.
     */
    public static class PageIterator implements Iterator {

        protected Iterator iterator = null;
        protected int start = 0;
        protected int pageSize = 0;
        protected int cursor = 0;

        /**
         * Creates a PageIterator based on the page range information.
         * Must only be called from another constructor that populates
         * the iterator.
         * @param start the starting position
         * @param pageSize the maximum number of results to return
         */
        protected PageIterator(int start, int pageSize) {
            this.start = start;
            this.pageSize = pageSize;
        }

        /**
         * Creates a new PageIterator based on the specified Iterator and
         * page range information.
         * @param iterator the iterator to base this iterator on
         * @param start the starting position
         * @param pageSize the maximum number of results to return
         */
        public PageIterator(Iterator iterator, int start, int pageSize) {
            this(start, pageSize);
            this.iterator = iterator;
            // Now consume "start" number of elements
            // The next element will be at position "start"
            // If we run out of elements in the iterator,
            // a subsequent call to hasNext() will return false as expected
            while (this.cursor < start && this.iterator.hasNext()) {
                this.iterator.next();
                this.cursor++;
            }
        }

        /**
         * Indicates whether we're at the start of the page.
         * @return true if at the start of the page; false otherwise
         */
        protected boolean isStartOfPage() {
            return (this.cursor <= this.start);
        }

        /**
         * Indicates whether we're at the end of the page.
         * Note: When this method returns "false", it does not mean that
         * there are any results to return; the underlying iterator
         * might have run out
         * @return true if the end of the page has been reached.; false
         * if fewer than a full page of results has been returned
         */
        protected boolean isEndOfPage() {
            return ((this.cursor - this.start) >= this.pageSize);
        }

        /**
         * Indicates whether this page Iterator has a next element.
         * @return true if there is a another element in this page; false
         * otherwise
         */
        public boolean hasNext() {
            return (!isEndOfPage() && this.iterator.hasNext());
        }

        /**
         * Returns the next element in this page.
         * @return the next element
         * @throws NoSuchElementException if there are no more elements in the
         * page
         */
        public Object next() {
            Object obj = null;

            if (isEndOfPage()) {
                // There are no more elements (as far as this page is concerned)
                throw new NoSuchElementException();
            
            } else {
                // Get the next element and increment the cursor count
                // This will also throw a NoSuchElementException if the
                // underlying iterator has been exhausted
                obj = this.iterator.next();
                this.cursor++;
            }
            
            return obj;
        }

        public void remove() {
            this.iterator.remove();
        }

    }




    /**
     * Provides a ListIterator over a single page of results.
     * This is preferable to PageIterator since the ListIterator can already be set to the
     * correct starting position; that is, no elements need be consumed
     * to reach the correct starting position.
     */
    public static class PageListIterator extends PageIterator implements ListIterator {
        
        /**
         * Creates a new PageIterator based on the specified ListIterator and
         * page range information.
         * @param iterator the ListIterator already at the correct start position
         * @param start the starting position. This must be the same as the position
         * that the iterator starts at; if it is not, then unexpected elements
         * will be returned
         * @param pageSize the maximum number of results to return
         */
        public PageListIterator(ListIterator iterator, int start, int pageSize) {
            super(start, pageSize);
            this.iterator = iterator;
            // Since the iterator is already at the correct position, no need
            // to consume elements from it
            // However, we do need to initialize the cursor to the starting position
            this.cursor = this.start;
        }

        /**
         * Return the index of the next item that would be returned.
         * @return the next index; this might be beyond the end of the
         * the page if the end of the page has been reached;  that is, 
         * at the end of the page: <code>(nextIndex - start = page size)</code>
         */
        public int nextIndex() {
            // Implementation dilemma:
            // The contract of nextIndex says to return the nextIndex or
            // the list size if the end has been reached.  In the case of
            // a regular ListItertor, the list size is equal to one greater
            // than the index position of the last element.
            //
            // Therefore, I'm returning the next element index OR one greater
            // than the last element if the end has been reached
            // This has the same numerical effect, but different semantics
            return this.cursor;
        }

        /**
         * Returns the index of the previous item that would be returned with
         * a call to <code>previous</code>.
         * @return the next index, or -1 if there is no previousIndex (that is,
         * iterator is at start of page)
         */
        public int previousIndex() {
            // Return -1 if cursor at start position, or else the previous index
            return (isStartOfPage() ? -1 : (this.cursor -1));
        }

        /**
         * Indicates whether this page iterator has a previous element.
         * @return true if there is a previous element; false if at the
         * start of the page
         */
        public boolean hasPrevious() {
            // It is somewhat redundant to call this.iterator.hasPrevious()
            // since it is generally impossible to be beyond the start of
            // the page, but not have any previous values
            return (!isStartOfPage() && ((ListIterator) this.iterator).hasPrevious());
        }

        /**
         * Returns the previous element.
         * @return the previous element
         * @throws NoSuchElementException if there is no previous element
         */
        public Object previous() {
            Object obj = null;

            if (!hasPrevious()) {
                // There are no previous elements (as far as this page is concerned)
                throw new NoSuchElementException();
            
            } else {
                // Get the next element and increment the cursor count
                // This will also throw a NoSuchElementException if the
                // underlying iterator has been exhausted
                obj = ((ListIterator) this.iterator).previous();
                this.cursor--;
            }
            
            return obj;
        }

        public void add(Object o) {
            ((ListIterator) this.iterator).add(o);
        }
        
        public void set(Object o) {
            ((ListIterator) this.iterator).set(o);
        }

    }

}
