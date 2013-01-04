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
package net.project.taglibs.pager;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.gui.pager.PageIndex;
import net.project.xml.XMLFormatException;
import net.project.xml.XMLFormatter;

/**
 * The IndexTag provides rendering of the index links on a paged list.
 * <p>
 * For example: <code><pre>
 * <a href="#">&lt;Previous</a> <a href="#">1</a> 2 <a href="#">3</a> <a href="#">Next&gt;</a>
 * </pre></code>
 * </p>
 */
public class IndexTag extends TagSupport {

    /**
     * The Href to include in links.
     */
    private String href = null;

    /**
     * The stylesheet to use for rendering.
     */
    private String stylesheet = null;

    /**
     * The maximum number of index links to display.
     */
    private int maxLinks = 0;

    /**
     * Indicates whether to display the index.
     */
    private boolean isDisplayIf = true;

    /**
     * The core index bean that handles all functions.
     */
    private PageIndex pageIndex = null;

    /**
     * Creates an empty IndexTag.
     */
    public IndexTag() {
        super();
    }

    //
    // Attribute setters
    //

    /**
     * Sets the Href to use when rendering links (<i>Required</i>).
     * @param href the Href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Sets the stylesheet to use when rendering the page index (<i>Required</i>).
     * @param stylesheet the path to stylesheet
     */
    public void setStylesheet(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    /**
     * Specifies the maximum number of index links to render (<i>Optional</i>).
     * <p>
     * <b>Caution:</b> Causing links to display may require additional values
     * to be fetched from the <code>IPageable</code> bean in order to determine
     * whether a particular page number is available.  However, it is up to
     * the <code>IPageable</code> implementor to determine how this is performed.
     * </p>
     * @param maxLinks the maximum number of index links to render; value must
     * be >= 0; default value is <code>0</code>
     */
    public void setMaxLinks(int maxLinks) {
        this.maxLinks = maxLinks;
    }

    /**
     * Specifies whether to display the index or not (<i>Optional</i>).
     * @param isDisplayIf true causes the index to be displayed;
     * default value is <code>true</code>
     */
    public void setDisplayIf(boolean isDisplayIf) {
        this.isDisplayIf = isDisplayIf;
    }

    //
    // Tag handlers
    //

    /**
     * Checks to ensrure this tag is inside a PagerTag.
     * @return <code>EVAL_BODY_INCLUDE</code>
     * @throws JspTagException if an ancestor PagerTag is not found
     */
    public int doStartTag() throws JspTagException {
        
        PagerTag pagerTag = (PagerTag) findAncestorWithClass(this, PagerTag.class);
        if (pagerTag == null) {
            throw new JspTagException("Error in index tag: not inside pager tag");
        }

        // Grab the PageIndex object that will handle all functions
        this.pageIndex = new PageIndex();
        this.pageIndex.setPagerBean(pagerTag.getPagerBean());
        this.pageIndex.setHref(this.href);
        this.pageIndex.setMaxIndexLinks(this.maxLinks);
        
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Renders the page index if required.
     * @return <code>EVAL_PAGE</code>
     * @throws JspTagException if there is a problem transforming the XML
     * or writing it the the output
     * @see #setDisplayIf
     */
    public int doEndTag() throws JspTagException {
        
        try {

            // Only render if flag is set
            if (this.isDisplayIf) {
                JspWriter out = pageContext.getOut();
                
                // Format the combined XML using the set stylesheet
                // and print it out
                XMLFormatter formatter = new XMLFormatter();
                formatter.setStylesheet(this.stylesheet);
                formatter.setXML(this.pageIndex.getXML());
                out.print(formatter.getPresentation());
            }

        } catch (XMLFormatException e) {
            throw new JspTagException("Error transforming XML in page index tag: " + e);

        } catch (IOException e) {
            throw new JspTagException("Error displaying pager index tag: " + e);

        } finally {
            // Clear out so that Tag object can be reused
            clear();

        }

        return EVAL_PAGE;
    }

    //
    // Utility methods
    //

    /**
     * Clears out all properties so that this tag may be reused.
     */
    private void clear() {
        this.href = null;
        this.stylesheet = null;
        this.maxLinks = 0;
        this.isDisplayIf = true;
        this.pageIndex = null;
    }

}
