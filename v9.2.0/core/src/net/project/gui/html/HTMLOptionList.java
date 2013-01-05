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
|   $Revision: 18888 $
|       $Date: 2009-02-08 19:22:41 -0200 (dom, 08 feb 2009) $
|     $Author: avinash $
|
+----------------------------------------------------------------------*/
package net.project.gui.html;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.project.util.HTMLUtils;
import net.project.util.Validator;

/**
 * Provides helper methods for constructing an HTML Option List based on
 * a collection of <code>IHTMLOption</code>s.
 * <p>
 * The option list never inlcudes the <code>&lt;select&gt;</code> portion
 * therefore it may be used for a single- or multi- select option list.
 * </p>
 * <p>
 * Example of option list returned: <code><pre>
 * &lt;option value="someValue"&gt;Some Display&lt;/option&gt;
 * &lt;option value="someOtherValue" selected&gt;Some Other Display&lt;/option&gt;
 * </pre></code>
 */
public class HTMLOptionList {

    //
    // Static members
    //

    /**
     * Convenience method to create an option list with no selected values.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @return the option list
     */
    public static String makeHtmlOptionList(Collection optionCollection) {
        return makeHtmlOptionList(optionCollection, (String) null);
    }

    /**
     * Convenience method to create an option list with at most a single value
     * selected.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectValue the value to select in the list, if found
     * @return the option list
     */
    public static String makeHtmlOptionList(Collection optionCollection, String selectValue) {
        return makeHtmlOptionList(optionCollection, new String[]{selectValue});
    }

    /**
     * Convenience method to create an option list with multiple values selected.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectValues the values to select in the list, if found
     * @return the html option list
     */
    public static String makeHtmlOptionList(Collection optionCollection, String[] selectValues) {
        HTMLOptionList optionList = new HTMLOptionList();
        optionList.setOptions(optionCollection);
        optionList.setSelectedValues((selectValues == null ? new String[]{} : selectValues));
        return optionList.toString();
    }

    /**
     * Convenience method to create an option list with multiple values selected.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectOptionCollection the collection of <code>IHTMLOption</code> to select
     * in the list (if found)
     * @return the html option list
     */
    public static String makeHtmlOptionList(Collection optionCollection, Collection selectOptionCollection) {
        HTMLOptionList optionList = new HTMLOptionList();
        optionList.setOptions(optionCollection);
        optionList.setSelectedValues((selectOptionCollection == null ? Collections.EMPTY_LIST : selectOptionCollection));
        return optionList.toString();
    }

    /**
     * Convenience method to create an option list with multiple values selected.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectOptionCollection the collection of <code>IHTMLOption</code> to select
     * in the list (if found)
     * @param addMissing true if the option to select should be added
     * if not present
     * @return the html option list
     */
    public static String makeHtmlOptionList(Collection optionCollection, Collection selectOptionCollection, boolean addMissing) {
        HTMLOptionList optionList = new HTMLOptionList();
        optionList.setOptions(optionCollection);
        optionList.setSelectedValues((selectOptionCollection == null ? Collections.EMPTY_LIST : selectOptionCollection));
        optionList.setAddMissing(addMissing);
        return optionList.toString();
    }

    /**
     * Convenience method to create an option list with at most a single value
     * selected.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectOption the option who's value to select in the list, if found
     * @return the html option list
     */
    public static String makeHtmlOptionList(Collection optionCollection, IHTMLOption selectOption) {
        return makeHtmlOptionList(optionCollection, selectOption, false);
    }

    /**
     * Creates an HTML option list where the specified option is either selected
     * if its value is already present in the list, or added to the list
     * (and selected) if it is not in the list.
     * @param optionCollection the collection of <code>IHTMLOption</code>s.
     * @param selectOption the option to select if present in the list
     * @param isAddMissing true if the option to select should be added
     * if not present
     * @return the html option list
     */
    public static String makeHtmlOptionList(Collection optionCollection, IHTMLOption selectOption, boolean isAddMissing) {
        HTMLOptionList optionList = new HTMLOptionList();
        optionList.setOptions(optionCollection);
        optionList.setSelectOption(selectOption);
        optionList.setAddMissing(isAddMissing);
        return optionList.toString();
    }

    //
    // Instance members
    //

    /**
     * The collection of <code>IHTMLOption</code>s from which to build
     * the option list.
     */
    private Collection optionCollection = null;

    /**
     * The values to select in the list.
     */
    private List selectValues = new ArrayList();

    /**
     * An option to select if in the list.
     * This is maintained as an IHTMLOption in case it is required to be
     * added into the list.
     */
    private IHTMLOption selectOption = null;

    /**
     * Indicates whether to add missing select options into the
     * option list.
     */
    private boolean isAddMissing = false;

    /**
     * Creates an empty HTMLOptionList.
     */
    private HTMLOptionList() {
        // Do nothing
    }

    /**
     * Sets the <code>IHTMLOption</code>s to build the list from.
     * @param optionCollection the options
     */
    private void setOptions(Collection optionCollection) {
        this.optionCollection = optionCollection;
    }

    /**
     * Sets the values to be selected in the option list.
     * @param values the values; if any value is found in the collection
     * of <code>IHTMLOption</code>s, that option is set to <code>selected</code>
     */
    private void setSelectedValues(String[] values) {
        this.selectValues.addAll(Arrays.asList(values));
    }

    /**
     * Sets the <code>IHTMLOption</code>s to be selected in the option list.
     * @param options the options; if any option is found in the collection
     * of <code>IHTMLOption</code>s, that option is set to <code>selected</code>
     */
    private void setSelectedValues(Collection options) {

        List valueList = new ArrayList();

        // Convert the option collection into a value list
        for (Iterator it = options.iterator(); it.hasNext(); ) {
            IHTMLOption nextOption = (IHTMLOption) it.next();
            valueList.add(nextOption.getHtmlOptionValue());
        }

        // Set the values to select
        this.selectValues.addAll(valueList);
    }

    /**
     * Sets an option that should be selected if present in the list.
     * @param option the option to select
     */
    private void setSelectOption(IHTMLOption option) {
        this.selectOption = option;
        // Add the value part so that it is selected if present when
        // the option list is constructed
        if (this.selectOption != null) {
            this.selectValues.add(option.getHtmlOptionValue());
        }
    }

    /**
     * Specifies whether to add missing select options into the list.
     * @param isAddMissing true if missing options should be added;
     * false otherwise
     */
    private void setAddMissing(boolean isAddMissing) {
        this.isAddMissing = isAddMissing;
    }


    /**
     * Returns the option list as a string based on the specified selected
     * values and collection of <code>IHTMLOption</code>s.
     * @return the Html Option list
     * @see HTMLOptionList the class description for an example
     */
    public String toString() {

        StringBuffer result = new StringBuffer();

        // Iterate over all options, building an option list
        for (Iterator it = this.optionCollection.iterator(); it.hasNext();) {
            IHTMLOption nextOption = (IHTMLOption) it.next();

            // <option value="someValue"
            result.append("<option value=\"").append(nextOption.getHtmlOptionValue()).append("\"");

            //If option offers an "CSS Style" use it
            if (nextOption instanceof IHTMLStyle) {
                IHTMLStyle style = (IHTMLStyle)nextOption;
                if (!Validator.isBlankOrNull(style.getStyle())) {
                    result.append(" style=\"").append(style.getStyle()).append("\"");
                }
            }

            if (nextOption instanceof IHTMLClass) {
                IHTMLClass htmlClass = (IHTMLClass)nextOption;
                if (!Validator.isBlankOrNull(htmlClass.getHTMLClass())) {
                    result.append(" class=\"").append(htmlClass.getHTMLClass()).append("\"");
                }
            }

            // selected
            if (this.selectValues.contains(nextOption.getHtmlOptionValue())) {
                result.append(" selected");
            }

            // >Some Display</option>\n
            result.append(">").append(HTMLUtils.escape(nextOption.getHtmlOptionDisplay())).append("</option>").append("\n");
        }

        // Now add any values that are not in the list
        if (this.selectOption != null && this.isAddMissing) {

            boolean isFound = false;

            // Look for that option and break when found
            for (Iterator it = this.optionCollection.iterator(); it.hasNext() && !isFound;) {
                IHTMLOption nextOption = (IHTMLOption) it.next();
                if (nextOption.getHtmlOptionValue().equals(this.selectOption.getHtmlOptionValue())) {
                    isFound = true;
                }
            }

            if (!isFound) {
                // Option was not found.  So we add it to start of option list
                result = new StringBuffer()
                        .append("<option value=\"").append(selectOption.getHtmlOptionValue()).append("\" selected>")
                        .append(HTMLUtils.escape(this.selectOption.getHtmlOptionDisplay())).append("</option>").append("\n")
                        .append(result);
            }

        }

        return result.toString();
    }

}
