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
|   $Revision: 19838 $
|       $Date: 2009-08-24 10:14:31 -0300 (lun, 24 ago 2009) $
|     $Author: dpatil $
|                                                                       
+----------------------------------------------------------------------*/
package net.project.workflow;

import java.io.Serializable;
import java.sql.SQLException;
import java.sql.Timestamp;

import net.project.base.DBErrorCodes;
import net.project.base.PnetException;
import net.project.base.property.PropertyProvider;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.util.Conversion;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
  * A Rule.  This class provides the properties that pertain to all
  * rules.  
  */
public abstract class Rule implements IJDBCPersistence, IXMLPersistence, Serializable, ErrorCodes {

    /**
      * RuleContext encapsulates the information required to check a rule
      */
    static class RuleContext implements java.io.Serializable {
        private User user;
        private Envelope envelope;
        private Transition transition;
    
        void setUser(User user) {
            this.user = user;
        }
        void setEnvelope(Envelope envelope) {
            this.envelope = envelope;
        }
        void setTransition(Transition transition) {
            this.transition = transition;
        }
        User getUser() {
            return this.user;
        }
        Envelope getEnvelope() {
            return this.envelope;
        }
        Transition getTransition() {
            return this.transition;
        }
    }

    /**
      * RuleProblem encapsulates the problem information that occurred when
      * a rule is checked
      */
    static class RuleProblem implements java.io.Serializable, net.project.persistence.IXMLPersistence {
        private Rule rule;
        private String reason;
        private boolean isError = false;

        void setRule(Rule rule) {
            this.rule = rule;
        }
        void setReason(String reason) {
            this.reason = reason;
        }
        void setError(boolean isError) {
            this.isError = isError;
        }
        public Rule getRule() {
            return this.rule;
        }
        public String getReason() {
            return this.reason;
        }
        public boolean isError() {
            return this.isError;
        }

        /*
            Implement IXMLPersistence
         */
        public java.lang.String getXML() {
            return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
        }

        public java.lang.String getXMLBody() {
            StringBuffer xml = new StringBuffer();
            xml.append("<rule_problem>");
            xml.append("<jsp_root_url>" + net.project.security.SessionManager.getJSPRootURL() + "</jsp_root_url>");
            xml.append("<is_error>" + XMLUtils.escape((isError() ? "1" : "0")) + "</is_error>");
            xml.append("<reason>" + XMLUtils.escape(getReason()) + "</reason>");
            xml.append(rule.getXMLBody());
            xml.append("</rule_problem>");
            return xml.toString();
        }
    }

    /* Persistent properties */
    private String ruleID = null;
    private String transitionID = null;
    private String workflowID = null;
    private String ruleTypeID = null;
    private String ruleStatusID = null;
    private String name = null;
    private String description = null;
    private String notes = null;
    // Database modifiable only
    private String createdBy = null;
    private java.util.Date createdDatetime = null;
    private String modifiedBy = null;
    private java.util.Date modifiedDatetime = null;
    private java.util.Date crc = null;
    private String recordStatus = null;
    /* End of persistent properties */
    
    /* De-normalized properties */
    private String ruleTypeName = null;
    private String ruleStatusName = null;
    private String createdByFullName = null;
    private String modifiedByFullName = null;

    private boolean isLoaded = false;
    /** user currently manipulating workflow */
    private User user = null;
    private DBBean db = null;

    /** Workflow to which rule belongs (implied by transition) */
    private Workflow workflow = null;

    /** Indicates whether the workflow may be removed.  This is set to true only
      * when the appropriate checks have been performed */
    private boolean isRemovePermitted = false;
    /** Indicates whether the workflow removal was successful or not. */
    private boolean isRemoveSuccessful = false;

    /*
        Rule checking attributes
     */
    /** indicates there is a problem with this rule */
    private boolean isProblem = false;
    /** problem information */
    private Rule.RuleProblem problemInfo = null;

    /**
      * Create new Rule
      */
    public Rule() {
         db = new DBBean();
         // Set up default values
         clear();
    }

    /**
      * Return a RuleType object based on the supplied rule ID
      * or null if there is a problem locating the rule ID
      * @param ruleID the rule ID
      * @return the RuleType object
      * @throws RuleException if there is a problem getting the rule type
      */
     public static RuleType getRuleType(String ruleID) throws RuleException {
         String ruleTypeID = null;
         RuleType ruleType = null;
         DBBean db = new DBBean();
    
         // Select ruleTypeID from rule table
         StringBuffer queryBuff = new StringBuffer();
         queryBuff.append("select rule_type_id ");
         queryBuff.append("from pn_workflow_rule_view ");
         queryBuff.append("WHERE rule_id = " + ruleID + " ");
    
         try {
             // Execute select statement
             db.executeQuery(queryBuff.toString());
    
             if (db.result.next()) {
                 ruleTypeID = db.result.getString("rule_type_id");
                 ruleType = RuleType.forID(ruleTypeID);
             } else {
                 throw new RuleException("Unable to get rule type for rule with id: " + ruleID);
             }
    
         } catch (SQLException sqle)  {
        	 Logger.getLogger(Rule.class).error("Rule.getRuleTypeForID threw an SQL exception: " + sqle);
             throw new RuleException("Rule get rule type operation failed");

         } finally {
            db.release();
        }
    
        return ruleType;
     }

     /**
       * Check the rule.  This template method calls an abstract method which
       * must be defined by the subclass.
       * @param context the rule context
       * @throws RuleException if unable to check the rule.  Note: If the rule
       * can be checked (regardless of whether the check fails or not) this method
       * will NOT throw a RuleException
       * @see #customCheck
       */
     public final void check(Rule.RuleContext context) throws RuleException {
         RuleStatus thisStatus = RuleStatus.forID(getRuleStatusID());
         if (thisStatus == null) {
             throw new RuleException("Rule check operation failed, unable to determine rule status");
         }
         setProblem(false);
         setProblemInfo(null);

        if (RuleStatus.DISABLED.equals(thisStatus)) {
            /* Nothing to do because this rule is disabled */
        } else {
            try {
                customCheck(context);
            } catch (PersistenceException pe) {
                throw new RuleException("Rule check operation failed: " + pe);
            }
        }
     }

     /**
       * Custom rule check to be implemented by all sub-classes.  Called from
       * check().  This method will typically will call setProblem() and setProblemInfo()
       * if there is a problem during the check.
       * @param context the rule context
       * @throws PersistenceException if unable to check the rule
       * @see #check
       * @see #setProblem
       * @see #setProblemInfo
       */
     abstract void customCheck(Rule.RuleContext context) throws PersistenceException;

     /**
       * Return whether there is a problem with the rule.<br />
       * This is usually called after check()
       * @return true if there is a problem with the rule
       * @see #check
       */
     public boolean isProblem() {
         return this.isProblem;
     }

     /**
       * Set whether this rule has a problem
       * @param isProblem the value to set
       */
     void setProblem(boolean isProblem) {
         this.isProblem = isProblem;
     }

     /**
       * Return the RuleProblem information or null if there is none.<br />
       * This is usually called after check().  Use isProblem() to determine
       * if there is any problem info to get.
       * @return the rule problem object or null
       * @see #check
       * @see #isProblem
       */
     public Rule.RuleProblem getProblemInfo() {
         return this.problemInfo;
     }

     /**
       * Set the problem info
       * @param problemInfo the rule problem object to set
       */
     void setProblemInfo(Rule.RuleProblem problemInfo) {
         this.problemInfo = problemInfo;
     }

    /**
      * Getter for property workflowID.
      * @return Value of property workflowID.
      */
    public String getWorkflowID() {
        return this.workflowID;
    }
    
    /**
      * Setter for property workflowID.
      * @param workflowID New value of property workflowID.
      */
    public void setWorkflowID(String workflowID) {
        this.workflowID = workflowID;
    }
    
    /**
      * Getter for property transitionID.
      * @return Value of property transitionID.
      */
    public String getTransitionID() {
        return this.transitionID;
    }
    
    /**
      * Setter for property transitionID.
      * @param transitionID New value of property transitionID.
      */
    public void setTransitionID(String transitionID) {
        this.transitionID = transitionID;
    }
    
    /**
      * Getter for property ruleTypeID.
      * @return Value of property ruleTypeID.
      */
    public String getRuleTypeID() {
        return this.ruleTypeID;
    }
    
    /**
      * Setter for property ruleTypeID.
      * @param ruleTypeID New value of property ruleTypeID.
      */
    public void setRuleTypeID(String ruleTypeID) {
        this.ruleTypeID = ruleTypeID;
    }

    /**
      * Getter for property ruleStatusID.
      * @return Value of property ruleStatusID.
      */
    public String getRuleStatusID() {
        return this.ruleStatusID;
    }
    
    /**
      * Setter for property ruleStatusID.
      * @param ruleStatusID New value of property ruleStatusID.
      */
    public void setRuleStatusID(String ruleStatusID) {
        this.ruleStatusID = ruleStatusID;
    }
    
    /**
      * Getter for property name.
      * @return Value of property name.
      */
    public String getName() {
        return this.name;
    }
    
    /**
      * Setter for property name.
      * @param name New value of property name.
      */
    public  void setName(String name) {
        this.name = name;
    }
    
    /**
      * Getter for property description.
      * @return Value of property description.
      */
    public  String getDescription() {
        return this.description;
    }
    
    /**
      * Setter for property description.
      * @param description New value of property description.
      */
    public  void setDescription(String description) {
        this.description = description;
    }
    
    /**
      * Getter for property notes.
      * @return Value of property notes.
      */
    public  String getNotes() {
        return notes;
    }
    
    /**
      * Setter for property notes.
      * @param notes New value of property notes.
      */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    String getCreatedBy() {
        return this.createdBy;
    }
    void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    java.util.Date getCreatedDatetime() {
        return this.createdDatetime;
    }
    void setCreatedDatetime(java.util.Date createdDatetime) {
        this.createdDatetime = createdDatetime;
    }
    String getModifiedBy() {
        return this.modifiedBy;
    }
    void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
    java.util.Date getModifiedDatetime() {
        return this.modifiedDatetime;
    }
    void setModifiedDatetime(java.util.Date modifiedDatetime) {
        this.modifiedDatetime = modifiedDatetime;
    }
    java.util.Date getCrc() {
        return this.crc;
    }
    void setCrc(java.util.Date crc) {
        this.crc = crc;
    }
    String getRecordStatus() {
        return this.recordStatus;
    }
    void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
    String getRuleTypeName() {
        return this.ruleTypeName;
    }
    void setRuleTypeName(String ruleTypeName) {
        this.ruleTypeName = ruleTypeName;
    }
    String getRuleStatusName() {
        return this.ruleStatusName;
    }
    void setRuleStatusName(String ruleStatusName) {
        this.ruleStatusName = ruleStatusName;
    }
    String getCreatedByFullName() {
        return this.createdByFullName;
    }
    void setCreatedByFullName(String createdByFullName) {
        this.createdByFullName = createdByFullName;
    }
    String getModifiedByFullName() {
        return this.modifiedByFullName;
    }
    void setModifiedByFullName(String modifiedByFullName) {
        this.modifiedByFullName = modifiedByFullName;
    }
    boolean isLoaded() {
        return this.isLoaded;
    }
    boolean setLoaded(boolean isLoaded) {
        return this.isLoaded = isLoaded;
    }

    /**
      * Return the workflow to which this rule belongs
      * @return the workflow
      */
    public Workflow getWorkflow() throws PersistenceException {
        if (this.workflow == null) {
            loadWorkflow();
        }
        return this.workflow;
    }

    void setWorkflow(Workflow workflow) {
        this.workflow = workflow;
    }

    /**
      * Clear all properties
      */
    public void clear() {
        ruleID = null;
        workflowID = null;
        transitionID = null;
        ruleTypeID = null;
        // Default is Enforced rule
        setRuleStatusID(RuleStatus.ENFORCED.getID());
        name = null;
        description = null;
        notes = null;
        
        createdBy = null;
        createdDatetime = null;
        modifiedBy = null;
        modifiedDatetime = null;
        crc = null;
        recordStatus = null;

        ruleTypeName = null;
        ruleStatusName = null;
        createdByFullName = null;
        modifiedByFullName = null;

        isLoaded = false;
    }

    public void setUser(User user) {
        this.user = user;
    }

    User getUser() {
        return this.user;
    }

    /**
      * Return Rule's id
      * @return id of rule
      */
    public  String getID() {
        return this.ruleID;
    }
    
    /*
        Implementing IJDBCPersistence
     */
    public  void setID(java.lang.String id) {
        this.ruleID = id;
    }
    

    /**
      * Load the generic rule
      * This should be called by the sub-class
      * @throws PersistenceException if there is a problem
      */
    public void load() throws net.project.persistence.PersistenceException {
        StringBuffer queryBuff = new StringBuffer();

        queryBuff.append("select rule_id, transition_id, workflow_id, ");
        queryBuff.append("rule_type_id, rule_type_name, rule_status_id, ");
        queryBuff.append("rule_status_name, rule_name, rule_description, ");
        queryBuff.append("notes, created_by_id, created_by_full_name, created_datetime, ");
        queryBuff.append("modified_by_id, modified_by_full_name, modified_datetime, ");
        queryBuff.append("crc, record_status ");
        queryBuff.append("from pn_workflow_rule_view rule ");
        queryBuff.append("where rule_id = " + getID() + " ");

        try {
            // Execute select statement
            db.executeQuery(queryBuff.toString());

            if (db.result.next()) {
                setID(db.result.getString("rule_id"));
                setTransitionID(db.result.getString("transition_id"));
                setWorkflowID(db.result.getString("workflow_id"));
                setName(db.result.getString("rule_name"));
                setDescription(db.result.getString("rule_description"));
                setNotes(db.result.getString("notes"));
                setRuleTypeID(db.result.getString("rule_type_id"));
                setRuleTypeName(db.result.getString("rule_type_name"));
                setRuleStatusID(db.result.getString("rule_status_id"));
                setRuleStatusName(db.result.getString("rule_status_name"));

                setCreatedBy(db.result.getString("created_by_id"));
                setCreatedDatetime((java.util.Date)db.result.getTimestamp("created_datetime"));
                setCreatedByFullName(db.result.getString("created_by_full_name"));
                setModifiedBy(db.result.getString("modified_by_id"));
                setModifiedDatetime((java.util.Date)db.result.getTimestamp("modified_datetime"));
                setModifiedByFullName(db.result.getString("modified_by_full_name"));
                setCrc((java.util.Date)db.result.getTimestamp("crc"));
                setRecordStatus(db.result.getString("record_status"));
                
                setLoaded(true);
            }

        } catch (SQLException sqle)  {
            setLoaded(false);
            Logger.getLogger(Rule.class).error("Rule.load() threw an SQL exception: " + sqle);
            throw new PersistenceException ("Rule load operation failed.", sqle);

        } finally {
           db.release();
       }
    }
   
    /**
      * Load the workflow to which this rule belongs
      * Sets attribute workflow
      * @throws PersistenceException if there is a problem loading the workflow
      * @see Workflow#load
      */
    private void loadWorkflow() throws PersistenceException {
        Workflow workflow = new Workflow();
        workflow.setID(getWorkflowID());
        workflow.load();
        setWorkflow(workflow);
    }

    /**
      * Store rule
      * Provides default implementation of store
      * @throws PersistenceException if there is a problem
      */
    public void store() throws net.project.persistence.PersistenceException {
        int errorCode = -1;
        String storedRuleID = null;

        if (isLoaded()) {
            // Modify existing rule
            /*
            procedure modify_rule (
                i_rule_id        in varchar2,
                i_rule_name      in varchar2,
                i_rule_description in varchar2,
                i_notes          in varchar2,
                i_rule_status_id in varchar2,
                i_modified_by_id in varchar2,
                i_crc            in date,
                o_return_value   out number);
            */
            try {
                db.prepareCall("BEGIN workflow_rule.modify_rule(?, ?, ?, ?, ?, ?, ?, ?);  END;");
                db.cstmt.setString(1, getID());
                db.cstmt.setString(2, getName());
                db.cstmt.setString(3, getDescription());
                db.cstmt.setString(4, getNotes());
                db.cstmt.setString(5, getRuleStatusID());
                db.cstmt.setString(6, this.user.getID());
                db.cstmt.setTimestamp(7, new Timestamp(getCrc().getTime()));
                db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);
        
                // Execute that sucker
                db.executeCallable();
        
                // Get error code for later handling
                errorCode = db.cstmt.getInt(8);
                storedRuleID = getID();
        
            } catch (SQLException sqle) {
            	Logger.getLogger(Rule.class).error(
                    "Rule.store(): User: " + this.user.getID() + ", unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("Rule store operation failed.", sqle);
        
            } finally {
                db.release();
            }
        
            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("Rule.store()", errorCode);
            } catch (net.project.base.UpdateConflictException e) {
                // CRC was different than that in database
                RecordModifiedException rme = 
                        new RecordModifiedException("The rule has been modified by another user.  Please try again.");
                rme.setErrorCode(RULE_RECORD_MODIFIED);
                throw rme;
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }
        
        } else {
            // Create new step
            /*
            procedure create_rule
              ( i_workflow_id    in varchar2,
                i_transition_id  in varchar2,
                i_rule_name      in varchar2,
                i_rule_description in varchar2,
                i_notes          in varchar2,
                i_rule_status_id in varchar2,
                i_rule_type_id   in varchar2,
                i_created_by_id  in varchar2,
                o_rule_id        out varchar2,
                o_return_value   out number);
            */
            try {
                db.prepareCall("BEGIN workflow_rule.create_rule(?, ?, ?, ?, ?, ?, ?, ?, ?, ?);  END;");
                db.cstmt.setString(1, getWorkflowID());
                db.cstmt.setString(2, getTransitionID());   
                db.cstmt.setString(3, getName());
                db.cstmt.setString(4, getDescription());
                db.cstmt.setString(5, getNotes());
                db.cstmt.setString(6, getRuleStatusID());
                db.cstmt.setString(7, getRuleTypeID());
                db.cstmt.setString(8, this.user.getID());
                db.cstmt.registerOutParameter(9, java.sql.Types.VARCHAR);
                db.cstmt.registerOutParameter(10, java.sql.Types.INTEGER);
        
                // Execute that sucker
                db.executeCallable();
        
                // Get newly created stepID
                storedRuleID = db.cstmt.getString(9);
                // Get error code for later handling
                errorCode = db.cstmt.getInt(10);
        
            } catch (SQLException sqle) {
            	Logger.getLogger(Rule.class).error(
                    "Rule.store(): User: " + this.user.getID() + ", unable to execute stored procedure: " +
                    sqle);
                throw new PersistenceException("Rule store operation failed.", sqle);
        
            } finally {
                db.release();
            }
        
            // Handle (throw) any exceptions that were sucked up in PL/SQL
            try {
                DBExceptionFactory.getException("Rule.store()", errorCode);
            } catch (PnetException e) {
                throw new PersistenceException(e.getMessage(), e);
            }

            /* Now set my ID so that it is available to sub-class */
            setID(storedRuleID);

        } // if (isLoaded)
    }

    /**
      * Prepare to remove rule.  Checks following:<br>
      * Workflow is not published
      * This routine populates the errors table
      */
    void prepareRemove() {
        Workflow workflow = null;
        boolean isMetCriteria = true;
        
        /* Default to not permitted */
        setRemovePermitted(false);
        
        /* Check to see if Workflow is published */
        workflow = new Workflow();
        workflow.setID(getWorkflowID());
        workflow.setUser(getUser());
        try {
            workflow.load();
            if (workflow.isPublished()) {
                errors.put("remove_is_published", null, "A rule may not be removed while the workflow to which is belongs is Published.");
                isMetCriteria &= false;
            }
        
        } catch (PersistenceException pe) {
            errors.put("remove_is_published", null, "There was a problem locating the workflow to which this rule belongs.");
            isMetCriteria &= false;
        }
            
        setRemovePermitted(isMetCriteria);
    }

    String getPrepareRemoveErrorsPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    String getRemoveResultPresentation() {
        return errors.getAllErrorsRemovePresentation();
    }

    public boolean isRemovePermitted() {
        return this.isRemovePermitted;
    }

    void setRemovePermitted(boolean isRemovePermitted) {
        this.isRemovePermitted = isRemovePermitted;
    }

    public boolean isRemoveSuccessful() {
        return this.isRemoveSuccessful;
    }

    void setRemoveSuccessful(boolean isRemoveSuccessful) {
        this.isRemoveSuccessful = isRemoveSuccessful;
    }

    /**
      * Delete the Rule from the database.
      * @throws PersistenceException Thrown to indicate a failure storing to the database, a system-level error.
      */
    public void remove() throws net.project.persistence.PersistenceException {
        /* Default to a failed removal */
        setRemoveSuccessful(false);

        /* Repeat the checks that should have been done prior to this method call */
        prepareRemove();

        if (!isRemovePermitted()) {
            /*
                Remove is not permitted
                Message will have been created and will be displayed to user
             */

        } else {
            /*
                Remove is permitted.
                Do it.
             */
            removeRule();
            setRemoveSuccessful(true);
        }
    }
    
    /**
      * Performs the actual remove
      * @throws PersistenceException if there is a problem performing the remove
      */
    private void removeRule() throws PersistenceException {

        if (getID() == null) {
            // This is not a loaded rule
            return;
        }

        /* errorCode will be returned from stored procedures */       
        int errorCode = -1;
        
        /*
            procedure remove_rule (
                i_rule_id        in varchar2,
                i_modified_by_id in varchar2,
                i_crc            in date,
                o_return_value   out number);
        */
        try {
            db.prepareCall("{call workflow_rule.remove_rule(?, ?, ?, ?)}");
            db.connection.setAutoCommit(false);
            db.cstmt.setString(1, getID());
            db.cstmt.setString(2, getUser().getID());
            db.cstmt.setTimestamp(3, new Timestamp(getCrc().getTime()));
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
            db.executeCallable();
            errorCode = db.cstmt.getInt(4);
            db.closeCStatement();
            if (errorCode != DBErrorCodes.OPERATION_SUCCESSFUL) {
                /* Rollback if anything went wrong
                   An exception is thrown later based on this errorCode */
                db.connection.rollback();

            } else {
                /*
                   Successfully removed the rule
                 */
                db.connection.commit();
                errorCode = DBErrorCodes.OPERATION_SUCCESSFUL;
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Rule.class).error(
                "Rule.remove(): User: " + getUser().getID() + ", unable to execute stored procedure: " +
                sqle);
            throw new PersistenceException("Rule remove operation failed.", sqle);

        } finally {
            if (db.connection != null) {
                try {
                    db.connection.rollback();
                } catch (SQLException sqle) {
                    /* Nothing we can do here except release everything */
                }
            }
            db.release();
        }

        // Handle (throw) any exceptions that were sucked up in PL/SQL
        try {
            DBExceptionFactory.getException("Rule.remove()", errorCode);

        } catch (net.project.base.UpdateConflictException e) {
            RecordModifiedException rme = 
                    new RecordModifiedException("The rule has been modified by another user.  Please try again.");
            rme.setErrorCode(RULE_RECORD_MODIFIED);
            throw rme;
        } catch (net.project.persistence.RecordLockedException e) {
            WorkflowPersistenceException wpe = 
                new WorkflowPersistenceException("The rule is currently locked by another user.  Please try again.");
                wpe.setErrorCode(RULE_RECORD_LOCKED);
                throw wpe;

        } catch (PnetException e) {
            throw new PersistenceException(e.getMessage(), e);
        }

    }

    /*
        Implementing IXMLPersistence
     */

    /**
      * Return XML including version tag
      * @return XML string
      */
    public  java.lang.String getXML() {
        return IXMLPersistence.XML_VERSION + getXMLBody();
    }

    /**
      * Return XML Body elements
      * @return xml string
      * @see #getCustomPropertiesXML
      */
    public  java.lang.String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<rule>\n");
        xml.append(getXMLElements());
        xml.append(getCustomPropertiesXML());
        xml.append("</rule>\n");
        return xml.toString();
    }

    /**
      * This must return a <custom_property_list /> xml structure
      * It should be overridden by sub-classes
      */
    String getCustomPropertiesXML() {
        return "";
    }

    /**
      * Return XML Elements
      * @return XML string elements (excludes <rule> tag)
      */
    private String getXMLElements() {
        StringBuffer xml = new StringBuffer();
        xml.append("<rule_id>" + XMLUtils.escape(getID()) + "</rule_id>");
        xml.append("<workflow_id>" + XMLUtils.escape(getWorkflowID()) + "</workflow_id>");
        xml.append("<transition_id>" + XMLUtils.escape(getTransitionID()) + "</transition_id>");
        xml.append("<rule_type_id>" + XMLUtils.escape(getRuleTypeID()) + "</rule_type_id>");
        xml.append("<rule_type_name>" + XMLUtils.escape(getRuleTypeName()) + "</rule_type_name>");
        xml.append("<rule_status_id>" + XMLUtils.escape(getRuleStatusID()) + "</rule_status_id>");
        xml.append("<rule_status_name>" + XMLUtils.escape(getRuleStatusName()) + "</rule_status_name>");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>");
        xml.append("<notes>" + XMLUtils.escape(getNotes()) + "</notes>");
        xml.append("<created_by_id>" + XMLUtils.escape(getCreatedBy()) + "</created_by_id>\n");
        xml.append("<created_datetime>" + XMLUtils.escape(Conversion.dateToString(getCreatedDatetime())) + "</created_datetime>\n");
        xml.append("<created_by_full_name>" + XMLUtils.escape(getCreatedByFullName()) + "</created_by_full_name>\n");
        xml.append("<modified_by_id>" + XMLUtils.escape(getModifiedBy()) + "</modified_by_id>\n");
        xml.append("<modified_datetime>" + XMLUtils.escape(Conversion.dateToString(getModifiedDatetime())) + "</modified_datetime>\n");
        xml.append("<modified_by_full_name>" + XMLUtils.escape(getModifiedByFullName()) + "</modified_by_full_name>\n");
        xml.append("<crc>" + XMLUtils.escape(Conversion.dateToString(getCrc())) + "</crc>");
        xml.append("<record_status>" + XMLUtils.escape(getRecordStatus()) + "</record_status>");
        return xml.toString();
    }

    /*=================================================================================================
      Error stuff
      ================================================================================================*/
    
    private ValidationErrors errors = new ValidationErrors();
    
    public void clearErrors() {
        errors.clearErrors();
    }

    /**
      * Validate the contents of the Envelope
      */
    public void validateAll() {
    }

    /**
      * Indicate whether there are any errors
      * @return true if there are errors, false otherwise
      */
    public boolean hasErrors() {
        return errors.hasErrors();
    }

    public String getErrorsTable() {
        return errors.getAllErrorsDefaultPresentation();
    }

    public String getFlagError(String fieldID, String label) {
        return errors.flagError(fieldID, label);
    }

    /*
        Validation Routines
     */

    public void validateRuleTypeID() {
        if (getRuleTypeID() == null || getRuleTypeID().equals("")) {
            errors.put("rule_type_id", "Rule Type", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    /**
      * Name must be non-null
      */
    public void validateName() {
        if (getName() == null || getName().equals("")) {
            errors.put("name", "Rule Name", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validateDescription() {
        if (getDescription() != null && getDescription().length() > 500) {
            errors.put("description", "Rule Description", PropertyProvider.get("prm.workflow.description.toomanychars.validation.message"));
        }
    }

    public void validateRuleStatusID() {
        if (getRuleStatusID() == null || getRuleStatusID().equals("")) {
            errors.put("rule_status_id", "Status", PropertyProvider.get("prm.workflow.requiredfield.validation.message"));
        }
    }

    public void validateNotes() {
        if (getNotes() != null && getNotes().length() > 4000) {
            errors.put("notes", "Rule Notes", PropertyProvider.get("prm.workflow.notes.toomanychars.validation.message"));
        }
    }

    /**
      * Generates an error if the workflow to which the transition to which this rule belongs is published
      * @throws PersistenceException if there was a problem determining the workflow
      */
    public void validateUnpublished() throws PersistenceException {
        Workflow workflow = null;
        workflow = getWorkflow();
        if (workflow.isPublished()) {
            errors.put("is_published", null, PropertyProvider.get("prm.workflow.ispublished.change.validation.message"));
        }
    }

    /*
        End of validation routines
     */
    
    /*=================================================================================================
      End of Error stuff
      ================================================================================================*/


}
