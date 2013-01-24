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

 package net.project.gui.html;

/**
 * Class which implements IHTMLOption for those times when you need to create
 * an additional entry in a list.
 *
 * @author Matthew Flower
 * @since Version 7.4
 */
public class HTMLOption implements IHTMLOption {
    private String optionValue;
    private String optionDisplay;

    /**
     * Standard constructor.
     *
     * @param optionValue this is the value that will be returned if the user
     * clicks on this HTML option.
     * @param optionDisplay this is the value that will be displayed in the
     * option list.
     */
    public HTMLOption(String optionValue, String optionDisplay) {
        this.optionValue = optionValue;
        this.optionDisplay = optionDisplay;
    }

    /**
     * Standard constructor.
     *
     * @param optionValue the value returned if the user selects this html
     * option.
     * @param optionDisplay the value displayed to the user for this html
     * option.
     */
    public HTMLOption(long optionValue, String optionDisplay) {
        this.optionValue = String.valueOf(optionValue);
        this.optionDisplay = optionDisplay;
    }

    /**
     * Returns the value for the <code>value</code> attribute of the HTML
     * option.
     *
     * @return a <code>String</code> value which will become the value="?" part
     * of the option tag.
     */
    public String getHtmlOptionValue() {
        return optionValue;
    }

    /**
     * Returns the value for the content part of the HTML option.
     *
     * @return a <code>String</code> value that will be displayed for this
     * html option.
     */
    public String getHtmlOptionDisplay() {
        return optionDisplay;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final HTMLOption htmlOption = (HTMLOption) o;

        if (optionValue != null ? !optionValue.equals(htmlOption.optionValue) : htmlOption.optionValue != null)
            return false;

        return true;
    }

    public int hashCode() {
        return (optionValue != null ? optionValue.hashCode() : 0);
    }
}
