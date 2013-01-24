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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.taglibs.history;

import net.project.gui.history.HistoryLevel;
import net.project.gui.history.PageLevel;
import net.project.util.Conversion;

/**
  * History Page tag.
  * Sets the page-level history item
  */
public class PageTag extends AbstractLevelTag {

    /** The page level. */
    private int level = 0;

    /**
     * Sets the page level number.
     * @param level a valid integer level
     */
    public void setLevel(String level) {
        this.level = Conversion.toInt(level);
    }

    /**
     * Returns a <code>PageLevel</code>.
     * @return a <code>PageLevel</code>.
     */
    public HistoryLevel getHistoryLevel() {
        return new PageLevel(this.level);
    }


}
