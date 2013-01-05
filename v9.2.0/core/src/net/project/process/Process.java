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
|   $Revision: 19481 $
|       $Date: 2009-07-06 17:03:54 -0300 (lun, 06 jul 2009) $
|     $Author: puno $
|
| Copyright (c) 2000;  Bentley Systems, Inc., 690 Pennsylvania Drive,
|                      Exton PA, 19341-1136, USA.  All Rights Reserved.
|
| This program is confidential, proprietary and unpublished property of Bentley Systems
| Inc. It may NOT be copied in part or in whole on any medium, either electronic or
| printed, without the express written consent of Bentley Systems, Inc.
|
+--------------------------------------------------------------------------------------*/
package net.project.process;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.methodology.model.LinkContainer;
import net.project.methodology.model.ObjectLink;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.workflow.IWorkflowable;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * A project process.
 * Process has phases, which have gates and deliverables.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class Process implements Serializable, IWorkflowable {
    protected String m_objectID = null;
    protected String m_processName = null;
    protected String m_processDesc = null;
    protected String m_currentPhaseID = null;
    protected String m_lastGatePassedID = null;
    protected String m_spaceID = null;
    protected boolean m_isLoaded = false;
    protected PhaseList m_processPhases = new PhaseList();

    /**
     * Construct an empty Process
     */
    public Process() {
    }

    /**
     * Construct an empty Process, set it's id, then load it from the database
     *
     * @param m_objectID the id of the process
     */
    public Process(String m_objectID) throws PersistenceException {
        this.m_objectID = m_objectID;

        load();
    }

    /**
     * Load the process from the database
     * 
     * @throws PersistenceException
     *             Thrown to indicate a failure loading from the database, a
     *             system-level error.
     */
    public void load() throws PersistenceException {
        m_isLoaded = false;
        this.clear();

        m_processPhases = new PhaseList();

        String queryStringLoadProcessProperties = "select process_name, process_desc, current_phase_id, last_gate_passed_id " +
            "from pn_process " +
            "where process_id = " + this.m_objectID + " " +
            "and record_status = 'A'";        
        String queryStringLoadProcessPhases = "select phase_id, sequence, count(PN_ENVELOPE_HAS_OBJECT.ENVELOPE_ID) env " +
            "from pn_phase, PN_ENVELOPE_HAS_OBJECT " +
            "where pn_phase.process_id = " + this.m_objectID + " " +
            "and pn_phase.record_status = 'A' " +
            "and PN_ENVELOPE_HAS_OBJECT.OBJECT_ID(+) = pn_phase.phase_id " +
            "and PN_ENVELOPE_HAS_OBJECT.RECORD_STATUS(+) = 'A' " +
            "GROUP BY phase_id , sequence " +
            "ORDER by sequence ";        

        DBBean db = new DBBean();
        try {
            db.executeQuery(queryStringLoadProcessProperties);
            if (db.result.next()) {
                this.m_processName = db.result.getString("process_name");
                this.m_processDesc = db.result.getString("process_desc");
                this.m_currentPhaseID = db.result.getString("current_phase_id");
                this.m_lastGatePassedID = db.result.getString("last_gate_passed_id");
            } else {
            	Logger.getLogger(Process.class).debug("Process.load() could not load process properties");
            }
            db.executeQuery(queryStringLoadProcessPhases);
            while (db.result.next()) {
                Phase m_phase = new Phase();
                m_phase.setID(db.result.getString("phase_id"));
                m_phase.setActiveEnvelopeId(db.result.getInt("env"));
                m_processPhases.add(m_phase);
            }
            this.m_isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(Process.class).debug("Process.load() threw a SQL exception: " + sqle);
            throw new PersistenceException("Process.load() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }
    }


    /**
	 * The purpose of this method is to retrieve the process identifier when the
	 * space if where the process belongs to is known.
	 * 
	 * @throws PersistenceException
	 *             Thrown to indicate a failure loading from the database, a
	 *             system-level error.
	 * @since 8.2.0
	 */
	private void loadId() throws PersistenceException {
		String queryStringLoadProcessId = "select process_id from pn_space_has_process where space_id = " + this.m_spaceID;
		DBBean db = new DBBean();
		try {
			db.executeQuery(queryStringLoadProcessId);
			if (db.result.next()) {
				this.m_objectID = db.result.getString("process_id");
			} else {
				Logger.getLogger(Process.class).debug("Process.loadId(String spaceId) could not load process identifier");
			}
		} catch (SQLException sqle) {
			Logger.getLogger(Process.class).debug("Process.loadId(String spaceId) threw a SQL exception: " + sqle);
			throw new PersistenceException("Process.loadId(String spaceId) thew a SQL exception: " + sqle, sqle);
		} finally {
			db.release();
		}
	}
    
    public void copyAll(String fromSpaceID, String toSpaceID) throws PnetException {

        int errorCode = 0;
        DBBean db = new DBBean();
        try {
        	int index = 0;
            db.prepareCall("begin PROCESS.COPY_PROCESS  (?,?,?,?); end;");
            DatabaseUtils.setInteger(db.cstmt, ++index, fromSpaceID);
            DatabaseUtils.setInteger(db.cstmt, ++index, toSpaceID);
            DatabaseUtils.setInteger(db.cstmt, ++index, SessionManager.getUser().getID());
            db.cstmt.registerOutParameter(++index, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(index);
        } // end try
        catch (SQLException sqle) {
        	Logger.getLogger(Process.class).debug("Process.copyAll():  unable to execute stored procedure: " + sqle);
            throw new PersistenceException("Process.copyAll operation failed! ", sqle);
        } finally {
            db.release();
        }

        net.project.database.DBExceptionFactory.getException("Process.copyAll()", errorCode);

    }

    /**
	 * Copy a process into a space, making copies of each phases, gate, deliverable in the process.
	 * 
	 * @param toSpaceID
	 *            a <code>String</code> containing the space id to which we
	 *            are going to copy the process.
	 * @return a <code>HashMap</code> with a map between old phases and new
	 *         phases.
	 * @throws PersistenceException
	 *             if an error occurs while storing/loading phases.
	 * @since 8.2.0
	 */
	public HashMap copyToSpace(String toSpaceID) throws PersistenceException {
		HashMap phaseIdMap = new HashMap();
		if (this.m_objectID == null) {
			this.loadId();
		}
		if (!this.m_isLoaded) {
			load();
		}
		this.loadPhases();
		DBBean db = new DBBean();
		try {
			/* Proceed to create a new process in the target space. */
			Process targetProcess = new Process();
			targetProcess.setSpaceID(toSpaceID);
			targetProcess.setName(this.m_processName);
			targetProcess.setDesc(this.m_processDesc);
			targetProcess.store();
			/*
			 * Now, maps the original phases ids that the phase current have
			 * with the new ones we'll get after we store.
			 */
			for (Iterator it = this.m_processPhases.iterator(); it.hasNext();) {
				Phase phase = (Phase) it.next();
				String oldPhaseID = phase.getID();
				
				// load gate and deliverable for old phase
				phase.loadGate();
				phase.loadDeliverables();
				Gate gate = phase.getGate();
				DeliverableList deliverableList = phase.getDeliverableList();
				
				// copy phase
				phase.setID(null);
				phase.setProcessID(targetProcess.getID());
				phase.store();
				
				// load and copy gate, if its available
				if (gate != null) {
					gate.setID(null);
					gate.setPhaseID(phase.getID());
					gate.store();
				}
				
				// copy deliverables 
				for (Iterator dIterator = deliverableList.iterator(); dIterator.hasNext();) {
					Deliverable deliverable = (Deliverable) dIterator.next();
					String oldDeliverableId = deliverable.getID();
					deliverable.setID(null);
					deliverable.setPhaseID(phase.getID());
					deliverable.store();
					
					/*
	                List<ObjectLink> documentLinks = LinkContainer.getInstance().getLinks(LinkContainer.DOCUMENT);
	                for(ObjectLink o: documentLinks){
	                	Integer toObjectIdOld = o.getToObjectIdOld();
	                	Integer fromObjectIdOld = o.getFromObjectIdOld();
	                	if(toObjectIdOld.equals(Integer.valueOf(oldDeliverableId))){
	                		documentLinks.remove(o);
	                		o.setToObjectIdNew(Integer.valueOf(deliverable.getID()));
	                		documentLinks.add(o);
	                		break;
	                	}
	                	if(fromObjectIdOld.equals(Integer.valueOf(oldDeliverableId))){
	                		documentLinks.remove(o);
	                		o.setFromObjectIdNew(Integer.valueOf(deliverable.getID()));
	                		documentLinks.add(o);
	                		break;
	                	}	                	
					}
	                LinkContainer.getInstance().setLinks(LinkContainer.DOCUMENT,documentLinks);
	                */
					LinkContainer.getInstance().updateLinks(Integer.valueOf(oldDeliverableId), Integer.valueOf(deliverable.getID()));
				}
				
				phaseIdMap.put(oldPhaseID, phase.getID());
			}
		} finally {
			db.release();
		}
		return phaseIdMap;
	}
    

    /**
	 * Load the phases for this Process
	 * 
	 * @throws PersistenceException
	 *             Thrown to indicate a failure loading from the database, a
	 *             system-level error.
	 */
    public void loadPhases() throws PersistenceException {
        for (int i = 0; i < m_processPhases.size(); i++) {
            Phase l_phase = (Phase)m_processPhases.get(i);
            l_phase.load();
            m_processPhases.set(i, l_phase);
        }
    }


    /**
	 * Store the process in the database
	 * 
	 * @throws PersistenceException
	 *             Thrown to indicate a failure storing in the database, a
	 *             system-level error.
	 */
	public void store() throws PersistenceException {
		String queryStringStoreProcess = "begin  PROCESS.STORE_PROCESS(?,?,?,?,?,?,?,?,?,?); end;";

		DBBean db = new DBBean();
		int index = 0;
		try {
			db.prepareCall(queryStringStoreProcess);
			DatabaseUtils.setInteger(db.cstmt, ++index, SessionManager.getUser().getID());
			DatabaseUtils.setInteger(db.cstmt, ++index, this.m_spaceID);
			DatabaseUtils.setInteger(db.cstmt, ++index, this.m_objectID);
			db.cstmt.setString(++index, m_processName);
			db.cstmt.setString(++index, m_processDesc);
			DatabaseUtils.setInteger(db.cstmt, ++index, this.m_currentPhaseID);
			DatabaseUtils.setInteger(db.cstmt, ++index, this.m_lastGatePassedID);
			db.cstmt.setString(++index, "A");
			db.cstmt.registerOutParameter(++index, java.sql.Types.INTEGER);
			int indexId = index;
			db.cstmt.registerOutParameter(++index, java.sql.Types.INTEGER);
			db.executeCallable();
			m_objectID = Integer.toString(db.cstmt.getInt(indexId));

		} catch (SQLException sqle) {
			Logger.getLogger(Process.class).debug("Process.store() threw a SQL exception: " + sqle);
			throw new PersistenceException(
					"Process.store() thew a SQL exception: " + sqle, sqle);
		} finally {
			db.release();
		}
	}

    /**
	 * Remove the process from the database
	 * 
	 * @throws PersistenceException
	 *             Thrown to indicate a failure removing the process from the
	 *             database, a system-level error.
	 */
	public void remove() throws PersistenceException {
		String queryStringRemoveProcess = "begin  PROCESS.REMOVE_PROCESS(?,?); end;";

		DBBean db = new DBBean();
		try {
			db.prepareCall(queryStringRemoveProcess);
			DatabaseUtils.setInteger(db.cstmt, 1, this.m_objectID);
			db.cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
			db.executeCallable();
		} catch (SQLException sqle) {
			Logger.getLogger(Process.class).debug("Process.remove() threw a SQL exception: " + sqle);
			throw new PersistenceException(
					"Process.remove() thew a SQL exception: " + sqle, sqle);
		} finally {
			db.release();
		}

	}

    /**
	 * Returns XML representation of the process
	 */
    public String getXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" ?>\n");
        sb.append("<process>\n");
        sb.append("<process_id>");
        sb.append(XMLUtils.escape(this.m_objectID));
        sb.append("</process_id>\n");
        sb.append("<process_name>");
        sb.append(XMLUtils.escape(this.m_processName));
        sb.append("</process_name>\n");
        sb.append("<process_desc>");
        sb.append(XMLUtils.escape(this.m_processDesc));
        sb.append("</process_desc>\n");
        sb.append("<current_phase_id>");
        sb.append(XMLUtils.escape(this.m_currentPhaseID));
        sb.append("</current_phase_id>\n");
        sb.append("<last_gate_passed_id>");
        sb.append(XMLUtils.escape(this.m_lastGatePassedID));
        sb.append("</last_gate_passed_id>\n");
        sb.append("</process>\n");
        return sb.toString();
    }

    /**
     * loads the process from XML
     */
    public void fromXML(java.lang.String xmlData) {
    }

    /**
     * set the Id of this process
     */
    public void setID(String m_m_objectID) {
        this.m_objectID = m_m_objectID;
    }

    /**
     * get the Id of this process
     */
    public String getID() {
        return this.m_objectID;
    }

    /**
     * get whether or not a process has been loaded from the database.
     */
    public boolean isLoaded() {
        return this.m_isLoaded;
    }


    /**
     * get whether or not the specified process has been loaded from the database.
     */
    public boolean isLoaded(String process_id) {
        if (m_objectID.equals(process_id))
            return this.m_isLoaded;
        else
            return false;
    }


    /**
     * get the process name
     */
    public String getName() {
        return this.m_processName;
    }

    /**
     * Set the name of this process
     */
    public void setName(String name) {
        this.m_processName = name;
    }


    /**
     * Return the type of this object
     */
    public String getType() {
        return "process";
    }

    /**
     * Set the description of this process
     */
    public void setDesc(String desc) {
        this.m_processDesc = desc;
    }

    /**
     * Return the description of this process
     */
    public String getDesc() {
        return m_processDesc;
    }

    /**
     * Set the space ID of this process
     */
    public void setSpaceID(String id) {
        this.m_spaceID = id;
    }

    /**
     * Return the space ID of this process
     */
    public String getSpaceID() {
        return m_spaceID;
    }


    /**
     * Add a phase to this process
     *
     * @param phase The phase to be added
     */
    public void addPhase(Phase phase) {
        m_processPhases.add(phase);
    }

    /**
     * Get the list of phases for this process
     *
     * @return An ArrayList of phases
     */
    public PhaseList getPhaseList() {
        return m_processPhases;
    }


    /**
     * Clear the data from this process.
     */
    public void clear() {
        //m_objectID  = null;
        m_processName = null;
        m_processDesc = null;
        m_currentPhaseID = null;
        m_lastGatePassedID = null;
        // m_spaceID = null;
        m_isLoaded = false;

        if (m_processPhases != null)
            m_processPhases.clear();
    }

    public String getObjectType() {
		return null;
	}

	public String getPresentation() {
		return null;
	}

	public String getSubType() {
		return null;
	}

	public String getVersionID() {
		return null;
	}

	public boolean isSpecialPresentation() {
		return false;
	}

	public String getXMLBody() throws SQLException {
		return null;
	}
}


