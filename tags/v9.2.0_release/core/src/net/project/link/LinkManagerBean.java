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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+--------------------------------------------------------------------------------------*/
package net.project.link;

import net.project.security.SessionManager;

/**
 * Bean that extends the functionality of a LinkManager object with presentation features.
 *
 * @author Brian Conneen
 * @since Version 2
 */
public class LinkManagerBean
        extends LinkManager
        implements java.io.Serializable {


    // Constants representing which links to view

    /**
     * Indicates that "from" links are to be viewed.
     * @see #setView
     */
    public static final int VIEW_FROM = 0;

    /**
     * Indicates that "to" links are to be viewed.
     * @see #setView
     */
    public static final int VIEW_TO = 1;

    /**
     * Indicates that both "from" and "to" links are to be viewed.
     * @see #setView
     */
    public static final int VIEW_ALL = 2;

    /**
     * Path to display links page, relative to JSP root.
     */
    public static final String DISPLAY_LINKS_PATH = "../link/displayLinks.jsp";

    /**
     * Path to add links page, relative to JSP root.
     */
    public static final String ADD_LINKS_PATH = SessionManager.getJSPRootURL()+"/link/AddLinkRedirect.jsp";

    /**
     * Specifies the view mode.
     */
    private int viewMode = VIEW_FROM;

    /**
     * Context appears to be a mechanism by which objects can maintain links
     * that are viewed at different times.
     * There appears to be only one context called GENERAL.
     */
    private int m_context = ILinkableObject.GENERAL; // default context is GENERAL

    /**
     * The originating object from which / to which to fetch links.
     */
    private ILinkableObject m_root = null;

    /**
     * Creates an empty LinkManagerBean.
     */
    public LinkManagerBean() {
        // Do nothing
    }

    /**
     * Specifies the view mode which controls what links to display as they
     * are related to the root object.
     * The default is {@link #VIEW_FROM}
     * @param view the view mode; one of {@link #VIEW_FROM}, {@link #VIEW_TO}, {@link #VIEW_ALL}
     * @see #getView
     */
    public void setView(int view) {
        this.viewMode = view;
    }

    /**
     * Returns the current view mode.
     * @return the view mode
     * @see #setView
     */
    public int getView() {
        return this.viewMode;
    }

    /**
     * Used to set the context the Bean will work in. Context can be one of three things
     * parent, child, all.  Specifying which links to display, and how to add a new link.
     * Should the link be added as a Parent, or Child.  When the context is set to all,
     * a new link is added a a child.
     * @param context an int for specifying context.
     * @see #getContext
     * @see ILinkableObject#GENERAL
     */
    public void setContext(int context) {
        this.m_context = context;
    }

    /**
     * Returns the current context
     * @return the context
     * @see #setContext
     */
    public int getContext() {
        return this.m_context;
    }

    /**
     * Used to set the default root_object for the getLinks() call to use.
     * @param object the linkable from / to which to get the links
     */
    public void setRootObject(ILinkableObject object) {
        this.m_root = object;
    }

    /**
     * Returns the XML presentation of the links for the current root object
     * using the specified view mode and context.
     * @return the XML representation of the DisplayLinks or null if the root object
     * is null
     */
    public String getLinksXML() {

        if (m_root == null) {
            return null;
        }

        StringBuffer xml = new StringBuffer();
        switch (viewMode) {
            case VIEW_FROM:
                xml.append(getLinksFrom(m_root, m_context).getXML());
                break;
            case VIEW_TO:
                xml.append(getLinksTo(m_root, m_context).getXML());
                break;
            case VIEW_ALL:
                xml.append(getAllLinks(m_root, m_context).getXML());
                break;
        }
        return xml.toString();
    }


    /**
     * Adds a link between to linkable objects.
     *
     * Used to add a link to an object, dependent on the set context.
     * If context is CHILD or ALL the from_object will be the parent and the to_object
     * will be the child.  If the context is PARENT the from_object will be the child and
     * the to_object will be the parent.
     *
     * @param object1ID the id of the object the link will be from
     * @param object2ID the id of the object the link will be to
     */
    public void addLink(String object1ID, String object2ID) {
        switch (viewMode) {
            case VIEW_FROM:
                addObjectLink(object1ID, object2ID, m_context);
                break;
            case VIEW_TO:
                addObjectLink(object2ID, object1ID, m_context);
                break;
            case VIEW_ALL:
                addObjectLink(object1ID, object2ID, m_context);
                break;
        }
    }

    /**
     * Removes a link.
     *
     * Used to remove a link to an object, dependent on the set context.
     * If context is CHILD or ALL the from_object will be the parent and the to_object
     * will be the child.  If the context is PARENT the from_object will be the child and
     * the to_object will be the parent.
     *
     * @param object1ID the id of the object the link will be from
     * @param object2ID the id of the object the link will be to
     * @param isFromLink true means the first object is the "from"; false
     * means the first object is to the "to"
     */
    public void removeLink(String object1ID, String object2ID, boolean isFromLink) {
        if (isFromLink)
            removeObjectLink(object1ID, object2ID, m_context);
        else
            removeObjectLink(object2ID, object1ID, m_context);
    }

}