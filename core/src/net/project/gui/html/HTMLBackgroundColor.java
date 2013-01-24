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
|   $Revision: 19925 $
|       $Date: 2009-09-08 12:56:09 -0300 (mar, 08 sep 2009) $
|     $Author: nilesh $
|
+-----------------------------------------------------------------------------*/
package net.project.gui.html;

import java.util.Iterator;
import java.util.LinkedHashMap;

import net.project.base.property.PropertyProvider;

/**
 * This class is designed to produce an HTML list of colors.
 *
 * @author Matthew Flower
 * @since Version 7.6.3
 */
public class HTMLBackgroundColor implements IHTMLOption, IHTMLClass {
    private static LinkedHashMap colorList = new LinkedHashMap();
    public static HTMLBackgroundColor NO_COLOR = new HTMLBackgroundColor(1, "", "prm.schedule.tasklistdecoration.nobackground.option");
    public static HTMLBackgroundColor LT_BLUE = new HTMLBackgroundColor(2, "background-lt-blue", "prm.schedule.tasklistdecoration.ltblue.color");
    public static HTMLBackgroundColor BLUE = new HTMLBackgroundColor(3, "background-blue", "prm.schedule.tasklistdecoration.blue.color");
    public static HTMLBackgroundColor PINK = new HTMLBackgroundColor(4, "background-pink", "prm.schedule.tasklistdecoration.pink.color");
    public static HTMLBackgroundColor GRAY = new HTMLBackgroundColor(5, "background-gray", "prm.schedule.tasklistdecoration.gray.color");
    public static HTMLBackgroundColor GREEN = new HTMLBackgroundColor(6, "background-green", "prm.schedule.tasklistdecoration.green.color");
    public static HTMLBackgroundColor LT_GREEN = new HTMLBackgroundColor(7, "background-lt-green", "prm.schedule.tasklistdecoration.ltgreen.color");
    public static HTMLBackgroundColor DK_RED = new HTMLBackgroundColor(8, "background-dk-red", "prm.schedule.tasklistdecoration.dkred.color");
    public static HTMLBackgroundColor DK_BLUE = new HTMLBackgroundColor(9, "background-dk-blue", "prm.schedule.tasklistdecoration.dkblue.color");
    public static HTMLBackgroundColor OLIVE = new HTMLBackgroundColor(10, "background-olive", "prm.schedule.tasklistdecoration.olive.color");
    public static HTMLBackgroundColor PURPLE = new HTMLBackgroundColor(11, "background-purple", "prm.schedule.tasklistdecoration.purple.color");
    public static HTMLBackgroundColor RED = new HTMLBackgroundColor(12, "background-red", "prm.schedule.tasklistdecoration.red.color");
    public static HTMLBackgroundColor SILVER = new HTMLBackgroundColor(13, "background-silver", "prm.schedule.tasklistdecoration.silver.color");
    public static HTMLBackgroundColor TEAL = new HTMLBackgroundColor(14, "background-teal", "prm.schedule.tasklistdecoration.teal.color");
    public static HTMLBackgroundColor WHITE = new HTMLBackgroundColor(15, "background-white", "prm.schedule.tasklistdecoration.white.color");
    public static HTMLBackgroundColor YELLOW = new HTMLBackgroundColor(16, "background-yellow", "prm.schedule.tasklistdecoration.yellow.color");
    public static HTMLBackgroundColor DEFAULT = NO_COLOR;

    public static java.util.Collection getAllColors() {
        return colorList.values();
    }
    
    public static HTMLBackgroundColor getByID(long id) {
        return (HTMLBackgroundColor)colorList.get(new Long(id));
    }

    public static HTMLBackgroundColor getByID(String id) {
        try {
            return getByID(Integer.parseInt(id));
        } catch (NumberFormatException e) {
            return DEFAULT;
        }
    }

    public static HTMLBackgroundColor getByClass(String style) {
        for (Iterator it = colorList.values().iterator(); it.hasNext();) {
            HTMLBackgroundColor color = (HTMLBackgroundColor) it.next();
            if (color.getHTMLClass().equals(style)) {
                return color;
            }
        }
        return DEFAULT;
    }

    private long id;
    private String htmlClass;
    private String nameToken;

    /**
     * Public constructor.
     *
     * @param id a <code>long</code> containing the unique id of this color
     * which will be stored in the database.
     * @param htmlClass a <code>String</code> containing the CSS style that we
     * should use to render this color in an option list.
     * @param nameToken a <code>String</code> which contains a token that we
     * can use to look up the name of this color.
     */
    public HTMLBackgroundColor(long id, String htmlClass, String nameToken) {
        this.htmlClass = htmlClass;
        this.nameToken = nameToken;
        this.id = id;
        colorList.put(new Long(id), this);
    }

    /**
     * Get the long value that uniquely identifies this background color.
     *
     * @return a <code>long</code> which uniquely identifies this background
     * color.
     */
    public long getID() {
        return id;
    }

    public String getHtmlOptionValue() {
        return String.valueOf(id);
    }

    public String getHtmlOptionDisplay() {
        return PropertyProvider.get(nameToken);
    }

    /**
     * Get an HTML Style which should be used for the "class" attribute when
     * the object is rendered as HTML.
     *
     * @return a <code>String</code> which contains the complete HTML class.
     */
    public String getHTMLClass() {
        return htmlClass;
    }

}
