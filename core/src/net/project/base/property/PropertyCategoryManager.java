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

 package net.project.base.property;

import java.sql.SQLException;
import java.util.HashMap;

import net.project.database.DBBean;

import org.apache.log4j.Logger;

class PropertyCategoryManager implements java.io.Serializable {
    private HashMap categories = null;

    void loadCategories() {

        String qstrLoadCategoryProperties = "select category_id, property from pn_prop_category_has_property";
        this.categories = new HashMap();

        DBBean db = new DBBean();
        try {

            // first load the category list
            String qstrLoadCategories = "select category_id, name, description from pn_property_category";
            db.executeQuery(qstrLoadCategories);

            while (db.result.next()) {
                PropertyCategory category = new PropertyCategory();

                category.setID(db.result.getString("category_id"));
                category.setName(db.result.getString("name"));
                category.setDescription(db.result.getString("description"));

                categories.put(category.getID(), category);
            }

            // next process and set the propeties to their categories.
            db.executeQuery(qstrLoadCategoryProperties);

            while (db.result.next()) {
                addCategoryProperty(db.result.getString("category_id"), db.result.getString("property"));
            }
        } catch (SQLException sqle) {
            Logger.getLogger(PropertyCategoryManager.class).debug("PropertyCategoryManager.loadCategories() threw and sqle: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    private void addCategoryProperty(String categoryID, String property) {
        PropertyCategory category = getCategory(categoryID);

        if (category != null) {
            category.addEntry(property);
        }
    }


    private PropertyCategory getCategory(String categoryID) {
        return (PropertyCategory) this.categories.get(categoryID);
    }


    private boolean containsToken(String cat, String tokenName) {
        if (cat != null && cat.equals("")) {
            return true;
        }

        PropertyCategory category = getCategory(cat);
        return (category != null) ? category.contains(tokenName) : false;
    }

    boolean containsToken(String[] categories, String tokenName) {
        boolean match = false;

        if (categories == null || categories.length <= 0) {
            match = false;
        } else {
            for (int i = 0; i < categories.length && !match; i++) {
                match = containsToken(categories[i], tokenName);
            }
        }

        return match;
    }
}
