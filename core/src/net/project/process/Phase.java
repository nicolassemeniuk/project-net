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

 package net.project.process;

import java.net.URLEncoder;
import java.sql.SQLException;

import net.project.base.Module;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DatabaseUtils;
import net.project.persistence.PersistenceException;
import net.project.security.Action;
import net.project.security.SessionManager;
import net.project.util.DateFormat;
import net.project.workflow.IWorkflowable;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * Phases can have a gate, and can have multiple deliverables.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class Phase implements IWorkflowable {
    protected String objectID = null;
    protected String m_phase_name = null;
    protected String m_phase_desc = null;
    protected String m_sequence = null;
    protected String m_status = null;
    protected String m_status_id = null;
    protected String m_process_id = null;
    protected boolean isLoaded = false;
    protected boolean hasGate = false;
    protected int activeEnvelopeId = 0;
    protected DeliverableList m_deliverables = new DeliverableList();
    protected Gate m_gate = null;
    protected String m_space_id = null;

    /** Represents the method used to report/calculate the progress of a phase */
    private ProgressReportingMethod progressReportingMethod;

    /** Phase progress (start date, finish date, percent complete */
    private Progress progress = new Progress();

    /**
     * Construct an empty Phase.
     */
    public Phase() {
    }

    /**
     * Construct an empty Phase, set it's id, then load it from the DB
     *
     * @param objectID the id of the phase
     */
    public Phase(String objectID) throws PersistenceException {
        this.objectID = objectID;

        load();
    }

    /**
     * Load the phase from the DB
     *
     * @throws PersistenceException Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void load() throws PersistenceException {
        DBBean db = new DBBean();        
        m_deliverables = new DeliverableList();
        isLoaded = false;
        hasGate = false;

        String qstrLoadPhaseProperties = "select pn_phase.phase_name, pn_phase.phase_desc, pn_phase.sequence, " +
                "pn_phase.process_id, pn_phase.status_id, pn_global_domain.code_name, pn_phase.progress_reporting_method, " +
                "pn_phase.start_date as manual_start_date, pn_phase.end_date as manual_end_date, entered_percent_complete as manual_percent_complete, " +
                "process.get_phase_workplan_start(" + this.objectID + ") as schedule_start_date, " +
                "process.get_phase_workplan_finish(" + this.objectID + ") as schedule_end_date, " +
                "process.get_phase_schedule_complete(" + this.objectID + ") as schedule_percent_complete, pn_space_has_process.space_id " +
                "FROM pn_phase, pn_global_domain, pn_space_has_process " +
                "WHERE pn_phase.phase_id = ? " +
                "AND pn_phase.status_id = pn_global_domain.code " +
                "AND pn_global_domain.table_name = 'pn_phase' " +
                "AND pn_global_domain.column_name = 'status_id' " +
                "AND pn_space_has_process.process_id = pn_phase.process_id " +
                "AND pn_phase.record_status = 'A'";

        String qstrLoadPhaseGate = "select gate_id from pn_gate where phase_id = ? " +
                "and record_status = 'A'";

        String qstrLoadPhaseDeliverables = "select pn_phase_has_deliverable.deliverable_id, PN_ENVELOPE_HAS_OBJECT.ENVELOPE_ID env " +
                "from pn_phase_has_deliverable, pn_deliverable, PN_ENVELOPE_HAS_OBJECT " +
                "where pn_phase_has_deliverable.phase_id = ? " +
                "and pn_deliverable.deliverable_id = pn_phase_has_deliverable.deliverable_id " +
                "and pn_deliverable.record_status = 'A' "  +
                "and PN_ENVELOPE_HAS_OBJECT.OBJECT_ID(+) = pn_phase_has_deliverable.deliverable_id " +
                "and PN_ENVELOPE_HAS_OBJECT.RECORD_STATUS(+) = 'A' ";

        try {
            db.prepareStatement(qstrLoadPhaseProperties);
            db.pstmt.setString(1, this.objectID);
            db.executePrepared();

            if (db.result.next()) {
                this.m_phase_name = db.result.getString("phase_name");
                this.m_phase_desc = db.result.getString("phase_desc");
                this.m_sequence = db.result.getString("sequence");
                this.m_status = PropertyProvider.get(db.result.getString("code_name"));
                this.m_status_id = db.result.getString("status_id");
                this.m_process_id = db.result.getString("process_id");
                setProgressReportingMethod(db.result.getString("progress_reporting_method"));

                this.progress.clear();

                if (this.progressReportingMethod.equals(ProgressReportingMethod.SCHEDULE)){
                    this.setStart(DatabaseUtils.makeDate(db.result.getTimestamp("schedule_start_date")));
                    this.setEnd(DatabaseUtils.makeDate(db.result.getTimestamp("schedule_end_date")));
                    this.setPercentComplete(db.result.getString("schedule_percent_complete"));
                } else {
                    this.setStart(DatabaseUtils.makeDate(db.result.getTimestamp("manual_start_date")));
                    this.setEnd(DatabaseUtils.makeDate(db.result.getTimestamp("manual_end_date")));
                    this.setPercentComplete(db.result.getString("manual_percent_complete"));
                }
                this.m_space_id = db.result.getString("space_id");
            } else {
            	Logger.getLogger(Phase.class).debug("Phase.load() could not load phase properties");
            }

            db.prepareStatement(qstrLoadPhaseGate);
            db.pstmt.setString(1, this.objectID);
            db.executePrepared();

            if (db.result.next()) {
                Gate l_gate = new Gate();
                l_gate.setID(db.result.getString("gate_id"));
                this.m_gate = l_gate;
                this.hasGate = true;
            } else {
                this.hasGate = false;
            }

            db.prepareStatement(qstrLoadPhaseDeliverables);
            db.pstmt.setString(1, this.objectID);
            db.executePrepared();

            while (db.result.next()) {
                Deliverable l_deliverable = new Deliverable();
                l_deliverable.setID(db.result.getString("deliverable_id"));
                l_deliverable.setActiveEnvelopeId(db.result.getInt("env"));
                this.m_deliverables.add(l_deliverable);
            }
            loadGate();
            this.isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(Phase.class).debug("Phase.load() threw a SQL exception: " + sqle);
            throw new PersistenceException("Phase.load() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Load the gate for this phase
     *
     * @throws PersistenceException Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void loadGate() throws PersistenceException {
        if (this.hasGate) {
            this.m_gate.load();
        }
    }

    /**
     * Load the deliverables for this phase
     *
     * @throws PersistenceException Thrown to indicate a failure loading from the database, a system-level error.
     */
    public void loadDeliverables() throws PersistenceException {
    	int deliverableSize = this.m_deliverables.size();
        for (int i = 0; i < deliverableSize; i++) {
            Deliverable l_deliverable = (Deliverable)this.m_deliverables.get(i);
            l_deliverable.load();
            this.m_deliverables.set(i, l_deliverable);
        }
    }

    /**
	 * Store the phase in the DB
	 * 
	 * @throws PersistenceException
	 *             Thrown to indicate a failure storing in the database, a
	 *             system-level error.
	 */
	public void store() throws PersistenceException {
		DBBean db = new DBBean();
		String qstrStorePhase = "begin  PROCESS.STORE_PHASE(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?); end;";

		try {
			db.prepareCall(qstrStorePhase);
			int index = 0;
			DatabaseUtils.setInteger(db.cstmt, ++index, SessionManager
					.getUser().getID());
			DatabaseUtils.setInteger(db.cstmt, ++index, this.objectID);
			DatabaseUtils.setInteger(db.cstmt, ++index, this.m_process_id);
			DatabaseUtils.setInteger(db.cstmt, ++index, SessionManager
					.getUser().getCurrentSpace().getID());
			db.cstmt.setString(++index, this.m_phase_name);
			db.cstmt.setString(++index, this.m_phase_desc);
			db.cstmt.setFloat(++index, Float.parseFloat(this.m_sequence)); // TODO:
																			// use
																			// number
																			// fomatter
			DatabaseUtils.setInteger(db.cstmt, ++index, this.m_status_id);
			db.cstmt.setString(++index, "A");
			db.cstmt.setString(++index, getProgressReportingMethod().getID());

			// only set the start date, finish date and % complete if the
			// progress method
			// is manual
			if (getProgressReportingMethod().equals(
					ProgressReportingMethod.MANUAL)) {
				DatabaseUtils.setTimestamp(db.cstmt, ++index, getProgress()
						.getStartDate());
				DatabaseUtils.setTimestamp(db.cstmt, ++index, getProgress()
						.getFinishDate());
				db.cstmt.setFloat(++index, Float.parseFloat(getProgress()
						.getPercentComplete())); // TODO: convert to user
													// number format
			} else {
				db.cstmt.setTimestamp(++index, null);
				db.cstmt.setTimestamp(++index, null);
				db.cstmt.setInt(++index, 0);
			}

			int objectIDIndex = ++index;

			db.cstmt
					.registerOutParameter(objectIDIndex, java.sql.Types.INTEGER);
			db.cstmt.registerOutParameter(++index, java.sql.Types.INTEGER);
			db.executeCallable();
			this.objectID = Integer.toString(db.cstmt.getInt(objectIDIndex));
		} catch (SQLException sqle) {
			Logger.getLogger(Phase.class).debug("Phase.store() threw a SQL exception: "
					+ sqle);
			throw new PersistenceException(
					"Phase.store() thew a SQL exception: " + sqle, sqle);
		} finally {
			db.release();
		}

	}

    /**
	 * Remove the phase from the DB
	 * 
	 * @throws PersistenceException
	 *             Thrown to indicate a failure removing the process from the
	 *             database, a system-level error.
	 */
    public void remove() throws PersistenceException {
        DBBean db = new DBBean();
        String qstrRemovePhase = "begin PROCESS.REMOVE_PHASE(?); end;";

        try {
            db.prepareCall(qstrRemovePhase);
            DatabaseUtils.setInteger(db.cstmt, 1, this.objectID);            
            db.executeCallable();
        } catch (SQLException sqle) {
        	Logger.getLogger(Phase.class).debug("Phase.remove() threw a SQL exception: " + sqle);
            throw new PersistenceException("Phase.remove() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }

    }

    /**
     * Returns XML representation of the phase.
     */
    public String getXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<phase>\n");
        sb.append("<phase_id>");
        sb.append(XMLUtils.escape(this.objectID));
        sb.append("</phase_id>\n");
        sb.append("<process_id>");
        sb.append(XMLUtils.escape(this.m_process_id));
        sb.append("</process_id>\n");
        sb.append("<phase_name>");
        sb.append(XMLUtils.escape(this.m_phase_name));
        sb.append("</phase_name>\n");
        sb.append("<phase_desc>");
        sb.append(XMLUtils.escape(this.m_phase_desc));
        sb.append("</phase_desc>\n");
        sb.append("<start_date>");
        sb.append(DateFormat.getInstance().formatDateMedium(getProgress().getStartDate()));
        sb.append("</start_date>\n");
        sb.append("<end_date>");
        sb.append(DateFormat.getInstance().formatDateMedium(getProgress().getFinishDate()));
        sb.append("</end_date>\n");
        sb.append("<sequence>");
        sb.append(XMLUtils.escape(this.m_sequence));
        sb.append("</sequence>\n");
        sb.append("<status>");
        sb.append(XMLUtils.escape(this.m_status));
        sb.append("</status>\n");
        sb.append("<activeEnvelopeId>");
        sb.append(this.activeEnvelopeId);
        sb.append("</activeEnvelopeId>\n");
        sb.append("<percent_complete>");
        //sb.append(XMLUtils.escape(this.m_entered_percent_complete));
        sb.append(XMLUtils.escape(getPercentComplete()));
        sb.append("</percent_complete>\n");
        if (this.hasGate)
            sb.append(this.m_gate.getXML());
        else
            sb.append("<gate />");
        sb.append("<jsp_root_url>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        sb.append("<phase_url>");
        try {
            sb.append(XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()+ "/project/Main.jsp?id="+ this.m_space_id+ "&page=" 
                    + URLEncoder.encode(net.project.security.SessionManager.getJSPRootURL()+ "/process/ViewPhase.jsp?action="+Action.VIEW+"&module="+Module.PROCESS+"&id=" + this.objectID, SessionManager.getCharacterEncoding())));
        } catch (Exception e) {
            Logger.getLogger(Phase.class).debug("error while encoding phase url: " +e.getMessage());
        }
        sb.append("</phase_url>");
        sb.append("</phase>\n");
        return sb.toString();
    }

    /**
     * Loads the phase from XML.
     */
    public void fromXML(java.lang.String xmlData) {
    }

    /**
     * Set the ID of this phase.
     */
    public void setID(String m_objectID) {
        this.objectID = m_objectID;
    }

    /**
     * Get the Id of this phase.
     */
    public String getID() {
        return this.objectID;
    }

    /**
     * Set the Id of this phase's process.
     */
    public void setProcessID(String id) {
        this.m_process_id = id;
    }


    /**
     * Get the Id of this phase's process.
     */
    public String getProcessID() {
        return this.m_process_id;
    }

    /**
     * Return the name of this phase.
     */
    public String getName() {
        return this.m_phase_name;
    }

    /**
     * Set the name of this phase.
     */
    public void setName(String name) {
        this.m_phase_name = name;
    }

    /**
     * Return the desc of this phase.
     */
    public String getDesc() {
        return this.m_phase_desc;
    }

    /**
     * Set the desc of this phase.
     */
    public void setDesc(String desc) {
        this.m_phase_desc = desc;
    }

    /**
     * Return the start_date of this phase.
     */
    public java.util.Date getStart() {
        return getProgress().getStartDate();
    }

    /**
     * Set the start_date of this phase.
     */
    public void setStart(java.util.Date start) {
        this.progress.setStartDate(start);
    }

    /**
     * Return the end_date of this phase.
     */
    public java.util.Date getEnd() {
        return getProgress().getFinishDate();
    }

    /**
     * Set the end_date of this phase.
     */
    public void setEnd(java.util.Date end) {
        this.progress.setFinishDate(end);
    }

    /**
     * Return the sequence of this phase.
     */
    public String getSequence() {
        return this.m_sequence;
    }

    /**
     * Set the sequence of this phase.
     */
    public void setSequence(String seq) {
        this.m_sequence = seq;
    }

    /**
     * Return the status_id of this phase.
     */
    public String getStatus() {
        return this.m_status_id;
    }

    /**
     * Set the status_id of this phase.
     */
    public void setStatus(String status) {
        this.m_status_id = status;
    }

    /**
     * Return the percent complete of this phase.
     */
    public String getPercentComplete() {
        // now returning "0" if the %complete is null due to that possibility occuring with schedule auto-calc.
        return (getProgress().getPercentComplete() != null) ? getProgress().getPercentComplete() : "0";
    }

    /**
     * Set the percent complete of this phase.
     */
    public void setPercentComplete(String percent) {
        this.progress.setPercentComplete(percent);
    }

    public Progress getProgress() {
        return this.progress;
    }

    public void setProgressReportingMethod(String progressMethodID) {
        this.progressReportingMethod = ProgressReportingMethod.getForID(progressMethodID);
    }


    public ProgressReportingMethod getProgressReportingMethod() {
        return this.progressReportingMethod;
    }


    /**
     * Return the type of this object.
     */
    public String getType() {
        return "phase";
    }

    /**
     * Set the gate of this phase.
     *
     * @param gate the gate for this phase
     */
    public void setGate(Gate gate) {
        m_gate = gate;
        hasGate = true;
    }

    /**
     * Get the gate of this phase.
     *
     * @return the gate for this phase
     */
    public Gate getGate() {
        return m_gate;
    }

    /**
     * Get whether or not this phase has a gate.
     *
     * @return boolean representing is this phase has a gate
     */
    public boolean hasGate() {
        return this.hasGate;
    }

    public int getActiveEnvelopeId() {
		return activeEnvelopeId;
	}

	public void setActiveEnvelopeId(int activeEnvelopeId) {
		this.activeEnvelopeId = activeEnvelopeId;
	}

	/**
     * add a deliverable to this phase
     *
     * @param deliverable the deliverable to add
     */
    public void addDeliverable(Deliverable deliverable) {
        m_deliverables.add(deliverable);
    }

    /**
     * Get the deliverables for this phase
     *
     * @return a DeliverableList of the deliverables for this phase
     */
    public DeliverableList getDeliverableList() {
        return m_deliverables;
    }

    /**
     * Set the deliverables for this phase.
     *
     * @param deliverables a DeliverableList of the deliverables for this phase
     */
    public void setDeliverableList(DeliverableList deliverables) {
        m_deliverables = deliverables;
    }

    /**
     * Get the string representation of this phase.
     *
     * @return the string representation of this phase
     */
    public String toString() {
        return null;
    }

    /**
     * Clear out any information stored inside of this Phase.
     */
    public void clear() {
        objectID = null;
        m_phase_name = null;
        m_phase_desc = null;
        m_sequence = null;
        m_status = null;
        m_status_id = null;
        m_process_id = null;
        isLoaded = false;
        hasGate = false;
        m_deliverables = new DeliverableList();
        m_gate = null;
        progress.clear();
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
