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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Provides a base class for all HTML input-based tags.
 * See an <a href="http://www.htmlhelp.com/reference/html40/">HTML 4.0</a>
 * reference for more details on the allowable attributes.
 */
public abstract class AbstractInputTag extends TagSupport {

    /**
     * The map of attribute name to value.
     */
    private Map attributeValueMap = new HashMap();
    private InputTagFilter filter = null;


    /**
     * Prints the HTML input element.
     * @return {@link #SKIP_BODY} always
     * @throws JspException if there is a problem writing the content
     */
    public int doStartTag() throws JspException {
        try {
            if (filter != null) {
                filter.filter(attributeValueMap);
            }

            JspWriter out = pageContext.getOut();
            out.print(constructInputElement());
        } catch (IOException ioe) {
            throw new JspTagException("Error displaying input text element: " + ioe);

        } finally {
            // Clear all attributes for re-use of this tag
            clearAll();
        }

        return EVAL_BODY_INCLUDE;
    }

    public int doEndTag() throws JspException {
        try {
            JspWriter out = pageContext.getOut();
            out.print(closeInputElement());
        } catch (IOException e) {
            throw new JspTagException("Error displaying input text element: " + e);
        } finally {
            clearAll();
        }

        return EVAL_PAGE;
    }

    protected String closeInputElement() {
        return "";
    }

    /**
     * Returns the attribute value map.
     * Each key is a <code>String</code> attribute name; each value
     * is either a <code>String</code>, <code>Boolean</code> or <code>Integer</code>.
     * @return the attribute value maps
     */
    protected Map getAttributeValueMap() {
        return this.attributeValueMap;
    }

    /**
     * Constructs the HTML for the input element.
     * Sub-classes should construct the appropriate HTML input element
     * from the current attributes.
     * @return the input text element HTML code
     * @throws JspTagException if there is a problem constructing the input element
     */
    protected abstract String constructInputElement() throws JspTagException;

    /**
     * Clears out all values set in this tag.
     * This allows the tag instance to be re-used.
     * Calls {@link #clear} to allow sub-classes to clear their custom
     * attributes.
     */
    protected void clearAll() {
        this.attributeValueMap = new HashMap();
        clear();
    }

    /**
     * Sub-classes should clear all their custom attributes.
     * This is called to prepare the tag instance for re-use.
     */
    protected abstract void clear();

    /**
     * Constructs a string containing all attributes in this element.
     * <p>
     * Note that for non-boolean attributes, {@link #process(java.lang.String, java.lang.Object)}
     * is called for each attribute.
     * </p>
     * @return the attributes as a space-separated string of <code>attributename="value"</code>,
     * except for boolean attributes which are present for <code>true</code> and absent for <code>false</code>
     * @see #process(java.lang.String, java.lang.Object)
     */
    protected String formatAllAttributes() {
        StringBuffer attributeText = new StringBuffer();

        // Iterate over all attributes, adding to HTML element
        for (Iterator it = getAttributeValueMap().keySet().iterator(); it.hasNext();) {
            String nextAttribute = (String) it.next();
            Object nextValue = getAttributeValueMap().get(nextAttribute);

            if (nextValue instanceof Boolean) {
                // For Boolean attributes, we simply use the attribute name
                // If it is set to true
                // For example: DISABLED or READONLY
                if (((Boolean) nextValue).booleanValue()) {
                    attributeText.append(" ").append(nextAttribute);
                }

            } else {
                // All other attributes we use the attribute name and value
                attributeText.append(" ").append(nextAttribute).append("=\"")
                        .append(process(nextAttribute, nextValue))
                        .append("\"");

            }

        }

        return attributeText.toString();
    }

    /**
     * Processes the specified attribute and value while formatting
     * all attributes.
     * <p>
     * Sub-classes should override this to apply and special handling
     * to particular attributes. <br>
     * The default implementation performs no action; that is, value
     * is returned unmodified.
     * </p>
     * @param attributeName the attribute to which the value belongs
     * @param value the value for the attribute
     * @return the processed value
     * @see #formatAllAttributes() 
     */
    protected Object process(String attributeName, Object value) {
        return value;
    }

    //
    // Attribute setters
    //

    // NOTE: attribute "type" is conspicuous by its absence; the goal
    // is to have sub classes representing the various types

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>name</code> attribute.
     * @param name
     */
    public void setName(String name) {
        this.attributeValueMap.put("name", name);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>Value</code> attribute.
     * This will be escaped to allow double and single quotes in the value.
     * @param value
     */
    public void setValue(String value) {
        this.attributeValueMap.put("value", value);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>CHECKED</code> attribute.
     * @param isChecked true if this input element's CHECKED attribute
     * should be included
     */
    public void setChecked(Boolean isChecked) {
        this.attributeValueMap.put("checked", isChecked);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>size</code> attribute.
     * @param size
     */
    public void setSize(Integer size) {
        this.attributeValueMap.put("size", size);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>maxlength</code> attribute.
     * @param maxLength
     */
    public void setMaxLength(Integer maxLength) {
        this.attributeValueMap.put("maxlength", maxLength);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>src</code> attribute.
     * @param src the URI source for an image
     */
    public void setSrc(String src) {
        this.attributeValueMap.put("src", src);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>alt</code> attribute.
     * @param alt the alt text
     */
    public void setAlt(String alt) {
        this.attributeValueMap.put("alt", alt);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>usemap</code> attribute.
     * @param useMap the URI for a client-side image map
     */
    public void setUseMap(String useMap) {
        this.attributeValueMap.put("usemap", useMap);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>DISABLED</code> attribute.
     * @param isDisabled
     */
    public void setDisabled(Boolean isDisabled) {
        this.attributeValueMap.put("disabled", isDisabled);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>DISABLED</code> attribute.
     * @param isDisabled
     */
    public void setDisabled(boolean isDisabled) {
        this.attributeValueMap.put("disabled", new Boolean(isDisabled));
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>READONLY</code> attribute.
     * @param isReadOnly
     */
    public void setReadOnly(Boolean isReadOnly) {
        this.attributeValueMap.put("readonly", isReadOnly);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>tabIndex</code> attribute.
     * @param tabIndex
     */
    public void setTabIndex(Integer tabIndex) {
        this.attributeValueMap.put("tabindex", tabIndex);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>accept</code> attribute.
     * @param accept the ContentTypes
     */
    public void setAccept(String accept) {
        this.attributeValueMap.put("accesskey", accept);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>accessKey</code> attribute.
     * @param accessKey
     */
    public void setAccessKey(String accessKey) {
        this.attributeValueMap.put("accesskey", accessKey);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>onFocus</code> attribute.
     * @param scriptForOnFocus
     */
    public void setOnFocus(String scriptForOnFocus) {
        this.attributeValueMap.put("onfocus", scriptForOnFocus);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>onSelect</code> attribute.
     * @param scriptForOnSelect
     */
    public void setOnSelect(String scriptForOnSelect) {
        this.attributeValueMap.put("onselect", scriptForOnSelect);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>onBlur</code> attribute.
     * @param scriptForOnBlur
     */
    public void setOnBlur(String scriptForOnBlur) {
        this.attributeValueMap.put("onblur", scriptForOnBlur);
    }

    /**
     * Sets value for the HTML <code>input</code> element's
     * <code>onChange</code> attribute.
     * @param scriptForOnChange
     */
    public void setOnChange(String scriptForOnChange) {
        this.attributeValueMap.put("onchange", scriptForOnChange);
    }

    //
    // Other common HTML attributes
    //

    /**
     * Sets the <code>id</code> attribute value.
     * <p/>
     * The setter is named differently because Bluestone doesn't permit
     * a request-time taglib attribute called <code>id</code>
     * @param id the id attribute value
     */
    public void setElementID(String id) {
        this.attributeValueMap.put("id", id);
    }

    public void setClass(String classValue) {
        this.attributeValueMap.put("class", classValue);
    }

    public void setStyle(String style) {
        this.attributeValueMap.put("style", style);
    }

    public void setTitle(String title) {
        this.attributeValueMap.put("title", title);
    }

    public void setLang(String lang) {
        this.attributeValueMap.put("lang", lang);
    }

    public void setDir(String dir) {
        this.attributeValueMap.put("dir", dir);
    }

    public void setOnClick(String onClick) {
        this.attributeValueMap.put("onclick", onClick);
    }

    public void setOnDBLClick(String onDBLClick) {
        this.attributeValueMap.put("ondblclick", onDBLClick);
    }

    public void setOnMouseDown(String onMouseDown) {
        this.attributeValueMap.put("onmousedown", onMouseDown);
    }

    public void setOnMouseUp(String onMouseUp) {
        this.attributeValueMap.put("onmouseup", onMouseUp);
    }

    public void setOnMouseOver(String onMouseOver) {
        this.attributeValueMap.put("onmouseover", onMouseOver);
    }

    public void setOnMouseMove(String onMouseMove) {
        this.attributeValueMap.put("onmousemove", onMouseMove);
    }

    public void setOnMouseOut(String onMouseOut) {
        this.attributeValueMap.put("onmouseout", onMouseOut);
    }

    public void setOnKeyPress(String onKeyPress) {
        this.attributeValueMap.put("onkeypress", onKeyPress);
    }

    public void setOnKeyDown(String onKeyDown) {
        this.attributeValueMap.put("onkeydown", onKeyDown);
    }

    public void setOnKeyUp(String onKeyUp) {
        this.attributeValueMap.put("onkeyup", onKeyUp);
    }

    public void setFilter(InputTagFilter filter) {
        this.filter = filter;
    }

    /**
     * Repalces the double quotes in the specified value.
     * Replaced with <code>&amp;#034;</code>.
     * @param value the value to replace quotes in
     * @return the value with all quotes replaced or null if the value
     * was null
     */
    protected String replaceDoubleQuotes(String value) {

        String result = null;

        if (value != null) {
            // Replace all " with &#034;
            result = value.replaceAll("\"", "&#034;");

        }

        return result;
    }
}
