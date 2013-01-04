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
package net.project.taglibs.output;

import java.io.IOException;
import java.util.Currency;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.money.Money;
import net.project.security.SessionManager;

/**
 * Provides a convenient mechanism for displaying formatted money values.
 * <p>
 * A <code>Money</code> is passed into this taglib, along with a <code>Currency</code>.
 * The <code>Currency</code> is typically the project default currency.
 * The money value is formatted and its currency is displayed.
 * If the money value is null then no value is displayed.
 * Currently does not make use of the <code>Currency</code> but in the future
 * it may be used to make decisions on what to do if no money value is specified.
 * </p>
 */
public class MoneyTag extends TagSupport {

    /**
     * The Money value to display.
     */
    private Money money = null;

    /**
     * The currency to assume if no Money value is specified.
     */
    private Currency currency = null;

    /**
     * Creates an empty MoneyTag.
     */
    public MoneyTag() {
        super();
    }

    /**
     * Prints the HTML output.
     * @return {@link #SKIP_BODY} always
     * @throws JspException if there is a problem writing the content
     */
    public int doStartTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.print(constructOutputElement());

        } catch (IOException ioe) {
            throw new JspTagException("Error displaying input text element: " + ioe);

        } finally {
            // Clear all attributes for re-use of this tag
            clear();
        }

        return (SKIP_BODY);
    }

    /**
     * Constructs the HTML to be displayed for the money value.
     * @return the HTML
     * @throws JspTagException
     */
    private String constructOutputElement() throws JspTagException {

        // One of money or currency is required
        if (this.money == null && this.currency == null) {
            throw new JspTagException("Either money or currency attribute is required");
        }

        StringBuffer result = new StringBuffer();

        if (money == null) {
            // No value to display, so it remains blank
            // We could display the currency if we want, but the field
            // shall remain blank for now
            result.append("&nbsp;");

        } else {
            // Display the value and currency
            result.append(this.money.formatValue(SessionManager.getUser()))
                    .append("&nbsp;")
                    .append(this.money.getCurrency());
        }

        return result.toString();
    }

    /**
     * Clears the values in this tag for reuse.
     */
    private void clear() {
        this.money = null;
        this.currency = null;
    }

    //
    // Attribute Setters
    //

    /**
     * Specifies the existing <code>Money</code> who's currency
     * to display.
     * @param money
     */
    public void setMoney(Object money) {
        this.money = (Money) money;
    }

    /**
     * Specifies the <code>Currency</code> to use in the event that
     * the specified Money object is null or not specified.
     * This is typically the Project Default Currency.
     * @param currency the currency
     */
    public void setCurrency(Object currency) {
        this.currency = (Currency) currency;
    }

}
