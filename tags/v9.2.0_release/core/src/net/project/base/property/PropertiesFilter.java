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
package net.project.base.property;


public class PropertiesFilter implements java.io.Serializable {

    private String name = null;
    private String value = null;
    private String type = null;
    private String[] categories = null;


    /* ------------------------------- Constructor(s)  ------------------------------- */

    public PropertiesFilter() {
        // do nothing
    }



    /* ------------------------------- Filter Methods  ------------------------------- */


    boolean matchesFilter(Token token) {

        boolean match = false;

        if (token != null)
            match = (matchName(token.getName()) && matchValue(token.getValue())
                    && matchType(token.getTypeString()) && matchCategory(token.getName())) ? true : false;
        else
            match = false;

        return match;
    }


    private boolean matchName(String name) {
        return contains(name, this.name);
    }

    private boolean matchValue(String value) {
        return contains(value, this.value);
    }

    private boolean matchType(String type) {
        return contains(type, this.type);
    }

    /** Returns true if the token.name matches at least one of the selected categories
     */
    private boolean matchCategory(String tokenName) {

        boolean match = false;

        if (this.categories == null || this.categories.length <= 0)
            match = true;
        else {

            // get the categories out of the singleton session instance of the PropertyBundle
            PropertyCategoryManager categoryManager = PropertyBundle.getInstance().getCategories();
            match = (categoryManager != null) ? categoryManager.containsToken(this.categories, tokenName) : false;
        }

        return match;
    }

    private boolean contains(String tokenString, String filterString) {

        boolean match = false;
        String lowerString = (tokenString != null) ? tokenString.toLowerCase() : null;

        if (filterString == null || filterString.equals(""))
            match = true;
        else if (lowerString != null) {
            match = (lowerString.indexOf(filterString.toLowerCase()) >= 0) ? true : false;
        }

        return match;

    }



    /* ------------------------------- Getter/Setter  ------------------------------- */

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setFilterCategories(String[] categoryList) {
        this.categories = categoryList;
    }

    public String[] getFilterCategories() {
        return this.categories;
    }

    /* ------------------------------- Utility Methods  ------------------------------- */

    public void clear() {
        this.name = null;
        this.value = null;
        this.type = null;
        this.categories = null;
    }

}



