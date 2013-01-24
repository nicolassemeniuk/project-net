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
|   $Revision: 20372 $
|       $Date: 2010-02-05 03:52:52 -0300 (vie, 05 feb 2010) $
|     $Author: ritesh $
|
+----------------------------------------------------------------------*/
package net.project.portfolio.view;

import java.util.Date;

import net.project.base.finder.FinderIngredients;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;

/**
 * Defines a View which is a named provider of data based on
 * a set of FinderIngredients.
 */
public interface IView extends IJDBCPersistence, IXMLPersistence {

    /**
     * Sets the ID of this view.
     * @param viewID the id of this view
     */
    public void setID(String viewID);

    /**
     * Returns the ID of this view.
     * @return the ID
     */
    public String getID();

    /**
     * Specifies this view's name.
     * @param name the name of the view
     */
    public void setName(String name);

    /**
     * Returns the view's display name.
     * @return the display name
     */
    public String getName();

    /**
     * Specifies this view's description.
     * @param description the description
     */
    public void setDescription(String description);

    /**
     * Returns the view's description.
     * @return the description
     */
    public String getDescription();

    /**
     * Specifies the id of the user who created this view.
     * @param createdByID the id of the user
     */
    public void setCreatedByID(String createdByID);

    /**
     * Returns the id of the user who created this view.
     * @return the user id
     */
    public String getCreatedByID();

    /**
     * Specifies the date on which this view was created.
     * @param createdDate the date
     */
    public void setCreatedDate(Date createdDate);

    /**
     * Returns the date on which this view was created.
     * @return the date
     */
    public Date getCreatedDate();

    /**
     * Specifies the id of the user who last modified this view.
     * @param modifiedByID the id of the user
     */
    public void setModifiedByID(String modifiedByID);

    /**
     * Returns the id of the user who last modified this view.
     * @return the user id
     */
    public String getModifiedByID();

    /**
     * Specifies the date on which this view was last modified.
     * @param modifiedDate the date
     */
    public void setModifiedDate(Date modifiedDate);

    /**
     * Returns the date on which this view was last modified.
     * @return the date
     */
    public Date getModifiedDate();

    /**
     * Specifies the record status.
     * @param recordStatus the record status
     */
    public void setRecordStatus(String recordStatus);

    /**
     * Returns the record status.
     * @return the record status
     */
    public String getRecordStatus();

    /**
     * Specifies the id of the FinderIngredients associated with this view.
     * This id may be used to load the finder ingredients.
     * @param ingredientsID the id of the FinderIngredients associated
     * with this view
     */
    public void setFinderIngredientsID(String ingredientsID);

    /**
     * Returns the information that defines the filter that produces this
     * view's results.
     * @return the filter data
     */
    FinderIngredients getFinderIngredients();

    /**
     * Returns the results from executing view.
     * @return the results
     * @throws PersistenceException if there is a problem getting the results
     */
    IViewResults getResults() throws PersistenceException;

    /**
     * Specifies whether the view is shared among other user
     * @param isViewShared
     */
    public void setViewShared(boolean viewShared);
    
    /**
     * Returns shared status of the view
     * @return
     */
    public boolean isViewShared();  
    
    /**
     * Specifies whether view is shared among all users
     * @param isViewShared
     */
    public void setVisibleToAll(boolean visibleToAll);
    
    /**
     * Returns whether view is shared among all users
     * @return
     */
    public boolean isVisibleToAll();  
    
	/**
	 * To set finder ingredients to this view when filters are applied on default tree view.
     * @param finderIngredients
     */
    public void setFinderIngredients(FinderIngredients finderIngredients);
}
