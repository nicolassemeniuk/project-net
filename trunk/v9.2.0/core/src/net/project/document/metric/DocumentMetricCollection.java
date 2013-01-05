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
|   $RCSfile$
|   $Revision: 18397 $
|   $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|   $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.document.metric;

public class DocumentMetricCollection extends net.project.base.metric.MetricCollection {

    public DocumentMetricCollection() {
        super();
        setName ("Document Metrics");
    }

    /**
     * Register all metrics in the net.project.document.metric package.
     * Registration is done by simply adding a new instance of the metric to the collection
     */
    protected void registerMetrics() {

        put (DocumentMetrics.SYSTEM_DOCUMENT_STORAGE_METRIC, new SystemDocumentStorageMetric());
    }
}
