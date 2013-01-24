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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.portfolio;

import net.project.project.ProjectSpace;
import net.project.space.SpaceMember;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;


/**
 * A project entry in a portfolio.
 * This presents additional information about a ProjectSpace that is
 * related to the owner of the portfolio in which this entry occurs.
 * <b>Note:</b> This class only adds a person's responsiblities to a
 * ProjectSpace.  It should not be enahanced for additional functionality
 * that is not related to a person.
 * @author unascribed
 */
public class ProjectPortfolioEntry extends ProjectSpace {

    /**
     * User-specific attributes of a project.
     * The user's role in the project.
     */
    private String personResponsibilities = null;

    /**
     * Creates an empty ProjectPortfolioEntry.
     */
    public ProjectPortfolioEntry() {
        // Do nothing
    }

    /**
     * Creates a portfolio entry for the specified project space.
     * This is an entry with no person responsibilities.
     * @param projectSpace the project space from which to create the entry
     */
    ProjectPortfolioEntry(ProjectSpace projectSpace) {
        super(projectSpace);
    }

    /**
     * Creates a portfolio entry for the specified project space and
     * space membership information.
     * @param projectSpace the project space from which to create the entry
     * @param spaceMember the membership information from which the
     * person responsibilities are fetched
     */
    ProjectPortfolioEntry(ProjectSpace projectSpace, SpaceMember spaceMember) {
        this(projectSpace);
        setPersonResponsibilities(spaceMember.getResponsibilities());
    }

    /**
     * Set the responsibilities description for the user on this project portfolio entry.
     * @param responsibilities the person's responsibilities
     * @see #getPersonResponsibilities
     */
    private void setPersonResponsibilities(String responsibilities) {
        this.personResponsibilities = responsibilities;
    }

    /**
     * Get the responsibilities description for the user on this project portfolio entry.
     * @return the responsibilities
     */
    public String getPersonResponsibilities() {
        return this.personResponsibilities;
    }


    //
    // Implementing IPortfolioEntry
    //
    // Note: All other methods already implemented in ProjectSpace or Space

    /**
     * Get the name of the Business Space that owns this project.
     * @return the name of the Business Space that owns this project, null if none.
     */
    public String getParentSpaceName() {
        return getParentBusinessName();
    }


    /**
     * Get an XML representation of the PortfolioEntry.
     * @return an XML representation of this object.
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Get an XML representation of the PortfolioEntry without xml tag.
     * @return an XML representation of this object without the xml tag.
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns this ProjectSpace as an XMLDocument.
     * @return the xml document
     */
    protected XMLDocument getXMLDocument() {

        net.project.xml.document.XMLDocument doc = new net.project.xml.document.XMLDocument();

        try {
            doc.startElement("ProjectPortfolioEntry");
            addAttributes(doc);
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Do nothing

        }

        return doc;
    }

    /**
     * Adds all XML attributes to the specified document, including
     * attributes from <code>ProjectSpace</code>.
     * @param doc the document to which to add attributes; it is assumed that
     * the document contains an open element.
     * @throws net.project.xml.document.XMLDocumentException if there is a problem adding attributes
     */
    protected void addAttributes(XMLDocument doc) throws XMLDocumentException {
        super.addAttributes(doc);
        doc.addElement("ParentSpaceName", getParentSpaceName());
        doc.addElement("PersonResponsiblities", getPersonResponsibilities());
    }

}
