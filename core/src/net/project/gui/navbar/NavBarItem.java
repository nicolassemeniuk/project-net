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
 * Interface that describes the method that class needs to implement in
 * order to have itself rendered on a NavBarMenu
 *
 * @author Matthew Flower
 * @since Gecko
 */
public interface NavBarItem
{
    /**
     * Get the xml that will generate this menu item.
     *
     * @param depth an <code>int</code> value that indicates how many
     * parents we have in XML when this method is being called.  This
     * allows us to render the XML is a pretty-printed fashion.  While
     * it might seem frivolous, the XML is rather complicated, and it
     * makes the debugger's job a lot easier.
     * @throws PnetException whenever the XML cannot be properly
     * rendered.
     */
    public String getNavBarXML(int depth) throws PnetException;

    /**
     * Add a child object to this NavBarItem.
     *
     * @param child a <code>NavBarItem</code> that is the new child
     * the this NavBarItem.
     * @throws PnetException if the child cannot be added for any reason.
     */
    public void addChild(NavBarItem child) throws PnetException;
}
