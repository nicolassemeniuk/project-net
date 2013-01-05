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
|     $RCSfile$
|    $Revision: 18397 $
|        $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|      $Author: umesha $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.api.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

/**
 * Provides statistics about server side runs.
 *
 * @author Tim Morrow
 * @since Version 7.6.4
 */
public class Stats {

    private static final String ATTRIBUTE_NAME = "net.project.api.model.Stats";

    public static Stats get(ServletContext servletContext) {

        Stats stats = (Stats) servletContext.getAttribute(ATTRIBUTE_NAME);
        if (stats == null) {
            stats = new Stats();
            servletContext.setAttribute(ATTRIBUTE_NAME, stats);
        }

        return stats;
    }

    private final List updateTimes = Collections.synchronizedList(new ArrayList());
    private final List selectTimes = Collections.synchronizedList(new ArrayList());

    public void logUpdate(long time) {
        updateTimes.add(new Long(time));
    }

    public void logSelect(long time) {
        selectTimes.add(new Long(time));
    }

    public void clear() {
        updateTimes.clear();
        selectTimes.clear();
    }

    public String getUpdateAverage() {
        return getAverage(updateTimes) + " ms over " + updateTimes.size() + " runs";

    }

    public String getSelectAverage() {
        return getAverage(selectTimes) + " ms over " + selectTimes.size() + " runs";
    }

    private String getAverage(Collection longs) {
        long result = 0L;

        if (longs.size() == 0) {
            return "0";
        }

        for (Iterator it = longs.iterator(); it.hasNext();) {
            Long nextLong = (Long) it.next();
            result += nextLong.longValue();
        }

        return new BigDecimal(result).divide(new BigDecimal(longs.size()), BigDecimal.ROUND_HALF_UP).toString();
    }



}
