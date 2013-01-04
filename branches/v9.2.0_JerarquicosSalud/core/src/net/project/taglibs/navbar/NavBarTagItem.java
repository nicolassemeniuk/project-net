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

import javax.servlet.jsp.tagext.BodyTagSupport;

import net.project.base.property.PropertyProvider;
import net.project.gui.navbar.NavBarItem;


/**
 * This class represents a single menu item that is displayed in a nav bar.
 *
 * @author Matthew Flower
 */
public abstract class NavBarTagItem extends BodyTagSupport
{
    /** This variable determine whether this tag will be processed or not.  It is
        set by the setDisplayIf() and setDisplayIfNot() methods */
    private boolean displayThis = true;
    
    /**
     * If this value is set to false, the menu item will not be displayed.
     * (True doesn't imply that it will be displayed, all IF's and IFNOT's
     * must be true to ensure that this menu item will display.
     *
     * There can be multiple "setDisplayIf" tags for an individual menu
     * item, allowing multiple booleans to be checked.  Please note, however,
     * that the comparisons work strictly as a boolean "AND", i.e. if any
     * value is false, the tag will not display.
     *
     * This tag can be used in conjunction with {@link #setDisplayIfNot} to
     * create more complex conditions.
     *
     * @see #setDisplayIfNot
     * @param argDisplayIf String to be converted to a boolean.  This string
     * can either be a string (true|false) or it can refer to a boolean
     * property.  Properties come in the form {\@property.name}
     */
    public void setDisplayIf(String argDisplayIf) {
        //First, determine whether or not this is a token at all
        if (argDisplayIf.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            //Don't display if the value of this boolean is false
            if (!PropertyProvider.getBoolean(PropertyProvider.stripTokenPrefix(argDisplayIf))) {
                this.displayThis = false;
            }
        } else {
            //argDisplayIfNot isn't a property, this should be a string that
            //has either "true" or "false" embedded in it.  Boolean.valueOf() 
            //describes which strings it supports.
            if (!Boolean.valueOf(argDisplayIf).booleanValue())
                this.displayThis = false;
        }
    }

    /**
     * If this value is false the tag will not display, otherwise it will.
     * This tag can be used along with the DisplayIf variable to selectively
     * display menu items according to logic embedded in the jsp page or
     * inside of a token.
     *
     * This attribute is often used in conjunction with the {@link #setDisplayIf}
     * tag.
     *
     * @see #setDisplayIfNot
     * @param argDisplayIfNot String to be converted to a boolean.  This
     * String can either be a string (true|false) or it can refer to a boolean
     * property.  Properties come in the form {\@property.name}
     */
    public void setDisplayIfNot(String argDisplayIfNot) {
        //First, check to see if this is actually a token
        if (argDisplayIfNot.startsWith(PropertyProvider.TOKEN_PREFIX)) {
            //Don't display if the value of this boolean is true
            if (PropertyProvider.getBoolean(PropertyProvider.stripTokenPrefix(argDisplayIfNot)))
                this.displayThis = false;
        } else {
            //argDisplayIfNot isn't a property, this should be a string that has 
            //either "true" or "false" embedded in it.  Boolean.valueOf() 
            //describes which strings it supports.
            if (Boolean.valueOf(argDisplayIfNot).booleanValue())
                this.displayThis = false;
        }
    }

    /**
     * Decide whether or not we are going to display this tag.  This is the result
     * of multiple setDisplayIf and setDisplayIfNot calls.
     *
     * @see #setDisplayThis
     * @return a <code>boolean</code> value
     */
    protected boolean getDisplayThis() {
        return this.displayThis;
    }

    /**
     * Sets absolutely whether or not we are going to display this tag.  This is
     * usually done in a clear method to reinitialize the display value before
     * another group of <code>setDisplayIf</code> and <code>setDisplayNotIf</code>
     * are called.
     *
     * @see #getDisplayThis
     * @param displayThis a <code>boolean</code> value that is the value of
     * the displayThis variable.
     */
    protected void setDisplayThis(boolean displayThis) {
        this.displayThis = displayThis;
    }


    /**
     * Add child allows sub navigation items to add themselves to the hierarchy.
     *
     * @param newChild New item to add to the hierarchy.
     */
    public abstract void addChild(NavBarItem newChild);
}
