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
package net.project.taglibs.template;

import java.util.HashMap;
import java.util.Stack;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.util.Conversion;

/**
  * Puts a default content into the template stack if there is not already
  * an entry for a specific name.
  */
public class PutDefaultTag extends BodyTagSupport {
    
    /** Name of section attribute */
    private String name = null;
    /** Content attribute */
    private String content = null;
    /** Specifies whether content is to be included to directly inserted */    
    private boolean isDirect = false;


    /**
      * Skips body if content attribute was specified.
      */
    public int doStartTag() throws JspTagException { 
        
        if (this.name == null) {
            throw new JspTagException("Error in PutDefaultTag: Required attribute 'name' not specified.");
        }

        if (this.content != null) {
            return SKIP_BODY;
        } else {
            this.isDirect = true;
            return EVAL_BODY_BUFFERED;
        }
    }
    
    /**
      * After body content
      * This stores the body content just processed
      */
    public int doAfterBody() {
        this.content = getBodyContent().getString();
        return (SKIP_BODY);
    }

    /**
      * Looks up last entry in template stack.  If section for name is NOT found,
      * adds entry.  If section for name is found, ignores content.
      */
    public int doEndTag() throws JspTagException {
        Stack stack = null;
        HashMap params = null;
        PageParameter param = null;

        // Get template stack from request
        stack = (Stack) pageContext.getAttribute(InsertTag.TEMPLATE_INSERT_TAG_STACK_ATTRIBUTE, PageContext.REQUEST_SCOPE); 
        if (stack == null) {
            throw new JspTagException("Error in PutDefaultTag: No template stack found in request.");
        }

        // peek at HashMap 
        params = (HashMap) stack.peek(); 
        if (params == null) {
            throw new JspTagException("Error in PutDefaultTag: No entry found in template stack.");
        }

        // get page parameter from HashMap 
        param = (PageParameter) params.get(this.name); 
        if (param == null) {
            // put a new PageParameter in the HashMap 
            params.put(name, new PageParameter(name, content, isDirect)); 
        }
        
        // clear out attributes
        clear();

        return EVAL_PAGE;
    }

    /**
      * Clean up
      */
    public void release() {
        clear();
    } 
    
    /*==========================================================================
        Setters for attributes
     =========================================================================*/
    
    public void setName(String name) {
        this.name = name;
    } 
    public void setContent(String content) {
        this.content = content;
    } 
    public void setDirect(String direct) {
        this.isDirect = Conversion.toBoolean(direct);
    }

    /*==========================================================================
        Additional methods
     =========================================================================*/

    private void clear() {
        name = null;
        content = null;
        isDirect = false;
    }
 }

