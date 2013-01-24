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
 * An operator in a formula.
 */
public class Operator implements IFormulaElement {

	public static final String PLUS = "+";
	public static final String MINUS = "-";
	public static final String MULTIPLY = "*";
	public static final String DIVIDE = "/";

    /**
     * Indicates whether the specified operator is binary.
     * Binary operators include <code>+, -, *, /</code>.
     * @param operator the operator to check
     * @return true if the operator is binary; false otherwise
     */
    private static boolean isBinary(String operator) {
        // Currently all operators are binary
        return true;
    }

    /**
     * The value of this operator (+, -, *, /).
     */
	private final String value;
	
	private final Integer order;
	
    /**
     * Creates a new operator with the specified value.
     *
     * @param value the value, which should be one of {@link #PLUS},
     * {@link #MINUS}, {@link #MULTIPLY}, {@link #DIVIDE}
     */
    public Operator(String value, Integer order){
	    this.value = value;
	    this.order = order;
	}

    /**
     * Returns the type of formula element.
     *
     * @return {@link Formula#OPERATOR}
     */
	public String getElementType(){
	    return Formula.OPERATOR;
	}

    /**
     * Returns this operator's value.
     *
     * @return the value
     * @see #Operator
     */
	public String getValue(){
	    return this.value;
	}

    /**
     * Indicates whether this Operator is a binary operator.
     * @return true if this operator is a binary operator (requires two
     * operands); false otherwise
     */
    boolean isBinary() {
        return Operator.isBinary(getValue());
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