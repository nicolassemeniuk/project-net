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
import javax.servlet.jsp.tagext.TagSupport;

/**
  * Inserts the content for a template section name.
  */
public class GetTag extends TagSupport {
    
    /** Name of section to include */
    private String name; 

    /**
      * Inserts the content for a the specified template section name.
      */
    public int doStartTag() throws JspTagException { 
        Stack stack = null;
        HashMap params = null;
        PageParameter param = null;

        if (this.name == null) {
            throw new JspTagException("Error in GetTag: Required attribute 'name' not found.");
        }

        // Get template stack from request
        stack = (Stack) pageContext.getAttribute(InsertTag.TEMPLATE_INSERT_TAG_STACK_ATTRIBUTE, PageContext.REQUEST_SCOPE); 
        if (stack == null) {
            throw new JspTagException("Error in GetTag: No template stack found in request.");
        }

        // peek at HashMap 
        params = (HashMap) stack.peek(); 
        if (params == null) {
            throw new JspTagException("Error in GetTag: No entry found in template stack.");
        }

        // get page parameter from HashMap 
        param = (PageParameter) params.get(this.name); 
        if (param != null) {
            String content = param.getContent(); 
            
            if (param.isDirect()) {
                // print content if direct attribute is true 
                try {
                    pageContext.getOut().print(content); 
                } catch (java.io.IOException ioe) {
                    throw new JspTagException("Error writing content in GetTag: " + ioe); 
                }
            
            } else {
                // include content if direct attribute is false 
                try {
                    pageContext.getOut().flush();
                    pageContext.include(content);
                } catch (java.io.IOException ioe) { 
                    throw new JspTagException("Error including content in GetTag: " + ioe); 
                } catch (javax.servlet.ServletException se) {
                    throw new JspTagException("Error including content in GetTag: " + se); 
                }
            
            } //end if
        
        } //end if
        
        return SKIP_BODY;
    } 

    public int doEndTag() throws JspTagException {
        clear();
        return EVAL_PAGE;
    }

    /**
      * Release resources so tag may be reused.
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

    /*==========================================================================
        Additional methods
     =========================================================================*/

    private void clear() {
        name = null;
    }
}
