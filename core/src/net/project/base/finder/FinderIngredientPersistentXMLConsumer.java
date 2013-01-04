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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import net.project.base.money.MoneyFilter;
import net.project.portfolio.view.PersonalPortfolioFinderIngredients;
import net.project.portfolio.view.ProjectVisibilityFilter;
import net.project.portfolio.view.SelectedProjectsFilter;
import net.project.project.ProjectVisibility;
import net.project.util.VisitException;
import net.project.xml.XMLException;
import net.project.xml.XMLUtils;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * Provides visit methods for consuming FinderIngredients persistence XML.
 * The XML that is consumed is produced by {@link FinderIngredientPersistentXMLProducer}.
 * <p>
 * Typical usage of this class is from inside {@link FinderIngredients}:
 * <pre><code>
 *   ...
 *   String xml = loadXML();
 *   FinderIngredientPersistenceXMLConsumer consumer = new FinderIngredientPersistentXMLConsumer(xml);
 *   accept(consumer);
 *   ...
 * </code></pre>
 * </p>
 * @author Tim Morrow
 * @since Version 7.4
 */
class FinderIngredientPersistentXMLConsumer extends EmptyFinderIngredientVisitor {

    /**
     * The current stack of elements being processed.
     */
    private Stack elementStack = new java.util.Stack();

    /**
     * Creates a new Consumer of persistent XML for the specified XML content.
     * The XML is not consumed at this time - it is simply parsed using a
     * SAX parser and the root element is made available on the element stack.
     *
     * @param xmlContent the XML to consume
     * @throws XMLException if there is a problem parsing the XML
     */
    FinderIngredientPersistentXMLConsumer(String xmlContent) throws XMLException {

        try {
            // Build the document
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new java.io.StringReader(xmlContent));
            this.elementStack.push(doc.getRootElement());

        } catch (org.jdom.JDOMException e) {
            // Problem building document from xml
            throw new XMLException("Error parsing FinderIngredients Persistent XML: " + e, e);

        } catch (IOException ioe) {
            // Problem building document from xml
            throw new XMLException("Error parsing FinderIngredients Persistent XML: " + ioe, ioe);
        }

    }

    /**
     * Visits the finder ingredients to consume XML.
     * This populates the specified FinderIngredients based on the current XML.
     * Visits the column list, filter list, grouping list and sorter list.
     * @param finderIngredients the finder ingredients to visit
     * @throws net.project.util.VisitException if the current element is not named <code>FinderIngredients</code>
     * or there is a problem visiting a component of the finder ingredients
     */
    public void visitFinderIngredients(FinderIngredients finderIngredients) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "FinderIngredients");


        // Process the child elements
        for (Iterator it = element.getChildren().iterator(); it.hasNext(); ) {
            Element nextElement = (Element) it.next();

            // Add the element to the stack to that the next visit
            // method can get it
            this.elementStack.push(nextElement);

            if (nextElement.getName().equals("FinderColumnList")) {
                finderIngredients.getFinderColumnList().accept(this);

            } else if (nextElement.getName().equals("FinderFilterList")) {
                finderIngredients.getFinderFilterList().accept(this);

            } else if (nextElement.getName().equals("FinderGroupingList")) {
                finderIngredients.getFinderGroupingList().accept(this);

            } else if (nextElement.getName().equals("FinderSorterList")) {
                finderIngredients.getFinderSorterList().accept(this);

            } else if (nextElement.getName().equals("MetaColumnList") || finderIngredients instanceof PersonalPortfolioFinderIngredients) {
                ((PersonalPortfolioFinderIngredients) finderIngredients).getMetaColumnList().loadFromXML(nextElement);

            } else {
                // Unknown element name
                // We're going to ignore it
            }

            this.elementStack.pop();

        }

    }

    //
    // Filter XML
    //

    /**
     * Visits the finder filter list and each filter in the list.
     * Assumes the current element on the stack is a <code>FinderFilterList</code>.
     * @param filterList the <code>FinderFilterList</code> to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML or
     * there is a problem visiting a filter
     */
    public void visitFinderFilterList(FinderFilterList filterList) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "FinderFilterList");

        // Grab FinderFilter elements
        for (Iterator it = element.getChild("FinderFilters").getChildren().iterator(); it.hasNext(); ) {
            Element nextElement = (Element) it.next();

            // Locate the filter matching this element from the list of filters
            // Filters are looked up by ID
            String elementID = nextElement.getChildTextTrim("ID");
            FinderFilter finderFilter = filterList.matchingID(elementID);
            if (finderFilter == null) {
                throw new VisitException("Unable to find FinderFilter for element ID " + elementID);
            }

            // Add the element to the stack to that the next visit
            // method can get it then visit the filter
            this.elementStack.push(nextElement);
            finderFilter.accept(this);
            this.elementStack.pop();

        }

    }

    /**
     * Visits the date filter.
     * Assumes the current element on the stack is a <code>DateFilter</code>.
     * @param filter the date filter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitDateFilter(DateFilter filter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "DateFilter");

        filter.setEmptyOptionSelected(XMLUtils.parseBoolean(element.getChildTextTrim("IsEmptyOptionSelected")));

        // Set the values
        filter.setDateRangeStart(XMLUtils.parseISODateTime(element.getChildTextTrim("DateRangeStart")));
        filter.setDateRangeFinish(XMLUtils.parseISODateTime(element.getChildTextTrim("DateRangeFinish")));

        filter.setSelected(true);
    }

    /**
     * Visits the number filter.
     * Assumes the current element on the stack is a <code>NumberFilter</code>.
     * @param filter the number filter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitNumberFilter(NumberFilter filter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "NumberFilter");

        String isEmptyOptionSelectedString = element.getChildTextTrim("IsEmptyOptionSelected");
        if (isEmptyOptionSelectedString != null && isEmptyOptionSelectedString.trim().length() > 0) {
            filter.setEmptyOptionSelected(XMLUtils.parseBoolean(isEmptyOptionSelectedString));
        }

        // Set the comparator
        Element filterComparatorElement = element.getChild("FilterComparator");
        if (filterComparatorElement != null) {
            NumberComparator comparator = NumberComparator.getForID(filterComparatorElement.getChildTextTrim("ID"));
            if (comparator != null) {
                filter.setComparator(comparator);
            }
        }

        // Set the number
        String numberString = element.getChildTextTrim("Number");
        if (numberString != null && numberString.trim().length() > 0) {
            filter.setNumber(XMLUtils.parseNumber(numberString));
        }

        if (filter.isEmptyOptionSelected() || (filter.getComparator() != null && filter.getNumber() != null)) {
            filter.setSelected(true);
        }
    }

    /**
     * Visits the text filter.
     * Assumes the current element on the stack is a <code>TextFilter</code>.
     * @param filter the text filter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitTextFilter(TextFilter filter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "TextFilter");

        filter.setEmptyOptionSelected(XMLUtils.parseBoolean(element.getChildTextTrim("IsEmptyOptionSelected")));

        // Set the comparator
        Element filterComparatorElement = element.getChild("FilterComparator");
        TextComparator comparator = TextComparator.getForID(filterComparatorElement.getChildTextTrim("ID"));
        filter.setComparator(comparator);

        // Set the value
        filter.setValue(element.getChildTextTrim("Value"));

        filter.setSelected(true);
    }

    /**
     * Visits the DomainFilter and populates it from the current element.
     * Assumes the current element on the stack is a <code>DomainFilter</code>.
     * @param filter the DomainFilter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitDomainFilter(DomainFilter filter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "DomainFilter");

        filter.setEmptyOptionSelected(XMLUtils.parseBoolean(element.getChildTextTrim("IsEmptyOptionSelected")));

        // Set the comparator
        Element filterComparatorElement = element.getChild("FilterComparator");
        DomainComparator comparator = DomainComparator.getForID(filterComparatorElement.getChildTextTrim("ID"));
        filter.setComparator(comparator);

        // Set the values
        List valueList = new ArrayList();
        for (Iterator it = element.getChild("SelectedValues").getChildren("Value").iterator(); it.hasNext(); ) {
            Element nextValueElement = (Element) it.next();
            valueList.add(nextValueElement.getTextTrim());
        }
        filter.setSelectedDomainOptions((String[]) valueList.toArray(new String[]{}));

        filter.setSelected(true);
    }

    /**
     * Visits the SelectedProjectsFilter and populates it from the current element.
     * Assumes the current element on the stack is a <code>SelectedProjectsFilter</code>.
     * @param filter the SelectedProjectsFilter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitSelectedProjectsFilter(SelectedProjectsFilter filter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "SelectedProjectsFilter");

        filter.setIgnoreOtherFilters(XMLUtils.parseBoolean(element.getChildTextTrim("IsIgnoreOtherFilters")));

        // Set the values
        List selectedProjectIDList = new ArrayList();
        for (Iterator it = element.getChild("SelectedProjectIDs").getChildren("ProjectID").iterator(); it.hasNext(); ) {
            Element nextValueElement = (Element) it.next();
            selectedProjectIDList.add(nextValueElement.getTextTrim());
        }
        filter.setSelectedProjectSpaceIDs((String[]) selectedProjectIDList.toArray(new String[]{}));

        filter.setSelected(true);
    }

    /**
     * Visits the ProjectVisibility filter.
     * Assumes the current element on the stack is a <code>ProjectVisibilityFilter</code>.
     * @param filter the ProjectVisibility filter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitProjectVisibilityFilter(ProjectVisibilityFilter filter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "ProjectVisibilityFilter");

        // Set the value
        filter.setProjectVisibility(ProjectVisibility.findByID(element.getChildTextTrim("ProjectVisibilityID")));

        filter.setSelected(true);
    }

    /**
     * Visits the money filter.
     * Assumes the current element on the stack is a <code>MoneyFilter</code>.
     * @param filter the moeny filter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitMoneyFilter(MoneyFilter filter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "MoneyFilter");

        // Process the value part
        // Note: The filter for this part may be empty if it was not selected
        Element valueElement = element.getChild("Value").getChild("NumberFilter");
        if (valueElement != null) {
            this.elementStack.push(valueElement);
            filter.getValueFilter().accept(this);
            this.elementStack.pop();

        }

        // Process the currency part
        // Note: The filter for this part may be empty if it was not selected
        Element currencyElement = element.getChild("Currency").getChild("DomainFilter");
        if (currencyElement != null) {
            this.elementStack.push(currencyElement);
            filter.getCurrencyFilter().accept(this);
            this.elementStack.pop();
        }

    }

    //
    // Column XML
    //

    /**
     * Visits the finder column list and each column in the list.
     * Assumes the current element on the stack is a <code>FinderColumnList</code>.
     * @param columnList the <code>FinderColumnList</code> to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitFinderColumnList(FinderColumnList columnList) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "FinderColumnList");

        // Grab FinderColumn elements
        for (Iterator it = element.getChild("FinderColumns").getChildren().iterator(); it.hasNext(); ) {
            Element nextElement = (Element) it.next();

            // Locate the column matching this element from the list of columns
            // Columns are looked up by ID
            String elementID = nextElement.getChildTextTrim("ID");
            FinderColumn finderColumn = columnList.getForID(elementID);
            if (finderColumn == null) {
                throw new VisitException("Unable to find FinderColumn for element ID " + elementID);
            }

            // Add the element to the stack to that the next visit
            // method can get it, then visit the column
            this.elementStack.push(nextElement);
            finderColumn.accept(this);
            this.elementStack.pop();

        }

    }

    //
    // Grouping XML
    //

    /**
     * Visits the finder grouping list and each grouping in the list.
     * Assumes the current element on the stack is a <code>FinderGroupingList</code>.
     * @param groupingList the <code>FinderGroupingList</code> to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitFinderGroupingList(FinderGroupingList groupingList) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "FinderGroupingList");

        // Grab FinderGrouping elements
        for (Iterator it = element.getChild("FinderGroupings").getChildren().iterator(); it.hasNext(); ) {
            Element nextElement = (Element) it.next();

            // Locate the grouping matching this element from the list of groupings
            // Groupings are looked up by ID
            String elementID = nextElement.getChildTextTrim("ID");
            FinderGrouping finderGrouping = groupingList.getForID(elementID);
            if (finderGrouping == null) {
                throw new VisitException("Unable to find FinderGrouping for element ID " + elementID);
            }

            // Add the element to the stack to that the next visit
            // method can get it, then visit the grouping
            this.elementStack.push(nextElement);
            finderGrouping.accept(this);
            this.elementStack.pop();

        }


    }

    //
    // Sorter XML
    //

    /**
     * Visits the finder sorter list and each sorter in the list.
     * Assumes the current element on the stack is a <code>FinderSorterList</code>.
     * @param sorterList the <code>FinderSorterList</code> to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitFinderSorterList(FinderSorterList sorterList) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "FinderSorterList");

        // Grab FinderSorter elements
        for (Iterator it = element.getChild("FinderSorters").getChildren().iterator(); it.hasNext(); ) {
            Element nextElement = (Element) it.next();

            // Locate the sorter matching this element from the list of sorterss
            // Sorters are looked up by ID
            String elementID = nextElement.getChildTextTrim("ID");
            FinderSorter finderSorter = sorterList.getForID(elementID);
            if (finderSorter == null) {
                throw new VisitException("Unable to find FinderSorter for element ID " + elementID);
            }

            // Add the element to the stack to that the next visit
            // method can get it, then visit the sorter
            this.elementStack.push(nextElement);
            finderSorter.accept(this);
            this.elementStack.pop();

        }

    }

    /**
     * Visits the FinderSorter and consumes the current Element in the stack.
     * Assumes the current element on the stack is a <code>FinderSorter</code>.
     * @param sorter the sorter to visit
     * @throws net.project.util.VisitException if there is a problem consuming the XML
     */
    public void visitFinderSorter(FinderSorter sorter) throws VisitException {

        Element element = (Element) this.elementStack.peek();
        assertSameElementName(element, "FinderSorter");

        // Grab the selected column element then select that column based on the ID
        Element selectedColumnElement = element.getChild("SelectedColumn");
        if (selectedColumnElement != null) {
            sorter.setSelectedColumnByID(selectedColumnElement.getChildTextTrim("ID"));
        }

        // Set the isDescending property
        sorter.setDescending(XMLUtils.parseBoolean(element.getChildTextTrim("IsDescending")));

        sorter.setSelected(true);
    }

    /**
     * Asserts that the specified element's name is the same as the specified name.
     * @param element the element to compare to the specified name
     * @param name the name to compare to the element's name
     * @throws NullPointerException if the element is null
     * @throws net.project.util.VisitException if the element's name does not match the specified name
     */
    private void assertSameElementName(Element element, String name) throws VisitException {

        if (element == null) {
            throw new NullPointerException("Element is required");
        }

        if (!element.getName().equals(name)) {
            throw new VisitException("Unexpected element name " + element.getName());
        }

    }
}
