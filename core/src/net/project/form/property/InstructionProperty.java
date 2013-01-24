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

import net.project.base.property.PropertyProvider;
import net.project.util.HTMLUtils;

public class InstructionProperty implements ICustomProperty {

    private final String instruction;
    private final boolean isText;

    public InstructionProperty(String instructionToken) {
        this(instructionToken, false);
    }

    public InstructionProperty(String instruction, boolean isText) {
        this.instruction = instruction;
        this.isText = isText;
    }

    /**
     * Returns this custom property as HTML presentation for editing property
     * value.
     * @return HTML presentation of custom property
     */
    public String getPresentationHTML() {

        StringBuffer result = new StringBuffer();

        result.append("<td class=\"tableContent\" colspan=\"2\">").append("<i>");

        // Now add the instruction text
        String instructionText = null;
        if (this.isText) {
            instructionText = this.instruction;
        } else {
            instructionText = PropertyProvider.get(this.instruction);
        }
        result.append(HTMLUtils.escape(instructionText));

        result.append("</i>").append("</td>");

        return result.toString();
    }

    public boolean isValueRequired() {
        return false;
    }
}
