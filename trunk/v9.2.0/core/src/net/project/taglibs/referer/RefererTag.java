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
package net.project.taglibs.referer;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * Stores the Referer Link
 */
public class RefererTag extends TagSupport {

    private String url = null ; 
    boolean outputter = false ;

    public RefererTag() {
        super();
        url = null;
    }

    public int doStartTag() throws JspException {

        if (outputter) {
            try {
                JspWriter out = pageContext.getOut();
                out.print(getURL());
            } catch(IOException ioe) {
              throw new JspTagException ("I/O exception: " + ioe);
            }
        }
            return(SKIP_BODY);
    }

    public String getURL() {
        String temp =  (String)pageContext.getAttribute("pnet_refLink",PageContext.SESSION_SCOPE) ; 
        this.url = temp != null ? temp  :"" ; 
        return this.url;
    }

    public void setURL(String url) {
        pageContext.setAttribute("pnet_refLink",url,PageContext.SESSION_SCOPE);
    }

    public void setOutputter(String value) {
        this.outputter = Boolean.valueOf(value).booleanValue();
    }
    
}
