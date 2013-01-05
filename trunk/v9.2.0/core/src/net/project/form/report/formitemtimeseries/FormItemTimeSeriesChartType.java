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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.form.report.formitemtimeseries;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.chart.IChart;

/**
 * A typed enumeration of FormItemTimeSeriesChartType classes.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemTimeSeriesChartType {
    /** This list contains all of the possible types for this typed enumeration. */
    private static List types = new ArrayList();
    /** Type to create a "Stacked Bar Chart". */
    public static final FormItemTimeSeriesChartType STACKED_BAR_CHART = new FormItemTimeSeriesChartType("10", "prm.global.chart.renderedcharttype.stackedbarchart.description", FormItemTimeSeriesStackedBarChart.class);
    /** Type to create a "Line Graph". */
    public static final FormItemTimeSeriesChartType LINE_GRAPH = new FormItemTimeSeriesChartType("20", "prm.global.chart.renderedcharttype.linegraph.description", FormItemTimeSeriesLineChart.class);
    /**
     * Default type of rendered chart to create.  Currently,
     * {@link net.project.form.report.formitemtimeseries.FormItemTimeSeriesChartType#STACKED_BAR_CHART}.
     */
    public static final FormItemTimeSeriesChartType DEFAULT = STACKED_BAR_CHART;

    /**
     * Get the FormItemTimeSeriesChartType that corresponds to a given ID.
     *
     * @param id a <code>String</code> value which is the primary key of a
     * <code>FormItemTimeSeriesChartType</code> we want to find.
     * @return a <code>FormItemTimeSeriesChartType</code> corresponding to the supplied ID, or
     * the DEFAULT <code>FormItemTimeSeriesChartType</code> if one cannot be found.
     */
    public static FormItemTimeSeriesChartType getForID(String id) {
        FormItemTimeSeriesChartType toReturn = DEFAULT;

        for (Iterator it = types.iterator(); it.hasNext();) {
            FormItemTimeSeriesChartType type = (FormItemTimeSeriesChartType)it.next();
            if (type.getID().equals(id)) {
                toReturn = type;
                break;
            }
        }

        return toReturn;
    }

    //**************************************************************************
    // Implementation code
    //**************************************************************************
    /** Unique identifier for this FormItemTimeSeriesChartType. */
    private String id;
    /** Token used to find a human-readable name for this rendered chart type. */
    private String displayToken;
    /** Class that implements this type of chart. */
    private Class implementingClass;

    /**
     * Private constructor which creates a new FormItemTimeSeriesChartType instance.
     *
     * @param id a <code>String</code> value which is a unique identifier for 
     * this typed enumeration.
     * @param displayToken a <code>String</code> value which contains a token to
     * look up the display name for this type.
     */
    private FormItemTimeSeriesChartType(String id, String displayToken, Class implementingClass) {
        this.id = id;
        this.displayToken = displayToken;
        this.implementingClass = implementingClass;
        types.add(this);
    }

    /**
     * Get the unique identifier for this type enumeration.
     *
     * @return a <code>String</code> value containing the unique id for this 
     * type.
     */
    public String getID() {
        return id;
    }

    /**
     * Get the appropriate implementation for this chart type.
     *
     * @return an instantiated version of the appropriate chart type. 
     */
    public IChart getImplementation() {
        IChart chartInstance = null;

        try {
            Constructor constructor = implementingClass.getConstructor(new Class[] {});
            chartInstance = (IChart)constructor.newInstance(new Object[] {});
        } catch (Exception e) {
            throw new PnetRuntimeException("Unable to create new instance of report.", e);
        }

        return chartInstance;
    }

    /**
     * Return a human-readable display name for this FormItemTimeSeriesChartType.
     *
     * @return a <code>String</code> containing a human-readable version of this
     * FormItemTimeSeriesChartType.
     */
    public String toString() {
        return PropertyProvider.get(displayToken);
    }
}
