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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.finder.ColumnDefinition;
import net.project.base.finder.FinderFilter;
import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderIngredients;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioView;
import net.project.portfolio.ProjectPortfolio;
import net.project.project.NoSuchPropertyException;
import net.project.project.ProjectSpace;
import net.project.project.ProjectSpaceFinder;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;


/**
 * Provides the results of a personal portfolio view.
 * These are ProjectSpaces in a portfolio queried based on FinderIngredients.
 * The ResultType of the view determines the format of the XML returned.
 * A tree view contains additional formatting information.
 *
 * @author Tim Morrow
 * @since version 7.4
 */
public class PersonalPortfolioViewResults implements IViewResults {

    /**
     * The portfolio on which to base results.
     * All projects returned in these results must belong to that portfolio.
     */
    private ProjectPortfolio basePortfolio = null;

    /**
     * Additional finder ingredients to limit the query.
     */
    private FinderIngredients ingredients = null;

    /**
     * The Portfolio Tree data, used when result type is tree.
     */
    private IPortfolioView tree = null;

    /**
     * The loaded project spaces when the result type is default.
     */
    private final List loadedResults = new ArrayList();

    /**
     * The ResultType, used for presentation purposes.
     */
    private ResultType resultType = null;

    private PersonalPortfolioView.SortParameters sortParameters;

    /**
     * Creates new results based on the sepcified base portfolio and FinderIngredients.
     * @param basePortfolio the portfolio on which the results are based; all
     * loaded projects will be in this portfolio
     * @param ingredients the FinderIngredients to use to refine the results.
     * @param resultType the type of results which affects the format of the
     * returned XML
     */
    PersonalPortfolioViewResults(ProjectPortfolio basePortfolio, FinderIngredients ingredients, ResultType resultType) {
        this.basePortfolio = basePortfolio;
        this.ingredients = ingredients;
        this.resultType = resultType;
    }

    PersonalPortfolioViewResults(ProjectPortfolio basePortfolio, FinderIngredients ingredients, ResultType resultType, PersonalPortfolioView.SortParameters sortParameters) {
        this(basePortfolio, ingredients, resultType);
        this.sortParameters = sortParameters;
    }

    /**
     * Loads the data for these view Results.
     * Uses the FinderIngredients to find the result data.
     * @throws PersistenceException if there is a problem loading
     */
    @SuppressWarnings("unchecked")    
    void load() throws PersistenceException {
        ProjectSpaceFinder finder = new ProjectSpaceFinder();
        finder.addFinderIngredients(this.ingredients);

        if (getResultType().equals(ResultType.TREE)) {
            tree = this.basePortfolio.getTreeView();

        } else {
            // The default view doesn't require the portfolio to already be
            // loaded; instead we limit the load of the filtered items based
            // on the portfolio ID
            this.loadedResults.addAll(this.basePortfolio.buildPortfolioEntries(finder.find()));
            applyPostLoadFilters(ingredients.getFinderFilterList().getAllFilters(), findSelectedProjectsFilter(ingredients.getFinderFilterList().getAllFilters()));
            if (sortParameters != null) {
                Collections.sort(loadedResults, sortParameters.createComparator());
            }
        }
    }

    /**
     * Returns the result type on which to base presentation decisions.
     * @return the result type.
     */
    public ResultType getResultType() {
        return this.resultType;
    }

    /**
     * Returns an unmodifiable ordered collection of <code>ProjectSpace</code>s which
     * make up the results of this view.
     * The elements are in the same order as they would be displayed.
     * Note: If these results are of type {@link ResultType#TREE} then
     * simply the default portfolio entries are returned
     *
     * @return the ordered collection of <code>ProjectSpace</code>s
     */
    public Collection getProjectSpaceResultElements() {
        Collection results = null;

        if (getResultType().equals(ResultType.TREE)) {
            // The tree view hides all the entries from us; we can't easily
            // access the additional non-membership entries
            // Therefore, we're basing elements on the membership project spaces
            // only
            results = this.basePortfolio;

        } else {
            // We give them all the loaded results
            results = this.loadedResults;
        }

        return Collections.unmodifiableCollection(results);
    }

    /**
     * Returns the XML for these ViewResults.
     * The XML format differs by result type.
     * @return the XML representation
     * @throws SQLException 
     * @see net.project.portfolio.PersonalProjectTreeView#getXML for information
     * on the tree view
     */
    public String getXML() throws SQLException {
        return getXMLDocument().getXMLString();
    }

    /**
     * Returns the XML for these ViewResults.
     * The XML format differs by result type.
     * @return the XML representation
     * @throws SQLException 
     * @see net.project.portfolio.PersonalProjectTreeView#getXMLBody for details
     * for information on the tree view
     */
    public String getXMLBody() throws SQLException {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns the XML for these ViewResults.
     * The XML format differs by result type.
     * @return the XML representation
     * @throws SQLException 
     */
    XMLDocument getXMLDocument() throws SQLException {

        XMLDocument doc = new XMLDocument();

        try {
            doc.startElement("PersonalPortfolioViewResults");
            doc.addElement(getResultType().getXMLDocument());

            doc.startElement("PortfolioEntries");

            if (getResultType().equals(ResultType.TREE)) {
                doc.addXMLString(this.tree.getXMLBody());

            } else {

                for (Iterator it = this.loadedResults.iterator(); it.hasNext(); ) {
                    ProjectSpace nextSpace = (ProjectSpace) it.next();
                    doc.addXMLString(nextSpace.getXMLBody());
                }

            }

            doc.endElement();
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Return partially built document

        }

        return doc;
    }


    public FinderIngredients getIngredients() {
        return ingredients;
    }

    /**
     * Applies filters that do not generate SQL where clause.
     * The method removes projects from this.loadedResults using currently active filters.
     * @param filters list of FinderFilter objects
     */
    private void applyPostLoadFilters(List filters, SelectedProjectsFilter selectedProjectsFilter) {
        for (Object o : filters) {
            FinderFilter finderFilter = (FinderFilter) o;
            if (finderFilter instanceof FinderFilterList) {
                FinderFilterList finderFilterList = (FinderFilterList) finderFilter;
                applyPostLoadFilters(finderFilterList.getAllFilters(), selectedProjectsFilter);
            }
            if (!finderFilter.isSelected()) continue;
            if (selectedProjectsFilter != null && selectedProjectsFilter.isIgnoreOtherFilters()) {
                if (finderFilter instanceof SelectedProjectsFilter)
                    processFilter(finderFilter, selectedProjectsFilter);
            } else {
                processFilter(finderFilter, selectedProjectsFilter);
            }
        }
    }

    private SelectedProjectsFilter findSelectedProjectsFilter(List filters) {
        for (Object o : filters) {
            FinderFilter finderFilter = (FinderFilter) o;
            if (finderFilter instanceof FinderFilterList) {
                FinderFilterList finderFilterList = (FinderFilterList) finderFilter;
                SelectedProjectsFilter filter = findSelectedProjectsFilter(finderFilterList.getAllFilters());
                if (filter != null) return filter;
            }
            if (!finderFilter.isSelected()) continue;
            if (finderFilter instanceof SelectedProjectsFilter)
                return (SelectedProjectsFilter) finderFilter;
        }
        return null;
    }

    /*
     * this functionality needs refactoring - moving the code to the corresponding classes 
     */
    private void processFilter(FinderFilter finderFilter, SelectedProjectsFilter selectedProjectsFilter) {
        if (finderFilter instanceof OwnedBusinessFilter) {
            OwnedBusinessFilter ownedBusinessFilter = (OwnedBusinessFilter) finderFilter;
            for (int i = loadedResults.size() - 1; i >= 0; i--) {
                ProjectSpace projectSpace = (ProjectSpace) loadedResults.get(i);
                String businessID = projectSpace.getParentBusinessID();
                if (!matchesSelectedProjectsFilter(selectedProjectsFilter, projectSpace) && !ownedBusinessFilter.matches(businessID)) {
                    loadedResults.remove(i);
                }
            }
        } else if (finderFilter instanceof SelectedProjectsFilter) {
            SelectedProjectsFilter projectsFilter = (SelectedProjectsFilter) finderFilter;
            if (projectsFilter.isIgnoreOtherFilters()) {
                for (int i = loadedResults.size() - 1; i >= 0; i--) {
                    ProjectSpace projectSpace = (ProjectSpace) loadedResults.get(i);
                    String id = projectSpace.getID();
                    if (!projectsFilter.matches(id)) {
                        loadedResults.remove(i);
                    }
                }
            }
        } else if (finderFilter instanceof PersonalPortfolioFinderIngredients.MetaTextFilter) {
            PersonalPortfolioFinderIngredients.MetaTextFilter metaTextFilter = (PersonalPortfolioFinderIngredients.MetaTextFilter) finderFilter;
            for (int i = loadedResults.size() - 1; i >= 0; i--) {
                ProjectSpace projectSpace = (ProjectSpace) loadedResults.get(i);
                String value = getMetaProperty(metaTextFilter.getColumnDefinition(), projectSpace);
                if (!matchesSelectedProjectsFilter(selectedProjectsFilter, projectSpace) && !metaTextFilter.matches(value)) {
                    loadedResults.remove(i);
                }
            }
        } else if (finderFilter instanceof PersonalPortfolioFinderIngredients.MetaSimpleDomainFilter && finderFilter.getID().equals(PersonalPortfolioIngredientsID.FILTER_SUBPROJECT_OF.getID())) {
            PersonalPortfolioFinderIngredients.MetaSimpleDomainFilter metaSimpleDomainFilter = (PersonalPortfolioFinderIngredients.MetaSimpleDomainFilter) finderFilter;
            for (int i = loadedResults.size() - 1; i >= 0; i--) {
                ProjectSpace projectSpace = (ProjectSpace) loadedResults.get(i);
                String subprojectOf = projectSpace.isSubproject() ? projectSpace.getParentProjectName() : "";
                if (!matchesSelectedProjectsFilter(selectedProjectsFilter, projectSpace) && !metaSimpleDomainFilter.matches(subprojectOf)) {
                    loadedResults.remove(i);
                }
            }
        } else if (finderFilter instanceof PersonalPortfolioFinderIngredients.MetaSimpleDomainFilter) {
            PersonalPortfolioFinderIngredients.MetaSimpleDomainFilter metaSimpleDomainFilter = (PersonalPortfolioFinderIngredients.MetaSimpleDomainFilter) finderFilter;
            for (int i = loadedResults.size() - 1; i >= 0; i--) {
                ProjectSpace projectSpace = (ProjectSpace) loadedResults.get(i);
                String value = getMetaProperty(metaSimpleDomainFilter.getColumnDefinition(), projectSpace);
                if (!matchesSelectedProjectsFilter(selectedProjectsFilter, projectSpace) && !metaSimpleDomainFilter.matches(value)) {
                    loadedResults.remove(i);
                }
            }
        } else if (finderFilter instanceof PersonalPortfolioFinderIngredients.PostLoadNumberFilter) {
            PersonalPortfolioFinderIngredients.PostLoadNumberFilter postLoadNumberFilter = (PersonalPortfolioFinderIngredients.PostLoadNumberFilter) finderFilter;
            for (int i = loadedResults.size() - 1; i >= 0; i--) {
                ProjectSpace projectSpace = (ProjectSpace) loadedResults.get(i);
                String value = projectSpace.getPercentComplete();
                if (!matchesSelectedProjectsFilter(selectedProjectsFilter, projectSpace) && !postLoadNumberFilter.matches(value)) {
                    loadedResults.remove(i);
                }
            }
        }
    }

    private boolean matchesSelectedProjectsFilter(SelectedProjectsFilter selectedProjectsFilter, ProjectSpace projectSpace) {
        String id = projectSpace.getID();
        if (selectedProjectsFilter != null && !selectedProjectsFilter.isIgnoreOtherFilters() && selectedProjectsFilter.matches(id)) return true;
        return false;
    }

    private String getMetaProperty(ColumnDefinition columnDefinition, ProjectSpace projectSpace) {
        String value = null;
        try {
            if (PersonalPortfolioColumnDefinition.META_EXTERNAL_PROJECT_ID.equals(columnDefinition)) {
                value = projectSpace.getMetaData().getProperty("ExternalProjectID");
            } else if (PersonalPortfolioColumnDefinition.META_PROJECT_MANAGER.equals(columnDefinition)) {
                value = projectSpace.getMetaData().getProperty("ProjectManager");
            } else if (PersonalPortfolioColumnDefinition.META_PROGRAM_MANAGER.equals(columnDefinition)) {
                value = projectSpace.getMetaData().getProperty("ProgramManager");
            } else if (PersonalPortfolioColumnDefinition.META_INITIATIVE.equals(columnDefinition)) {
                value = projectSpace.getMetaData().getProperty("Initiative");
            } else if (PersonalPortfolioColumnDefinition.META_PROJECT_CHARTER.equals(columnDefinition)) {
                value = projectSpace.getMetaData().getProperty("ProjectCharter");
            } else if (PersonalPortfolioColumnDefinition.META_FUNCTIONAL_AREA.equals(columnDefinition)) {
                value = projectSpace.getMetaData().getProperty("FunctionalArea");
            } else if (PersonalPortfolioColumnDefinition.META_TYPE_OF_EXPENSE.equals(columnDefinition)) {
                value = projectSpace.getMetaData().getProperty("TypeOfExpense");
            }
        } catch (NoSuchPropertyException e) {}
        return value;
    }
}

