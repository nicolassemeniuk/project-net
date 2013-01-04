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
|   $Revision: 19952 $
|       $Date: 2009-09-11 15:33:15 -0300 (vie, 11 sep 2009) $
|     $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.form;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

import net.project.base.ObjectType;
import net.project.database.DBBean;
import net.project.database.DBFormat;
import net.project.persistence.PersistenceException;

import org.apache.log4j.Logger;

/**
 * Represents a set of FieldFilterConstaint constraints for a single FormField
 * used to filter a FormList. Limits the Formlist items displayed.
 *
 * @author Roger Bly
 * @since 11/2000
 */
public class FieldFilter implements java.io.Serializable {
    protected FormList m_list = null;
    protected FormField m_field = null;
    protected ArrayList m_constraints = null;
    protected String m_joinOperator = "and";

    // db access bean
    protected boolean m_isLoaded = false;

    public FieldFilter() {
        m_constraints = new ArrayList();
    }

    public FieldFilter(FormField field) {
        m_constraints = new ArrayList();
        m_field = field;
    }


    /** Set the FormField this filter is for. */
    public void setField(FormField field) {
        m_field = field;
    }

    /** get the FormField for this filter is for. */
    public FormField getField() {
        return m_field;
    }

    /** Set the FormList this filter is for. */
    public void setList(FormList list) {
        m_list = list;
    }

    /** get the FormList for this filter is for. */
    public FormList getList() {
        return m_list;
    }

    /**
     * Set the boolen operator used to join this FieldFilter to the preceeding
     * filter.
     *
     * @param joinOperator the String boolean operator "and" or "or".
     */
    public void setJoinOperator(String joinOperator) {
        m_joinOperator = joinOperator;
    }

    /**
     * Add a FieldFilterConstraint to the constraint list
     */
    public void addConstraint(FieldFilterConstraint constraint) {
        m_constraints.add(constraint);
    }

    /** get the FieldFilterConstraint at position i in the constraint list */
    public FieldFilterConstraint getConstraint(int i) {
        return (FieldFilterConstraint)m_constraints.get(i);
    }

    /** return the number of constraint for the field filter */
    public int size() {
        return m_constraints.size();
    }

    /** Return an SQL representation of the field filter to be used in a WHERE clause. */
    public String getSQL() {
        return m_field.getFilterSQL(this, m_joinOperator);
    }
    
    /** Return an SQL representation of the field filter with table name to be used in a WHERE clause. */
    public String getSQL(String tableName) {
    	m_field.setFormTableName(tableName);
        return m_field.getFilterSQL(this, m_joinOperator);
    }

    /** has this FieldFilter been loaded from the database. */
    public boolean isLoaded() {
        return m_isLoaded;
    }

    /**
     * Return an iterator pointing to the list of constraints.
     *
     * @return a <code>Iterator</code> value which points to all of the
     * constraints that this FieldFilter contains.
     */
    public Iterator getConstraintsIterator() {
        return m_constraints.iterator();
    }

    public void store() throws PersistenceException {
        DBBean db = new DBBean();
        try {
            store(db);
        } finally {
            db.release();
        }
    }

    /** store the filter in the database */
    public void store(DBBean db)
        throws PersistenceException {
        String query;
        FieldFilterConstraint constraint = null;
        String value_id;
        int num_constraints = 0;
        int num_values = 0;

        if ((m_list == null) || (m_field == null))
            throw new NullPointerException("Could not store list filter.  list or field is null.");

        try {
            // delete the old filter for this list field.
            query = "delete from pn_class_list_filter where class_id=" + m_list.m_class_id + " and list_id=" + m_list.getID() + " and field_id=" + m_field.getID();
            db.setQuery(query);
            db.executeQuery();

            // Insert the filter information into the pn_class_list_filter table.
            num_constraints = m_constraints.size();

            // For each constraint
            for (int i = 0; i < num_constraints; i++) {
                constraint = (FieldFilterConstraint)m_constraints.get(i);
                num_values = constraint.size();

                // For each constraint value
                for (int j = 0; j < num_values; j++) {
                    if ((constraint.getOperator() != null) && (constraint.get(j) != null) && !(constraint.get(j)).equals("")) {
                        // get new value_id and register in the pn_object table.
                        value_id = m_list.m_form.objectManager.dbCreateObjectWithPermissions(ObjectType.FORM_FILTER_VALUE, "A", m_field.m_form.getSpace().getID(), m_field.m_form.getUser().getID());

                        query = "insert into pn_class_list_filter (class_id, list_id, field_id, value_id, operator, filter_value) " +
                            "values (" + m_list.m_class_id + "," + m_list.getID() + "," + m_field.getID() + "," + value_id + "," +
                            DBFormat.varchar2(constraint.getOperator()) + "," + DBFormat.varchar2((String)constraint.get(j)) + ")";
                        db.setQuery(query);
                        db.executeQuery();
                    }
                }
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(FieldFilter.class).error("FieldFilter.store failed " + sqle);
        }
    }


    /** load the filter from the database */
    public void load() {
        String lastOperator = null;
        FieldFilterConstraint constraint = null;
        String query;

        m_isLoaded = false;
        DBBean db = new DBBean();
        try {
            query = "select operator, filter_value from pn_class_list_filter where class_id=" + m_list.m_class_id + " and list_id=" + m_list.getID() + " and field_id=" + m_field.getID() +
                "order by operator";
            db.setQuery(query);
            db.executeQuery();

            while (db.result.next()) {
                // if new operator
                if (!db.result.getString(1).equals(lastOperator)) {
                    if (lastOperator != null)
                        m_constraints.add(constraint);

                    constraint = new FieldFilterConstraint();
                    constraint.setOperator(db.result.getString(1));
                    lastOperator = db.result.getString(1);
                }
                constraint.add(db.result.getString(2));
            }
            m_constraints.add(constraint);
            m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(FieldFilter.class).error("FieldFilter.load() failed " + sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Determines if this filter is going to be creating a valid SQL Statement.
     *
     * @return a <code>String</code> value indicating whether this filter will
     * be returning valid SQL where clause.  If false, this filter will
     * contribute nothing to the SQL Statement.
     */
    public boolean isSelected() {
        boolean isSelected = false;

        //First check to see if the constraint list even exists.
        if (m_constraints != null) {
            //Look through each constraint to see if there is anything that is going
            //to be applied.
            for (Iterator it = m_constraints.iterator(); it.hasNext();) {
                FieldFilterConstraint fieldFilterConstraint = (FieldFilterConstraint)it.next();

                //For each constaint, look through all of its data to see if there
                //is any valid data
                for (Iterator it2 = fieldFilterConstraint.iterator(); it2.hasNext();) {
                    Object o = it2.next();
                    if (!((o == null) || (o.toString().equals("")))) {
                        return true;
                    }
                }
            }
        }

        return isSelected;
    }

    /**
     * Returns a string representation of the object. In general, the
     * <code>toString</code> method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     *
     * @return  a string representation of the object.
     */
    public String toString() {
        StringBuffer description = new StringBuffer();

        //Iterate through the constraints, showing each
        boolean visitedFirst = false;
        for (Iterator it = m_constraints.iterator(); it.hasNext();) {
            FieldFilterConstraint fieldFilterConstraint = (FieldFilterConstraint)it.next();

            String constraintString = fieldFilterConstraint.toString(m_field);

            if (!constraintString.equals("")) {
                if (visitedFirst) {
                    description.append(" ").append(m_joinOperator).append(" ");
                } else {
                    visitedFirst = true;
                }

                description.append(constraintString);
            }
        }

        return description.toString();
    }
}

