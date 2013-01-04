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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.taglibs.links;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.base.PnetException;
import net.project.channel.Channel;
import net.project.channel.IChannelModifier;
import net.project.gui.toolbar.ToolbarException;
import net.project.link.ILinkableObject;
import net.project.link.LinkManagerBean;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.util.Conversion;

/**
  * InsertTag is the taglib tag for links<br>
  * @see net.project.link.LinkManagerBean
  */
public class InsertTag extends TagSupport implements IChannelModifier  {

    static final String DEFAULT_DISPLAY_LINK_MANAGER_BEAN_NAME = "display_linkMgr";

    /** LinkManager being built */
    private LinkManagerBean linkManager = null;

    /** bean name of link manager to be created */
    private String name = null;

    private Integer view = null;
    
    private Integer context = null;
    
    /** Bean name of root object */
    private String rootObjectBeanName = null;
    
    /** Actual root object */
    private ILinkableObject rootObject = null;
    
    /** Scope (session, request etc.) of root object */
    private Integer rootObjectScope = new Integer(PageContext.PAGE_SCOPE);
    
    private String referringLink = null;

    private Integer module = null;
    
    private String objectID = null;
    
    private Boolean isModifiable = Boolean.TRUE;

    /** Indicates whether links are displayed by a channel or
      * should be displayed by this taglib. */
    private boolean isDisplayedByChannel = false;

    /**
      * Start of tag.  Called after the attributes are set.
      * Create new link manager and set all the attributes
      * @return flag indicating whether to process body or not
      */
    public int doStartTag() throws JspTagException {
        
        // Creates a new link manager.
        linkManager = new LinkManagerBean();

        if (view != null) {
            linkManager.setView(view.intValue());
        }
        if (context != null) {
            linkManager.setContext(context.intValue());
        }
        if (rootObjectBeanName != null && rootObjectScope != null) {
            linkManager.setRootObject((ILinkableObject) pageContext.getAttribute(rootObjectBeanName, rootObjectScope.intValue()));
        }

        // Set the appropriate security parameters for including the links display
        if (module != null) {
            pageContext.setAttribute("module", "" + module, PageContext.REQUEST_SCOPE);

        }
        if (objectID != null) {
            pageContext.setAttribute("id", objectID, PageContext.REQUEST_SCOPE);
        }
        pageContext.setAttribute("action", "" + Action.VIEW, PageContext.REQUEST_SCOPE);

        // Now put the link manager bean in session scope
        if (name == null) {
            name = DEFAULT_DISPLAY_LINK_MANAGER_BEAN_NAME;
        }
        pageContext.setAttribute(name, linkManager, PageContext.SESSION_SCOPE);

        /* Continue evaluating tag body */
	return EVAL_BODY_INCLUDE;
    }

    /**
      * End of tag.
      * Write out presentation of Link manager if not handled by a channel.
      * @return flag indicating whether to continue processing page
      */
    public int doEndTag() throws JspTagException {
        try {
            
            if (!isDisplayedByChannel) {
                // Include links here
                try {
                    pageContext.include(LinkManagerBean.DISPLAY_LINKS_PATH);

                } catch (java.io.IOException ioe) { 
                    throw new JspTagException("Error inserting links: " + ioe); 

                } catch (javax.servlet.ServletException se) {
                    throw new JspTagException("Error inserting links: " + se); 

                } //end try
            
            } else {
                // Leave inclusion of links to channel
                // (already performed by channel)
            }
        
        } finally {
            /* Empty out links manager and clear all attributes*/
            clear();
        }
        return EVAL_PAGE;
    }

    /*
        Attribute set methods
     */
    public void setName(String name) {
        this.name = name;
    }
    public void setView(String view) {
        if (view != null) {

            if (view.equalsIgnoreCase("all")) {
                this.view = new Integer(LinkManagerBean.VIEW_ALL);
            
            } else if (view.equalsIgnoreCase("from")) {
                this.view = new Integer(LinkManagerBean.VIEW_FROM);
            
            } else if (view.equalsIgnoreCase("to")) {
                this.view = new Integer(LinkManagerBean.VIEW_TO);
            
            }
        
        } else {
            this.view = null;

        }
    }
    public void setcontext(String context) {
        this.context = new Integer(Conversion.toInt(context));
    }
    public void setRootObjectName(String rootObjectBeanName) {
        this.rootObjectBeanName = rootObjectBeanName;
    }
    
    /**
      * Sets the scope of the root object.
      * @param rootObjectScope may be one of "application", "session", "request", "page"
      * @see javax.servlet.jsp.PageContext
      */
    public void setRootObjectScope(String rootObjectScope) {
        if (rootObjectScope != null) {
            if (rootObjectScope.equals("application")) {
                this.rootObjectScope = new Integer(PageContext.APPLICATION_SCOPE);
            
            } else if (rootObjectScope.equals("session")) {
                this.rootObjectScope = new Integer(PageContext.SESSION_SCOPE);
            
            } else if (rootObjectScope.equals("request")) {
                this.rootObjectScope = new Integer(PageContext.REQUEST_SCOPE);

            } else if (rootObjectScope.equals("page")) {
                this.rootObjectScope = new Integer(PageContext.PAGE_SCOPE);
            }
        
        } else {
            this.rootObjectScope = null;
        
        }
    }

    public void setReferringLink(String referringLink) {
        this.referringLink = referringLink;
    }
    public void setModule(String module) {
        this.module = new Integer(Conversion.toInt(module));
    }
    public void setObjectID(String objectID) {
        this.objectID = objectID;
    }
    public void setModifiable(String isModifiable) {
        this.isModifiable = new Boolean(Conversion.toBoolean(isModifiable));
    }


    /*
        Other methods
     */

    /**
      * Customize the channel which will be displaying links.
      * If this method is called, links will not be included by this
      * taglib, assumes a channel will include the links (using the include
      * path set by this method).
      * @param channel the channel to customize
      */
    public void modifyChannel(Channel channel) throws PnetException {
        
        try {

            // Set the include page for displaying links
            channel.setInclude(LinkManagerBean.DISPLAY_LINKS_PATH);

            // Now construct modify button link URL
            StringBuffer modifyHref = 
            	new StringBuffer("javascript:self.document.location='" 
            			+ LinkManagerBean.ADD_LINKS_PATH);
            if (modifyHref.toString().indexOf('?') == -1) {
                modifyHref.append('?');
            }

            modifyHref.append("context=" + linkManager.getContext());
            modifyHref.append("&view=" + linkManager.getView());
            modifyHref.append("&action=" + net.project.security.Action.MODIFY);
            
            if (module != null) {
                modifyHref.append("&module=" + module);
            }
            if (objectID != null) {
                modifyHref.append("&id=" + objectID);
            }
            if (referringLink != null) {
                modifyHref.append("&refererLink=" + java.net.URLEncoder.encode(referringLink));
            }
            modifyHref.append("';");

            // Add a modify channel button if this is modifiable
            if (isModifiable != null && isModifiable.booleanValue()) {
                //channel.addChannelButtonTokens("modify", null, SessionManager.getJSPRootURL() + modifyHref.toString());
            	// SessionManager.getJSPRootURL() not needed for javascript function call
                channel.addChannelButtonTokens("modify", null, modifyHref.toString());
            }

            isDisplayedByChannel = true;

        } catch (ToolbarException te) {
            throw new PnetException("Error customizing channel in links InsertTag: " + te);

        }
    }

    /**
      * Clear all properties, resets to default values.
      */
    private void clear() {
        linkManager = null;
        name = null;
        view = null;
        context = null;
        rootObjectBeanName = null;
        rootObject = null;
        rootObjectScope = new Integer(PageContext.PAGE_SCOPE);
        referringLink = null;
        module = null;
        objectID = null;
        isModifiable = Boolean.TRUE;
        isDisplayedByChannel = false;
    }

}

