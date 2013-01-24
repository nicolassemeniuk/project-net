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

 package net.project.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.project.persistence.IXMLPersistence;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.log4j.Logger;

/**
 * Provides a mechanism for displaying errors on a view.
 *
 * @author Matthew Flower
 */
public class ErrorReporter implements IXMLPersistence {
    private String overallError;
    private final List errorDescriptions = new ArrayList();
    private final List warnings = new ArrayList();
    private final Map parameters = new HashMap();
    private final Map attributes = new HashMap();

    /**
     * Creates an empty error reporter.
     */
    public ErrorReporter() {
    }

    /**
     * Indicates whether any errors were found.
     * Defined as this error reporting having any error descriptions.
     * @return true if there is at least one error description in
     * this error reporter; false otherwise
     */
    public boolean errorsFound() {
        return !errorDescriptions.isEmpty();
    }

    /**
     * Indicates if any warnings were found.
     *
     * @return a <code>boolean</code> indicating if warnings were found.
     */
    public boolean warningsFound() {
        return !warnings.isEmpty();
    }

    /**
     * Clears out all errors.
     */
    public void clear() {
        overallError = "";
        errorDescriptions.clear();
        warnings.clear();
        parameters.clear();
        attributes.clear();
    }

    /**
     * Returns the overall error.
     * @return the overall error, typically displayed before any
     * individual errors
     */
    public String getOverallError() {
        return overallError;
    }

    /**
     * Specifies an overall error.
     * @param overallError an error message
     */
    public void setOverallError(String overallError) {
        this.overallError = overallError;
    }

    /**
     * Saves all request attributes and parameters.
     * @param request the request from which to get attributes and parameters
     */
    public void populateFromRequest(HttpServletRequest request) {
        //Clear out the parameter list to make sure we aren't putting in stuff
        //that shouldn't be there.
        attributes.clear();
        parameters.clear();

        //Add all of the attributes to the internal data structure.
        Enumeration attrEnum = request.getAttributeNames();
        while (attrEnum.hasMoreElements()) {
            String attributeName = (String)attrEnum.nextElement();
            Object attributeValue = request.getAttribute(attributeName);
            attributes.put(attributeName, attributeValue);
        }

        //Add all of the parameters to the internal data structure
        Enumeration paramEnum = request.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            String paramName = (String)paramEnum.nextElement();
            String paramValue = request.getParameter(paramName);
            parameters.put(paramName, paramValue);
        }
    }

    /**
     * Returns an unmodifiable collection of error descriptions.
     * @return a collection where each element is an <code>ErrorDescription</code>
     */
    public java.util.Collection getErrorDescriptions() {
        return Collections.unmodifiableList(errorDescriptions);
    }

    /**
     * Adds an error with the specified error message.
     * @param errorDescription the error message to add; ignored if null or blank
     */
    public void addError(String errorDescription) {
        if (!Validator.isBlankOrNull(errorDescription)) {
            errorDescriptions.add(new ErrorDescription(errorDescription));
        }
    }

    /**
     * Adds an error description.
     * @param description the error description; ignored if null
     */
    public void addError(ErrorDescription description) {
        if (description!=null) {
            errorDescriptions.add(description);
        }
    }

    public void addWarning(String warningDescription) {
        warnings.add(warningDescription);
    }

    /**
     * Returns a map of request parameters captured by {@link #populateFromRequest}.
     * @return a map where each element is a <code>String</code> parameter name
     * and each value is a <code>String</code> parameter value
     */
    public Map getParameters() {
        return Collections.unmodifiableMap(parameters);
    }

    /**
     * Returns this object's XML representation, including the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXMLBody
     * @see IXMLPersistence#XML_VERSION
     */
    public String getXML() {
        String xml;
        try {
            xml = getXMLDocument().getXMLString();
        } catch (XMLDocumentException e) {
        	Logger.getLogger(ErrorReporter.class).debug("Unable to get XML due to an unexpected XMLDocumentError: "+e);
            xml = "";
        }
        return xml;
    }

    /**
     * Returns this object's XML representation, without the XML version tag.
     * @return XML representation of this object
     * @see IXMLPersistence#getXML
     */
    public String getXMLBody() {
        String xml;
        try {
            xml = getXMLDocument().getXMLBodyString();
        } catch (XMLDocumentException e) {
        	Logger.getLogger(ErrorReporter.class).debug("Unable to get XML body due to an unexpected XMLDocumentError: "+e);
            xml = "";
        }
        return xml;
    }

    private XMLDocument getXMLDocument() throws XMLDocumentException {
        XMLDocument doc = new XMLDocument();
        doc.startElement("ErrorReport");
        doc.addElement("SummaryDescription", getOverallError());
        doc.startElement("DetailedErrors");

        for (Iterator it = errorDescriptions.iterator(); it.hasNext();) {
            ErrorDescription errorDescription = (ErrorDescription)it.next();
            doc.startElement("ErrorDescription");
            doc.addElement("ErrorText", errorDescription.getErrorText());
            doc.addElement("FieldName", errorDescription.getFieldName());
            doc.endElement();
        }

        doc.endElement(); //DetailedErrors

        doc.startElement("Warnings");
        for (Iterator it = warnings.iterator(); it.hasNext();) {
            String warning = (String) it.next();
            doc.startElement("Warning");
            doc.addElement("Description", warning);
            doc.endElement();
        }
        doc.endElement(); //Warnings

        doc.endElement(); //ErrorReport
        return doc;
    }
}
