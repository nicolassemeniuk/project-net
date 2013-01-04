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

 package net.project.form;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A filter constraint used by the FieldFilter class. A contraint has the
 * structure of: [field] [operator] [parameter] For example, field123 > 25 or
 * field123 in [34, 23, 45]
 */
public class FieldFilterConstraint extends ArrayList {
    /** The filter operator defaults to "=" */
    protected String m_operator = "=";

    public FieldFilterConstraint(int i) {
        super(i);
    }

    public FieldFilterConstraint() {
        super();
    }

    /**
     * Sets the operator or operand of the constraint. For example: =, <, >, !=,
     * in, etc. If the operator is not a support operator, the set will not take
     * place. <br><br> A contraint has the structure of: [field] [operator]
     * [parameter] <br> For example, field123 > 25 or field123 in [34, 23, 45]
     *
     * @param operator the operator token. @return true if the setOperator is a
     * valid operator. false otherwise.
     */
    public boolean setOperator(String operator) {
        m_operator = operator;
        return true;
    }

    /**
     * @return the operator of this constraint.
     */
    public String getOperator() {
        return m_operator;
    }

    /**
     * Return a comma separated list of all parameter. Parameters are returned
     * as Strings.
     *
     * @return a comma separated list of all parameter.
     */
    public String getParameterCsvList() {
        String csv_string = null;

        for (int i = 0; i < this.size(); i++)
            csv_string = csv_string + "," + this.get(i);

        return csv_string;
    }

    /**
     * Returning a string representing what will be queried from the database.
     *
     * @return a string representation of this collection.
     */
    public String toString(FormField field) {
        //If there isn't any data to filter on, return an empty string
        if ((this.size() == 0) || (this.get(0) == null)) {
            return "";
        }

        StringBuffer description = new StringBuffer();

        description.append(field.getFieldLabel()).append(" ");

        if (m_operator.equals("in")) {
            description.append("=");
        } else {
            description.append(m_operator);
        }
        description.append(" ");

        for (Iterator it = this.iterator(); it.hasNext();) {
            Object o = it.next();
            FieldData fieldData = new FieldData();
            fieldData.add(o);

            description.append(field.formatFieldData(fieldData).trim());

            if (it.hasNext()) {
                description.append(", or ");
            }
        }

        return description.toString();
    }
}


