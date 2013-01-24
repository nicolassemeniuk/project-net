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

import net.project.base.ObjectType;
import net.project.base.RecordStatus;
import net.project.base.URLFactory;
import net.project.base.property.PropertyProvider;
import net.project.database.ClobHelper;
import net.project.database.DBBean;
import net.project.link.ILinkableObject;
import net.project.persistence.PersistenceException;
import net.project.security.SessionManager;
import net.project.workflow.IWorkflowable;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * Deliverables are part of a phase.
 *
 * @author BrianConneen
 * @since 03/00
 */
public class Deliverable implements ILinkableObject,IWorkflowable  {
    protected String objectID = null;
    protected String m_deliverable_name = null;
    protected String m_deliverable_desc = null;
    protected String m_deliverable_comments = null;
    protected String m_status = null;
    protected String m_status_id = null;
    protected String m_methodology_deliverable_id = null;
    protected String m_is_optional = null;
    protected String m_phase_id = null;
    protected boolean isLoaded = false;
    protected int activeEnvelopeId = 0;

    private RecordStatus recordStatus = null;

    /**
     * Construct an empty Deliverable.
     */
    public Deliverable() {
    }

    /**
     * Construct an empty Deliverable, set it's id, then load it from the DB.
     *
     * @param deliverable_id the id of the deliverable.
     */
    public Deliverable(String deliverable_id) throws PersistenceException {
        objectID = deliverable_id;
        load();
    }

    /**
     * Returns the record status of this linkable Deliverable.
     * @return the record status
     */
    public RecordStatus getRecordStatus() {
        return recordStatus;
    }

    /**
     * Load the deliverable from the DB.
     *
     * @throws PersistenceException Thrown to indicate a failure loading from the database,
     * a system-level error.
     */
    public void load() throws PersistenceException {
        String qstrLoadDeliverableProperties = "select pn_deliverable.deliverable_name, pn_deliverable.deliverable_desc, " +
            "pn_deliverable.deliverable_comments_clob, pn_deliverable.methodology_deliverable_id, " +
            "pn_deliverable.is_optional, pn_deliverable.status_id, pn_deliverable.record_status, " +
            "pn_global_domain.code_name, pn_phase_has_deliverable.phase_id " +
            "FROM pn_deliverable, pn_global_domain, pn_phase_has_deliverable " +
            "WHERE pn_deliverable.deliverable_id = " + this.objectID + " " +
            "AND pn_deliverable.status_id = pn_global_domain.code " +
            "AND pn_global_domain.table_name = 'pn_deliverable' " +
            "AND pn_global_domain.column_name = 'status_id' " +
            "AND pn_phase_has_deliverable.deliverable_id = pn_deliverable.deliverable_id ";

        DBBean db = new DBBean();
        try {
            db.executeQuery(qstrLoadDeliverableProperties);

            if (db.result.next()) {
                this.m_deliverable_name = db.result.getString("deliverable_name");
                this.m_deliverable_desc = db.result.getString("deliverable_desc");
                this.m_deliverable_comments = ClobHelper.read(db.result.getClob("deliverable_comments_clob"));
                this.m_status = PropertyProvider.get(db.result.getString("code_name"));
                this.m_status_id = db.result.getString("status_id");
                this.m_methodology_deliverable_id = db.result.getString("methodology_deliverable_id");
                this.m_is_optional = db.result.getString("is_optional");
                this.m_phase_id = db.result.getString("phase_id");
                this.recordStatus = RecordStatus.findByID(db.result.getString("record_status"));
            } else {
            	Logger.getLogger(Deliverable.class).debug("Deliverable.load() could not load deliverable properties");
            }
            this.isLoaded = true;
        } catch (SQLException sqle) {
        	Logger.getLogger(Deliverable.class).debug("Deliverable.load() threw a SQL exception: " + sqle);
            throw new PersistenceException("Deliverable.load() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Store the deliverable in the DB.
     *
     * @throws PersistenceException Thrown to indicate a failure storing in the database,
     * a system-level error.
     */
    public void store() throws PersistenceException {
        String qstrStoreDeliverable = "{call PROCESS.STORE_DELIVERABLE(?,?,?,?,?,?,?,?,?,?,?, ?,?)}";

        DBBean db = new DBBean();
        try {
            int index = 0;
            int deliverableIDIndex = 0;
            int commentsClobIndex = 0;

            db.setAutoCommit(false);

            db.prepareCall(qstrStoreDeliverable);

            db.cstmt.setInt(++index, Integer.parseInt(SessionManager.getUser().getID()));
            if ((m_phase_id == null) || (m_phase_id.length() == 0)) {
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                db.cstmt.setInt(++index, Integer.parseInt(m_phase_id));
            }
            if ((objectID == null) || (objectID.length() == 0)) {
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                db.cstmt.setInt(++index, Integer.parseInt(objectID));
            }
            db.cstmt.setInt(++index, Integer.parseInt(SessionManager.getUser().getCurrentSpace().getID()));
            db.cstmt.setString(++index, m_deliverable_name);
            db.cstmt.setString(++index, m_deliverable_desc);
            db.cstmt.setInt(++index, Integer.parseInt(m_status_id));
            if ((m_methodology_deliverable_id == null) || (m_methodology_deliverable_id.length() == 0)) {
                db.cstmt.setNull(++index, java.sql.Types.INTEGER);
            } else {
                db.cstmt.setInt(++index, Integer.parseInt(m_methodology_deliverable_id));
            }
            db.cstmt.setInt(++index, Integer.parseInt(m_is_optional));
            db.cstmt.setString(++index, "A");
            db.cstmt.setInt(++index, (m_deliverable_comments == null ? 1 : 0));
            db.cstmt.registerOutParameter((commentsClobIndex = ++index), java.sql.Types.CLOB);
            db.cstmt.registerOutParameter((deliverableIDIndex = ++index), java.sql.Types.INTEGER);

            db.executeCallable();

            // If comments are not null, stream to clob
            if (m_deliverable_comments != null) {
                ClobHelper.write(db.cstmt.getClob(commentsClobIndex), m_deliverable_comments);
            }

            // Grab newly created ID
            objectID = Integer.toString(db.cstmt.getInt(deliverableIDIndex));

            db.commit();

        } catch (SQLException sqle) {
            try {
                db.rollback();
            } catch (SQLException e) {
                // Throw original error
            }
            throw new PersistenceException("Deliverable.store() thew a SQL exception: " + sqle, sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Remove the deliverable from the DB.
     *
     * @throws PersistenceException Thrown to indicate a failure removing the process
     *         from the database, a system-level error.
     */
    public void remove() throws PersistenceException {
        String qstrRemoveDeliverable = "begin  PROCESS.REMOVE_DELIVERABLE(?,?); end;";

        DBBean db = new DBBean();
        try {
            db.prepareCall(qstrRemoveDeliverable);

            db.cstmt.setInt(1, Integer.parseInt(objectID));
            db.cstmt.registerOutParameter(2, java.sql.Types.INTEGER);
            db.executeCallable();
        } catch (SQLException sqle) {
        	Logger.getLogger(Deliverable.class).debug("Deliverable.remove() threw a SQL exception: " + sqle);
            throw new PersistenceException("Deliverable.remove() thew a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }

    }


    /**
     * Returns XML representation of the deliverable.
     *
     * @return string containing XML represntation
     */
    public String getXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<deliverable>\n");
        sb.append("<deliverable_id>");
        sb.append(XMLUtils.escape(this.objectID));
        sb.append("</deliverable_id>\n");
        sb.append("<deliverable_name>");
        sb.append(XMLUtils.escape(this.m_deliverable_name));
        sb.append("</deliverable_name>\n");
        sb.append("<deliverable_desc>");
        sb.append(XMLUtils.escape(this.m_deliverable_desc));
        sb.append("</deliverable_desc>\n");
        sb.append("<deliverable_comments>");
        sb.append(XMLUtils.escape(this.m_deliverable_comments));
        sb.append("</deliverable_comments>\n");
        sb.append("<status>");
        sb.append(XMLUtils.escape(this.m_status));
        sb.append("</status>\n");
        sb.append("<activeEnvelopeId>");
        sb.append(this.activeEnvelopeId);
        sb.append("</activeEnvelopeId>\n");
        sb.append("<methodology_deliverable_id>");
        sb.append(XMLUtils.escape(this.m_methodology_deliverable_id));
        sb.append("</methodology_deliverable_id>\n");
        sb.append("<is_optional>");
        sb.append(XMLUtils.escape(this.m_is_optional));
        sb.append("</is_optional>\n");
        sb.append("<jsp_root_url>" + XMLUtils.escape(net.project.security.SessionManager.getJSPRootURL()) + "</jsp_root_url>");
        sb.append("</deliverable>\n");
        return sb.toString();
    }

    /**
     * loads the deliverable from XML.
     *
     * @param xmlData the XML string from which to recreate this
     */
    public void fromXML(java.lang.String xmlData) {
    }

    /**
     * Set the Id of this deliverable.
     *
     * @param m_objectID the ID of the deliverable
     */
    public void setID(String m_objectID) {
        this.objectID = m_objectID;
    }

    /**
     * get the Id of this deliverable
     *
     * @return the ID of this deliverable
     */
    public String getID() {
        return this.objectID;
    }

    /**
     * Return the name of this deliverable.
     *
     * @return the name of this deliverable
     */
    public String getName() {
        return this.m_deliverable_name;
    }

    /**
     * Set the name of this deliverable.
     *
     * @param name the name for this deliverable
     */
    public void setName(String name) {
        this.m_deliverable_name = name;
    }

    /**
     * Return the type of this object.
     *
     * @return the string representation of this object's type
     */
    public String getType() {
        return "deliverable";
    }

    /**
     * Get the string representation of this deliverable.
     *
     * @return the string representation of this deliverable
     */
    public String toString() {
        return null;
    }

    /**
     * Get the URL for this object.
     *
     * @return the URL for recreating this deliverable
     */
    public String getURL() {
        return URLFactory.makeURL(objectID, ObjectType.DELIVERABLE);
    }

    /**
     * Return the description of this deliverable.
     *
     * @return the description for this deliverable
     */
    public String getDesc() {
        return this.m_deliverable_desc;
    }

    /**
     * Set the description of this deliverable.
     *
     * @param desc the decription for this deliverable
     */
    public void setDesc(String desc) {
        this.m_deliverable_desc = desc;
    }

    /**
     * Return the comments of this deliverable.
     *
     * @return the comments for this deliverable
     */
    public String getComments() {
        return this.m_deliverable_comments;
    }

    /**
     * Set the comments of this deliverable.
     *
     * @param comments the comments for this deliverable
     */
    public void setComments(String comments) {
        this.m_deliverable_comments = comments;
    }

    /**
     * Return the phase_id of this deliverable.
     *
     * @return the phase_id
     */
    public String getPhaseID() {
        return this.m_phase_id;
    }

    /**
     * Set the phase_id of this deliverable.
     *
     * @param id the phase this deliverable belongs to
     */
    public void setPhaseID(String id) {
        this.m_phase_id = id;
    }
    
    public int getActiveEnvelopeId() {
		return activeEnvelopeId;
	}

	public void setActiveEnvelopeId(int activeEnvelopeId) {
		this.activeEnvelopeId = activeEnvelopeId;
	}

	/**
     * Get whether this deliverable is optional.
     *
     * @return either 1 or 0 for yes or no to optional
     */
    public String getOptional() {
        return this.m_is_optional;
    }


    /**
     * Set whether this deliverable is optional.
     *
     * @param optional 1 or 0 for yes or no
     */
    public void setOptional(String optional) {
        this.m_is_optional = optional;
    }

    /**
     * Set the status of this deliverable.
     *
     * @param status the status code
     */
    public void setStatus(String status) {
        this.m_status_id = status;
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
