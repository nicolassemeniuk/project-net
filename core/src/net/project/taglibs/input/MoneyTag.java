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
package net.project.taglibs.input;

import java.util.Currency;

import javax.servlet.jsp.JspTagException;

import net.project.base.money.Money;
import net.project.security.SessionManager;

/**
 * A Money HTML input tag.
 * <p>
 * A money input tag requires either a currency or money object.
 * If a money object is specified, the symbol for its currency is displayed.
 * If a money object is not specified (or is null) the currency is required and
 * its symbol is used for display.
 * If a money object is specified and its currency differs from the specified
 * currency, a selection list is displayed to allow selection between the
 * money's currency and the specified currency.
 * </p>
 * <p>
 * This tag produces two HTML input fields; the first is an text field and is
 * named <code><i>name</i>_value</code> where <code>name</code> is an attribute
 * for this taglib.  The second field is hidden and is called <code><i>name</i>_currencyCode</code>
 * (case intended) and is set to the currency code. <br>
 * Some other attributes are defaulted, unless explicitly set in this tag: <br>
 * <ul>
 * <li>maxlength: the greater of 20 or the actual length of the formatted money
 * value</li>
 * <li>size: the greater of 20 or the maxlength, but no more than 40</li>
 * <li>align: right</li>
 * </ul>
 * </p>
 * <p>
 * For example: <br>
 * <code>&lt;input:money name="cost" money="&lt;%=project.getCost()%&gt;" currency="&lt;%=project.getDefaultCurrency()%&gt;" /&gt;</code> <br>
 * Produces HTML: <br>
 * <code>$&amp;nbsp;&lt;input type="text" value="1,500" name="cost_value" /&gt;&lt;input type="hidden" value="USD" name="cost_currencyCode" /&gt;&amp;nbsp;USD <br>
 * Which is displayed as: <br>
 * <code>$ 1,500 USD</code>
 * </p>
 */
public class MoneyTag extends TextTag {

    /**
     * The Money value to display in the input field.
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
     * Constructs the input element for capturing Money values.
     * This includes a text field and either a hidden field or a select list.
     * @return the HTML for inputting a money value
     * @throws JspTagException if there is a problem constructing the input element;
     * for example, neither money nor currency attributes have been specified
     */
    protected String constructInputElement() throws JspTagException {

        // One of money or currency is required
        if (this.money == null && this.currency == null) {
            throw new JspTagException("Either money or currency attribute is required");
        }

        // Name attribute is required
        if (getAttributeValueMap().get("name") == null) {
            throw new JspTagException("Name attribute is required");
        }

        // Set the value to be the formatted money value
        String formattedValue = null;
        if (this.money != null) {
            formattedValue = this.money.formatValue(SessionManager.getUser());
            getAttributeValueMap().put("value", formattedValue);
        }

        // Figure out the appropriate max length of the field
        // It will be either defaulted to 20, or use the size specified
        // by the attribute "size"
        // However, we also check to make sure that the formatted length
        // of the value (which includes digit grouping, decimal places etc.)
        // will fit in the maxlength
        // 20 is deemed sufficient for entering money values including all
        // necessary punctuation
        Integer maxLength = (Integer) getAttributeValueMap().get("maxlength");
        if (maxLength == null) {
            maxLength = new Integer(20);
        }
        if (formattedValue != null) {
            // If we have a formatted value, then the max length might need to
            // be bigger to accommodate any formatting
            maxLength = new Integer(Math.max(formattedValue.length(), maxLength.intValue()));
        }
        getAttributeValueMap().put("maxlength", maxLength);

        // Set the default size if not specified by tag already
        // size is the greater of 20 or maxlength, no more than 40
        if (getAttributeValueMap().get("size") == null) {
            getAttributeValueMap().put("size", new Integer(Math.min(Math.max(maxLength.intValue(), 20), 40)));
        }

        // Save the original input name and add "_value" to the actual name
        String originalName = (String) getAttributeValueMap().get("name");
        getAttributeValueMap().put("name", originalName + "_value");

        // Get the display currency from the correct place
        // We use money if it was specified
        // Note:  The currency symbol is virtually always the same as the
        // currency code (generally it only returns a symbol for the currency
        // associated with the country for the current locale).  Also,
        // by placing the symbol to the left of the input field, the fields
        // do not line up with other text boxes
        // Thus, we're choosing not to include the currency symbol
        Currency displayCurrency = null;

        if (money == null) {
            displayCurrency = this.currency;
        } else {
            displayCurrency = this.money.getCurrency();
        }

        // Now construct the HTML
        StringBuffer elementText = new StringBuffer();

        // Input field
        // Note: By calling formatAllAttributes() we allow the TextTag to
        // process the attributes (escaping values etc.)
        elementText.append("<input type=\"text\"");
        elementText.append(formatAllAttributes());
        elementText.append(" />");

        // If the currency code of the specified money (if any) is different
        // from the currency code of the specified currency (if any) then
        // we present a selection including the currency code of the money
        // and the currency code of the specified currency
        // This allows a user to switch the currency from the currency of
        // the money value to the project default currency
        if (this.currency != null && this.currency.getCurrencyCode().equals(displayCurrency.getCurrencyCode())) {
            // The currency code we're displaying is identical to the
            // specified currency code

            // Add the hidden currency code field
            elementText.append("<input type=\"hidden\" name=\"").append(originalName).append("_currencyCode").append("\"")
                    .append(" value=\"").append(displayCurrency.getCurrencyCode()).append("\" />");

            // Display the currency code
            elementText.append("&nbsp;").append(displayCurrency.getCurrencyCode());

        } else {
            // The currency code is different
            // Provide a selection of currency codes
            elementText.append("&nbsp;");
            elementText.append("<select name=\"").append(originalName).append("_currencyCode").append("\">")
                    .append("<option value=\"").append(displayCurrency.getCurrencyCode()).append("\" selected>")
                    .append(displayCurrency.getCurrencyCode()).append("</option>")
                    .append("<option value=\"").append(this.currency.getCurrencyCode()).append("\">")
                    .append(this.currency.getCurrencyCode()).append("</option>")
                    .append("</select>");
        }

        return elementText.toString();
    }

    /**
     * Clears the values in this tag for reuse.
     */
    protected void clear() {
        super.clear();
        this.money = null;
        this.currency = null;
    }

    //
    // Attribute Setters
    //

    /**
     * Specifies the existing <code>Money</code> who's currency
     * to display.
     * This value is not required; however, when absent a currency is
     * required.
     * @param money
     */
    public void setMoney(Object money) {
        this.money = (Money) money;
    }

    /**
     * Specifies the <code>Currency</code> to use in the event that
     * the specified Money object is null or not specified.
     * This is typically the Project Default Currency.
     * Also, when the money object is non-null and its currency is different
     * from the currency specified here, a selection list of currencies is
     * produced containing the money's currency and the specified currency.
     * @param currency the currency
     */
    public void setCurrency(Object currency) {
        this.currency = (Currency) currency;
    }

}
