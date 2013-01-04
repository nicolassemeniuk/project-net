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
package net.project.portfolio.view;

import java.util.ArrayList;
import java.util.List;

import net.project.base.finder.FinderIngredientsID;

/**
 * Provides the unique IDs of ingredients components of PersonalPortfolioFinderIngredients.
 * <p>
 * <b>An ID may <i>NEVER</i> be changed from release to release.</b>
 * The actual value of the ID is irrelevant.  It is used to match persisted
 * components with actual components in the FinderColumnList, FinderFilterList,
 * FinderSorterList, FinderGrouperList.
 * </p>
 */
class PersonalPortfolioIngredientsID extends FinderIngredientsID {

    private static final List ALL_ID_LIST = new ArrayList();

    //
    // Remember..
    // DO NOT CHANGE THE VALUE OF THE ID FROM RELEASE TO RELEASE
    // Otherwise stored values may not be loaded successfully
    //

    // Column IDs
    // These are used to uniquely identify columns as used by filters and
    // sorters
    static final FinderIngredientsID COLUMN_NAME = new PersonalPortfolioIngredientsID("column_1");
    static final FinderIngredientsID COLUMN_DESCRIPTION = new PersonalPortfolioIngredientsID("column_2");
    static final FinderIngredientsID COLUMN_PERCENT_COMPLETE = new PersonalPortfolioIngredientsID("column_3");
    static final FinderIngredientsID COLUMN_START_DATE = new PersonalPortfolioIngredientsID("column_4");
    static final FinderIngredientsID COLUMN_END_DATE = new PersonalPortfolioIngredientsID("column_5");
    static final FinderIngredientsID COLUMN_STATUS_CODE = new PersonalPortfolioIngredientsID("column_6");
    static final FinderIngredientsID COLUMN_COLOR_CODE = new PersonalPortfolioIngredientsID("column_7");
    static final FinderIngredientsID COLUMN_PROJECT_SPONSOR = new PersonalPortfolioIngredientsID("column_8");
    static final FinderIngredientsID COLUMN_IMPROVEMENT_CODE = new PersonalPortfolioIngredientsID("column_9");
    static final FinderIngredientsID COLUMN_FINANCIAL_STATUS_COLOR_CODE = new PersonalPortfolioIngredientsID("column_10");
    static final FinderIngredientsID COLUMN_FINANCIAL_STATUS_IMPROVEMENT = new PersonalPortfolioIngredientsID("column_11");
    static final FinderIngredientsID COLUMN_SCHEDULE_STATUS_COLOR_CODE = new PersonalPortfolioIngredientsID("column_12");
    static final FinderIngredientsID COLUMN_SCHEDULE_STATUS_IMPROVEMENT = new PersonalPortfolioIngredientsID("column_13");
    static final FinderIngredientsID COLUMN_RESOURCE_STATUS_COLOR_CODE = new PersonalPortfolioIngredientsID("column_14");
    static final FinderIngredientsID COLUMN_RESOURCE_STATUS_IMPROVEMENT = new PersonalPortfolioIngredientsID("column_15");
    static final FinderIngredientsID COLUMN_CURRENT_STATUS_DESCRIPTION = new PersonalPortfolioIngredientsID("column_16");
    static final FinderIngredientsID COLUMN_COST_CENTER = new PersonalPortfolioIngredientsID("column_17");
    static final FinderIngredientsID COLUMN_PRIORITY_CODE = new PersonalPortfolioIngredientsID("column_18");
    static final FinderIngredientsID COLUMN_RISK_RATING_CODE = new PersonalPortfolioIngredientsID("column_19");
    static final FinderIngredientsID COLUMN_PROJECT_ID = new PersonalPortfolioIngredientsID("column_20");
    static final FinderIngredientsID COLUMN_BUDGETED_TOTAL_COST_VALUE = new PersonalPortfolioIngredientsID("column_21");
    static final FinderIngredientsID COLUMN_BUDGETED_TOTAL_COST_CURRENCY = new PersonalPortfolioIngredientsID("column_22");
    static final FinderIngredientsID COLUMN_CURRENT_ESTIMATED_TOTAL_COST_VALUE = new PersonalPortfolioIngredientsID("column_23");
    static final FinderIngredientsID COLUMN_CURRENT_ESTIMATED_TOTAL_COST_CURRENCY = new PersonalPortfolioIngredientsID("column_24");
    static final FinderIngredientsID COLUMN_ACTUAL_COST_TO_DATE_VALUE = new PersonalPortfolioIngredientsID("column_25");
    static final FinderIngredientsID COLUMN_ACTUAL_COST_TO_DATE_CURRENCY = new PersonalPortfolioIngredientsID("column_26");
    static final FinderIngredientsID COLUMN_ESIMATED_ROI_VALUE = new PersonalPortfolioIngredientsID("column_27");
    static final FinderIngredientsID COLUMN_ESIMATED_ROI_CURRENCY = new PersonalPortfolioIngredientsID("column_28");
    static final FinderIngredientsID COLUMN_OWNING_BUSINESS = new PersonalPortfolioIngredientsID("column_29");
    static final FinderIngredientsID COLUMN_DEFAULT_CURRENCY_CODE = new PersonalPortfolioIngredientsID("column_30");
    static final FinderIngredientsID COLUMN_INCLUDES_EVERYONE = new PersonalPortfolioIngredientsID("column_31");
    static final FinderIngredientsID COLUMN_META_EXTERNAL_PROJECT_ID = new PersonalPortfolioIngredientsID("column_32");
    static final FinderIngredientsID COLUMN_META_PROJECT_MANAGER = new PersonalPortfolioIngredientsID("column_33");
    static final FinderIngredientsID COLUMN_META_PROGRAM_MANAGER = new PersonalPortfolioIngredientsID("column_34");
    static final FinderIngredientsID COLUMN_META_INITIATIVE = new PersonalPortfolioIngredientsID("column_35");
    static final FinderIngredientsID COLUMN_META_FUNCTIONAL_AREA = new PersonalPortfolioIngredientsID("column_36");
    static final FinderIngredientsID COLUMN_META_PROJECT_CHARTER = new PersonalPortfolioIngredientsID("column_37");
    static final FinderIngredientsID COLUMN_META_TYPE_OF_EXPENSE = new PersonalPortfolioIngredientsID("column_38");
    static final FinderIngredientsID COLUMN_SUBPROJECT_OF = new PersonalPortfolioIngredientsID("column_39");

    // Filter IDs
    // These are used to uniquely identify filters in the finder ingredients
    static final FinderIngredientsID FILTER_NAME = new PersonalPortfolioIngredientsID("filter_1");
    static final FinderIngredientsID FILTER_DESCRIPTION = new PersonalPortfolioIngredientsID("filter_2");
    static final FinderIngredientsID FILTER_PERCENT_COMPLETE = new PersonalPortfolioIngredientsID("filter_3");
    static final FinderIngredientsID FILTER_START_DATE = new PersonalPortfolioIngredientsID("filter_4");
    static final FinderIngredientsID FILTER_END_DATE = new PersonalPortfolioIngredientsID("filter_5");
    static final FinderIngredientsID FILTER_STATUS_CODE = new PersonalPortfolioIngredientsID("filter_6");
    static final FinderIngredientsID FILTER_COLOR_CODE = new PersonalPortfolioIngredientsID("filter_7");
    static final FinderIngredientsID FILTER_BUSINESS_OWNER = new PersonalPortfolioIngredientsID("filter_8");
    static final FinderIngredientsID FILTER_PARENT_PROJECT = new PersonalPortfolioIngredientsID("filter_9");
    static final FinderIngredientsID FILTER_PROJECT_SPONSOR = new PersonalPortfolioIngredientsID("filter_10");
    static final FinderIngredientsID FILTER_IMPROVEMENT_CODE = new PersonalPortfolioIngredientsID("filter_11");
    static final FinderIngredientsID FILTER_FINANCIAL_STATUS_COLOR_CODE = new PersonalPortfolioIngredientsID("filter_12");
    static final FinderIngredientsID FILTER_FINANCIAL_STATUS_IMPROVEMENT_CODE = new PersonalPortfolioIngredientsID("filter_13");
    static final FinderIngredientsID FILTER_SCHEDULE_STATUS_COLOR_CODE = new PersonalPortfolioIngredientsID("filter_14");
    static final FinderIngredientsID FILTER_SCHEDULE_STATUS_IMPROVEMENT_CODE = new PersonalPortfolioIngredientsID("filter_15");
    static final FinderIngredientsID FILTER_RESOURCE_STATUS_COLOR_CODE = new PersonalPortfolioIngredientsID("filter_16");
    static final FinderIngredientsID FILTER_RESOURCE_STATUS_IMPROVEMENT_CODE = new PersonalPortfolioIngredientsID("filter_17");
    static final FinderIngredientsID FILTER_CURRENT_STATUS_DESCRIPTION = new PersonalPortfolioIngredientsID("filter_18");
    static final FinderIngredientsID FILTER_BUDGETED_TOTAL_COST = new PersonalPortfolioIngredientsID("filter_19");
    static final FinderIngredientsID FILTER_CURRENT_ESTIMATED_TOTAL_COST = new PersonalPortfolioIngredientsID("filter_20");
    static final FinderIngredientsID FILTER_ACTUAL_COST_TO_DATE = new PersonalPortfolioIngredientsID("filter_21");
    static final FinderIngredientsID FILTER_ESIMATED_ROI = new PersonalPortfolioIngredientsID("filter_22");
    static final FinderIngredientsID FILTER_COST_CENTER = new PersonalPortfolioIngredientsID("filter_23");
    static final FinderIngredientsID FILTER_PRIORITY_CODE = new PersonalPortfolioIngredientsID("filter_24");
    static final FinderIngredientsID FILTER_RISK_RATING_CODE = new PersonalPortfolioIngredientsID("filter_25");
    static final FinderIngredientsID FILTER_SELECTED_PROJECTS = new PersonalPortfolioIngredientsID("filter_26");
    static final FinderIngredientsID FILTER_PROJECT_VISIBILITY = new PersonalPortfolioIngredientsID("filter_27");
    static final FinderIngredientsID FILTER_OWNING_BUSINESS = new PersonalPortfolioIngredientsID("filter_28");
    static final FinderIngredientsID FILTER_DEFAULT_CURRENCY_CODE = new PersonalPortfolioIngredientsID("filter_29");
    static final FinderIngredientsID FILTER_INCLUDES_EVERYONE = new PersonalPortfolioIngredientsID("filter_30");
    static final FinderIngredientsID FILTER_META_EXTERNAL_PROJECT_ID = new PersonalPortfolioIngredientsID("filter_31");
    static final FinderIngredientsID FILTER_META_PROJECT_MANAGER = new PersonalPortfolioIngredientsID("filter_32");
    static final FinderIngredientsID FILTER_META_PROGRAM_MANAGER = new PersonalPortfolioIngredientsID("filter_33");
    static final FinderIngredientsID FILTER_META_INITIATIVE = new PersonalPortfolioIngredientsID("filter_34");
    static final FinderIngredientsID FILTER_META_FUNCTIONAL_AREA = new PersonalPortfolioIngredientsID("filter_35");
    static final FinderIngredientsID FILTER_META_PROJECT_CHARTER = new PersonalPortfolioIngredientsID("filter_36");
    static final FinderIngredientsID FILTER_META_TYPE_OF_EXPENSE = new PersonalPortfolioIngredientsID("filter_37");
    static final FinderIngredientsID FILTER_SUBPROJECT_OF = new PersonalPortfolioIngredientsID("filter_38");

    // Sorter IDs
    static final FinderIngredientsID SORTER_1 = new PersonalPortfolioIngredientsID("sorter_1");
    static final FinderIngredientsID SORTER_2 = new PersonalPortfolioIngredientsID("sorter_2");
    static final FinderIngredientsID SORTER_3 = new PersonalPortfolioIngredientsID("sorter_3");

    /**
     * Returns the FinderIngredientsID with the matching internal id.
     * @param id the id of the <code>FinderIngredientsID</code> to find
     * @return the found item or null if not found
     */
    static FinderIngredientsID findByID(String id) {
        return FinderIngredientsID.findByID(ALL_ID_LIST, id);
    }

    /**
     * Creates a new ID with the specified internal id.
     * @param id the internal id
     */
    private PersonalPortfolioIngredientsID(String id) {
        super(ALL_ID_LIST, id);
    }

}

