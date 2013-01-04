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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.taglibs.pager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.gui.pager.IPageable;
import net.project.gui.pager.PagerBean;
import net.project.taglibs.TaglibUtils;

/**
 * The PagerTag provides a facility for initializing IPageable objects and
 * handling navigation through paged lists.
 * <p>
 * See <a href="doc-files/PagerTag-Usage.html">Usage Intructions</a>.
 * </p>
 */
public class PagerTag extends TagSupport {

    /** The name of the attribute that implements IPageable. */
    private String pageableAttributeName = null;

    /** The scope of the attribute that implements IPageable. */
    private String pageableAttributeScope = null;

    /** The size of a page. */
    private Integer pageSize = null;

    /**
     * The core bean that handles all paging activities.
     */
    private PagerBean pagerBean = null;

    /**
     * Creates an empty PagerTag.
     */
    public PagerTag() {
        super();
    }

    /**
     * Returns the current PagerBean.
     * @return the PagerBean
     */
    PagerBean getPagerBean() {
        return this.pagerBean;
    }

    //
    // Attribute setters
    //

    /**
     * Sets the name of the attribute that implements <code>IPageable</code> (<i>Required</i>).
     * @param pageableAttributeName the name of the attribute
     */
    public void setName(String pageableAttributeName) {
        this.pageableAttributeName = pageableAttributeName;
    }

    /**
     * Specifies the scope of the named attribute (<i>Optional</i>).
     * @param scope the scope, one of "page", "request", "session", "application";
     * default is <code>page</code>
     */
    public void setScope(String scope) {
        this.pageableAttributeScope = scope;
    }

    /**
     * Specifies the size of a page (<i>Required</i>).
     * @param pageSize the maximum number of items to display on a page;
     */
    public void setPageSize(int pageSize) {
        this.pageSize = new Integer(pageSize);
    }

    //
    // Tag handlers
    //

    /**
     * Sets paging on in IPageable object specified by name attribute.
     * @return flag indicating whether to process body or not
     * @throws JspException if there is a problem locating determining the
     * correct scope from the specified scope
     * @throws JspTagException if a required attribute is missing
     */
    public int doStartTag() throws JspException {
        this.pagerBean = new PagerBean();
        
        IPageable pageable = (IPageable) pageContext.getAttribute(this.pageableAttributeName, TaglibUtils.getPageContextScope(this.pageableAttributeScope));
        
        if (pageable == null) {
            throw new JspTagException("Unable to locate pageableAttribute " + this.pageableAttributeName + " in scope " + this.pageableAttributeScope);
        }
        if (this.pageSize == null) {
            throw new JspTagException("pageSize is a required attribute");
        }

        this.pagerBean.setPageable(pageable);
        this.pagerBean.setPageSize(this.pageSize.intValue());
        this.pagerBean.initialize(pageContext.getRequest());
        this.pagerBean.setPagingOn();
        return EVAL_BODY_INCLUDE;
    }

    /**
     * Turns paging off in IPageable object and clears out all tag properties.
     * @return flag indicating whether to continue processing page
     */
    public int doEndTag() {
        try {
            
            this.pagerBean.setPagingOff();

        } finally {
            clear();
        
        }
        
        return EVAL_PAGE;
    }

    //
    // Utility methods
    //

    /**
     * Clears out all properties so that this tag may be reused.
     */
    private void clear() {
        this.pageableAttributeName = null;
        this.pageableAttributeScope = null;
        this.pageSize = null;
        this.pagerBean = null;
    }

}
