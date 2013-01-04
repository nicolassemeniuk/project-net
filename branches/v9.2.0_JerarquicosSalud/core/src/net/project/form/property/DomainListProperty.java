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
 * Custom property that provides a list of domain values.
 */
class DomainListProperty extends PersistentCustomProperty {

    private ArrayList domainValues = new ArrayList();

    /**
     * Creates a new DomainListProperty with the specified id and display name.
     * @param id the id of this property
     * @param displayName the display name (that is, label) of this property
     */
    protected DomainListProperty(String id, String displayName) {
        super(id, displayName);
    }
    
    /**
     * Adds a new domain value to this domain list.
     * @param id the id of the value
     * @param displayName the display name for the value
     */
    public void addDomainValue(String id, String displayName) {
        domainValues.add(new DomainValue(id, displayName));
    }

    /**
     * Returns the presentation of this DomainListProperty
     * as an HTML &lt;select&gt; element in a table division.
     * @return the HTML string
     */
    public String getPresentationHTML() {
        StringBuffer s = new StringBuffer();

        // Label
        s.append("<td class=\"tableHeader\" align=\"left\">" + getDisplayName() + ":</td>");

        // HTML Form field
        s.append("<td class=\"tableHeader\" align=\"left\">");
        s.append("<select ");
        s.append("name=\"" + this.getID() + "\" ");
        s.append(">");
        s.append(getDomainValuesHTML());
        s.append("</select>");
        s.append("</td>");

        return s.toString();
    }

    /**
     * Returns the domain values as HTML &lt;option&gt; elements.
     * @return the HTML string
     */
    private String getDomainValuesHTML() {
        StringBuffer s = new StringBuffer();
        DomainValue domainValue = null;
        
        Iterator it = this.domainValues.iterator();
        while (it.hasNext()) {
            domainValue = (DomainValue) it.next();

            s.append("<option ");
            s.append("value=\"" + domainValue.getID() + "\" ");
            
            // If domain value's id is same as this property's value
            // then that one has been selected
            if (domainValue.getID().equals(getValue())) {
                s.append("selected ");
            }
            s.append(">");
            s.append( (domainValue.getDisplayName() == null ? "" : domainValue.getDisplayName()) );
            s.append("</option>\n");

        }

        return s.toString();
    }

    /**
     * A DomainValue is an item in this DomainList.
     */
    private static class DomainValue {

        private String id;
        private String displayName;

        public DomainValue(String id, String displayName) {
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

