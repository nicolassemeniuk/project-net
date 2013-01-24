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
package net.project.base.finder;

import java.util.Iterator;

import net.project.base.money.MoneyFilter;
import net.project.portfolio.view.PersonalPortfolioFinderIngredients;
import net.project.portfolio.view.ProjectVisibilityFilter;
import net.project.portfolio.view.SelectedProjectsFilter;
import net.project.project.ProjectSpace;
import net.project.util.VisitException;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides visit methods for producing XML for persistence of FinderIngredients.
 * <p>
 * <b>Note:</b> This class and {@link FinderIngredientPersistentXMLConsumer} are
 * very tightly coupled. Any changes here may require changes in that class;
 * great care must be taken to maintain backwards compatibility.
 * </p>
 * <p>
 * <b>These XML structures are persisted to the database and should rarely
 * change throughout the development life of the application.
 * Any change to the XML structure may require specific handling to maintain
 * backwards compatibility with old structures.</b>
 * </p>
 * <p>
 * Example usage occurs within FinderIngredients:
 * <pre><code>
 *   ...
 *   FinderIngredientPersistentXMLProducer producer = new FinderIngredientPersistentXMLProducer();
 *   accept(producer);
 *   String xml = producer.getXMLDocument().getXMLBodyString();
 *   ...
 * </code></pre>
 * </p>
 *
 * @author Tim Morrow
 * @since Version 7.4
 */
class FinderIngredientPersistentXMLProducer extends EmptyFinderIngredientVisitor {

    /**
     * The XMLDocument constructed by this Visitor.
     */
    private XMLDocument doc = new XMLDocument();

    /**
     * Returns the XMLDocument containing the persistent xml.
     * @return the XMLDocument
     */
    public XMLDocument getXMLDocument() {
        return doc;
    }

    /**
     * Visits the finder ingredients to produce XML.
     * Visits the column list, filter list, grouping list and sorter list.
     * Only enough information to uniquely identify the selected items
     * are persisted as XML.
     *
     * @param finderIngredients the finder ingredients to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML or
     * there is a problem visiting a component of the finder ingredients
     */
    public void visitFinderIngredients(FinderIngredients finderIngredients) throws VisitException {
        try {
            doc.startElement("FinderIngredients");
            finderIngredients.getFinderColumnList().accept(this);
            finderIngredients.getFinderFilterList().accept(this);
            finderIngredients.getFinderGroupingList().accept(this);
            finderIngredients.getFinderSorterList().accept(this);
            if (finderIngredients instanceof PersonalPortfolioFinderIngredients) {
                ((PersonalPortfolioFinderIngredients) finderIngredients).getMetaColumnList().saveToXML(doc);
            }
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    //
    // Filter XML
    //

    /**
     * Visits the finder filter list and each selected filter in the list.
     *
     * @param filterList the <code>FinderFilterList</code> to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML or
     * there is a problem visiting a filter
     */
    public void visitFinderFilterList(FinderFilterList filterList) throws VisitException {

        try {
            doc.startElement("FinderFilterList");
            doc.startElement("FinderFilters");
            for (Iterator it = filterList.getSelectedFilters().iterator(); it.hasNext(); ) {
                FinderFilter nextElement = (FinderFilter) it.next();
                nextElement.accept(this);
            }
            doc.endElement();
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    /**
     * Visits the date filter.
     * @param filter the date filter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitDateFilter(DateFilter filter) throws VisitException {

        try {
            doc.startElement("DateFilter");
            doc.addElement("ID", filter.getID());
            doc.addElement("IsEmptyOptionSelected", new Boolean(filter.isEmptyOptionSelected()));
            doc.addElement("DateRangeStart", filter.getDateRangeStart());
            doc.addElement("DateRangeFinish", filter.getDateRangeFinish());
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    /**
     * Visits the number filter.
     * @param filter the number filter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitNumberFilter(NumberFilter filter) throws VisitException {
        try {
            doc.startElement("NumberFilter");
            doc.addElement("ID", filter.getID());
            filter.getComparator().accept(this);
            doc.addElement("IsEmptyOptionSelected", new Boolean(filter.isEmptyOptionSelected()));
            doc.addElement("Number", filter.getNumber());
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    /**
     * Visits the text filter.
     * @param filter the text filter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitTextFilter(TextFilter filter) throws VisitException {
        try {
            doc.startElement("TextFilter");
            doc.addElement("ID", filter.getID());
            filter.getComparator().accept(this);
            doc.addElement("IsEmptyOptionSelected", new Boolean(filter.isEmptyOptionSelected()));
            doc.addElement("Value", filter.getValue());
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    /**
     * Visits the DomainFilter and appends its XML to the current document.
     * @param filter the DomainFilter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitDomainFilter(DomainFilter filter) throws VisitException {

        try {
            doc.startElement("DomainFilter");
            doc.addElement("ID", filter.getID());
            filter.getComparator().accept(this);
            doc.addElement("IsEmptyOptionSelected", new Boolean(filter.isEmptyOptionSelected()));

            doc.startElement("SelectedValues");
            for (Iterator it = filter.getSelectedDomainOptions().iterator(); it.hasNext(); ) {
                DomainOption nextOption = (DomainOption) it.next();
                doc.addElement("Value", nextOption.getValue());
            }
            doc.endElement();

            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    /**
     * Visits the SelectedProjectsFilter and appends its XML to the current document.
     * @param filter the SelectedProjectsFilter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitSelectedProjectsFilter(SelectedProjectsFilter filter) throws VisitException {

        try {
            doc.startElement("SelectedProjectsFilter");
            doc.addElement("ID", filter.getID());

            doc.addElement("IsIgnoreOtherFilters", new Boolean(filter.isIgnoreOtherFilters()));

            doc.startElement("SelectedProjectIDs");
            for (Iterator it = filter.getSelectedProjectSpaces().iterator(); it.hasNext(); ) {
                ProjectSpace nextProject = (ProjectSpace) it.next();
                doc.addElement("ProjectID", nextProject.getID());
            }
            doc.endElement();

            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }

    }

    /**
     * Visits the ProjectVisibility filter.
     * @param filter the ProjectVisibility filter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitProjectVisibilityFilter(ProjectVisibilityFilter filter) throws VisitException {
        try {
            doc.startElement("ProjectVisibilityFilter");
            doc.addElement("ID", filter.getID());
            doc.addElement("ProjectVisibilityID", filter.getProjectVisibility().getID());
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    /**
     * Visits the money filter.
     * @param filter the money filter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitMoneyFilter(MoneyFilter filter) throws VisitException {
        try {
            doc.startElement("MoneyFilter");

            doc.addElement("ID", filter.getID());

            // Only selected filters are normally visited
            // However, since MoneyFilter is a composite filter and only
            // some of its component filters may be selected
            // We must be sure to only include in the XML the selected
            // filters
            // Otherwise, when consuming the XML a filter might get selected
            // when it actually has no data
            doc.startElement("Value");
            NumberFilter valueFilter = filter.getValueFilter();
            if (valueFilter.isSelected()) {
                valueFilter.accept(this);
            }
            doc.endElement();

            doc.startElement("Currency");
            DomainFilter currencyFilter = filter.getCurrencyFilter();
            if (currencyFilter.isSelected()) {
                currencyFilter.accept(this);
            }
            doc.endElement();


            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

    //
    // Column XML
    //

    /**
     * Visits the finder column list and each selected column in the list.
     *
     * @param columnList the <code>FinderColumnList</code> to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitFinderColumnList(FinderColumnList columnList) throws VisitException {

        try {
            doc.startElement("FinderColumnList");
            doc.startElement("FinderColumns");
            for (Iterator it = columnList.getSelectedColumns().iterator(); it.hasNext(); ) {
                FinderColumn nextElement = (FinderColumn) it.next();
                nextElement.accept(this);
            }
            doc.endElement();
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }

    }

    //
    // Grouping XML
    //

    /**
     * Visits the finder grouping list and each selected grouping in the list.
     *
     * @param groupingList the <code>FinderGroupingList</code> to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitFinderGroupingList(FinderGroupingList groupingList) throws VisitException {

        try {
            doc.startElement("FinderGroupingList");
            doc.startElement("FinderGroupings");
            for (Iterator it = groupingList.getSelectedGroupings().iterator(); it.hasNext(); ) {
                FinderGrouping nextElement = (FinderGrouping) it.next();
                nextElement.accept(this);
            }
            doc.endElement();
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }


    }

    //
    // Sorter XML
    //

    /**
     * Visits the FinderSorterList and each selected sorter in the list.
     *
     * @param sorterList the <code>FinderSorterList</code> to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitFinderSorterList(FinderSorterList sorterList) throws VisitException {

        try {
            doc.startElement("FinderSorterList");
            doc.startElement("FinderSorters");
            for (Iterator it = sorterList.getSelectedSorters().iterator(); it.hasNext(); ) {
                FinderSorter nextElement = (FinderSorter) it.next();
                nextElement.accept(this);
            }
            doc.endElement();
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }

    }

    /**
     * Visits the FinderSorter and adds its persistent XML representation to
     * the XMLDocument.
     * @param sorter the sorter to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitFinderSorter(FinderSorter sorter) throws VisitException {

        try {
            doc.startElement("FinderSorter");
            doc.addElement("ID", sorter.getID());
            doc.startElement("SelectedColumn");
            doc.addElement("ID", sorter.getSelectedColumn().getID());
            doc.endElement();
            doc.addElement("IsDescending", new Boolean(sorter.isDescending()));
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }

    }

    /**
     * Visits the filter comparator and adds it to the persistence XML representation.
     * @param filterComparator the filter comparator to visit
     * @throws net.project.util.VisitException if there is a problem constructing the XML
     */
    public void visitFilterComparator(FilterComparator filterComparator) throws VisitException {

        try {
            doc.startElement("FilterComparator");
            doc.addElement("ID", filterComparator.getID());
            doc.endElement();

        } catch (XMLDocumentException e) {
            throw new VisitException(e);

        }
    }

}
