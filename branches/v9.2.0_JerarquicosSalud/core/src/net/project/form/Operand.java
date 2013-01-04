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
|   $Revision: 20348 $
|       $Date: 2010-01-29 11:23:05 -0300 (vie, 29 ene 2010) $
|     $Author: nilesh $
|
+----------------------------------------------------------------------*/
package net.project.form;

/**
 * Provides an Operand in a Formula.
 */
public class Operand implements IFormulaElement {

    private final String value;
    private final Integer order;

    /**
     * Creates a new Operand with the specified value.
     *
     * @param value the value, which is usually a fieldID
     */
    public Operand(String value, Integer order){
	    this.value = value;
	    this.order = order;
	}

    /**
     * Returns the element type of this formula element.
     *
     * @return {@link Formula#OPERAND}
     */
	public String getElementType(){
	    return Formula.OPERAND;
	}

    /**
     * Returns the value of this element.
     * The actual value depends on whether this is an operand or an operator.
     *
     * @return the value
     */
    public String getValue() {
        return this.value;
    }

    /**
	 * Returns the order of this element.
     * The actual order by which elements are rendered.
     * 
	 * @return the order
	 */
	public Integer getOrder() {
		return this.order;
	}
}


