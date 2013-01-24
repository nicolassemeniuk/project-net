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

 /*-----------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+-----------------------------------------------------------------------------*/
package net.project.discussion;

/**
 * An image used to annotate a discussion group or post.
 *
 * @author Bern McCarty
 * @since 01/2000
 */
public class ColorDecoration {
    private String colorID = null;
    private String m_rgb_color = null;

    /**
     * Sets the ID of this color.
     *
     * @param value the color_id for this color.
     */
    public void setColorID(String value) {
        colorID = value;
    }

    /**
     * Get the ID of this color.
     *
     * @return the color_id for this color.
     */
    public String getColorID() {
        return colorID;
    }

    /**
     * Sets the URI used to access the color. The format #CCCCCC is used.
     *
     * @param value the RGB string for this color.
     */
    public void setColor(String value) {
        m_rgb_color = value;
    }

    /**
     * Get the RGB string for this color.  Retrurned in the format #CCCCCC.
     *
     * @return the RGB string for this color.
     */
    public String getColorI() {
        return m_rgb_color;
    }
}



