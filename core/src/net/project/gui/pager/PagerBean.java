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
 * Provides methods for managing paging.
 */
public class PagerBean {

    /**
     * The default parameter name for indicating the starting number of
     * results to display, currently <code>page_start</code>.
     */
    public static final String DEFAULT_PAGE_START_PARAMETER_NAME = "page_start";

    /**
     * The IPageable object to control.
     */
    private IPageable pageable = null;

    /**
     * The maximum number of items on a page.
     */
    private int pageSize = 0;

    /**
     * The current starting position.
     */
    private int pageStart = 0;

    /**
     * The parameter name that specifies the page start position.
     */
    private String pageStartParameterName = DEFAULT_PAGE_START_PARAMETER_NAME;

    /**
     * Creates an empty PagerBean.
     */
    public PagerBean() {
        // Do nothing
    }

    /**
     * Specifies the IPageable object that this PagerBean controls.
     * @param pageable the IPageable object
     */
    public void setPageable(IPageable pageable) {
        this.pageable = pageable;
    }

    /**
     * Sets the page size; this is the maximum number of items to display
     * on a page.
     * @param pageSize the pageSize
     * @see #getPageSize
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * Returns the page size.
     * @return the page size
     * @see #setPageSize
     */
    public int getPageSize() {
        return this.pageSize;
    }

    /**
     * Sets the starting element number for the current page.
     * @param pageStart the starting element number
     */
    private void setPageStart(int pageStart) {
        this.pageStart = pageStart;
    }


    /**
     * Returns the starting element number for the current page.
     * @return the element number
     */
    public int getPageStart() {
        return this.pageStart;
    }

    /**
     * Sets the parameter name that specifies the page start position.
     * @param name the parameter name; this parameter should be available in
     * the request
     * @see #getPageStartParameterName
     */
    public void setPageStartParameterName(String name) {
        this.pageStartParameterName = name;
    }

    /**
     * Returns the current parameter name.
     * @return the parameter name
     * @see #setPageStartParameterName
     */
    public  String getPageStartParameterName() {
        return this.pageStartParameterName;
    }

    /**
     * Initializes this page bean from the request.
     * Grabs the page start parameter value; defaults to zero if not found
     * @param request the request from which to get the page start parameter
     */
    public void initialize(javax.servlet.ServletRequest request) {
        
        String pageStartValue = request.getParameter(this.pageStartParameterName);
        
        // Use parameter value if present
        // Otherwise default to zero
        if (pageStartValue != null && pageStartValue.trim().length() > 0) {
            setPageStart(Integer.valueOf(pageStartValue).intValue());
        
        } else {
            setPageStart(0);
        
        }
    }

    /**
     * Returns the current page number; starts at 0.
     * @return the page number
     */
    public int getCurrentPage() {
        return (this.pageStart / this.pageSize);
    }

    /**
     * Returns the starting element number for the specified page number.
     * Does not check to see if pageNumber if valid for this PagerBean.
     * <p>
     * For example, if pageSize is 10 and pageNumber is 2 (that is, the third page)
     * then this returns 20.
     * </p>
     * @param pageNumber the number of page to get starting element number
     * @return the starting element for that page
     */
    public int getPageStartForPage(int pageNumber) {
        return (this.pageSize * pageNumber);
    }


    /**
     * Turns paging on; <code>IPageable</code> is passed the page range.
     */
    public void setPagingOn() {
        this.pageable.setPagingOn(true);
        this.pageable.setPageRange(this.pageStart, this.pageSize);
    }

    /**
     * Turns paging off in the <code>IPageable</code>.
     */
    public void setPagingOff() {
        this.pageable.setPagingOn(false);
    }

    /**
     * Indicates whether there is a previous page available.
     * @return true if there is a previous page; false otherwise
     */
    public boolean hasPreviousPage() {
        return (getCurrentPage() > 0);
    }

    /**
     * Returns the previous page number if one is available.
     * @return the previous page number, or -1 if there is no previous page.
     */
    public int getPreviousPageNumber() {
        return (getCurrentPage() - 1);
    }

    /**
     * Indicates whether there is a next page available.
     * <p>
     * <b>Note:</b> The <code>IPageable</code> is asked if it has an element
     * at the starting position for the next page
     * </p>
     * @return true if there is a next page; false otherwise
     */
    public boolean hasNextPage() {
        return hasPage(getNextPageNumber());
    }

    /**
     * Returns the next page number.
     * @return the next page number or the total number of pages if there
     * is no next page
     */
    public int getNextPageNumber() {
        return getCurrentPage() + 1;
    }

    /**
     * Indicates whether there is a page with the specified page number.
     * @param pageNumber the page number to check for
     * @return true if there is at least one element on the specified page number;
     * false otherwise
     */
    public boolean hasPage(int pageNumber) {
        return this.pageable.hasElement(getPageStartForPage(pageNumber));
    }
}
