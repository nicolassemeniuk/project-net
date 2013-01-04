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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.discussion;

import java.io.Serializable;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;

/**
 * Provides setter & getter methods for presentation items related to the
 * DiscussionGroup class
 *
 * @author AdamKlatzkin
 * @since 01/00
 */
public class DiscussionGroupBean extends DiscussionGroup implements Serializable {
    /**
     * @deprecated as of Version 7.4.  Please use {@link #setID} instead.
     * @param id discussion group id
     */
    public void setId(String id) {
        setID(id);
    }

    /**
     * @deprecated as of Version 7.4.  Please use {@link #getID} instead.
     * @return String the discussion group id
     */
    public String getId() {
        return getID();
    }

    /**
     * @return String get the post id of the currently active post within the
     * discussion group
     */
    public String getCurrentPostID() {
        return m_PostListMaintainer.getCurrentPostID();
    }

    /**
     * Generates an HTML representation of the discussion group's post list
     */
    public String getPostListAsHTML() {
        return m_PostListMaintainer.toHTML();
    }

    /**
     * @return String[] array of strings which are the names for the view their
     * index represents.
     */
    public String[] getViewList() {
        // This method used to simply return the VIEW_STRINGS array (i.e., "return VIEW_STRINGS;").
        // Now that the array elements are token names, this method loops through the array, gets the
        // corresponding token value with the PropertyProvider.get() method, and assigns each value
        // to a place in the new array "view_string_values".  -- Brian Janko 11/11/2002
        String[] view_string_values = new String[VIEW_STRINGS.length];
        for (int i = 0; i < VIEW_STRINGS.length; i++) {
            view_string_values[i] = PropertyProvider.get(VIEW_STRINGS[i]);
        }
        return view_string_values;
    }

    /**
     * @return int current active view one of DiscussionGroup.VIEW_*
     */
    public int getView() {
        return m_view;
    }

    /**
     * @param view the current view.  Should be the string {"0", "1", ...} where
     * the number is a member of DiscussionGroup.VIEW_*
     */
    public void setViewString(String view) throws PersistenceException {
        m_view = Integer.parseInt(view);
        load();
    }

    /**
     * @return String the current sort type one of DiscussionGroup.SORT_*
     */
    public String getSort() {
        return Integer.toString(m_sort);
    }

    /**
     * @param sort the sort type to set the current mode one of
     * DiscussionGroup.SORT_*
     */
    public void setSort(String sort) throws PersistenceException {
        int iSort = Integer.parseInt(sort);
        if (m_sort == iSort) {
            // toggle the order
            // 0 --> 1, 1 --> 0
            m_order = 1 - m_order;
        } else {
            m_sort = iSort;
        }

        if (!isThreaded()) {
            load();
        }
    }
}
