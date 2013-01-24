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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * A Formula object maintains the formula as a stack to be used to calculate this fields value at runtime.
 * It also maintains vital information 1. whether this field is part of formula of
 * some other calculation field. 2. Whether any of the fields used to calculate this fields value
 * is deleted/invalid.
 */
public class Formula implements java.io.Serializable {

    /**
     * Formula element type of Operand, currently <code>"operand"</code>.
     * @see Operand#getElementType
     */
    public static final String OPERAND = "operand";

    /**
     * Formula element type of Operator, currently <code>"operator"</code>.
     * @see Operator#getElementType
     */
    public static final String OPERATOR = "operator";

    /**
     * The stack containing the formula.
     */
    private final Stack stack;

    /**
     * Creates an empty Formula.
     */
    public Formula() {
        stack = new java.util.Stack();
    }

    /**
     * Pushes a object to this stack.
     *
     * @param fe the FormulaElement object
     * @return the element just added to the stack
     * @see Stack#push
     */
    public IFormulaElement push(IFormulaElement fe) {
        return (IFormulaElement) this.stack.push(fe);
    }

    /**
     * Pops a formula element from the top of the stack.
     *
     * @return the element or null if the stack is empty
     * @see Stack#pop
     */
    public IFormulaElement pop() {

        IFormulaElement value = null;

        if (!isEmpty()) {
            value = (IFormulaElement) this.stack.pop();
        }

        return value;
    }

    /**
     * Returns an iterator over the formula elements in this formula.
     *
     * @return an iterator where each element is of type <code>IFormulaElement</code>
     */
    public Iterator iterator() {
        return stack.iterator();
    }

    /**
     * Indicates whether this formula is empty.
     *
     * @return true if the formula is empty (has no elements); false otherwise
     */
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    /**
     * Clears all elements from this formula.
     */
    public void clear() {
        stack.clear();
    }

    /**
     * Returns the number of elements in this formula.
     *
     * @return the number of elements in this formula
     */
    public int getLength() {
        return this.stack.size();
    }

    /**
     * Removes the formula element at the specified index position.
     *
     * @param index the position from which to remove the element
     */
    public void removeElementAt(int index) {
        this.stack.removeElementAt(index);
    }

    /**
     * Stores the formula.
     *
     * @param classID the ID of the designed form on which this calculation
     * field that owns this formula is defined
     * @param fieldID the ID of the calculation field that owns this formula
     * @throws PersistenceException if there is a problem storing
     */
    public void store(String classID, String fieldID) throws PersistenceException {

        DBBean db = new DBBean();

        try {
            String deleteStatement = "delete from pn_calculation_field_formula where class_id = " + classID + " and field_id =" + fieldID;

            db.setAutoCommit(false);
            db.createStatement();
            db.stmt.addBatch(deleteStatement);

            for (int i = this.getLength(); i > 0; i--) {
                IFormulaElement nextElement = this.pop();
                String value = nextElement.getValue();
                String elementType = nextElement.getElementType();
                Integer order = nextElement.getOrder();

                String query = "insert into pn_calculation_field_formula (class_id, field_id, order_id, op_value, op_type)" +
                        "values(" + classID + "," + fieldID + "," + order + ", '" + value + "'," + "'" + elementType + "'" + ")";

                db.stmt.addBatch(query);
            }

            db.stmt.executeBatch();

            // Commit all changes
            db.commit();

        } catch (SQLException sqle) {
            throw new PersistenceException("Error storing formula: " + sqle, sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Loads the formula.
     *
     * @param classID the ID of the designed form on which the calculation field
     * which owns this formula is defined
     * @param fieldID the ID of the calculation field that owns this formula
     */
    public void load(String classID, String fieldID) {

        String query = "select p.order_id, p.op_value, p.op_type " +
                "from pn_calculation_field_formula p" + " " +
                "where p.field_id = " + fieldID + " and p.class_id =" + classID + " " +
                "order by p.order_id asc";

        DBBean db = new DBBean();

        try {
            db.setQuery(query);
            db.executeQuery();

            while (db.result.next()) {
                String elementType = db.result.getString("op_type");
                String elementValue = db.result.getString("op_value");
                Integer elementOrder = db.result.getInt("order_id");

                if (elementType.equals(this.OPERAND)) {
                    this.push(new Operand(elementValue, elementOrder));

                } else if (elementType.equals(this.OPERATOR)) {
                    this.push(new Operator(elementValue, elementOrder));

                }
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Formula.class).error("Formula.load failed " + sqle);
            //throw new PersistenceException("failed to load formula");

        } finally {
            db.release();
        }

    }

    /**
     * Returns a collection of the elements in this Formula in reversed order.
     * This means the elements are returned in infix order since a loaded formula
     * is reversed.
     * Does not affect the formula itself.
     * @return an unmodifiable collection of the formula elements infix order;
     * each element is an {@link IFormulaElement}
     */
    Collection getReversedElements() {

        // Create the result list from the stack elements
        List result = new ArrayList(this.stack);
        Collections.reverse(result);

        return Collections.unmodifiableCollection(result);
    }

}
