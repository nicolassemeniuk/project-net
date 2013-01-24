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

 /*--------------------------------------------------------------------------------------+
|
|    $RCSfile$
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
+--------------------------------------------------------------------------------------*/
package net.project.process;

import java.sql.SQLException;
import java.sql.Timestamp;

import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * A phase gate.  Gates are part of a phase.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class Gate {
    protected String objectID = null;
    protected String gateName = null;
    protected String gateDesc = null;
    protected java.util.Date gateDate = null;
    protected String status = null;
    protected String statusID = null;
    protected String phaseID = null;
    protected boolean isLoaded = false;

    /**
     * Construct an empty Gate.
     */
    public Gate() {
    }

    /**
     * Construct an empty Gate, set it's id, then load it from the DB
     *
     * @param gate_id the id of the gate
     */
    public Gate(String gate_id)
        throws PersistenceException {
        this.objectID = gate_id;

        load();
    }

    /**
     * Clear out all of the values being stored in this gate.
     */
    public void clear() {
        objectID = null;
        gateName = null;
        gateDesc = null;
        gateDate = null;
        status = null;
        statusID = null;
        phaseID = null;
        isLoaded = false;
    }

    /**
     * Load the gate from the DB.
     *
     * @throws PersistenceException Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void load() throws PersistenceException {
        String qstrLoadGateProperties = "SELECT pn_gate.gate_name, pn_gate.gate_desc, pn_gate.gate_date, " +
            "pn_gate.phase_id, pn_gate.status_id, pn_global_domain.code_name " +
            "FROM pn_gate, pn_global_domain " +
            "WHERE pn_gate.gate_id = " + this.objectID + " " +
            "AND pn_gate.status_id = pn_global_domain.code " +
            "AND pn_global_domain.table_name = 'pn_gate' " +
            "AND pn_global_domain.column_name = 'status_id' ";
        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadGateProperties);

            if (db.result.next()) {
                this.gateName = db.result.getString("gate_name");
                this.gateDesc = db.result.getString("gate_desc");
                this.gateDate = db.result.getTimestamp("gate_date");
                this.status = PropertyProvider.get(db.result.getString("code_name"));
                this.statusID = db.result.getString("status_id");
                this.phaseID = db.result.getString("phase_id");
            } else {
            	Logger.getLogger(Gate.class).debug("Gate.load() could not load gate properties");
            }

            this.isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(Gate.class).debug("Gate.load() threw a SQL exception: " + sqle);
            throw new PersistenceException("Gate.load() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Store the gate in the DB
     *
     * @throws PersistenceException Thrown to indicate a failure storing in the database,
     * a system-level error.
     */
    public void store() throws PersistenceException {
        String qstrStoreGate = "begin  PROCESS.STORE_GATE(?,?,?,?,?,?,?,?,?,?,?); end;";

        DBBean db = new DBBean();
        try {
            db.prepareCall(qstrStoreGate);
            db.cstmt.setInt(1, Integer.parseInt(SessionManager.getUser().getID()));
            if ((objectID == null) || (objectID.length() == 0))
                db.cstmt.setNull(2, java.sql.Types.INTEGER);
            else
                db.cstmt.setInt(2, Integer.parseInt(objectID));

            if ((phaseID == null) || (phaseID.length() == 0))
                db.cstmt.setNull(3, java.sql.Types.INTEGER);
            else
                db.cstmt.setInt(3, Integer.parseInt(phaseID));

            db.cstmt.setInt(4, Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
            db.cstmt.setString(5, gateName);
            db.cstmt.setString(6, gateDesc);
            db.cstmt.setTimestamp(7, new Timestamp(gateDate.getTime()));
            db.cstmt.setInt(8, Integer.parseInt(statusID));
            db.cstmt.setString(9, "A");
            db.cstmt.registerOutParameter(10, java.sql.Types.INTEGER);
            db.cstmt.registerOutParameter(11, java.sql.Types.INTEGER);
            db.executeCallable();
            objectID = Integer.toString(db.cstmt.getInt(10));
        } catch (SQLException sqle) {
        	Logger.getLogger(Gate.class).debug("Gate.store() threw a SQL exception: " + sqle);
            throw new PersistenceException("Gate.store() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }


    }

    /**
     * Remove the gate from the DB
     *
     * @throws PersistenceException Thrown to indicate a failure removing the process
     *         from the database, a system-level error.
     */
    public void remove() throws PersistenceException {
        String qstrRemoveGate = "begin  PROCESS.REMOVE_GATE(?,?); end;";

        DBBean db = new DBBean();
        try {
            db.prepareCall(qstrRemoveGate);

            db.cstmt.setInt(1, Integer.parseInt(objectID));
            db.cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
            db.executeCallable();
        } catch (SQLException sqle) {
        	Logger.getLogger(Gate.class).debug("Gate.remove() threw a SQL exception: " + sqle);
            throw new PersistenceException("Gate.remove() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }

    }


    /**
     * Returns XML representation of the gate
     *
     * @return the XML String
     */
    public String getXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<gate>\n");
        sb.append("<gate_id>");
        sb.append(XMLUtils.escape(this.objectID));
        sb.append("</gate_id>\n");
        sb.append("<phase_id>");
        sb.append(XMLUtils.escape(this.phaseID));
        sb.append("</phase_id>\n");
        sb.append("<gate_name>");
        sb.append(XMLUtils.escape(this.gateName));
        sb.append("</gate_name>\n");
        sb.append("<gate_desc>");
        sb.append(XMLUtils.escape(this.gateDesc));
        sb.append("</gate_desc>\n");
        sb.append("<gate_date>");
        sb.append(XMLUtils.formatISODateTime(this.gateDate));
        sb.append("</gate_date>\n");
        sb.append("<status>");
        sb.append(XMLUtils.escape(this.status));
        sb.append("</status>\n");
        sb.append("</gate>\n");
        return sb.toString();
    }

    /**
     * loads the gate from XML
     *
     * @param xmlData the XML String to recreate from
     */
    public void fromXML(java.lang.String xmlData) {
    }

    /**
     * set the Id of this gate
     */
    public void setID(String m_objectID) {
        this.objectID = m_objectID;
    }

    /**
     * get the Id of this gate
     */
    public String getID() {
        return this.objectID;
    }

    /**
     * set the Phase Id of this gate
     */
    public void setPhaseID(String m_objectID) {
        this.phaseID = m_objectID;
    }

    /**
     * get the Phase Id of this gate
     */
    public String getPhaseID() {
        return this.phaseID;
    }

    /**
     * set the Name of this gate
     */
    public void setName(String name) {
        this.gateName = name;
    }

    /**
     * Return the name of this gate
     */
    public String getName() {
        return this.gateName;
    }

    /**
     * set the description of this gate
     */
    public void setDesc(String desc) {
        this.gateDesc = desc;
    }


    /**
     * Return the description of this gate
     */
    public String getDesc() {
        return this.gateDesc;
    }

    /**
     * set the date of this gate
     */
    public void setDate(java.util.Date date) {
        this.gateDate = date;
    }

    /**
     * Return the date of this gate
     */
    public java.util.Date getDate() {
        return this.gateDate;
    }

    /**
     * set the status of this gate
     */
    public void setStatus(String status) {
        this.statusID = status;
    }

    /**
     * Return the status of this gate
     */
    public String getStatus() {
        return this.statusID;
    }


    /**
     * Return the type of this object
     */
    public String getType() {
        return "gate";
    }

    /**
     * Get the string representation of this gate
     *
     * @return the string representation of this gate
     */
    public String toString() {
        return null;
    }


}



