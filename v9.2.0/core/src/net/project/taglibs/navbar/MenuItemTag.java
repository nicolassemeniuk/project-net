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
package net.project.taglibs.navbar;

import javax.servlet.jsp.JspTagException;

import net.project.base.property.PropertyProvider;
import net.project.gui.navbar.NavBarItem;
import net.project.gui.navbar.NavbarMenuItem;

/**
 * <code>InsertTag</code> is responsible for generating menu items for the
 * left navigation bar.  The menu item tag supports the concept of embedded
 * tags, which translate into the concept of submenus.
 *
 * @author Matthew Flower
 * @version 1.0
 */
public class MenuItemTag extends NavBarTagItem {
    /** Data about this menu item.  This has to be stored outside of this tag as
        Tags are reused and we can't assume that our data will hang around for later.
        This class holds the tree structure, the URL, and the label. */
    private NavbarMenuItem menuData = new NavbarMenuItem();

    /**
     * Gets the value that is going to be displayed to the user as a menu item.
     * This is a required attribute.
     *
     * @see #setDisplayLabel
     * @return The menu item to be displayed to the user.
     */
    public String getDisplayLabel() {
        return this.menuData.getDisplayLabel();
    }

    /**
     * Sets the text that is going to be displayed to the user as a menu item.
     * This is a required attribute.
     *
     * @see #getDisplayLabel
     * @param argDisplayLabel The menu item to be displayed to the user.  This
     * can be either a token or a string value.  To use a token, begin the
     * value with an ampersand.
     */
    public void setDisplayLabel(String argDisplayLabel) {
        //We are going to check whether or not this is a token by the
        //first character being the "TOKEN_PREFIX" which is currently
        //an at sign.
        if (argDisplayLabel.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            //This is a token, lookup the token.
            this.menuData.setDisplayLabel(PropertyProvider.get(argDisplayLabel));
        } else {
            //This is not a token, store this value as is.
            this.menuData.setDisplayLabel(argDisplayLabel);
        }
        
    }

    /**
     * Gets the value of the URL that this menu item will point to.  This
     * should be a virtual path.  The path should be relative to the directory
     * from which the navbar taglib was originally invoked.  For example, if
     * the navbar taglib was called like this from the /project/NavBar.jsp file
     * <p>
     * <code><pre>
     * &lt;navbar:navbar&gt;
     *   &lt;navbar:menuitem displayLabel="hello" url="/include/hello.jsp"&gt;
     * &lt;/navbar&gt;
     * </pre></code>
     * <p>
     * <code>getURL()</code> would return "/include/hello.jsp" which would
     * *really* point to /project/include/hello.jsp once the entire page was
     * rendered.
     *
     * @see #setUrl
     * @return the value of url that this menu item will point to.
     */
    public String getUrl() {
        return this.menuData.getUrl();
    }

    /**
     * Sets the value of url that this menu item will point to when clicked.
     *
     * @see #getUrl
     * @param argUrl URL that this menu item will point to when clicked.
     */
    public void setUrl(String argUrl) {
        this.menuData.setUrl(argUrl);
    }

    /**
     * This method is defined by the Tag interface (which is implemented by
     * BodyTagSupport, our superclass).  Do start tag runs after all the
     * attributes are set and we are ready to process the tag.  
     *
     * @return
     * <ul>
     *   <li><code>EVAL_BODY_BUFFERED</code> is returned if we are going to
     *   look for subtags inside of the body of the tag.
     *   <li><code>SKIP_BODY</code> is returned if the insides aren't to be
     *   examined.</li>
     * @throws JspTagException if any unexpected error occurs.  This is
     * required by the specification -- at this point, I'm not throwing
     * any.
     */
    public int doStartTag() throws JspTagException {
        /* Display this is set according to the displayIf and displayNotIf properties.
           Attributes are examined before calling doStartTag(), so it is already set. */
        if (getDisplayThis()) {
            //Add this menu item to the tree.  I unfortunately coded myself in a corner
            //with this one, so I have to check what the parent is in order to cast.
            //This should probably be fixed.
            NavBarTagItem parent = (NavBarTagItem)getParent();
            parent.addChild(this.menuData);

            //We need to look for embedded tags, return the appropriate tag to do so.
            return EVAL_BODY_BUFFERED;
        } else {
            //Our display attributes have indicated that we shouldn't render this tag,
            //so skip it.
            return SKIP_BODY;
        }
    }


    /**
     * This method is called after the JSP taglib is done processing the body.  
     *
     * @return an <code>int</code> value defined by {@link javax.servlet.jsp.tagext.BodyTagSupport}
     * @exception JspTagException if an error occurs
     */
    public int doAfterBody() throws JspTagException {
        return SKIP_BODY;
    }

    /**
     * Prepare this tag class to be reused.  This is the part of the class where
     * we clear out all private member variables.
     *
     * @return int SKIP_PAGE if JSP processing for the current page should be
     * halted immediately; EVAL_PAGE if it should continue.
     * @exception JspTagException if an error occurs (This is required by the
     * Tag interface.
     */
    public int doEndTag() throws JspTagException {
        //Clear out all private member variables
        clear();
        //Keep on evaluating the page
        return EVAL_PAGE;
    }

    /**
     * Resets the internal state of this class to be prepared for the next
     * call to this taglib.  This is called automatically by the web container
     * in preparation for the this taglib being reused.
     */
    public void clear()
    {
        setDisplayThis(true);
        menuData = new NavbarMenuItem();
    }

    /**
     * Add child allows sub menus to add themselves to this menu.
     */
    public void addChild(NavBarItem newChild)
    {
        menuData.addChild(newChild);
    }
}
