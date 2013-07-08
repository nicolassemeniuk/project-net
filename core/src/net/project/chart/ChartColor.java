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
package net.project.chart;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChartColor {
    private static final List chartColors = new ArrayList();
    public static Color GREEN = new Color(0x4d, 0x8e, 0x4d);
    public static Color BLUE = new Color(0x7d, 0x91, 0xf2);
    public static Color RED = new Color(0xd3, 0x00, 0x00);
    public static Color WHITE = new Color(0xff, 0xff, 0xff);
    public static Color YELLOW = new Color(0xf4, 0xe5, 0x0e);
    public static Color PURPLE = new Color(0x66, 0x00, 0x99);
    public static Color BROWN = new Color(0x7c, 0x5b, 0x3f);
    public static Color LIGHT_GREEN = new Color(0x7f, 0xff, 0x7f);
    public static Color LIGHT_BLUE = new Color(0xcb, 0xd6, 0xff);
    public static Color CREAM = new Color(0xff, 0xfb, 0xce);
    public static Color DARK_BLUE = new Color(0x00, 0x00, 0xC0);    
    public static Color CYAN = Color.CYAN;
    public static Color MAGENTA = Color.MAGENTA;
    public static Color ORANGE = Color.ORANGE;
    public static Color GRAY = Color.GRAY;
    public static Color DARK_GRAY = Color.DARK_GRAY;
    public static Color PINK = Color.PINK;

    static {
        //Colors are ordered by their acceptability for a chart.
        chartColors.add(GREEN);
        chartColors.add(BLUE);
        chartColors.add(RED);
        chartColors.add(YELLOW);
        chartColors.add(BROWN);
        chartColors.add(CYAN);
        chartColors.add(LIGHT_GREEN);
        chartColors.add(LIGHT_BLUE);
        chartColors.add(CREAM);
        chartColors.add(DARK_BLUE);           
        chartColors.add(PURPLE);
        chartColors.add(GRAY);
        chartColors.add(DARK_GRAY);
        chartColors.add(ORANGE);
        chartColors.add(MAGENTA);
        chartColors.add(PINK);
    }

    public static List getChartColorList() {
        return Collections.unmodifiableList(chartColors);
    }
}
