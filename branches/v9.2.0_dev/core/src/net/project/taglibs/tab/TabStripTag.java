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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.taglibs.tab;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.gui.tab.TabException;
import net.project.gui.tab.TabStrip;

/**
 * Provides a Tab strip taglib to which tabs may be added.
 * <p>
 * Please <a href="doc-files/TabStripTag-usage.html">read the documentation</a> for usage instructions.
 * </p>
 *
 * @author Tim Morrow
 * @since Version 2.0
 */
public class TabStripTag extends TagSupport {

    /**
     * TabStrip object being created.
     * This does all the work of producing the tab strip presentation.
     */
    private TabStrip tabStrip = null;

    /**
     * The content of the selected tab.
     * This will be output after the tabs are drawn.
     */
    private String tabContent = null;

    /**
     * The width of the tab strip.
     */
    private String width = null;

    /**
     * Start of tag.
     * @return {@link #EVAL_BODY_INCLUDE}
     */
    public int doStartTag() {
        this.tabStrip = new TabStrip();
        return EVAL_BODY_INCLUDE;
    }

    /**
     * End of tag.
     * Draws tab strip and clears all attributes so this tag may be reused.
     * @return {@link javax.servlet.jsp.tagext.Tag#EVAL_PAGE}
     * @throws JspTagException if there is a problem displaying the history.
     */
    public int doEndTag() throws JspTagException {
        JspWriter out;

        try {

            out = pageContext.getOut();

            try {
                //Set the width of the tab strip
                tabStrip.setWidth(width);

                // First print the tab strip
                out.print(this.tabStrip.getPresentation());

                // Now print the content of the selected tab (if any)
                if (this.tabContent != null) {
                    out.print(this.tabContent);
                }

            } catch (TabException te) {
                throw new JspTagException("Error getting tab strip presentation: " + te);

            } catch (IOException ioe) {
                throw new JspTagException("Error in tab strip tag: " + ioe);

            }

        } finally {
            clear();

        }

        return EVAL_PAGE;
    }

    public void release() {
        clear();
    }

    //
    // Utility methods
    //


    /**
     * Clear all attributes.
     */
    private void clear() {
        this.tabStrip = null;
        this.tabContent = null;
        this.width = null;
    }

    /**
     * Return the tab strip that is being built.  This is called from nested
     * tags.
     * @return the tab strip object
     */
    TabStrip getTabStrip() {
        return this.tabStrip;
    }

    /**
     * Sepcifies the tab content to draw after the tabs (if any).
     * @param tabContent the tab content
     */
    void setTabContent(String tabContent) {
        this.tabContent = tabContent;
    }

    /**
     * Get the width of the tab strip table.
     *
     * @return a <code>String</code> which will be inserted into the width=""
     * statement which creates the table.
     */
    public String getWidth() {
        return width;
    }

    /**
     * Set the width of the tab strip table.  If null or blank, the width will
     * not be specified.
     *
     * @param width a <code>String</code> value indicating the width of the tab
     * strip table.  A null or blank value will prevent a width attribute from
     * being added.
     */
    public void setWidth(String width) {
        this.width = width;
    }
}
