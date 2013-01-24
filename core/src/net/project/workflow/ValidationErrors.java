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
package net.project.workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import net.project.base.property.PropertyProvider;
import net.project.persistence.IXMLPersistence;
import net.project.xml.XMLFormatter;
import net.project.xml.XMLUtils;

/**
 * Validation Errors
 */
public class ValidationErrors implements java.io.Serializable, IXMLPersistence {

    /**
     * An error table entry
     */
    private class Entry implements java.io.Serializable, IXMLPersistence {
        String fieldID = null;
        String fieldName = null;
        String errorText = null;

        public String getXML() {
            return IXMLPersistence.XML_VERSION + getXMLBody();
        }

        public String getXMLBody() {
            StringBuffer xml = new StringBuffer();
            xml.append("<error>");
            xml.append("<field_id>" + quote(fieldID) + "</field_id>");
            xml.append("<field_name>" + quote(fieldName) + "</field_name>");
            xml.append("<error_text>" + quote(errorText) + "</error_text>");
            return xml.toString();
        }
    }

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
     * Add a validation error
     * @param fieldID the field id (unique identifier of field)
     * @param fieldName the descriptive name of the field
     * @param errorText the text of the error
     */
    public void put(String fieldID, String fieldName, String errorText) {
        Entry entry = (Entry)errorMap.get(fieldID);
        if (entry == null) {
            entry = new Entry();
            errorOrder.add(fieldID);
            errorMap.put(fieldID, entry);
        }
        entry.fieldID = fieldID;
        entry.fieldName = fieldName;
        entry.errorText = errorText;
    }

    /**
     * Return the Presentation for an error in the form of an HTML table ROW
     * for the specified field.  If the field has no error this method returns
     * the empty string ("") such that it may always be called and will not
     * affect the document into which it is inserted if there are no errors.
     * e.g.<br><CODE>
     * &lt;tr>&lt;td><br>
     * &lt;span class="fieldWithError">The error text&lt;/span>
     * &lt;/td>&lt;/tr><BR></CODE>
     * @param fieldID the field to get the errors for (e.g. "workflowID")
     * @return the HTML error string
     */
    public String getErrorRow(String fieldID) {
        String prefix = "<tr><td>";
        String suffix = "</td></tr>";
        return getErrorHTML(fieldID, prefix, suffix);
    }

    /**
     * Return the presentation for errors in the form of an HTML SPAN tag
     * for the specified field.  If the field has no error this method returns
     * the empty string ("") such that it may always be called and will not
     * affect the document into which it is inserted if there are no errors.
     * e.g.<BR><code>
     * &lt;span class="fieldWithError">The error text&lt;/span><BR>
     * </CODE>
     * @param fieldID the field to get the errors for (e.g. "workflowID")
     * @return the HTML error string
     */
    public String getError(String fieldID) {
        return getErrorHTML(fieldID, "", "");
    }

    /**
     * This returns "label" flagged as being an error, based on the presence
     * of an error for "fieldID".  <br>
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
    public String flagError(String fieldID, String label) {
        String errorText = null;
        Entry entry = (Entry)errorMap.get(fieldID);
        if (entry != null) {
            errorText = "<span class=\"fieldWithError\"> " +
                label + "</span>";
        } else {
            errorText = label;
        }
        return errorText;
    }

    /**
     * Get the error text (with HTML tags included) for the specified field
     * If the field has no error it returns an empty string ("")
     * @param fieldID the field to get the error for (e.g. "workflowID")
     * @param prefix the additional HTML to prefix
     * @param suffix the additional HTML to suffix
     * @return the HTML error string
     */
    private String getErrorHTML(String fieldID, String prefix, String suffix) {
        String errorText = null;
        Entry entry = (Entry)errorMap.get(fieldID);
        if (entry != null) {
            String fieldName = entry.fieldName;
            String error = entry.errorText;
            if (fieldName == null) {
                fieldName = "";
            } else {
                fieldName += ": ";
            }
            if (error == null) {
                error = "";
            }
            prefix = prefix + "<span class=\"fieldWithError\"> ";
            suffix = "</span>" + suffix;
            errorText = prefix + fieldName + error + suffix;
        } else {
            errorText = "";
        }
        return errorText;
    }

    /**
     * Return the default presentation for all errors
     * @return the html string
     */
    public String getAllErrorsDefaultPresentation() {
        StringBuffer html = new StringBuffer();
        Entry entry = null;
        String fieldName = null;
        String error = null;

        if (hasErrors()) {
            html.append("<table>\n");
            html.append("<tr class=\"fieldWithError\"> " +
                "<td colspan=\"2\">" + PropertyProvider.get("prm.workflow.haserrors.generic.validation.message") + "</td></tr>\n");
            Iterator it = errorOrder.iterator();
            while (it.hasNext()) {
                entry = (Entry)errorMap.get(it.next());
                fieldName = entry.fieldName;
                error = entry.errorText;
                if (fieldName == null) {
                    fieldName = "";
                } else {
                    fieldName += " - ";
                }
                if (error == null) {
                    error = "";
                }
                html.append("<tr class=\"fieldWithError\">");
                html.append("<td>" + fieldName + "</td>");
                html.append("<td>" + error + "</td>");
                html.append("</tr>\n");
            }
            html.append("<tr><td>&nbsp;</td></tr>");
            html.append("</table>\n");
        }

        return html.toString();
    }

    /**
     * Return the presentation for removal errors
     * @return the html string
     */
    public String getAllErrorsRemovePresentation() {
        StringBuffer html = new StringBuffer();
        Entry entry = null;
        String fieldName = null;
        String error = null;

        if (hasErrors()) {
            html.append("<table>\n");
            html.append("<tr class=\"fieldWithError\"> " +
                "<td colspan=\"2\">" + PropertyProvider.get("prm.workflow.haserrors.remove.validation.message") + "</td></tr>\n");
            Iterator it = errorOrder.iterator();
            while (it.hasNext()) {
                entry = (Entry)errorMap.get(it.next());
                fieldName = entry.fieldName;
                error = entry.errorText;
                if (fieldName == null) {
                    fieldName = "";
                } else {
                    fieldName += " - ";
                }
                if (error == null) {
                    error = "";
                }
                html.append("<tr class=\"fieldWithError\">");
                html.append("<td>" + fieldName + "</td>");
                html.append("<td>" + error + "</td>");
                html.append("</tr>\n");
            }
            html.append("<tr><td>&nbsp;</td></tr>");
            html.append("</table>\n");
        }

        return html.toString();
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
            entry = (Entry)errorMap.get(it.next());
            xml.append(entry.getXMLBody());
        }
        xml.append("</error_list>");

        return xml.toString();
    }

    /**
     * Set the stylesheet to use for the menu
     * @param stylesheetFileName the stylesheet path
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

}
