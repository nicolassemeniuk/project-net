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
|
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.taglibs.navbar;

import javax.servlet.jsp.JspTagException;

import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.gui.navbar.NavBarItem;
import net.project.gui.navbar.NavBarSearchItem;

import org.apache.log4j.Logger;

/**
 * <code>SearchTag</code> is responsible for rendering the search tool that is shown
 * in the left navigation bar.  This tag is meant to be called in the context of a
 * NavBarTag.
 *
 * @author Matthew Flower
 * @since Gecko
 * @see net.project.taglibs.navbar.NavBarTag
 * @see net.project.gui.navbar.NavBarItem
 */
public class SearchTag extends NavBarTagItem
{
    /** Container class that knows how to render and store information about
        a search tag */
    private NavBarSearchItem navBarSearchItem = new NavBarSearchItem();

    /**
     * Determine what type of search we will be doing.  Valid values for this search
     * are defined in the IObjectSearch class.  
     *
     * @param searchType an <code>int</code> value
     * @see net.project.search.IObjectSearch
     */
    public void setSearchType(int searchType)
    {
        this.navBarSearchItem.setSearchType(searchType);
    }
    
    /**
     * Gets the title that will be displayed on top of the search tool.
     *
     * @see #setDisplayLabel
     * @return the title that will be displayed on top of the search tool
     */
    public String getDisplayLabel() 
    {
        return this.navBarSearchItem.getDisplayLabel();
    }

    /**
     * Stores the title that will be displayed on top of the search tool.
     *
     * @see #getDisplayLabel
     * @param argDisplayLabel The title that will be displayed on top of the search tool
     */
    public void setDisplayLabel(String argDisplayLabel)
    {
        if (argDisplayLabel.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            this.navBarSearchItem.setDisplayLabel(PropertyProvider.get(argDisplayLabel));
        } else {
            this.navBarSearchItem.setDisplayLabel(argDisplayLabel);
        }
    }

    /**
     * Gets the value of url that the search will be submitted to.
     *
     * @see #setUrl
     * @return the value of url that the search will be submitted to.
     */
    public String getUrl() 
    {
        return navBarSearchItem.getUrl();
    }

    /**
     * Sets the value of url that the search will be submitted to
     *
     * @see #getUrl
     * @param argUrl a <code>String</code> value indicating a url that the
     * search will be sent to for processing.
     */
    public void setUrl(String argUrl)
    {
        navBarSearchItem.setUrl(argUrl);
    }

    /**
     * Sets the security module to submit when search is submitted.
     * @param module the security module
     * @see net.project.base.Module
     */
    public void setModule(int module) {
        navBarSearchItem.setModule(module);
    }
    
    /**
     * Sets the security action to submit when search is submitted.
     * @param action the security action
     * @see net.project.security.Action
     */
    public void setAction(int action) {
        navBarSearchItem.setAction(action);
    }

    /**
     * This method is called prior to the processing of any tag.  It's return
     * values are defined in it's parent class.  This method is hardwired in
     * the search tag to always check its body content.
     *
     * @return <code>EVAL_BODY_BUFFERED</code> to indicate that
     *         the body of the action should be evaluated.
     * @exception JspTagException if an error occurs.  
     */
    public int doStartTag() throws JspTagException {
        return EVAL_BODY_BUFFERED;
    }

    /**
     * This method is called after the processing of any tag is completed.  It
     * is responsible for processing any data that it might have collected and
     * adding it to its parent so that it can be rendered when the navigation
     * bar is rendered.
     *
     * @return an <code>int</code> value defined by {@link javax.servlet.jsp.tagext.TagSupport}
     * @exception JspTagException if an error occurs.  This will occur primarily
     * because the parent object is not a NavBarTag.  (Right now, the search
     * can only be embedded in a nav bar tag.)
     */
    public int doEndTag() throws JspTagException {
        try {
            //Now that all the parameters are set, generate the xml
            if (getDisplayThis()) {
                addSearchToNavbar();
            }

            //Clear out any variables so this tag can be easily reused.
            clear();

            //Continue evaluating the page
            return EVAL_PAGE;
        } catch (PnetException pe) {
        	Logger.getLogger(SearchTag.class).error("Unable to create search tool in navbar: " + pe);
            throw new JspTagException(pe.toString());
        }
    }

    /**
     * Generate the xml for this search and add it to the parent NavBarTag so
     * it can be rendered in HTML for the tag lib.
     *
     * @exception PnetException if an error occurs, primarily because the
     * parent of this object is not a {@link net.project.taglibs.navbar.NavBarTag}
     */
    public void addSearchToNavbar() throws PnetException
    {
        /* First, check to make sure that the parent object is a NavBarTag.
           currently, that is the only kind of parent object that we support. */
        if (!(getParent() instanceof NavBarTag)) {
        	Logger.getLogger(SearchTag.class).error("The search tag can only be embedded in a <navbar:navbar> tag, "+
                                       "and only at the top level.");
            throw new PnetException("The search tag can only be embedded in a <navbar:navbar> tag, "+
                                    "and only at the top level.");
        }
       
        //Second, add ourself to the list of code being rendered
        ((NavBarTag)getParent()).addChild(navBarSearchItem);
    }

    /**
     * Clear out all private member variables in preparation for the next call to tag.
     * (Tags are defined as being reusable, they can be returned to a pool as well.)
     */
    public void clear() {
        //Always create a NavBarSearchItem rather than trying to clear it - the old one
        //need to hang around to help create the xml.
        navBarSearchItem = new NavBarSearchItem();
    }

    /**
     * Search Tags don't support children.
     */
    public void addChild(NavBarItem newChild) {
        throw new RuntimeException("Search tags don't support child nav bar tags.");
    }
}
