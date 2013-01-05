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


/**
 * Custom property for a single checkbox.
 */
class CheckBoxProperty extends PersistentCustomProperty
{
    private int colspan=1;

    /**
     * Creates a new CheckBoxProperty with the specified id and display name.
     * @param id the id of this property
     * @param displayName the display name (that is, label) of this property
     */
    protected CheckBoxProperty(String id, String displayName) {
        super(id, displayName);
    }

    public int getColspan() {
        return colspan;
    }

    public void setColspan(int colspan) {
        this.colspan = colspan;
    }


    /**
     * Returns the presentation of this CheckBoxProperty
     * as an HTML &lt;select&gt; element in a table division.
     * @return the HTML string
     */
    public String getPresentationHTML()
    {
        StringBuffer s = new StringBuffer();

        s.append("<td class=\"tableHeader\" align=\"left\" ");
        if (colspan > 1) {
            s.append("colspan=\""+colspan+"\"");
        }
        s.append(">\n");
        s.append("<input type=checkbox ");
        s.append("name=\"" + this.getID() + "\" ");
        s.append("value=\"1\" ");

        // If radio button's id is same as this property's value
        // then that radio button has been selected
        if ((getValue() != null) && getValue().equals("1"))
        {
            s.append("checked ");
        }
        s.append(">");
        s.append("&nbsp;&nbsp;" + getDisplayName());
        s.append("</td>\n");

        return s.toString();
    }

}


