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
|
+----------------------------------------------------------------------*/
package net.project.gui.navbar;

import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.PnetException;

import org.apache.log4j.Logger;

/**
 * Container to hold information about the current menu item.  This object is
 * a container for the {@link net.project.taglibs.navbar.MenuItemTag MenuItemTag}
 * class.
 *
 * @author Matthew Flower
 * @since Gecko
 */
public class NavbarMenuItem implements NavBarItem {
    /** string to be displayed in the menu */
    private String displayLabel = "";
    /** url that this menu item will lead to when clicked */
    private String url = "";
    /** List of sub menu items */
    private ArrayList children = new ArrayList();
    /** Parent menu */
    private NavBarItem parent;

    /**
     * Creates a new <code>NavbarMenuItem</code> instance.  This instance
     * will not have a parent
     */
    public NavbarMenuItem()
    {
        //Empty implementation
    }

    /**
     * Creates a new <code>NavbarMenuItem</code> instance and specifies
     * that there is a parent node for this node.
     *
     * @param parent a <code>NavbarMenuItem</code> value to serve as the
     * parent node.
     */
    public NavbarMenuItem(NavbarMenuItem parent)
    {
        this.parent = parent;
    }

    /**
     * Gets the text that is going to displayed as this menu bar item.
     *
     * @see #setDisplayLabel
     * @return the value of displayLabel, this will be a blank string (instead
     * of a null) if no display label has been defined.
     */
    public String getDisplayLabel() 
    {
        if (this.displayLabel != null)
            return this.displayLabel;
        else
            return "";
    }

    /**
     * Sets the text that will be displayed as this menu item.
     *
     * @see #getDisplayLabel
     * @param argDisplayLabel Value to assign to this.displayLabel
     */
    public void setDisplayLabel(String argDisplayLabel)
    {
        this.displayLabel = argDisplayLabel;
    }

    /**
     * Gets the URL that the client will be directed to if they
     * click on this URL.
     *
     * @see #setUrl
     * @return the value of url
     */
    public String getUrl() 
    {
        if (this.url != null)
            return this.url;
        else
            return "";
    }

    /**
     * Identifies the location that this menu item will point to when clicked.
     *
     * @see #getUrl
     * @param argUrl URL to point to when menu item is clicked.
     */
    public void setUrl(String argUrl)
    {
        this.url = net.project.xml.XMLUtils.escape(argUrl);
    }

    /**
     * Return all submenus of the current menu.  Each item in this list is 
     * guaranteed to be of type TreeNode.  At the time that this javadoc was
     * written, every object is still a MenuItem.
     *
     * @return an <code>Iterator</code> value.  Each iteration should contain a
     * class that implements TreeNode.
     * @see net.project.util.TreeNode
     */
    public Iterator children() {
        return children.listIterator();
    }
    
    /**
     * Get the parent menu to this menu.
     *
     * @see #setParent
     * @return a <code>NavBarItem</code> value that is the parent to the current menu,
     * or null if this is already a top level menu.
     */
    public NavBarItem getParent() {
        return parent;
    }

    /**
     * Indicates that there is a parent menu node and provides a pointer to it.
     *
     * @see #getParent
     * @param parent a <code>NavBarItem</code> that will serve as this objects parent
     */
    public void setParent(NavBarItem parent)
    {
        if (this.parent == null) {
            this.parent = parent;

            try {
                parent.addChild(this);
            } catch (PnetException pe) {
            	Logger.getLogger(NavbarMenuItem.class).error("Unable to add this class to the parent "+
                                           "NavBarItem.  " + pe);
            }
        }
    }

    /**
     * Determines whether or not this Menu Item has any sub menus
     *
     * @return boolean true if there are no submenus, false if there are submenus.
     */
    public boolean isLeaf() {
        return (children.size() == 0);
    }

    /**
     * Add a child item.  This item will be rendered when getXML is called for
     * this class.
     *
     * @param newChild a <code>NavBarItem</code> value
     */
    public void addChild(NavBarItem newChild) {
        children.add(newChild);
    }

    public String getNavBarXML(int depth) throws PnetException
    {
        //Stringbuffer to store our generated XML
        StringBuffer xml = new StringBuffer();
        
        //Padding might seem frivolous, but consider how difficult it is
        //to debug multiple levels of embedded menu tags - you just might change
        //your mind about removing this code.
        StringBuffer leftPad = new StringBuffer();
        for (int i=0; i<depth; i++)
            leftPad.append("  ");

        /* Generate the XML for this menu item.  We add padding to the left
           side to make xml output more readable */
        xml.append(leftPad).append("<Menu>\n");
        xml.append(leftPad).append("  <URL>").append(getUrl()).append("</URL>\n");
        xml.append(leftPad).append("  <Label>").append(getDisplayLabel()).append("</Label>\n");
        xml.append(leftPad).append("  <Depth>").append(depth).append("</Depth>\n");

        //Iterate through all the children recursively and generate their xml.
        NavBarItem child;
        Iterator itKids = children.iterator();
        while (itKids.hasNext()) {
            child = (NavBarItem)itKids.next();

            //Check first to make sure that the child has any children, if not, don't bother processing.
            xml.append(child.getNavBarXML(depth+1));
        }

        //Close the menu tag
        xml.append(leftPad).append("</Menu>\n");

        return xml.toString();
    }
}
