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

package net.project.taglibs.display;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.property.PropertyProvider;
import net.project.security.AuthorizationFailedException;
import net.project.security.SessionManager;


public class GetTag extends TagSupport {
    
    private String name = null;
    private String type = null;
    private String defaultText = null;
    private String ifExists = null;
    private String href = null;
    private String enableLink = null;
    private String className = null;
    private String target = null;
    
    /** Arguments passed to property provider, set through use of param tags */
    private ArrayList paramValues = new ArrayList();
    
    /* -------------------------------  Constructors  ------------------------------- */
    
    public GetTag() {
        super();
    }
    
    
    /* -------------------------------  Implementing setters and getters  ------------------------------- */
    
    public void setName(String name) {
        this.name = name;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setDefault(String defaultText) {
        this.defaultText = defaultText;
    }
    
    public void setIf(String ifToken) {
        this.ifExists = ifToken;
    }
    
    public void setHref(String s) {
        href = s;
    }
    
    public void setCssClass(String s) {
        className = s;
    }
    
    public void setTarget(String s) {
        target = s;
    }
    
    public void setEnableLink(String token) {
        this.enableLink = token;
    }
    
    /* -------------------------------  Overriding TagSupport methods  ------------------------------- */

    public int doStartTag() {
        return EVAL_BODY_INCLUDE;
    }
    
    public int doEndTag() throws JspTagException, AuthorizationFailedException {
        JspWriter out = pageContext.getOut();
        
        try {
            lookup(out);
            this.ifExists = null;
        }
        catch (IOException ioe) {
            throw new JspTagException("I/O exception: " + ioe);
        }
        
        clear();
        
        return EVAL_PAGE;
    }
    
    public void release() {
        super.release();
        clear();
    }
    
    
    /* -------------------------------  Implementing utility methods  ------------------------------- */
    
    
    private void lookup(JspWriter out) throws IOException {
        String value = null;
        String css = null;
        boolean display = false;
        
        if (this.ifExists == null || this.ifExists.equals(""))
            display = true;
        else {
            display = PropertyProvider.getBoolean(this.ifExists);
        }
        
        
        if (display) {
            
            // if enableLink has been specified, then see if it is true or not, else show it if href is defined
            boolean showLink = (this.enableLink != null && !this.enableLink.equals("")) ? PropertyProvider.getBoolean(this.enableLink) : true;
            
            css = PropertyProvider.isToken(className) ? getValue("class", PropertyProvider.get(className)) : getValue("class", className);
            
            // Get the actual value for the named property, inserting parameter values
            // if necessary
            if (this.paramValues.size() > 0) {
                value = PropertyProvider.get(this.name, this.paramValues.toArray());

            } else {
                value = PropertyProvider.get(this.name);
            
            }
            
            // if there is no value for the token, then try to insert the default text if available and make it
            // link to the add token screen to create the token
            if (value == null) {
                value = (this.defaultText != null) ? this.defaultText : "";
                
                value += showAddTokenLink();
                
            } // end if value == null
            
            else if (this.href != null && !this.href.equals("") && showLink) {
                
                String hrefTag = ( PropertyProvider.isToken(this.href) ) ? PropertyProvider.get(this.href) : this.href;
                
                String hrefTarget = ( PropertyProvider.isToken(this.target) ) ? PropertyProvider.get(this.target) : this.target;
                hrefTarget = getValue("target", hrefTarget);
                
                value = "<a href=" + hrefTag + " " + hrefTarget + " " + css + ">" + value + "</a>";
                
            } // end if value != null
            
            else if (this.className != null && !this.className.equals("")) {
                value = "<span " + css + ">" + value + "</span>";
            }
            
            out.print(value);
        } // end if (display)
        
    }
    
    private String showAddTokenLink() {
        
        String text = "";
        
        text += "&nbsp;<a href='" + SessionManager.getJSPRootURL() + "/configuration/brand/AddToken.jsp?tokenName=" + this.name +
        "&contextID=" + PropertyProvider.getActiveBrandID() + "&language=" + PropertyProvider.getActiveLanguage() +"&refresh=true&goBack=true' >" + this.name + "</a>";
        
        return text;
    }
    
    
    private String getValue(String attributeName, String value) {
        return (value == null || value.equals("")) ? "" : attributeName + "= '" + value + "' ";
    }
    
    /**
     * Returns the list of parameter values being maintained by this Get Tag.
     * @return parameter values
     */
    List getParamValues() {
        return this.paramValues;
    }

    private void clear() {
        this.name = null;
        this.type = null;
        this.ifExists = null;
        this.href = null;
        this.enableLink = null;
        this.defaultText = null;
        this.className = null;
        this.target = null;
        this.paramValues = new ArrayList();
    }

}
