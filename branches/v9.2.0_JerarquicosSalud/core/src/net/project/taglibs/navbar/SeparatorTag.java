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
package net.project.taglibs.navbar;

import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.TagSupport;

import net.project.gui.navbar.NavBarItem;
import net.project.gui.navbar.NavBarSeparatorItem;

/**
 * Separator tag introduces the ability to add one or more blank lines
 * in between menu items.
 *
 * This tag must be used inside the context of a NavBarTag.
 * 
 * @author Matthew Flower
 * @since Gecko
 * @see net.project.taglibs.navbar.NavBarTag
 */
public class SeparatorTag extends TagSupport
{
    /** The number of rows that we are going to render **/
    private int rows = 1;

    /**
     * Determine how many rows of separators we are going to render
     *
     * @return an <code>int</code> value containing the number of rows
     * we are going to return.  
     */
    public int getRows() {
        return this.rows;
    }

    /**
     * Sets the number of rows of separators we are going to generate
     *
     * @param argRows Number of rows to generate
     */
    public void setRows(int argRows) {
        this.rows = argRows;
    }

    /**
     * This method is called automatically by the taglib as soon as the
     * separator tag ends.  We use it to generate the class that will
     * make our xml and to store it in the parent object.  (Which must,
     * coincidentally, be a NavBarTag object.
     *
     * @return an <code>int</code> value specified by {@link javax.servlet.jsp.tagext.TagSupport}
     * @exception JspTagException if an error occurs (required by TagSupport)
     */
    public int doEndTag() throws JspTagException {
        //Add the separator XML to the navbar
        addSeparatorToNavBar();
        //Clear out the private member variables for the next round
        clear();

        return EVAL_PAGE;
    }

    /**
     * Adds this separator to the Navigation bar for rendering.  Currently,
     * this requires that our getParent() will be returning the NavBarTag
     * and not a submenu of it.
     */
    public void addSeparatorToNavBar()
    {
        //Assuming that our parent is a NavBarTag, we are going just
        //call addChild to add ourselves.
        ((NavBarTag)getParent()).addChild(((NavBarItem)new NavBarSeparatorItem(rows)));
    }

    /**
     * Clean out the private member variables for this tag in preparation
     * for its next run.
     */
    public void clear()
    {
        this.rows = 1;
    }
}
