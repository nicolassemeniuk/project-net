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

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnetRuntimeException;
import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * Provides an Enumeration of color code values.
 * <p>
 * This enumeration replaces (and is compatible with) the color codes stored
 * in the <code>PN_GLOBAL_DOMAIN</code> table with <code>TABLE_NAME 'pn_project_spsace'</code>
 * and <code>COLUMN_NAME 'color_code_id'</code>.
 * </p>
 */
public class ColorCode implements IHTMLOption, Serializable {

    //
    // Static members
    //

    /**
     * The list of all color codes.
     */
    private static List colorCodeList = new ArrayList();

    /**
     * EMPTY Color Code.
     * This provided as a convenient mechanism for indicating a <code>null</code>
     * color code; JSP pages have problems setting null values in
     * setters.
     * Equality should always be based on <code>==</code>.  All <code>get</code>
     * methods throw exceptions.
     */
    public static final ColorCode EMPTY = new EmptyColorCode();

    /**
     * Green color.
     */
    public static final ColorCode GREEN = new ColorCode("100", "prm.project.color.green.name", "prm.project.color.green.icon", "prm.project.color.green.rgb");

    /**
     * Yellow color.
     */
    public static final ColorCode YELLOW = new ColorCode("200", "prm.project.color.yellow.name", "prm.project.color.yellow.icon", "prm.project.color.yellow.rgb");

    /**
     * Red color.
     */
    public static final ColorCode RED = new ColorCode("300", "prm.project.color.red.name", "prm.project.color.red.icon", "prm.project.color.red.rgb");

    /**
     * Returns the ColorCode with the specified id.
     * @param id the id of the color code to find
     * @return the ColorCode with matching id, or {@link #EMPTY} if no color code is
     * found with that id
     */
    public static ColorCode findByID(String id) {
        ColorCode foundColorCode = EMPTY;
        boolean isFound = false;

        for (Iterator it = ColorCode.colorCodeList.iterator(); it.hasNext() && !isFound;) {
            ColorCode nextColorCode = (ColorCode) it.next();
            if (nextColorCode.getID().equals(id)) {
                foundColorCode = nextColorCode;
                isFound = true;
            }
        }

        return foundColorCode;
    }

    /**
     * Returns a list of all color codes.
     * @return all color codes; each element is a <code>ColorCode</code>
     */
    public static List getAllColorCodes() {
        return Collections.unmodifiableList(ColorCode.colorCodeList);
    }

    /**
     * Returns an Html Radio group for selecting a color.
     * A selection is mandatory.
     * @param elementName the name for the radio group
     * @param selectOption the option who's value to select by default
     * @return the Html Radio group selection
     * @see #getHtmlRadioSelection(String, IHTMLOption, boolean)
     */
    public static String getHtmlRadioSelection(String elementName, IHTMLOption selectOption) {
        return getHtmlRadioSelection(elementName, selectOption, false);
    }

    /**
     * Returns an Html Radio group for selecting a color code.
     * For example: <code><pre>
     * &lt;table border="0" cellspacing="2" cellpadding="0" align="left"&gt;
     * &lt;tr&gt;
     * &lt;td bgcolor="<i>rgbValue1</i>">
     * &nbsp;&lt;input type="radio" name="<i>elementName</i>" value="<i>someValue1</i>"&gt;<i>Some Display 1</i>&amp;nbsp;
     * &lt;/td&gt;
     * &lt;td bgcolor="<i>rgbValue2</i>">
     * &lt;input type="radio" name="<i>elementName</i>" value="<i>someValue2</i> checked"&gt;<i>Some Display 2</i>&amp;nbsp;
     * &lt;/td&gt;
     * &lt;/tr&gt;
     * &lt;/table&gt;
     * </pre></code>
     * @param elementName the name for the radio group
     * @param selectOption the option who's value to select by default
     * @param isOptional true if the radio group should contain an optional
     * element; false otherwise
     * @return the Html Radio group selection
     */
    public static String getHtmlRadioSelection(String elementName, IHTMLOption selectOption, boolean isOptional) {

        StringBuffer result = new StringBuffer();

        result.append("<table border=\"0\" cellspacing=\"2\" cellpadding=\"0\" align=\"left\">");
        result.append("<tr>");

        // Grab the color codes into a local list so we can modify if necessary
        List colorCodes = new ArrayList();
        colorCodes.addAll(getAllColorCodes());

        // Iterate over all color codes building an Html Radio Group
        for (Iterator it= colorCodes.iterator(); it.hasNext(); ) {
            ColorCode nextColorCode = (ColorCode) it.next();

            result.append("<td class=\"tableContentFontOnly\" bgcolor=\"").append(nextColorCode.getColorRGB()).append("\">");
            result.append("&nbsp;");
            result.append("<input type=\"radio\" name=\"").append(elementName).append("\"");
            result.append(" value=\"").append(nextColorCode.getID()).append("\"");
            if (selectOption != null && nextColorCode.getID().equals(selectOption.getHtmlOptionValue())) {
                result.append(" checked");
            }
            result.append(">");

            result.append(nextColorCode.getName());
            result.append(" &nbsp;");

            result.append("</td> ");
        }

        if (isOptional) {
            // Add empty value element
            result.append("<td class=\"tableContentFontOnly\">");
            result.append("&nbsp;");
            result.append("<input type=\"radio\" name=\"").append(elementName).append("\"");
            result.append(" value=\"\"");
            if (selectOption == null) {
                result.append(" checked");
            }
            result.append(">");
            result.append(PropertyProvider.get("prm.project.color.none.name"));
            result.append(" &nbsp;");
            result.append("</td> ");
        }

        result.append("</tr>");
        result.append("</table>");

        return result.toString();
    }

    //
    // Instance members
    //

    /**
     * The unique id of the color code.
     */
    private String id = null;

    /**
     * The token which provides the color code display name.
     */
    private String nameToken = null;

    /**
     * The token for HTTP URL of the image that represents the color code.
     */
    private String imageURLToken = null;

    /**
     * The token for the RGB hex code that presents this color.
     */
    private String colorRGBToken = null;

    /**
     * Creates an empty ColorCode.
     */
    private ColorCode() {
        // Do nothing
    }

    /**
     * Creates a new ColorCode.
     * @param id the id
     * @param nameToken the token for the display name
     * @param imageURLToken the HTTP URL to the image for the color
     * @param colorRGBToken the token defining the RGB value for this color
     */
    private ColorCode(String id, String nameToken, String imageURLToken, String colorRGBToken) {
        this.id = id;
        this.nameToken = nameToken;
        this.imageURLToken = imageURLToken;
        this.colorRGBToken = colorRGBToken;
        colorCodeList.add(this);
    }

    /**
     * Returns the internal id of this ColorCode.
     * @return the id
     */
    public String getID() {
        return this.id;
    }

    /**
     * Returns the display name of this ColorCode.
     * @return the display name
     */
    public String getName() {
        return PropertyProvider.get(this.nameToken);
    }

    /**
     * Returns the token containing the name of this color code.
     * @return the name token
     */
    public String getNameToken() {
        return this.nameToken;
    }
    /**
     * Returns the URL to the image that represents this ColorCode.
     * @return the URL
     */
    public String getImageURL() {
        return PropertyProvider.get(this.imageURLToken);
    }

    /**
     * Returns the hex RGB value for this ColorCode, suitable for HTML.
     * @return the hex RGB color, for example <code>#99FF99</code>
     */
    public String getColorRGB() {
        return PropertyProvider.get(this.colorRGBToken);
    }

    public Color getColor() {
        int red = 0;
        int green = 0;
        int blue = 0;

        String colorRGB = getColorRGB();
        if (colorRGB.startsWith("#") && colorRGB.length() >=7) {
            red = Integer.decode("#" + colorRGB.substring(1,3)).intValue();
            green = Integer.decode("#" + colorRGB.substring(3,5)).intValue();
            blue = Integer.decode("#" + colorRGB.substring(5,7)).intValue();
        } else {
            throw new PnetRuntimeException("Unable to decode colorRGB (" + colorRGB + ") into a java.awt.Color value");
        }

        return new Color(red, green, blue);
    }

    /**
     * Indicates whether the specified object is a ColorCode with
     * matching ID.
     * @param o the ColorCode object to compare
     * @return true if the specified ColorCode has a matching id; false otherwise
     */
    public boolean equals(Object o) {
        boolean isEqual = false;

        if (this == o) {

            isEqual = true;

        } else {

            if (o instanceof ColorCode) {
                ColorCode colorCode = (ColorCode) o;
                if (id.equals(colorCode.id)) {
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
     * Returns presentation for this color as an Html image.
     * <p>
     * For example:
     * <code>&lt;img src="<i>imageUrl</i>" alt="<i>name</i>" /&gt;</code>
     * </p>
     * @return the Html image for this color
     */
    public String getHtmlImagePresentation() {
        StringBuffer result = new StringBuffer();
        result.append("<img src=\"").append(getImageURL()).append("\"");
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
     * @return the color code id
     */
    public String getHtmlOptionValue() {
        return getID();
    }

    /**
     * Returns the value for the content part of the HTML option.
     * @return the color code name
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
        doc.startElement("ColorCode");
        doc.addElement("ID", getID());
        doc.addElement("NameToken", this.nameToken);
        doc.addElement("ImageURL", getImageURL());
        doc.addElement("ColorRGB", getColorRGB());
        doc.endElement();
        return doc;
    }

    //
    // End IHTMLOption
    //

    /**
     * Provides an implementation of ColorCode that does not allow
     * invocation of any methods.
     * All methods throw <code>RuntimeException</code>s.
     */
    private static final class EmptyColorCode extends ColorCode {

        EmptyColorCode() {
            super();
        }

        public String getID() {
            throw new UnsupportedOperationException("Unsupported operation");
        }

        public String getName() {
            throw new UnsupportedOperationException("Unsupported operation");
        }

        public String getImageURL() {
            throw new UnsupportedOperationException("Unsupported operation");
        }

        public String getColorRGB() {
            throw new UnsupportedOperationException("Unsupported operation");
        }

    }

}
