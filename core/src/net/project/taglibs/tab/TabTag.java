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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.gui.tab.Tab;
import net.project.gui.tab.TabStrip;
import net.project.util.Conversion;

/**
 * Provides the presentation of a single tab on a tab strip.
 */
public class TabTag extends BodyTagSupport {

    /** Label to display on tab */
    private String label = null;

    /** Label token to display on tab */
    private String labelToken = null;

    /** href when tab is clicked */
    private String href = null;

    /** whether tab is selected or not */
    private Boolean isSelected = null;

    /** indicates whether to display this tab or not. */
    private Boolean isDisplay = null;

    /** indicates whether this tab is clickable. */
    private Boolean isClickable = null;

    /**
     * Start of tag.
     * Skips the body if this tab is not selected; otherwise, evaluates the
     * body.
     * @return {@link #EVAL_BODY_BUFFERED} if this tab is selected; {@link #SKIP_BODY}
     * if this tag is not selected
     */
    public int doStartTag() throws JspTagException {

        // Find the outer tabstrip
        TabStripTag tabStripTag = (TabStripTag) findAncestorWithClass(this, TabStripTag.class);
        if (tabStripTag == null) {
            throw new JspTagException("Error in tab tag: tab not inside tab strip");
        }

        // Grab the TabStrip from the TabStripTag
        // and create a new Tab
        // The new tab is automatically added to the collection of tabs
        TabStrip tabStrip = tabStripTag.getTabStrip();
        Tab tab = tabStrip.newTab();

        // Now set all the attributes
        if (label != null) {
            tab.setLabel(label);
        }

        if (labelToken != null) {
            tab.setLabelToken(labelToken);
        }

        if (href != null) {
            tab.setHref(href);
        }

        if (this.isDisplay != null) {
            tab.setDisplay(isDisplay.booleanValue());
        }

        if (this.isClickable != null) {
            tab.setClickable(isClickable.booleanValue());
        }

        if (isSelected != null) {
            // The "selected" attribute was specified; always use
            tab.setSelected(isSelected.booleanValue());

        }

        // If this tab has been selected then we evaluate the body
        // Otherwise, we skip the body
        if (tab.isSelected()) {
            return EVAL_BODY_BUFFERED;
        } else {
            return SKIP_BODY;
        }
    }


    /**
     * Grabs the body content and stores it so that it may be output
     * by the enclosing tab stip after all tags are drawn.
     * @return {@link #SKIP_BODY}
     * @throws JspException if there is a problem finding the enclosing tab
     * strip tag
     */
    public int doAfterBody() throws JspException {

        // Find the outer tabstrip
        TabStripTag tabStripTag = (TabStripTag) findAncestorWithClass(this, TabStripTag.class);
        if (tabStripTag == null) {
            throw new JspTagException("Error in tab tag: tab not inside tab strip");
        }

        // Grab the body content and pass it to the tab strip
        // So that it may be printed after drawing all the tabs
        BodyContent bc = getBodyContent();
        tabStripTag.setTabContent(bc.getString());

        return SKIP_BODY;
    }

    /**
     * End of tag.
     * Clears this Tag's attributes so it may be reused
     * @return {@link javax.servlet.jsp.tagext.Tag#EVAL_PAGE}
     * @throws JspTagException if the tab tag is not defined nested within
     * a tab strip tag.
     */
    public int doEndTag() throws JspTagException {
        clear();
        return EVAL_PAGE;
    }

    public void release() {
        clear();
    }

    //
    // Attribute set methods
    //


    /**
     * Sets the display label for this tab.
     * @param label the label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Sets the display label for this tab as a token name.
     * @param labelToken the token for the label
     */
    public void setLabelToken(String labelToken) {
        this.labelToken = labelToken;
    }

    /**
     * Sets the Href for this tab available when the tab is not selected.
     * @param href the href
     */
    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Indicates whether this tab should be drawn as currently selected.
     * @param selected <code>"true"</code> means the tab will be selected; <code>"false"</code> means it
     * will be unselected
     */
    public void setSelected(String selected) {
        this.isSelected = new Boolean(Conversion.toBool(selected));
    }

        /**
     * Indicates whether this tab should be drawn as currently selected.
     * @param clickable <code>"true"</code> means the tab will be selected; <code>"false"</code> means it
     * will be unselected
     */
    public void setClickable(String clickable) {
        this.isClickable = new Boolean(Conversion.toBool(clickable));
    }

    /**
     * Indicates whether this tab should be displayed or not.
     * @param isDisplay true if this tab should be displayed; false if not
     * Defaults to <code>true</code>
     */
    public void setDisplay(Boolean isDisplay) {
        this.isDisplay = isDisplay;
    }

    public void setDisplay(boolean isDisplay) {
        this.isDisplay = new Boolean(isDisplay);
    }



    //
    // Utility Methods
    //


    /**
     * Clears all properties of this tab.
     */
    private void clear() {
        label = null;
        labelToken = null;
        href = null;
        isSelected = null;
        isDisplay = null;
        isClickable = null;
    }

}
