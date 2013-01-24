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
package net.project.form.property;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Custom property that provides a group of radio buttons.
 */
class RadioGroupProperty extends PersistentCustomProperty
{

    private ArrayList radioButtons = new ArrayList();

    /**
     * Creates a new RadioGroupProperty with the specified id and display name.
     * @param id the id of this property
     * @param displayName the display name (that is, label) of this property
     */
    protected RadioGroupProperty(String id, String displayName) {
        super(id, displayName);
    }

    /**
     * Adds a new radio button to the Radio Group.
     * @param id the id of the value
     * @param displayName the display name for the value
     */
    public void addRadioButton(String id, String displayName) {
        this.radioButtons.add(new RadioButton(id, displayName));
    }

    /**
     * Returns the presentation of this DomainListProperty
     * as an HTML &lt;select&gt; element in a table division.
     * @return the HTML string
     */
    public String getPresentationHTML()
    {
        StringBuffer s = new StringBuffer();
        RadioButton radioButton = null;

        // Label
        s.append("<td class=\"tableHeader\" align=\"left\" colspan=\"2\">" + getDisplayName() + ":<br>");
        //s.append("<table border=1>\n");

        Iterator it = this.radioButtons.iterator();
        while (it.hasNext())
        {
            radioButton = (RadioButton) it.next();
            //s.append("<tr>\n");
            //s.append("<td class=tableContent>");
            s.append("<input type=radio ");
            s.append("name=\"" + this.getID() + "\" ");
            s.append("value=\"" + radioButton.getID() + "\" ");

            // If radio button's id is same as this property's value
            // then that radio button has been selected
            if (radioButton.getID().equals(getValue()))
            {
                s.append("checked ");
            }
            s.append(">");
            s.append("&nbsp;&nbsp;" + radioButton.getDisplayName());
            //s.append("</td></tr>\n");
            s.append("<br>");
        }
        //s.append("</table>");
        s.append("</td>\n");

        return s.toString();
    }



    /**
     * A RadioButton in this Radio Group.
     */
    private static class RadioButton
    {

        private String id;
        private String displayName;

        public RadioButton(String id, String displayName) {
            this.id = id;
            this.displayName = displayName;
        }

        public String getID() {
            return this.id;
        }

        public String getDisplayName() {
            return this.displayName;
        }

    }
}


