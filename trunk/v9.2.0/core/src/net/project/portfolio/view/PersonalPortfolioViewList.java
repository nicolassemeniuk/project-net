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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.persistence.PersistenceException;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides collections of personal portfolio views.
 * This includes the default tree view and any additional custom filter views.
 *
 * @author Tim Morrow
 * @since version 7.4
 */
public class PersonalPortfolioViewList implements IViewList {

    /**
     * The context to which each view in this list belongs.
     */
    private PersonalPortfolioViewContext context = null;

    /**
     * The loaded views.
     */
    private final List loadedViewList = new ArrayList();

    /**
     * Specifies the context for this view list.
     * All views loaded in this list belong to that context.
     * @param context the context
     */
    public void setViewContext(IViewContext context) {
        this.context = (PersonalPortfolioViewContext) context;
    }

    /**
     * Returns the default views in this view list.
     * These are views which cannot be modifiable by the user.
     * @return the list of default views or the empty list if there are none
     */
    public List getDefaultViews() {
        List defaultViewsList = new ArrayList();
        defaultViewsList.add(new DefaultView(this.context));
        return Collections.unmodifiableList(defaultViewsList);
    }

    /**
     * Returns only views that may be modified by the user.
     * @return a list where each element is a <code>PersonalPortfolioView</code>
     */
    public List getModifiableViews() {
        return Collections.unmodifiableList(this.loadedViewList);
    }

    /**
     * Returns all views, including unmodifiable views.
     * Assumes {@link #load} has been called to load the modifiable views.
     * @return a list where each element is a <code>PersonalPortfolioView</code>
     */
    public List getAllViews() {
        List allViewsList = new ArrayList();
        allViewsList.addAll(getDefaultViews());
        allViewsList.addAll(getModifiableViews());
        return Collections.unmodifiableList(allViewsList);
    }

    /**
     * Returns the view with the specified id or null if none is found.
     * @param id the id of the view to get
     * @return the view with the specified id or null if no view with that
     * id is found; looks in all views
     */
    public IView forID(String id) {
        IView foundView = null;
        boolean isFound = false;

        for (Iterator it = getAllViews().iterator(); it.hasNext() && !isFound;) {
            IView nextView = (IView) it.next();
            if (nextView.getID().equals(id)) {
                foundView = nextView;
                isFound = true;
            }
        }

        return foundView;
    }

    /**
     * Loads all the active views in this list for the current context.
     * Clears out current views before loading.
     * @throws PersistenceException if there is a problem loading
     * @throws IllegalStateException if the context has not been set
     */
    public void load() throws PersistenceException {

        if (this.context == null) {
            throw new IllegalStateException("Context must be specified before loading");
        }

        this.loadedViewList.clear();
        ViewFinder viewFinder = new ViewFinder();
        this.loadedViewList.addAll(viewFinder.findByContext(this.context));
    }

    /**
     * Returns the view for the specified id or the default view.
     * If no default view is found for the specified scenario then it returns
     * the first view in the list.
     * @param id the id for which to get the view
     * @return the view with the specified id or the default view if no view
     * is found with that id, or the id is null
     */
    public IView getViewOrDefault(String id, DefaultScenario scenario) {
        IView view = null;

        // Grab the default view ID (if any) for the specified scenario
        String defaultViewID = this.context.getDefaultViewID(scenario);

        // First try and locate the view for the specified ID
        if (id != null) {
            view = forID(id);
        }

        // If we don't have a view yet, try and find the default view (if any)
        if (view == null && defaultViewID != null) {
            view = forID(defaultViewID);
        }

        // We still don't have a view, so use the first view in the list
        if (view == null) {
            view = (PersonalPortfolioView) getDefaultViews().get(0);
        }

        return view;
    }

    public String getModifiableViewsXML() {
        return getModifiableViewsXMLDocument().getXMLString();
    }

    public String getModifiableViewsXMLBody() {
        return getModifiableViewsXMLDocument().getXMLBodyString();
    }

    private XMLDocument getModifiableViewsXMLDocument() {

        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("ViewList");

            for (Iterator it = getModifiableViews().iterator(); it.hasNext();) {
                doc.addElement(((PersonalPortfolioView) it.next()).getXMLDocument());
            }

            doc.endElement();

        } catch (XMLDocumentException e) {
            // Return partially built document

        }

        return doc;
    }

    //
    // Nested top-level classes
    //

    /**
     * The Default entry in the PersonalPortfolioViewList for all users.
     * This provides the tree view of all Projects in a user's Personal Portfolio.
     */
    private class DefaultView extends PersonalPortfolioView {

        /**
         * Creates the default view based on the specified context.
         * The context determines the space and the portfolio from which
         * to produce the results.
         * @param viewContext the view context for this view
         */
        private DefaultView(PersonalPortfolioViewContext viewContext) {
            super(viewContext);
            setID("default");
            setName(PropertyProvider.get("prm.portfolio.personal.view.default.name"));
            setDescription(PropertyProvider.get("prm.portfolio.personal.view.default.description"));
        }

        /**
         * Returns the results for this default tree view.
         * The XML of these results is different; it includes presentation
         * information to aid the display of the tree
         * @return the results
         * @throws PersistenceException
         */
        public IViewResults getResults() throws PersistenceException {

            // Create TREE-type loaded results
            PersonalPortfolioViewResults results = new PersonalPortfolioViewResults(PersonalPortfolioViewList.this.context.getPortfolio(), new PersonalPortfolioFinderIngredients(PersonalPortfolioViewList.this.context.getPortfolio()), ResultType.TREE, getSortParameters());
            results.load();

            return results;
        }


    }

}

