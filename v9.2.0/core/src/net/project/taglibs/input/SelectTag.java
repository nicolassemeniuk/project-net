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

 package net.project.taglibs.input;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.jsp.JspTagException;

import net.project.gui.html.HTMLOption;
import net.project.gui.html.HTMLOptionList;
import net.project.gui.html.IHTMLOption;

/**
 * Provides a convenient mechanism for rendering an HTML select list from
 * a collection of <code>{@link net.project.gui.html.IHTMLOption}</code>s.
 *
 * @author Tim Morrow
 * @since Version 7.7.0
 */
public class SelectTag extends AbstractInputTag {

    /**
     * The options to display in the select list.
     */
    private Collection options;

    /**
     * The default selected option.
     */
    private Collection defaultSelectedOption;

    /**
     * Creates an empty SelectTag.
     */
    public SelectTag() {
        super();
    }

    /**
     * Constructs the HTML for the select element.
     * Attributes are added in the order used in the taglib.
     * Calls {@link #formatAllAttributes}.
     * @return the select element HTML code
     * @throws javax.servlet.jsp.JspTagException if there is a problem constructing the input element
     */
    protected String constructInputElement()  throws JspTagException {
        StringBuffer elementText = new StringBuffer();

        elementText.append("<select ");
        elementText.append(formatAllAttributes());
        elementText.append(" >");
        elementText.append(HTMLOptionList.makeHtmlOptionList(this.options, this.defaultSelectedOption, true));
        elementText.append("</select>");

        return elementText.toString();
    }

    /**
     * Clear's this select element's properties so that the tag
     * may be reused.
     */
    protected void clear() {
        this.options = null;
        this.defaultSelectedOption = null;
    }

    //
    // Attribute setters
    //

    /**
     * Specifies the colleciton of <code>IHTMLOptions</code> that make up
     * the options in this select list.
     * <p>
     * No ordering is applied to the options, the collection elements should
     * already be ordered for display.
     * </p>
     * @param options the collection where each element is an {@link IHTMLOption}
     */
    public void setOptions(Collection options) {
        this.options = options;
    }

    /**
     * Specifies the <code>IHTMLOption</code> that should be selected by default
     * in the option list.
     * <p>
     * The option to select is identified as the first option in the option collection
     * with a value matching the specified option's value.  When no matching option
     * is found, no option is selected.
     * </p>
     * @param option the option to select by default
     */
    public void setDefaultSelected(IHTMLOption option) {
        this.defaultSelectedOption = new ArrayList();
        defaultSelectedOption.add(option);
    }

    public void setDefaultSelected(String option) {
        this.defaultSelectedOption = new ArrayList();
        defaultSelectedOption.add(new HTMLOption(option, ""));
    }

    public void setDefaultSelectedMulti(Collection options) {
        this.defaultSelectedOption = options;
    }

    /**
     * Sets value for the HTML <code>select</code> element's
     * <code>MULTIPLE</code> attribute.
     * @param isMultiple true if this is a multi-select; false otherwise
     */
    public void setMultiple(Boolean isMultiple) {
        super.getAttributeValueMap().put("multiple", isMultiple);
    }

}
