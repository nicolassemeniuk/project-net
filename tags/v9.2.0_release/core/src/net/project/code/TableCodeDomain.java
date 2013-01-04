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
|    $Revision: 18397 $
|    $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|    $Author: umesha $
|
+----------------------------------------------------------------------*/
package net.project.code;

import java.sql.SQLException;
import java.util.ArrayList;

import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.persistence.RemoveException;
import net.project.util.Conversion;

import org.apache.log4j.Logger;

/**
 * An abstract group of codes.
 */
public class TableCodeDomain extends CodeDomain {
    public String tableName = null;
    public String columnName = null;
    public String objectID = null;

    /** constructor */
    public TableCodeDomain() {
        codes = new ArrayList(15);
    }

    /**
     * Set the database tablename for this domain.
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * Set the database column for this domain.
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }


    /**
     * Set the object context for this domain.  This is optional.  Some domains
     * are specific to objects.  If the object context is not set (or set to
     * null), the Global  (default) domain for the table_name, column_name will
     * be returned.
     */
    public void setObjectContext(String objectID) {
        this.objectID = objectID;
    }

    /*************************************************************************************************
     * Implementing IJDBCPersistnace.
     *************************************************************************************************/

    /** Load the domain from the persistance store. */
    public void load() {
        String qstrGetCodes = null;
        Code code = null;

        // clear the old code list.
        codes.clear();

        // if the object context is not set, use global domain.
        //** HMS ** Removed code for custom_domain table
        qstrGetCodes =
            "select " +
            "  code, code_name, code_desc, code_url, presentation_sequence, " +
            "  is_default " +
            "from " +
            "  pn_global_domain " +
            "where " +
            "  table_name= '" + this.tableName + "'"+
            "  and column_name='" + this.columnName + "'" +
            "  and record_status='A' " +
            "order by " +
            "  presentation_sequence asc";

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrGetCodes);

            while (db.result.next()) {

                code = createCode();

                code.objectID = db.result.getString("code");
                code.setNameToken(db.result.getString("code_name"));
                code.description = db.result.getString("code_desc");
                code.uri = db.result.getString("code_url");
                code.presentationSequence = Integer.parseInt(db.result.getString("presentation_sequence"));
                code.isDefault = Conversion.toBoolean(db.result.getString("is_default"));

                addCode(code);

            }
        } catch (SQLException sqle) {
        	Logger.getLogger(TableCodeDomain.class).debug("TableCodeDomain.load() threw a SQL exception");
        } finally {
            db.release();
        }
    } // end load

    /**
     * Returns a new Code object.
     * Sub-classes should override this method to return a Code of the appropriate
     * type for the sub-class.
     * @return the new Code
     */
    protected Code createCode() {
        return new Code();
    }

    /**
     * Adds the code to the code list.
     * Subclasses may override this method to decide whether a code should
     * be added. For example, A property may be defined that requires the
     * excludion of certain domain values
     * @param code the code to add
     */
    protected void addCode(Code code) {
        this.codes.add(code);
    }

    /** Save the domain to the persistance store. */
    public void store() throws PersistenceException {
        throw new PersistenceException("store() not supported for this class.");
    }

    /** Remove this domain from the persistance store. */
    public void remove() throws RemoveException {
        throw new RemoveException("remove() not supported for this class.");
    }

}





