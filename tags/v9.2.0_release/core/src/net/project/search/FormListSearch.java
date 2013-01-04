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
+--------------------------------------------------------------------------------------*/
package net.project.search;

import net.project.base.property.PropertyProvider;

/**
 * This class is used for searching FormList Objects.  It provides the forms
 * for simple and advanced searching as well as a properly formatted XML string
 * of the results.
 */
public class FormListSearch extends GenericSearch {
    /**
     * Creates a new FormListSearch.
     */
    public FormListSearch() {
        super();
    }

    /**
     Get the display name of the this search Type.
     */
    public String getDisplayName() {
        return PropertyProvider.get("prm.global.search.formlist.results.channel.title"); // Form List
    }

    /**
     * Generates a properly formatted XML string. The XML string will contain
     * the results from the search as well as the column headers for the two
     * fields used to describe each of the result objects.
     *
     * The XML should be in the following format:<pre>
     * <channel>
     *   <table_def>
     *     <col>COLUMN1 NAME</col>
     *     <col>COLUMN2 NAME</col>
     *        .
     *        .
     *   </table_def>
     *   <content>                        --YOU MUST HAVE THE SAME NUMBER OF DATA
     *      <row>                          -- DATA TAGS IN EACH ROW AS COLUMNS
     *       <data>TEXT DATA</data>
     *       <data_href>                  --USE <DATA> AND <DATA_HREF> INTERCHANGABLY
     *         <label>TEXT DATA</label>
     *         <href>URL</href>
     *       </data_href>
     *        .
     *        .
     *     </row>
     *      .
     *      .
     *    </content>
     *  </channel></pre>
     * @param start the starting row (inclusive, starts at 1)
     * @param end the ending row (inclusive, ends at maximum size of results)
     * @return XML Formatted results of the search
     */
    public String getXMLResults(int start, int end) {
        return "";
    }

    /**
     * Returns the default search type, used to determine which should be
     * presented to the user by default.
     * @return the search type
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public int getDefaultSearchType() {
        return BROWSE_SEARCH;
    }

    /**
     * Indicates whether a particular search type is supported, used to determine
     * which search type options are presented to the user.
     * @param searchType the search type constant
     * @return true if the search type is supported, false otherwise
     * @see #SIMPLE_SEARCH
     * @see #ADVANCED_SEARCH
     * @see #BROWSE_SEARCH
     */
    public boolean isSearchTypeSupported(int searchType) {
        if (searchType == BROWSE_SEARCH) {
            return true;
        }
        return false;
    }

}

