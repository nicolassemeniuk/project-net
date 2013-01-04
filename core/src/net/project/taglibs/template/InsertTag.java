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

import net.project.security.SessionManager;

/*
 * Insert tag dynamically includes a specified JSP page
 */
public class InsertTag extends TagSupport {
    
    /** Name of request attribute used for maintaining context */
    static final String TEMPLATE_INSERT_TAG_STACK_ATTRIBUTE = "template-stack";


    /** the name of the templage page to dynamically include */
    private String template;
    
    /** the stack of hashes used to permit nesting of insert tags */
    private Stack stack;
    
    /**
      * Start of insert tag.  This creates a new entry in the stack.
      * @throws JspTagException if there is a problem
      */
    public int doStartTag() throws JspTagException {
        stack = getStack(); 
        stack.push(new HashMap()); 
        return EVAL_BODY_INCLUDE;
    } 

    /**
      * End of insert tag. This includes the template JSP page.
      * @throws JspTagException if there is a problem including the template JSP.
      */
    public int doEndTag() throws JspTagException { 
        if (template == null) {
            template = SessionManager.getDefaultLayoutTemplate();
            if (template == null) {
                throw new JspTagException("Error inserting template. Required attribute 'template' not set and " +
                                          "no default value found in configuration file.");
            }
        }

        try {
            // Include the template
            pageContext.include(template);
        
        } catch (java.io.IOException ioe) { 
            throw new JspTagException("Error inserting template: " + ioe); 
        
        } catch (javax.servlet.ServletException se) {
            throw new JspTagException("Error inserting template: " + se); 

        } //end try
        
        stack.pop(); 
        clear();

        return EVAL_PAGE;
    }

    /**
      * Frees up resources so tag may be reused
      */
    public void release() {
        clear();
        stack = null;
    } 
    
    /*==========================================================================
        Setters for attributes
     =========================================================================*/

    /**
      * setter method for template attribute
      */
    public void setTemplate(String template) {
        this.template = template; 
    } 


    /*==========================================================================
        Additional methods
     =========================================================================*/

    /**
      * Get the template stack.
      * @return the stack
      */
    protected Stack getStack() {
        Stack s = null;

        if (this.stack == null) {
            // try to get stack from request scope 
            s = (Stack) pageContext.getAttribute(TEMPLATE_INSERT_TAG_STACK_ATTRIBUTE, 
                                                       PageContext.REQUEST_SCOPE); 
            // if the stack's not present, create a new one and 
            // put it into request scope 
            if (s == null) {
                s = new Stack(); 
                pageContext.setAttribute(TEMPLATE_INSERT_TAG_STACK_ATTRIBUTE, s, 
                                         PageContext.REQUEST_SCOPE); 
            }
            this.stack = s;
        } else {
        	//verify that request has stack assigned
        	s = (Stack) pageContext.getAttribute(TEMPLATE_INSERT_TAG_STACK_ATTRIBUTE, 
                    PageContext.REQUEST_SCOPE);
        	if (s == null) {
        		pageContext.setAttribute(TEMPLATE_INSERT_TAG_STACK_ATTRIBUTE, this.stack, 
                        PageContext.REQUEST_SCOPE);
        	}
        }


        return this.stack; 
    } 

    private void clear() {
        template = null;
    }

}

