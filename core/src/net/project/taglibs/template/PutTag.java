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
import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.util.Conversion;

/**
  * Puts the content of a template section into the template stack
  */
public class PutTag extends BodyTagSupport {
    
    /** Name of section attribute */
    private String name = null;
    /** Content attribute */
    private String content = null;
    /** Specifies whether content is to be included to directly inserted */    
    private boolean isDirect = false;


    /**
      * Ensures tag is within InsertTag.
      * If content attribute is set then the body is skipped, else it is
      * evaluated and assumed to be direct.
      */
    public int doStartTag() throws JspTagException { 
        InsertTag parent = null;

        // Locate the enclosing InsertTag
        parent = (InsertTag) findAncestorWithClass(this, InsertTag.class); 
        if (parent == null) {
            throw new JspTagException("Error in PutTag: Not inside InsertTag.");
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
      * Creates new section entry for current section name and content
      */
    public int doEndTag() throws JspTagException {
        InsertTag parent = null;
        Stack stack = null;
        HashMap params = null;
        
        // Locate the enclosing InsertTag
        parent = (InsertTag) findAncestorWithClass(this, InsertTag.class); 
        if (parent == null) {
            throw new JspTagException("Error in PutTag: Not inside InsertTag.");
        }
        
        // get template stack from insert tag 
        stack = parent.getStack(); 
        if (stack == null) {
            throw new JspTagException("Error in PutTag: No template stack found.");
        }

        // peek at HashMap on the stack 
        params = (HashMap) stack.peek(); 
        if (params == null) {
            throw new JspTagException("Error in PutTag: No entry found in template stack.");
        }

        // put a new PageParameter in the HashMap 
        params.put(name, new PageParameter(name, content, isDirect)); 
        
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
