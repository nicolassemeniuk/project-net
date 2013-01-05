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
package net.project.taglibs.history;

import java.io.IOException;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.gui.history.History;
import net.project.gui.history.HistoryException;
import net.project.util.Conversion;

/**
  * History tag.<br>
  * This tag allows the definition and insertion of history.
  */
public class HistoryTag extends TagSupport {

    /** Name of page context attribute storing history object */
    static final String HISTORY_OBJECT_VARIABLE = "historyTagHistoryObject";
    static final String HISTORY_OBJECT_CLASS = "net.project.gui.history.History";

    /** History object being created */
    private History history = null;

    /** style attribute */
    private String style = null;
    /** stylesheet attribute */
    private String stylesheet = null;
    /** target attribute */
    private String target = null;
    /** displayHere attribute */
    private boolean displayHere = true;

    /** Track whether displayHere has been set yet */
    private boolean isDisplayHereSet = false;
    
    /**
      * Start of tag.  Called after the attributes are set.
      * Attempts to retrieve an existing history from the session.  If not found
      * a new one is created then placed into the session.
      * @return flag indicating whether to process body or not
      */
    public int doStartTag() {
        history = (History) pageContext.getAttribute(HISTORY_OBJECT_VARIABLE, PageContext.SESSION_SCOPE);
        if (history == null) {
            history = new History();
            pageContext.setAttribute(HISTORY_OBJECT_VARIABLE, history, PageContext.SESSION_SCOPE);
        }

        /* Continue evaluating tag body */
	return EVAL_BODY_INCLUDE;
    }

    /**
      * End of tag.
      * If the displayHere attribute is set, the history is drawn.
      * @return flag indicating whether to continue processing page
      * @throws JspTagException if there is a problem displaying the history.
      */
    public int doEndTag() throws JspTagException {
        JspWriter out;
        String presentation = null;
        
        /* Set all the attributes in the history class */
        setHistoryAttributes();

        try {
            if (displayHere) {

                /* Get the presentation */
                try {
                    presentation = history.getPresentation();

                } catch (HistoryException he) {
                    throw new JspTagException("Error getting presentation: " + he);

                }

                /* Output history now */
                out = pageContext.getOut();
                try {
                    out.print(presentation);

                } catch (IOException ioe) {
                    throw new JspTagException("Error in history tag: " + ioe);

                }
            }
            
        } finally {
            // Reset history attributes and taglib attributes
            history.clear();
            clear();
        }
        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setStyle(String style) {
        this.style = style;
    }
    public void setStylesheet(String stylesheetPath) {
        this.stylesheet = stylesheetPath;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public void setDisplayHere(String displayHere) {
        this.displayHere = Conversion.toBool(displayHere);
        this.isDisplayHereSet = true;
    }

    /*
        Other methods
     */

    /**
      * Clear all attributes
      */
    private void clear() {
        style = null;
        stylesheet = null;
        displayHere = true;
        target = null;
        isDisplayHereSet = false;
    }

    /**
      * Set this tags attributes in the history object
      */
    private void setHistoryAttributes() {
        if (style != null) {
            history.setStyle(style);
        }
        if (stylesheet != null) {
            history.setStylesheet(stylesheet);
        }
        if (target != null) {
            history.setTarget(target);
        }
    }

    /**
      * Return the history that is being built.  This is called from nested
      * tags (to minimize their need to access session).
      * @return the history object
      */
    History getHistory() {
        return this.history;
    }

}

