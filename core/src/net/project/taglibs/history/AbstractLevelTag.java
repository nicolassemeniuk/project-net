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
package net.project.taglibs.history;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.gui.history.History;
import net.project.gui.history.HistoryLevel;
import net.project.util.Conversion;

/**
  * Provides common functions for all history tags.
  */
public abstract class AbstractLevelTag extends TagSupport {

    /** Text to display in history */
    protected String display = null;

    /** Fully qualified Jsp page to navigate to when history item clicked */
    protected String jspPage = null;

    /** Query string to send in URL when history item clicked */
    protected String queryString = null;

    /** Inidcates whether to draw with anchor or just text */    
    protected boolean active = false;

    /** Inidcates whether to display this or not when drawing */
    protected boolean show = true;

    //
    // Tag attribute setters
    //

    /**
     * Sets the text to display on this history tag.
     * @param display the display text; or a token name, prefixed with the
     * token prefix
     * @see net.project.base.property.PropertyProvider#TOKEN_PREFIX
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * Sets a token name to use on the display.
     * @param displayToken the name of a token, without the token prefix.
     * @deprecated Use <code>setDisplay</code> instead, passing a token
     * INCLUDING a token prefix
     */
    public void setDisplayToken(String displayToken) {
        setDisplay(net.project.base.property.PropertyProvider.TOKEN_PREFIX + displayToken);
    }

    /**
     * Sets the URL to navigate to when history link clicked, excluding parameters.
     * @param jspPage the URL
     */
    public void setJspPage(String jspPage) {
        this.jspPage = jspPage;
    }

    /**
     * Specifies the QueryString to append to the URL.
     * @param queryString the query string
     */
    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
    
    /**
     * Specifies whether this level is active (has a hyperlink) or not.
     * @param active "true" means it can be clicked
     */
    public void setActive(String active) {
        this.active = Conversion.toBool(active);
    }

    /**
     * Specifies whether to display this history level or not.
     * @param show "true " means it is shown
     */
    public void setShow(String show) {
        this.show = Conversion.toBool(show);
    }

    /**
      * End of tag.
      * Adds this level to the history with all attributes set.
      * @return <code>EVAL_PAGE</code>
      * @throws JspTagException if this level tag is not defined nested within
      * a history tag.
      */
    public int doEndTag() throws JspTagException {

        // Grab parent HistoryTag
        HistoryTag historyTag = (HistoryTag) findAncestorWithClass(this, HistoryTag.class);
        if (historyTag == null) {
            throw new JspTagException("Error in level tag: not inside history tag");
        }

        // Grab the History object from the history tag
        History history = historyTag.getHistory();

        HistoryLevel level = getHistoryLevel();

        if (this.display != null) {
            if (net.project.base.property.PropertyProvider.isToken(this.display)) {
                level.setDisplayToken(net.project.base.property.PropertyProvider.stripTokenPrefix(this.display));
            } else {
                level.setDisplay(this.display);
            }
        }

        if (this.jspPage != null) {
            level.setJspPage(this.jspPage);
        }

        if (this.queryString != null) {
            level.setQueryString(this.queryString);
        }

        level.setActive(this.active);
        level.setShow(this.show);

        history.setLevel(level);

        return EVAL_PAGE;
    }

    /**
     * Returns the History Level subclass suitable for this level tag.
     * @return a HistoryLevel
     */
    public abstract HistoryLevel getHistoryLevel();

}
