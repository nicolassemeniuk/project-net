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

/**
  * Display tag.
  * This tag has no attributes.  It simply draws the previously defined history
  * object.
  */
public class DisplayTag extends TagSupport {

    /**
      * Start of tag.
      * @return flag indicating whether to process body or not
      */
    public int doStartTag() {
	return SKIP_BODY;
    }

    /**
      * End of tag.
      * Here the history is retrieved from the session and drawn.
      * @return flag indicating whether to continue processing page
      * @throws JspTagException if there is a problem locating the history in
      * the session or if there is a problem displaying the history.
      */
    public int doEndTag() throws JspTagException {
        JspWriter out;
        String presentation = null;
        History history = null;

        /* Grab history object from page context */
        history = (History) pageContext.getAttribute(HistoryTag.HISTORY_OBJECT_VARIABLE, PageContext.SESSION_SCOPE);
        if (history == null) {
                throw new JspTagException("Error in history display tag, no previous history definition found.");
        }

        /* Draw history */
        try {
            try {
                presentation = history.getPresentation();
            } catch (HistoryException he) {
                throw new JspTagException("Error getting history presentation: " + he);
            }
            
            out = pageContext.getOut();
            try {
                out.print(presentation);

            } catch (IOException ioe) {
                throw new JspTagException("Error in history tag: " + ioe);

            }
        } finally {
            clear();
        }
        return EVAL_PAGE;
    }

    /*
        Other methods
     */

    /**
      * Clear all attributes.
      */
    private void clear() {
        /* Nothing to do */
    }

}


