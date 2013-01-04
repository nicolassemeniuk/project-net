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
package net.project.form;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A filter constraint used by the FieldFilter class.
 *
 * @author Roger Bly
 * @since 11/2000
 */
public class FormFilterConstraint {
    protected String m_operator = null;
    protected FormField m_field = null;

    private ArrayList m_parameters = null;
    private ArrayList m_parameter_types = null;
    private int m_next_parameter;
    private int m_next_parameter_type;

    private HashMap m_parameterMap = null;

    public FormFilterConstraint() {
        m_parameters = new ArrayList(5);
        m_parameter_types = new ArrayList(5);
        m_parameterMap = new HashMap(5);
    }

    /**
     * Sets the operator or operand of the constraint.  For example: =, <, >,
     * !=, in, etc. If the operator is not a support operator, the set will not
     * take place.
     *
     * A contraint has the structure of:  [field] [operator] [parameter]
     * <br>
     * For example,  field123 > 25   or   field123 in [34, 23, 45]
     *
     * @param operator the operator token.
     * @return true if the setOperator is a valid operator.  false otherwise.
     */
    public boolean setOperator(String operator) {
        m_operator = operator;
        return true;
    }

    /**
     * Sets the field of the constraint.
     *
     * @param field the <code>FormField</code> that this constraint is going to
     * be filtering on.
     */
    public void setField(FormField field) {
        m_field = field;
    }

    /**
     * Sets the operator or operand of the constraint.  For example: =, <, >,
     * !=, in, etc. If the operator is not a support operator, the set will not
     * take place.
     *
     * @param parameter The operand of the constraint.
     * @param type The type of the operand (ie. Text, Date, Number, etc.)
     * @return true if the typer is a valid parameter type for a
     * FormFieldConstraint,  false otherwise.
     */
    public boolean addParameter(String parameter, String type) {
        m_parameters.add(parameter);
        m_parameter_types.add(type);
        m_parameterMap.put(parameter, type);
        return true;
    }

    /**
     * Returns the operator of this constraint.
     *
     * @return the operator of this constraint.
     */
    public String getOperator() {
        return m_operator;
    }

    /**
     * Returns the FormField of this constraint.
     *
     * @return the FormField of this constraint.
     */
    public FormField getField() {
        return m_field;
    }

    /**
     * A Vector of all parameters of this constraint. This vector has a parallel
     * index order with the vector returned by {@link #getParameterTypes}.
     *
     * @return Arraylist of Parameters
     */
    public ArrayList getParameters() {
        return m_parameters;
    }

    /**
     * A Vector of all parameters of this constraint. This vector has a parallel
     * index order with the vector returned by {@link #getParameterTypes}.
     *
     * @return Arraylist of Parameters
     */
    public HashMap getParametersMap() {
        return m_parameterMap;
    }

    /**
     * A Vector of all parameter types of this constraint.  This vector has a
     * parallel index order with the vector returned by
     * {@link #getParameterTypes}.
     *
     * @return ArrayList of Paramter Types
     */
    public ArrayList getParameterTypes() {
        return m_parameter_types;
    }

    /**
     * Returns a comma separated list of all parameters.
     *
     * @return a comma separated list of all parameters.
     */
    public String getParameterCsvList() {

        int vector_size = m_parameters.size();
        String csv_string = null;
        int i;

        for (i = 0; i < vector_size; i++) {
            csv_string = csv_string + "," + m_parameters.get(i);
        }

        return csv_string;
    }

}


