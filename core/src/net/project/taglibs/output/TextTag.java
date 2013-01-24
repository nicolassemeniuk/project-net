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
package net.project.taglibs.output;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.security.User;
import net.project.util.HTMLUtils;
import net.project.util.TextFormatter;

/**
 * Provides a way of processing text for display.
 * <ul>
 * <li>wrapColumn - the column number at which to wrap text; line breaks never
 * occur in the middle of a word
 * <li>class - the CSS class to apply to the text; this should almost always
 * be specified since some processing may cause the text appearance to change
 * (such as use of the &lt;pre /&gt; tag)
 * </ul>
 */
public class TextTag extends BodyTagSupport {

    /**
     * The column at which to wrap.
     */
    private Integer wrapColumn = null;

    /**
     * The CSS class to apply where appropriate.
     */
    private String cssClass = null;

    /**
     * Indicates whether to parse the text for hyperlinks.
     */
    private Boolean isHyperlink = null;

    /**
     * Creates an empty TextTag.
     */
    public TextTag() {
        super();
    }

    /**
     * Evaluates Body.
     * @return {@link #EVAL_BODY_BUFFERED} always
     * @throws JspException if there is a problem
     */
    public int doStartTag() throws JspException {
        return EVAL_BODY_BUFFERED;
    }

    /**
     * Processes the tag body and prints the processed content.
     * @return {@link #EVAL_PAGE} always.
     * @throws JspException
     */
    public int doEndTag() throws JspException {

        try {
            JspWriter out = pageContext.getOut();
            out.print(process(getBodyContent().getString()));

        } catch (IOException ioe) {
            throw new JspTagException("Error displaying input text element: " + ioe);

        } finally {
            // Clear all attributes for re-use of this tag
            clear();
        }

        return EVAL_PAGE;
    }

    /**
     * Processes the content.
     * @param content the text to process
     * @return  the processed content, which may include HTML tags necessary
     * for displaying the content
     */
    private String process(String content) {

        // Format the text
        // Wraps the text if a wrap column was specified
        // Looks for hyperlinks
        String formattedText = null;

        if (this.wrapColumn != null) {
            // Wrap and convert to HTML (line breaks etc.)
            formattedText = HTMLUtils.formatHtml(TextFormatter.adjustRightColumn(content, this.wrapColumn.intValue(), getUser().getLocale()));
        } else {
            // Simply covnert to HTML (line breaks etc.)
            formattedText = HTMLUtils.formatHtml(content);

        }
        if (isHyperlink != null && isHyperlink.booleanValue()) {
            formattedText = TextFormatter.makeHyperlinkable(formattedText);
        }

        // Now produce resultant HTML
        StringBuffer result = new StringBuffer();

        result.append("<span");
        if (this.cssClass != null) {
            result.append(" class=\"").append(this.cssClass).append("\"");
        }
        result.append(">");
        result.append(formattedText);
        result.append("</span>");

        return result.toString();
    }

    /**
     * Returns the current user context.
     * @return the current user from the session scope
     */
    private User getUser() {
        return (User) pageContext.getAttribute("user", PageContext.SESSION_SCOPE);
    }

    /**
     * Clears the values in this tag for reuse.
     */
    private void clear() {
        this.wrapColumn = null;
        this.cssClass = null;
        this.isHyperlink = null;
    }

    //
    // Attribute Setters
    //

    /**
     * Specifies the column at which to wrap text.
     * @param wrapColumn the column to wrap text at.
     */
    public void setWrapColumn(Integer wrapColumn) {
        this.wrapColumn = wrapColumn;
    }

    /**
     * Sepcifies the CSS class to apply where appropriate.
     * @param cssClass the CSS class
     */
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * Indicates whether to parse for hyperlinks.
     * A hyperlink is found when <code>http://</code>, <code>https://</code>
     * <code>www.</code> or <code>mailto:</code> is found in the text.
     * @param isHyperlink true if we should parse for hyperlinks; false otherwise.
     * Default value is false.
     */
    public void setHyperlink(Boolean isHyperlink) {
        this.isHyperlink = isHyperlink;
    }
}
