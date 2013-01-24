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
|   $Revision: 20658 $
|       $Date: 2010-04-01 01:53:03 -0300 (jue, 01 abr 2010) $
|     $Author: dpatil $
|
+----------------------------------------------------------------------*/
package net.project.report;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.base.finder.FinderFilterList;
import net.project.base.finder.FinderGroupingList;
import net.project.base.finder.FinderSorterList;
import net.project.persistence.IXMLPersistence;

import com.lowagie.text.Document;

/**
 * Interface that defines how the reporting servlet interfaces with individual
 * reports.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public interface IReport extends IXMLPersistence {
    /**
     * Return the human readable name of this report.
     *
     * @return a <code>String</code> value containing the human-readable form
     * of the report name.
     */
    public String getReportName();

    /**
     * Write the report to a PDF document encapsulated by the document object.
     *
     * @param document a <code>com.lowagie.text.Document</code> object that should
     * construct the PDF.
     * @throws ReportException if any exception occurs while trying to construct
     * the report.  Exceptions other than report exception will be wrapped in a
     * report exception and rethrown.
     */
    public void writeReport(Document document) throws ReportException;

    /**
     * Populate internal data structures based on the results of a form
     * submission.
     *
     * @param request the source of the data for this object.
     */
    public void populateParameters(HttpServletRequest request) throws MissingReportDataException;

    /**
     * Get the list of filters that can be applied to this report.
     *
     * @return a <code>FinderFilterList</code> object which contains all of the
     * possible filters that can be applied to this report.
     */
    public abstract FinderFilterList getFilterList();

    /**
     * Get the list of all {@link net.project.base.finder.FinderSorter} objects
     * that can be applied to this report.
     *
     * @return a <code>FinderSorterList</code> object which contains all of the
     * possible <code>FinderSorter</code> objects that can be applied to this
     * Report.
     */
    public abstract FinderSorterList getSorterList();

    /**
     * Get the list of all {@link net.project.base.finder.FinderGrouping}
     * objects that can be applied to this report.
     *
     * @return a <code>FinderGroupingList</code> object which contains all of the
     * possible <code>FinderGrouping</code> objects that can be applied to this
     * report.
     */
    public abstract FinderGroupingList getGroupingList();

    /**
     * Get a list of fields that the "populateRequest" method expects to see in
     * a request, but were prepopulated prior to showing the user the parameters
     * page.  This is more than a little bit of a hack.
     *
     * @return a <code>Map</code> of name value pairs.
     */
    public abstract Map getPrepopulatedParameters();

    ReportScope getScope();

    void setScope(ReportScope scope);
    
    public ReportData getData();
}

