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

import net.project.base.PnetException;

/**
 * Container to hold information about rendering a separator navbar item.
 * This object is a container for {@link net.project.taglibs.navbar.MenuItemTag}
 *
 * @author Matthew Flower
 * @since Gecko
 */
public class NavBarSeparatorItem implements NavBarItem {
    private int rows = 1;

    /**
     * Creates a new <code>NavBarSeparatorItem</code> instance.
     *
     * @param Rows an <code>int</code> value specifying the number of
     * separator rows that this item is going to generate.
     */
    public NavBarSeparatorItem(int rows)
    {
        this.rows = rows;
    }
    
    /**
     * Gets the number of rows that we are going to generate
     *
     * @see #setRows
     * @return the number of separator rows that we are going to generate
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Sets the number of separator rows that we are going to generate.
     *
     * @see #getRows
     * @param argRows the number of separator rows that we are going to generate
     */
    public void setRows(int argRows) {
        this.rows = argRows;
    }

    /**
     * Generate the XML that is required to render this object as a separator once
     * it gets to XSL.
     *
     * @param depth an <code>int</code> value indicating how far this object is
     * embedded.  This is used primarily for formatting the XML in a pretty format.
     * @return a <code>String</code> value which contains the XML for this separator.
     */
    public String getNavBarXML(int depth) {
        StringBuffer xml = new StringBuffer();

        //Calculate how far this node should be indented (in spaces)
        StringBuffer leftPad=new StringBuffer();
        for (int i =0; i<depth; i++)
            leftPad.append("  ");

        //Generate the XML for this separator.
        xml.append(leftPad).append("<Separator>\n");
        xml.append(leftPad).append("  <Rows>").append(rows).append("</Rows>\n");
        xml.append(leftPad).append("</Separator>\n");

        return xml.toString();
    }

    /**
     * Add a child object to this NavBarItem (!!!Child objects are not supported
     * by the FeaturedMenuItem, calling this will just raise an error.)  
     *
     * @param child a <code>NavBarItem</code> that would have became the child
     * object of this object.
     */
    public void addChild(NavBarItem child) throws PnetException {
        throw new PnetException("The separator tag does not support child objects");
    }
}
