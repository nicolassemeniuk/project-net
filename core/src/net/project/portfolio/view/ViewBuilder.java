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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import net.project.base.finder.FinderIngredients;
import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;

/**
 * Provides a mechanism for creating and modifying views.
 * The ViewBuilder determines the type of view being built from the view context.
 * The view context is required for loading, storing and removing.
 *
 * @author Tim Morrow
 * @since version 7.4
 */
public abstract class ViewBuilder {

    /**
     * The ViewContext, used for producing a list of views.
     */
    private final IViewContext viewContext;

    /**
     * A view being built.
     */
    private IView view = null;

    /**
     * The current default view settings.
     */
    private Collection defaultViewSettings = null;

    /**
     * The list of scenario IDs for which the view is to be default.
     */
    private Collection defaultForScenarioIDs = new ArrayList();

    /**
     * Creates a new ViewBuilder for the specified context and sets it up
     * for creating a view.
     * @param viewContext the context; this is used for returning the list
     * of view from this builder and for initializing a new view for creating
     */
    public ViewBuilder(IViewContext viewContext) {
        this.viewContext = viewContext;
        createView();
    }

    /**
     * Returns a view list for the current context.
     * @return the current view list
     * @throws PersistenceException
     */
    public IViewList getViewList() throws PersistenceException {
        return this.viewContext.makeViewList();
    }

    /**
     * Sets this ViewBuilder up for creating a view.
     * Initializes the view.
     */
    public void createView() {
        clear();
        this.view = this.viewContext.makeView();
    }

    /**
     * Sets this ViewBuilder up for editiing the view with the specified
     * ID.
     * After calling, {@link #load} should be called to actually load it.
     *
     * @param viewID the ID view to edit; this must be the ID of a view
     * returned by the current view context's view list
     * @throws ViewException if there is a problem locating the view for
     * the specified id
     * @throws NullPointerException if the specified ID is null
     */
    public void editView(String viewID) throws ViewException {

        if (viewID == null) {
            throw new NullPointerException("viewID is required");
        }

        try {
            IView editView = this.viewContext.makeViewList().forID(viewID);
            if (editView == null) {
                throw new ViewException(PropertyProvider.get("prm.view.manageviews.removelink.exception")+"(Id:" + viewID +")");
            }
            clear();
            this.view = editView;

        } catch (PersistenceException e) {
            throw new ViewException("Error getting list of views: " + e, e);

        }
    }

    /**
     * Sets this ViewBuilder to remove the view with the specified ID.
     * After calling, {@link #remove} should be called to actually remove it.
     * This method is designed to make it possible to remove a view that
     * is not loaded.  It checks to make sure the current context has
     * a view with the specified ID.
     *
     * @param viewID the ID view to remove; this must be the ID of a view
     * returned by the current view context's view list
     * @throws ViewException if there is a problem locating the view for
     * the specified id
     * @throws NullPointerException if the specified ID is null
     */
    public void removeView(String viewID) throws ViewException {

        if (viewID == null) {
            throw new NullPointerException("viewID is required");
        }

        try {
            IView removeView = this.viewContext.makeViewList().forID(viewID);
            if (removeView == null) {
            	throw new ViewException(PropertyProvider.get("prm.view.manageviews.removelink.exception") + viewID );
            }
            clear();
            this.view = removeView;

        } catch (PersistenceException e) {
            throw new ViewException("Error getting list of views: " + e, e);

        }

    }

    /**
     * Returns the current view being editied.
     * @return the view
     */
    protected IView getView() {
        return this.view;
    }

    /**
     * Returns the ID of the current view, or null if it does not have one.
     * @return the view's ID
     */
    public String getID() {
        return this.view.getID();
    }

    /**
     * Specifies the view name.
     * @param name the name
     * @see #getName
     */
    public void setName(String name) {
        this.view.setName(name);
    }

    /**
     * Returns the view name.
     * @return the view name
     * @see #setName
     */
    public String getName() {
        return this.view.getName();
    }

    /**
     * Specifies the description of the view.
     * @param description the view description
     * @see #getDescription
     */
    public void setDescription(String description) {
        this.view.setDescription(description);
    }

    /**
     * Returns the description of the view.
     * @return the view description
     * @see #setDescription
     */
    public String getDescription() {
        return this.view.getDescription();
    }

    /**
     * Returns the finder ingredients for the current view.
     * @return the finder ingredients
     */
    public FinderIngredients getFinderIngredients() {
        return this.view.getFinderIngredients();
    }

    /**
     * Loads the view for the current id into this view builder.
     * @throws PersistenceException if there is a problem loading
     */
    public void load() throws PersistenceException {
        this.view.load();
    }

    /**
     * Stores the built view in the current context.
     * @throws PersistenceException if there is a problem storing
     * @throws SQLException 
     */
    public void store() throws PersistenceException, SQLException {
        // Store first to generate a view ID
        this.view.store();

        // Now update the default view settings based on the current
        // list of scenario IDs for which this view is default

        // Iterate over each default view setting
        // Updates the default view ID if this view is set to be default for
        // the setting's scenario
        // Clears the default view ID if this view is no longer the default for
        // the setting's scenario
        for (Iterator it = getDefaultViewSettings().iterator(); it.hasNext(); ) {
            DefaultViewSetting nextSetting = (DefaultViewSetting) it.next();

            if (this.defaultForScenarioIDs.contains(nextSetting.getScenario().getID())) {
                // This view is set to be default for the setting's scenario
                // Set it has such
                nextSetting.setDefaultID(getID());

            } else {
                // This view is not marked as default for the setting's scenario
                // If the setting's current view is this view, then clear it out
                if (nextSetting.hasValue() && nextSetting.isDefault(getID())) {
                    nextSetting.clearDefaultID();
                }
            }
        }

        // Store the settings
        this.viewContext.storeDefaultViewSettings(getDefaultViewSettings());
    }


    /**
     * Removes the current view.
     * @throws PersistenceException if there is a problem removing
     */
    public void remove() throws PersistenceException {
        this.view.remove();
    }

    /**
     * Clears all modifiable properties.
     */
    private void clear() {
        this.defaultViewSettings = null;
        this.defaultForScenarioIDs = new ArrayList();
    }

    /**
     * Returns the ordered collection of pages for rendering filters in
     * this view builder
     * @return the orderd collection of filter pages
     */
    public abstract Collection getFilterPages();
    
    /**
     * Returns the ordered collection of pages for rendering sorters in
     * this view builder
     * @return the ordered collection of sorter pages
     */
    public abstract Collection getSorterPages();

    /**
     * Gets the filter page for the specified.
     * @param id the id of the page to get
     * @return the filter page
     * @throws ViewException if a page cannot be found with that id
     */
    public ViewBuilderFilterPage getFilterPageForID(String id) throws ViewException {
        return (ViewBuilderFilterPage) getPageForID(id, getFilterPages());
    }

    /**
     * Gets the sorter page for the specified.
     * @param id the id of the page to get
     * @return the sorter page
     * @throws ViewException if a page cannot be found with that id
     */
    public ViewBuilderSorterPage getSorterPageForID(String id) throws ViewException {
        return (ViewBuilderSorterPage) getPageForID(id, getSorterPages());
    }

    /**
     * Returns a ViewBuilder page for a specified ID.
     * @param id the ID of the page to get
     * @param pageCollection the collection of pages to look in
     * @return the ViewBuilderPage
     * @throws ViewException if a page cannot be found with that id
     */
    private ViewBuilderPage getPageForID(String id, Collection pageCollection) throws ViewException {
        ViewBuilderPage foundPage = null;

        for (Iterator it = pageCollection.iterator(); it.hasNext(); ) {
            ViewBuilderPage nextPage = (ViewBuilderPage) it.next();

            if (nextPage.getID().equals(id)) {
                foundPage = nextPage;
                break;
            }
        }

        if (foundPage == null) {
            throw new ViewException("Unable to find view builder page with id " + id);
        }

        return foundPage;
    }

    /**
     * Returns the default view settings for the current context.
     * These are presented to the user.
     *
     * @return the collection where each element is a <code>DefaultViewSetting</code>.
     * @throws PersistenceException if there is a problem loading the default
     * settings
     */
    public Collection getDefaultViewSettings() throws PersistenceException {
        if (this.defaultViewSettings == null) {
            this.defaultViewSettings = this.viewContext.getDefaultViewSettings();
        }
        return this.defaultViewSettings;
    }

    /**
     * Updates the current default view settings from the request.
     * The request is expected to contain parameters named for <code>parameterName</code>
     * where each parameter value is the ID of a scenario for which this view
     * is marked as the default view.
     *
     * @param request the request from which to get the parameter values
     * @param parameterName the name of the parameter
     */
    public void updateDefaultViewSettings(HttpServletRequest request, String parameterName) {
        updateDefaultViewSettings(request.getParameterValues(parameterName));
    }
    
    /**
     * Updates the current default view settings from project portfolio.
     * where each value in parameter values is the ID of a scenario for which this view
     * is marked as the default view.
     * @param values
     */
    public void updateDefaultViewSettings(String[] values) {
        this.defaultForScenarioIDs.clear();
        if (values != null) {
            this.defaultForScenarioIDs.addAll(Arrays.asList(values));
        }
    }
    
    /**
     * Specifies whether the view is shared among other user
     * @param isViewShared
     */
    public void setViewShared(boolean isViewShared){
    	this.view.setViewShared(isViewShared);
    }
    
    /**
     * Returns shared status of the view
     * @return
     */
    public boolean isViewShared(){
    	return this.view.isViewShared();
    }  
    
    /**
     * Specifies whether the view is shared among other user
     * @param isViewShared
     */
    public void setVisibleToAll(boolean isVisibleToAll){
    	this.view.setVisibleToAll(isVisibleToAll);
    }
    
    /**
     * Returns shared status of the view
     * @return
     */
    public boolean isVisibleToAll(){
    	return this.view.isViewShared();
    }  
    
	/**
	 * To set finder ingredients to this view when filters are applied on default tree view.
     * @param finderIngredients
     */
    public void setFinderIngredients(FinderIngredients finderIngredients){
    	this.view.setFinderIngredients(finderIngredients);
    }
}
