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
package net.project.business;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.project.persistence.IXMLPersistence;
import net.project.security.SessionManager;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

/**
  * Validation Errors
  */
public class ValidationErrors implements java.io.Serializable, IXMLPersistence {

    /** The error table */
    private HashMap errorMap = null;
    /** ArrayList of maintaining order of added elements */
    private ArrayList errorOrder = null;

    private XMLFormatter xmlFormatter;

    /**
      * Creates new validation errors object
      */
    public ValidationErrors() {
        errorMap = new HashMap();
        errorOrder = new ArrayList();
    }

    /**
      * Clears all existing errors
      */
    public void clearErrors() {
        errorMap = new HashMap();
        errorOrder = new ArrayList();
    }
    
    /**
      * Indicate whether there are any errors in the table
      * @return true if there are errors, false otherwise
      */
    public boolean hasErrors() {
        return (errorMap.size() > 0 ? true : false);
    }

    /**
      * Add a validation error with a specific value that caused the error.
      * @param fieldID the field id (unique identifier of field)
      * @param errorValue the value in error (optional)
      * @param errorText the text of the error; this is actually a message
      * pattern that should contain a <code>{0}</code> place holder for the
      * errorValue
      */
    public void put(String fieldID, String errorValue, String errorText) {
        Entry entry = (Entry) errorMap.get(fieldID);
        if (entry == null) {
            entry = new Entry();
            errorOrder.add(fieldID);
            errorMap.put(fieldID, entry);
        }
        entry.fieldID = fieldID;
        entry.errorValue = errorValue;
        entry.errorText = errorText;
    }

    /**
     * Adds a general validation error with no error value.
     * @param fieldID the field id (unique identifier of field)
     * @param errorText the text of the error; since no value is specified
     * the error message does not require a <code>{0}</code> place holder
     */
    public void put(String fieldID, String errorText) {
        put(fieldID, null, errorText);
    }

    /**
      * This returns "label" flagged as being an error, based on the presence
      * of an error for "fieldID".<br>
      * E.g.<br>
      * <code>flagError("workflow_name", "Workflow Name:");</code><br>
      * would return<br>
      * <code>&lt;span class="fieldWithError">Workflow Name:&lt;/span></code><br>
      * if there was an error for workflow_name, or simply<br>
      * <code>Workflow Name:</code><br>
      * if not.
      * @param fieldID the id of the field to get the error for
      * @param label the label or text to flag in the error style
      * @return the label flagged as an error
      */
    public String getFlagErrorHTML(String fieldID, String label) {
        String errorText = null;
        Entry entry = (Entry) errorMap.get(fieldID);
        
        if (entry != null) {
            errorText = "<span class=\"fieldWithError\"> " + label + "</span>";
        
        } else {
            errorText = label;
        
        }
        
        return errorText;
    }

    /**
     * Returns the current error message for specified field using specified pattern.
     * If there is not error associated with that field, an empty string is returned.
     * @param fieldID the id of the field for which to get a message
     * @return the formatted error message or empty string
     */
    public String getFormattedErrorMessage(String fieldID) {
        String formattedErrorMessage = null;
        Entry entry = (Entry) errorMap.get(fieldID);
        
        if (entry != null) {
            // Construct a new message format and set the locale to the user's locale
            MessageFormat formatter = new MessageFormat("");
            formatter.setLocale(SessionManager.getUser().getLocale());
            formatter.applyPattern(entry.errorText);

            formattedErrorMessage = formatter.format(new Object[]{entry.errorValue});
            
        
        } else {
            formattedErrorMessage = "";
        
        }
        
        return formattedErrorMessage;
    }

    /**
     * Returns the current error message for the specified field formatted
     * for html.
     * @param fieldID id of field for which to get error message
     * @return the HTML error message which is a <code>&lt;span...&gt;</code>
     * tag containing the error message using the appropriate CSS class; or
     * the empty string if the specified field has no error
     */
    public String getErrorMessageHTML(String fieldID) {
        String errorMessage = null;

        if (hasErrorForField(fieldID)) {
            errorMessage = "<span class=\"fieldWithError\">" + getFormattedErrorMessage(fieldID) + "</span>";
        } else {
            errorMessage = "";
        }

        return errorMessage;
    }
    
    private boolean hasErrorForField(String fieldID) {
        return (errorMap.get(fieldID) != null);
    }

    public String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    public String getXMLBody() {
        Entry entry = null;
        StringBuffer xml = new StringBuffer();
        Iterator it = errorOrder.iterator();

        xml.append("<error_list>");
        while (it.hasNext()) {
            entry = (Entry) errorMap.get(it.next());
            xml.append(entry.getXMLBody());
        }
        xml.append("</error_list>");

        return xml.toString();
    }

    /**
      * Set the stylesheet to use for the menu
      * @param stylesheet the stylesheet path
      */
    public void setStylesheet(String stylesheetFileName) {
        // set the XML formatter stylesheet
        xmlFormatter.setStylesheet(stylesheetFileName);
    }

    /**
      * Get propreties presentation based on the
      * current stylesheet and envelope properties XML
      * @return properties presentation
      */
    public String getPresentation() {
        return xmlFormatter.getPresentation(getXML());
    }

    /**
      * quotes string to HTML, turns null strings into empty strings
      * @param s the string
      * @return the quotes string
      */
    private String quote(String s) {
        return XMLUtils.escape(s);
    }

    /**
      * An error table entry
      */
    private class Entry implements java.io.Serializable, IXMLPersistence {
        String fieldID = null;
        String errorValue = null;
        String errorText = null;

        public String getXML() {
            return IXMLPersistence.XML_VERSION + getXMLBody();
        }
        public String getXMLBody() {
            StringBuffer xml = new StringBuffer();
            xml.append("<error>");
            xml.append("<field_id>" + quote(fieldID) + "</field_id>");
            xml.append("<error_value>" + quote(errorValue) + "</error_value>");
            xml.append("<error_text>" + quote(errorText) + "</error_text>");
            return xml.toString();
        }
    }

    public String getAllErrorMessages() {
        return this.getAllErrorMessagesHTML();
    }


    /**
     * Return all error message HTML as an HTML table.
     * @return the HTML table containing one error message per row
     */
    public String getAllErrorMessagesHTML() {
        String errorMessage = null;
        StringBuffer result = new StringBuffer();
        
        // Return nothing if there are no errors
        if (!hasErrors()) {
            return "";
        }

        // Start table
        result.append("<table width=\"100%\" border=\"0\" class=\"tableContent\">");
        
        // Add each error in order, one per table row
        Iterator it = this.errorOrder.iterator();
        while (it.hasNext()) {
            errorMessage = getFormattedErrorMessage((String) it.next());
            
            result.append("<tr><td class=\"fieldWithError\">");
            result.append(errorMessage);
            result.append("</td></tr>");
        }

        // End table
        result.append("</table>");

        return result.toString();
    }


}


