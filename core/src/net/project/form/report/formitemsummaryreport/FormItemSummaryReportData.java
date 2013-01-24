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
package net.project.form.report.formitemsummaryreport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.project.base.property.PropertyProvider;
import net.project.form.Form;
import net.project.form.FormField;
import net.project.form.report.FormFieldFilter;
import net.project.persistence.PersistenceException;
import net.project.report.SummaryDetailReportData;
import net.project.security.SessionManager;
import net.project.util.Validator;

/**
 * Class to provide the data required to crate the Form Item Summary Report.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class FormItemSummaryReportData extends SummaryDetailReportData {
    /** ID of the Form where the field exists that we are going to report on. */
    private String formID;
    /** ID of the field that we are going to report on. */
    private String fieldID;
    /** Results of a call to the {@link #load } method. */
    private SummarizedFieldData summaryData = new SummarizedFieldData();
    /** A mapping between field ID and a FormField object. */
    private Map formFields = new HashMap();
    /** The form we are reporting on. */
    private Form form;
    /**
     * Token pointing to: "{0} parameter was not found.  This is a required
     * field."
     */
    private String MISSING_PARAMETER_ERROR_TOKEN = "prm.report.errors.missingparametererror.message";
    /**
     * Token pointing to: "{0} present is present, but is not a valid database
     * identifier."
     */
    private String INVALID_DB_IDENTIFIER_ERROR_TOKEN = "prm.report.errors.invaliddbidentifier.message";

    /**
     * Standard constructor -- creates a new FormItemSummaryReportData object.
     */
    public FormItemSummaryReportData() {
        repopulateFinderFilters();
    }

    /**
     * Populate the finder filters list with all of the appropriate filters.
     * During the lifetime of this object, it is likely that this method will
     * be called more than one because we won't have the id of the form yet when
     * this method is called.
     */
    private void repopulateFinderFilters() {
        try {
            //Clear out any filters that already exist
            filterList.clear();

            //Show all of the filters for a given form
            if (Validator.isValidDatabaseIdentifier(formID)) {
                form = new Form(SessionManager.getUser(), SessionManager.getUser().getCurrentSpace(), formID);
                form.load();
                List formFields = form.getFields();

                for (int i = 0; i < formFields.size(); i++) {
                    FormField formField = (FormField)formFields.get(i);
                    this.formFields.put(formField.getID(), formField);

                    if (!formField.isFilterable()) {
                        continue;
                    }

                    FormFieldFilter fff = new FormFieldFilter(String.valueOf(i*10),
                        formField.getElementName(), formField);
                    filterList.add(fff);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use Options | File Templates.
            throw new RuntimeException(e);
        }
    }

    /**
     * Populate this report data object with data from the database.
     *
     * @throws PersistenceException if there is a difficulty loading the data
     * from the database.
     */
    public void load() throws PersistenceException {
        FormItemSummaryReportFinder finder = new FormItemSummaryReportFinder();
        finder.addFinderFilterList(filterList);
        finder.addFinderSorterList(sorterList);

        List results = finder.findFieldSummary(getSpaceID(), getFormID(), getFieldID());

        if (results.size() > 0) {
            summaryData = (SummarizedFieldData)results.get(0);
        } else {
            summaryData = new SummarizedFieldData();
        }
    }

    /**
     * Clear out any data stored in this object and reset.
     */
    public void clear() {
        summaryData = null;
    }

    /**
     * Update the internal <code>FinderFilterList</code>, <code>FinderSorterList</code>,
     * and <code>FinderGroupingList</code> objects based on the results of a
     * html form post.
     *
     * @param request a <code>HttpServletRequest</code> object that will be used
     * to find the data needed to populate the <code>FinderFilterList</code>,
     * <code>FinderSorterList</code>, and <code>FinderGroupingList</code>
     * objects.
     */
    public void updateParametersFromRequest(HttpServletRequest request) {
    	HttpSession session = request.getSession();
        //Get the form ID field.
        String formID = request.getParameter("formID");
        if (Validator.isBlankOrNull(formID)) {
        	formID = (String) session.getAttribute("formID");
        	if(Validator.isBlankOrNull(formID)){
        		throw new IllegalArgumentException(PropertyProvider.get(MISSING_PARAMETER_ERROR_TOKEN, "formID"));
        	}
        }
        if (!Validator.isValidDatabaseIdentifier(formID)) {
            throw new IllegalArgumentException(PropertyProvider.get(INVALID_DB_IDENTIFIER_ERROR_TOKEN, "formID"));
        }
        
        session.setAttribute("formID", formID);
        this.formID = formID;

        //Get the form field ID field
        String formFieldID = request.getParameter("formFieldID");

        if (Validator.isBlankOrNull(formFieldID)) {
        	formFieldID = (String) session.getAttribute("formFieldID");
        	if (Validator.isBlankOrNull(formFieldID)) {
        		throw new IllegalArgumentException(PropertyProvider.get(MISSING_PARAMETER_ERROR_TOKEN, "formFieldID"));
        	}
        }

        if (!Validator.isValidDatabaseIdentifier(formFieldID)) {
            throw new IllegalArgumentException(PropertyProvider.get(INVALID_DB_IDENTIFIER_ERROR_TOKEN, "formFieldID"));
        }
        
        session.setAttribute("formFieldID", formFieldID);
        this.fieldID = formFieldID;

        //If there is a new form id, we need to repopulate to get the
        //proper filters for this form.
        repopulateFinderFilters();

        super.updateParametersFromRequest(request);
    }

    /**
     * Get the ID of the form which contains the field we are reporting on.
     *
     * @return a <code>String</code> value containing the primary key of the
     * form whose field we are reporting on.
     */
    public String getFormID() {
        return formID;
    }

    /**
     * Set the ID of the form which contains the field we are reporting on.
     *
     * @param formID a <code>String</code> value containing the primary key of
     * the form which contains the field we are reporting on.
     */
    public void setFormID(String formID) {
        this.formID = formID;
    }

    /**
     * Get the primary key of the field that we are reporting on.
     *
     * @return a <code>String</code> value containing the primary key of the
     * field we are reporting on.
     */
    public String getFieldID() {
        return fieldID;
    }

    /**
     * Set the primary key of the field that we are reporting on.
     *
     * @param fieldID a <code>String</code> value containing the primary key of
     * the field we are reporting on.
     */
    public void setFieldID(String fieldID) {
        this.fieldID = fieldID;
    }

    /**
     * Get the summary data that this object is designed to produce.  This
     * mostly consists of a Map of each field to the number of the times that
     * that field appears in the query results.
     *
     * @return a <code>SummarizedFieldData</code> object which contains the
     * information needed to create the chart and the summary data table of the
     * form item summary report.
     */
    public SummarizedFieldData getSummaryData() {
        return summaryData;
    }

    /**
     * Get the form field that we are reporting on.
     *
     * @return a <code>FormField</code> corresponding to the form field ID that
     * we are reporting on.
     */
    public FormField getFormField() {
        return (FormField)formFields.get(fieldID);
    }

    /**
     * Get the form object that contains the field we are reporting on.
     *
     * @return a <code>Form</code> object which contains the field we are
     * reporting on.
     */
    public Form getForm() {
        return form;
    }
}
