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
package net.project.base.finder;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.project.base.property.PropertyProvider;
import net.project.gui.html.IHTMLOption;

/**
 * An individual option that will appear in a domain filter.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class DomainOption implements IHTMLOption, Serializable {

    /**
     * The value for this DomainOption.
     */
    private final String value;

    /**
     * Indicates whether we already have the text, or whether it is a token.
     */
    private final boolean isText;

    /**
     * The token used to display this DomainOption.
     */
    private String displayToken;

    /**
     * The text to display for this DomainOption.
     */
    private String displayText;

    /**
     * Sorts a list of domain options based on their display names.
     *
     * @param domainOptionList the list of <code>DomainOption</code>s to sort
     */
    public static void sort(List domainOptionList) {

        Collections.sort(domainOptionList,
                new Comparator() {
                    public int compare(Object o1, Object o2) {
                        DomainOption option1 = (DomainOption) o1;
                        DomainOption option2 = (DomainOption) o2;

                        return option1.getDisplayName().compareTo(option2.getDisplayName());
                    }
                });

    }

    /**
     * Standard constructor to create a domain option.
     *
     * @param value a <code>String</code> value indicating the value of the
     * option list as appears in the <code>&lt;option value="?"></code> item
     * which will be generated from this <code>DomainOption</code>.
     * @param displayToken a <code>String</code> value containing a token for
     * the string that the domain option will display when presented in an
     * option list.
     */
    public DomainOption(String value, String displayToken) {
        this(value, displayToken, false);
    }

    /**
     * Creates a DomainOption indicating whether the display is a token or
     * actual text.
     *
     * @param value a <code>String</code> value indicating the value of the
     * option list as appears in the <code>&lt;option value="?"></code> item
     * which will be generated from this <code>DomainOption</code>.
     * @param display the display text for the token (if isText is true) or
     * a token name (if isText is false)
     * @param isText true if the display value is actual text, false if it
     * is a token
     */
    public DomainOption(String value, String display, boolean isText) {
        this.value = value;
        this.isText = isText;
        if (isText) {
            this.displayText = display;
        } else {
            this.displayToken = display;
        }
    }

    /**
     * Returns the value of this DomainOption.
     * This is the value stored in a column.
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Returns the display name for this DomainOption.
     * This is always looked up from the display token.
     * @return the display name
     */
    public String getDisplayName() {
        if (isText) {
            return this.displayText;
        } else {
            return PropertyProvider.get(this.displayToken);
        }
    }

    //
    // Implementing IHTMLOption
    //

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return getValue();
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return getDisplayName();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DomainOption)) return false;

        final DomainOption domainOption = (DomainOption) o;

        if (!value.equals(domainOption.value)) return false;

        return true;
    }

    public int hashCode() {
        return value.hashCode();
    }

}
