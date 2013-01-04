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
package net.project.code;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;
import net.project.security.SessionManager;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides an enumeration of Improvement codes.
 * <p>
 * Each Improvement Code supports an image URL for the colors:
 * <code>ColorCode.GREEN</code>, <code>ColorCode.YELLOW</code>, <code>ColorCode.RED</code>.
 * </p>
 */
public class ImprovementCode implements IHTMLOption, Serializable {

    //
    // Static members
    //

    /**
     * The list of all ImprovementCodes.
     */
    private static List improvementCodeList = new ArrayList();

    /**
     * EMPTY Improvement Code.
     * This provided as a convenient mechanism for indicating a <code>null</code>
     * improvement code; JSP pages have problems setting null values in
     * setters.
     * Equality should always be based on <code>==</code>.  All <code>get</code>
     * methods throw exceptions.
     */
    public static final ImprovementCode EMPTY = new EmptyImprovementCode();

    /**
     * Improving code.
     */
    public static final ImprovementCode IMPROVING = new ImprovementCode("100",
            "prm.global.code.improvement.improving.name",
            "prm.global.code.improvement.improving.icon.green",
            "prm.global.code.improvement.improving.icon.yellow",
            "prm.global.code.improvement.improving.icon.red",
            "prm.global.code.improvement.image.title");

    /**
     * No Change code.
     */
    public static final ImprovementCode NO_CHANGE = new ImprovementCode("200",
            "prm.global.code.improvement.nochange.name",
            "prm.global.code.improvement.nochange.icon.green",
            "prm.global.code.improvement.nochange.icon.yellow",
            "prm.global.code.improvement.nochange.icon.red",
            "prm.global.code.improvement.image.title");

    /**
     * Worsening code.
     */
    public static final ImprovementCode WORSENING = new ImprovementCode("300",
            "prm.global.code.improvement.worsening.name",
            "prm.global.code.improvement.worsening.icon.green",
            "prm.global.code.improvement.worsening.icon.yellow",
            "prm.global.code.improvement.worsening.icon.red",
            "prm.global.code.improvement.image.title");

    /**
     * Returns the ImprovementCode with the specified id.
     * @param id the ImprovementCode or {@link #EMPTY} if none is found with the
     * specified id
     */
    public static ImprovementCode findByID(String id) {
        ImprovementCode foundImprovementCode = EMPTY;
        boolean isFound = false;

        for (Iterator it = ImprovementCode.improvementCodeList.iterator(); it.hasNext() && !isFound; ) {
            ImprovementCode nextImprovementCode = (ImprovementCode) it.next();
            if (nextImprovementCode.id.equals(id)) {
                foundImprovementCode = nextImprovementCode;
                isFound = true;
            }
        }

        return foundImprovementCode;
    }

    /**
     * Returns a list of all ImprovementCodes.
     * @return the list where each element is an <code>ImprovementCode</code>.
     */
    public static List getAllImprovementCodes() {
        return Collections.unmodifiableList(ImprovementCode.improvementCodeList);
    }

    //
    // Instance members
    //

    /**
     * The internal id of the improvment code.
     */
    private String id = null;

    /**
     * The token that provides the display name of the improvment code.
     */
    private String nameToken = null;

    /**
     * The map of color codes to image URL tokens
     */
    private Map colorImageURLTokenMap = null;

    /**
     * The token containing the title to display when displaying an image.
     */
    private String imageTitleToken = null;

    /**
     * Creates an empty ImprovementCode.
     */
    private ImprovementCode() {
        // Do nothing
        // Do not add to list
    }

    /**
     * Creates a new ImprovementCode with the specified id and token.
     * @param id the id of the code
     * @param nameToken the token that provides the display name of
     * the improvment code
     * @param greenImageURLToken
     * @param yellowImageURLToken
     * @param redImageURLToken
     */
    private ImprovementCode(String id, String nameToken,
                            String greenImageURLToken, String yellowImageURLToken, String redImageURLToken,
                            String imageTitleToken) {
        this.id = id;
        this.nameToken = nameToken;
        this.imageTitleToken = imageTitleToken;

        // Add the tokens for color image URLs to the map
        this.colorImageURLTokenMap = new HashMap(3);
        this.colorImageURLTokenMap.put(ColorCode.GREEN, greenImageURLToken);
        this.colorImageURLTokenMap.put(ColorCode.YELLOW, yellowImageURLToken);
        this.colorImageURLTokenMap.put(ColorCode.RED, redImageURLToken);

        // Add this code to the list of all codes
        improvementCodeList.add(this);
    }

    /**
     * Returns the id of this ImprovementCode.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this ImprovementCode.
     * @return the display name, looked up from a token
     */
    public String getName() {
        return PropertyProvider.get(this.nameToken);
    }

    /**
     * Returns the name token for displaying this ImprovementCode.
     * @return the token for the display name
     */
    public String getNameToken() {
        return this.nameToken;
    }

    /**
     * Returns the image URL for the specified color.
     * @param colorCode the color code for which to get the image
     * @return the color image URL
     * @throws NullPointerException if the specified color code is null
     * @throws IllegalArgumentException if this ImprovementCode doesn't
     * have an image URL for the specified color
     */
    public String getImageURL(ColorCode colorCode) {

        if (colorCode == null) {
            throw new NullPointerException("colorCode is required");
        }

        // Grab the token from the map
        String imageURLToken = (String) this.colorImageURLTokenMap.get(colorCode);

        // If we didn't find the token, then the color isn't supported
        if (imageURLToken == null) {
            throw new IllegalArgumentException("ImprovementCode does not support an image URL for the specified color");
        }

        // Lookup the value for the token
        return PropertyProvider.get(imageURLToken);
    }

    /**
     * Returns the token used to render the title (aka tooltip) for the image
     * displayed for this improvement code.
     * This token accepts two paramters:  The improvement name and the Color Code name.
     * @return the token name
     */
    public String getImageTitleToken() {
        return this.imageTitleToken;
    }

    /**
     * Indicates whether the specified object is an ImprovementCode with
     * matching ID.
     * @param o the ImprovementCode object to compare
     * @return true if the specified ImprovementCode has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof ImprovementCode) {
                ImprovementCode improvementCode = (ImprovementCode) o;
                if (id.equals(improvementCode.id)) {
                    isEqual = true;
                }
            }

        }

        return isEqual;
    }

    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Returns presentation for this improvement code as an Html image for
     * the specified color.
     * <p>
     * For example:
     * <code>&lt;img src="<i>imageUrl</i>" alt="<i>name</i>" /&gt;</code>
     * </p>
     * @return the Html image for this improvement code and color code
     */
    public String getHtmlImagePresentation(ColorCode colorCode) {
        StringBuffer result = new StringBuffer();
        result.append("<img src=\"").append(SessionManager.getJSPRootURL()).append(getImageURL(colorCode)).append("\"");
        result.append(" alt=\"").append(getName()).append("\"");
        result.append(" />");
        return result.toString();
    }

    //
    // Implementing IHTMLOption
    //

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     * @return the id of this ImprovementCode
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     * @return the display name of this ImprovementCode
     */
    public String getHtmlOptionDisplay() {
        return getName();
    }

    /**
     * Creates an XML document.
     * @return the XML document
     * @throws XMLDocumentException if there is a problem creating
     */
    public XMLDocument getXMLDocument() throws XMLDocumentException {
        XMLDocument doc = new XMLDocument();

        doc.startElement("ImprovementCode");
        doc.addElement("ID", getID());
        doc.addElement("NameToken", this.nameToken);
        doc.addElement("ImageTitleToken", this.imageTitleToken);
        doc.endElement();

        return doc;
    }

    //
    // End IHTMLOption
    //

    private static final class EmptyImprovementCode extends ImprovementCode {

        EmptyImprovementCode() {
            super();
        }

        public String getID() {
            throw new UnsupportedOperationException("Unsupported operation");
        }

        public String getName() {
            throw new UnsupportedOperationException("Unsupported operation");
        }

        public String getImageURL(ColorCode colorCode) {
            throw new UnsupportedOperationException("Unsupported operation");
        }

    }

 }