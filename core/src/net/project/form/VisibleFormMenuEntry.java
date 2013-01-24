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
package net.project.form;

import net.project.util.Conversion;
import net.project.xml.XMLUtils;

/**
 * A VisibleFormMenuEntry is a FormMenuEntry owned by a space which is
 * visible to another space wishing to access the form menu entry.
 */
public class VisibleFormMenuEntry
        extends FormMenuEntry {

    /**
     * Returns this VisibleFormMenuEntry's attributes as an XML string.
     * This includes the underlying FormMenuEnty's attributes appended with
     * <code>&lt;is_visible&gt;</code> element set to true
     */
    protected String getXMLAttributes() {
        StringBuffer xml = new StringBuffer();

        xml.append(super.getXMLAttributes());
        xml.append("<is_visible>" + XMLUtils.escape(Integer.toString(Conversion.booleanToInt(true))) + "</is_visible>");

        return xml.toString();
    }

}
