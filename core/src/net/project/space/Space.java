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

 package net.project.space;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.database.DBFormat;
import net.project.gui.html.IHTMLOption;
import net.project.persistence.PersistenceException;
import net.project.resource.Roster;
import net.project.security.SecurityProvider;
import net.project.security.User;
import net.project.security.group.GroupCollection;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.security.group.GroupTypeID;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;

/**
 * An abstract base class for Spaces.
 */
public abstract class Space implements ISpaceTypes, IHTMLOption, Serializable {

    /**
     * the business_space_id, project_space_id, etc.or this space
     */
    protected String spaceID = null;

    /**
     * the parent space id for this space
     */
    protected String parentSpaceID = null;

    /**
     * the id of the space that owns this space
     */
    protected String ownerSpaceID = null;
    
    /**
     * the id of the space that is related to this space
     */
    protected String relatedSpaceID = null;

    /**
     * the name of the space.  Project_name, business_name, etc.
     */
    protected String name = null;

    /**
     * the long name or description of the space
     */
    protected String description = null;

    private String recordStatus = null;

    /**
     * The user-defined subtype of space.
     */
    protected String userDefinedSubtype = null;

    /**
     * Get the user-defined flavor of space. A Space may have several
     * user-defined flavors of each subclass. For instance, BusinessSpace may
     * have flavors that include: operating company, division, department,
     * group.
     */
    protected String flavor = null;

    private SpaceRelationship relationshipToParent = null;
    private SpaceRelationship relationshipToChild = null;

    /**
     * the Roster for this space
     */
    protected Roster roster = null;

    // db access bean
    protected boolean isLoaded = false;


    protected SpaceType spaceType = null;

    /* --------------------------------------- Constructors ---------------------------------------*/

    /**
     * Construct an empty Space
     */
    public Space() {
    }


    /**
     * Construct a Space to be restored from persistence.
     */
    public Space(String spaceID) {
        this.spaceID = spaceID;
    }

    /**
     * Creates a space from the specified copy. This is a shallow clone.
     *
     * @param source the source Space from which to set this Space's properties
     */
    protected Space(Space source) {
        this.spaceID = source.spaceID;
        this.parentSpaceID = source.parentSpaceID;
        this.ownerSpaceID = source.ownerSpaceID;
        this.relatedSpaceID = source.relatedSpaceID;
        this.name = source.name;
        this.description = source.description;
        this.recordStatus = source.recordStatus;
        this.userDefinedSubtype = source.userDefinedSubtype;
        this.flavor = source.flavor;
        this.relationshipToParent = source.relationshipToParent;
        this.relationshipToChild = source.relationshipToParent;
        this.roster = source.roster;
        this.isLoaded = source.isLoaded;
        this.spaceType = source.spaceType;
    }

    /* --------------------------------------- Getters & Setters ---------------------------------------*/


    /**
     * Set the Space's database ID
     */
    public void setID(String id) {
        this.spaceID = id;
    }


    /**
     * Get the Space's database ID
     */
    public String getID() {
        return this.spaceID;
    }


    /**
     * Set the Spaces name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Get the Space's name
     */
    public String getName() {
        return this.name;
    }


    /**
     * Set the Spaces description
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Get the Space's description
     */
    public String getDescription() {
        return this.description;
    }


    /**
     * Sets this space's record status.
     *
     * @param recordStatus the record status
     */
    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }


    /**
     * Returns this space's record status.
     *
     * @return the record status
     */
    public String getRecordStatus() {
        return this.recordStatus;
    }


    /**
     * Set the Space's relationship to it's parent Space, if any
     */
    public void setRelationshipToParent(SpaceRelationship relationship) {
        this.relationshipToParent = relationship;
    }


    /**
     * Get the Space's relationship to it's parent Space, if any
     */
    public SpaceRelationship getRelationshipToParent() {
        return this.relationshipToParent;
    }


    /**
     * Set the Space's relationship to it's child Space, if any
     */
    public void setRelationshipToChild(SpaceRelationship relationship) {
        this.relationshipToChild = relationship;
    }


    /**
     * Get the Space's relationship to it's child Space, if any
     */
    public SpaceRelationship getRelationshipToChild() {
        return this.relationshipToChild;
    }


    /**
     * Set the Roster for this Space.  Not all Space subclasses must support
     * Roster.
     */
    public void setRoster(Roster roster) {
        this.roster = roster;
    }


    /**
     * Get the Space's Roster.  A roster will be created and loaded if needed.
     * Not all Space subclasses must support Roster.
     */
    public Roster getRoster() {

        // If this space's roster is null or it is not loaded for this space
        // Then we load a roster for this space and cache it
        // The roster may no longer be loaded for this space if this space's
        // id changed
        
        if (this.roster == null || !this.roster.isLoaded(this)) {
            // Create and load a new roster
            this.roster = new Roster();
            this.roster.setSpace(this);
            this.roster.load();

        }

        return this.roster;
    }




    /* ---------------------------------------- Space Typing Methods ------------------------------------------*/

    /**
     * Set the type of space as defined in ISpaceTypes
     *
     * @see net.project.space.ISpaceTypes
     */
    protected void setType(String spaceTypeString) {

        if (spaceTypeString == null) {
            this.spaceType = null;

        } else {
            Iterator itr = SpaceTypes.iterator();
            while (itr.hasNext()) {
                SpaceType type = (SpaceType) itr.next();
                if (spaceTypeString.equals(type.getID())) {
                    this.spaceType = type;
                    break;
                }
            }
        }

    }

    /**
     * Get the type of space as defined in ISpaceTypes
     *
     * @return the type string for the object as defined in ISpaceTypes.
     * @see net.project.space.ISpaceTypes
     */
    public String getType() {
        if (this.spaceType != null) {
            return this.spaceType.getID();
        } else {
            return null;
        }
    }

    /**
     * Get the type of space as defined in SpaceTypes
     *
     * @return <code>SpaceType</code> object as defined in SpaceTypes.
     * @see net.project.space.SpaceType
     */
    public SpaceType getSpaceType() {
        return this.spaceType;
    }

    /**
     * Indicates whether this space is of the same type as the specified space
     * type.
     *
     * @param spaceType the String space type
     * @return true if this space is of the specified space type; false
     *         otherwise
     */
    public boolean isTypeOf(String spaceType) {
        if (spaceType.equals(this.spaceType.getID())) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Set the user-defined subtype of space. This is typically used for
     * user-defined Space types.
     */
    public void setUserDefinedSubtype(String subtype) {
        this.userDefinedSubtype = subtype;
    }


    /**
     * Get the user-defined subtype of space.
     *
     * @return the user-defined subtype string.
     */
    public String getUserDefinedSubtype() {
        return this.userDefinedSubtype;
    }


    /**
     * Set the flavor of space. A Space may have several user-defined flavors of
     * each subclass. For instance, BusinessSpace may have flavors that include:
     * operating company, division, department, group.
     */
    public void setFlavor(String flavor) {
        this.flavor = flavor;
    }

    /**
     * Get the user-defined flavor of space. A Space may have several
     * user-defined flavors of each subclass. For instance, BusinessSpace may
     * have flavors that include: operating company, division, department,
     * group.
     *
     * @return the flavor string for this Space.
     */
    public String getFlavor() {
        return this.flavor;
    }



    /* ---------------------------------------- Space Relationship Methods ------------------------------------------*/

    /**
     * Set the parent Space of this Space.
     */
    public void setParentSpaceID(String parentSpaceID) {
        this.parentSpaceID = parentSpaceID;
    }


    /**
     * Get the parent Space of this Space.
     */
    public String getParentSpaceID() {
        return this.parentSpaceID;
    }


    /**
     * Set the owner Space of this Space.
     */
    public void setOwnerSpaceID(String ownerSpaceID) {
        this.ownerSpaceID = ownerSpaceID;
    }


    /**
     * Get the owner Space of this Space.
     */
    public String getOwnerSpaceID() {
        return this.ownerSpaceID;
    }

    public String getRelatedSpaceID() {
		return relatedSpaceID;
	}


	public void setRelatedSpaceID(String relatedSpaceID) {
		this.relatedSpaceID = relatedSpaceID;
	}    

    /* -------------------------------  Implementing IJDBCPersistence  ------------------------------- */




	public abstract void load() throws net.project.persistence.PersistenceException;

    public abstract void store() throws net.project.persistence.PersistenceException;

    public abstract void remove() throws net.project.persistence.PersistenceException;

    public boolean isLoaded() {
        return this.isLoaded;
    }

    /*   Set this Space as loaded.     */
    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }


    /* -------------------------------  Implementing IXMLPersistence  ------------------------------- */

    public abstract String getXML();

    public abstract String getXMLBody();

    /* Returns generic (non-typed) properties about a space in XML form
     * @return XML document containing generic space properties
     * @author Phil Dixon
     * @since Gecko
     */
    public String getXMLProperties() {

        StringBuffer xml = new StringBuffer();

        xml.append("<Space>");

        xml.append("<id>" + XMLUtils.escape(getID()) + "</id>");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>");
        xml.append("<flavor>" + XMLUtils.escape(getFlavor()) + "</flavor>");
        xml.append("<type>" + XMLUtils.escape(getType()) + "</type>");
        xml.append("<ownerSpaceID>" + XMLUtils.escape(getOwnerSpaceID()) + "</ownerSpaceID>");
        xml.append("<recordStatus>" + XMLUtils.escape(getRecordStatus()) + "</recordStatus>");
        if (this.spaceType != null) {
            xml.append(this.spaceType.getXMLBody());
        }
        xml.append("</Space>");

        return xml.toString();
    }


    /* ---------------------------------------- Space Security Methods ------------------------------------------*/
    /**
     * Add a new user to a space. This will create all appropriate portfoliio
     * entries.  Requires a valid invitation code.
     *
     * @param memberID New Member ID
     * @param invitationCode A valid space invitation code
     * @throws PersistenceException If DB store operation fails
     * @since EMU
     */
    public void addMember(String memberID, String invitationCode) throws PersistenceException {
        DBBean db = new DBBean();

        try {
            addMember(memberID, invitationCode, db);

        } catch (SQLException sqle) {
        	Logger.getLogger(Space.class).error("Space.addMember threw an SQLException: " + sqle);
            throw new PersistenceException("Space add member operation failed: " + sqle, sqle);

        } finally {
            db.release();
        }

    }

    /**
     * Add a new user to a space. This will create all appropriate portfoliio
     * entries.  Requires a valid invitation code.
     *
     * @param memberID New Member ID
     * @param invitationCode A valid space invitation code
     * @param db DBBean instance
     * @throws SQLException If DB store operation fails
     * @since EMU
     */
    public void addMember(String memberID, String invitationCode, DBBean db) throws SQLException {

	int errorCode;

        try {

            db.prepareCall("begin space.add_member (?,?,?,?); end;");

            db.cstmt.setString(1, getID());
            db.cstmt.setString(2, memberID);
            db.cstmt.setString(3, invitationCode);
            db.cstmt.registerOutParameter(4, java.sql.Types.INTEGER);

            db.executeCallable();
            errorCode = db.cstmt.getInt(4);

// if we get a 103 exceptoin here -- it is because the person already is a member of this space.
// this error can occur if the user hits "finish" too many times.  So, rather than throw an exception,
// we'll just swallow that particular error.

            if (errorCode != 103) {
                DBExceptionFactory.getException("Space.addMember()", errorCode);
            }

        } catch (net.project.base.PnetException pe) {
            // An error occurred in the stored procedure; re-throw as SQLException
            throw new SQLException(pe.toString());
        }
    }


    /**
     * Returns the groups owned by this space.
     *
     * @return the groups owned by this space
     */
    public GroupCollection getOwnedGroups() throws PersistenceException {
        GroupCollection groups = new GroupCollection();

        groups.setSpace(this);
        groups.loadOwned();

        return groups;
    }


    /**
     * Returns all the groups available in this space.  This includes owned
     * groups as well as other groups inherited from other spaces.
     *
     * @return the groups available to this space
     */
    public GroupCollection getAvailableGroups() throws PersistenceException {
        GroupCollection groups = new GroupCollection();

        groups.setSpace(this);
        groups.loadAll();

        return groups;
    }


    /**
     * Indicates whether the user is a member of this space. A user (or any
     * IGroupMember) is defined to be a member of a space if the user is the
     * owner of a principal role in this space.
     *
     * @param groupMember the user or group member to check
     * @return true if the user is a member of the space; false otherwise
     */
    public boolean isUserSpaceMember(net.project.security.group.IGroupMember groupMember) {
        boolean isSpaceMember = false;
        GroupProvider groupProvider = new GroupProvider();

        try {
            isSpaceMember = groupProvider.hasPrincipalGroup(getID(), groupMember.getID());

        } catch (GroupException e) {
            // Problem creating the group
            // User will in effect not be a member of the space

        } catch (PersistenceException e) {
            // Database error occurred
            // User will in effect not be a member of the space

        }

        return isSpaceMember;
    }


    /**
     * Indicates whether the user is a space administrator of this space. A user
     * is defined to be a space administrator if the user is a member of the
     * Space Administrator role in this space. <p>If you want to cause a
     * security error in a JSP page then use {@link #securityCheckSpaceAdministrator(User,
        * String)} instead.</p> Note: The security settings for this space will be
     * reloaded on each call.
     *
     * @param user the user to check
     * @return true if the user is a space administrator; false otherwise
     */
    public boolean isUserSpaceAdministrator(User user) {
        SecurityProvider securityProvider = new SecurityProvider();
        securityProvider.setCurrentSpace(user, this);
        return securityProvider.isUserSpaceAdministrator();
    }

    /**
     * Indicates whether the user is a Power User of this space.
     *
     * @param user the user to check
     * @return true if the user is a Power User; false otherwise
     */
    public boolean isUserPowerUser(User user) {
        SecurityProvider securityProvider = new SecurityProvider();
        securityProvider.setCurrentSpace(user, this);
        return securityProvider.isUserPowerUser();
    }
    
    /**
     * Indicates whether the user is a specified Group User of this space.
     *
     * @param user the user to check
     * @return true if the user is a specified Group User; false otherwise
     */
    public boolean isUserGroupUser(User user, GroupTypeID groupTypeID) {
        SecurityProvider securityProvider = new SecurityProvider();
        securityProvider.setCurrentSpace(user, this);
        return securityProvider.isUserGroupUser(groupTypeID);
    }

    /**
     * Checks that the specified user has access to space as a space
     * administrator (or is an application administrator). This method throws an
     * exception if the user has no access; otherwise it simply returns with no
     * exception.<br> This method may be called from a JSP page to cause the
     * engine to route to a security errors page if the user is not a space
     * administrator.<br> <p>If you simply want to test for being a space
     * administrator, then use {@link #isUserSpaceAdministrator(User)}
     * instead.</p> Note: The security settings for this space will be reloaded
     * on each call.
     *
     * @param user the user to check.
     * @param deniedMessage a message to display if access is denied.  If the
     * message is null a default message will be used.
     * @throws net.project.security.AuthorizationFailedException if the user has
     * no access.
     */
    public void securityCheckSpaceAdministrator(User user, String deniedMessage)
        throws net.project.security.AuthorizationFailedException {
        // Create a new security provider and set the current user and space
        SecurityProvider securityProvider = new SecurityProvider();
        securityProvider.setCurrentSpace(user, this);
        securityProvider.securityCheckSpaceAdministrator(deniedMessage);
    }


    /* ---------------------------------------- Other Methods ------------------------------------------*/


    /**
     * Return the class_id of the system form for this space with the specified
     * form name.
     */
    public String getSystemFormID(String formName) {
        String classID = null;

        DBBean db = new DBBean();

        try {
            db.executeQuery("select c.class_id from pn_class c, pn_space_has_class shc where shc.space_id=" +
                this.getID() + " and c.class_id = shc.class_id and c.class_name=" + DBFormat.varchar2(formName));

            if (db.result.next()) {
                classID = db.result.getString("class_id");
            }
        } catch (SQLException sqle) {
        	Logger.getLogger(Space.class).debug("Space.getSystemFormID() threw an SQL Exception: " + sqle);
            classID = null;
        } finally {
            db.release();
        }

        return classID;
    }


    /**
     * Clear all properties of this space
     */
    public void clear() {
        //setID(null);
        setParentSpaceID(null);
        setName(null);
        setDescription(null);
        // don't clear type, type is set only during construction.
        //setType(null);
        setRoster(null);
        setRelationshipToParent(null);
        setRelationshipToChild(null);
        setParentSpaceID(null);
        setOwnerSpaceID(null);
        setUserDefinedSubtype(null);
        setFlavor(null);
        setRecordStatus(null);
    }


    /**
     * Logs access to this space for user.
     *
     * @param user the user who is accessing the space
     * @throws PersistenceException if there is a problem logging the access
     */
    public void logAccess(User user) throws PersistenceException {
        StringBuffer query;
        boolean isPriorEntry = false;
        int index;

        // don't log when IDs are null.   Causes database exception on unique keys.
        if (this.getID() == null || user.getID() == null)
        	return;
        
        DBBean db = new DBBean();

        try {
            // First determine whether there is an existing entry for this
            // space and user
            query = new StringBuffer();
            query.append("select 1 as is_prior_entry from pn_space_access_history ");
            query.append("where space_id = ? and person_id = ? ");

            db.prepareStatement(query.toString());

            index = 0;
            db.pstmt.setString(++index, this.getID());
            db.pstmt.setString(++index, user.getID());

            db.executePrepared();

            if (db.result.next()) {
                isPriorEntry = true;
            }

            // Now log the access
            query = new StringBuffer();

            if (isPriorEntry) {
                query.append("update pn_space_access_history ");
                query.append("set access_date = ? ");
                query.append("where space_id = ? and person_id = ? ");

                db.prepareStatement(query.toString());

                index = 0;
                db.pstmt.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
                db.pstmt.setString(++index, getID());
                db.pstmt.setString(++index, user.getID());

            } else {
                query.append("insert into pn_space_access_history ");
                query.append("(space_id, person_id, access_date) ");
                query.append("values (?, ?, ?) ");

                db.prepareStatement(query.toString());

                index = 0;
                db.pstmt.setString(++index, getID());
                db.pstmt.setString(++index, user.getID());
                db.pstmt.setTimestamp(++index, new java.sql.Timestamp(new java.util.Date().getTime()));
            }

            // Perform the update or insert
            db.executePrepared();
        } catch (SQLException sqle) {
        	Logger.getLogger(Space.class).error("Space.logAccess() threw an SQL exception: " + sqle);
            throw new PersistenceException("Space log operation failed.", sqle);
        } finally {
            db.release();
        }
    }

    public String getHtmlOptionDisplay() {
        return getName();
    }

    public String getHtmlOptionValue() {
        return getID();
    }
    /**
     * @param id the space id whose creation date is required
     * Returns the date of creation of the space or business
     * @throws PersistenceException if there is a problem 
     */
    public java.util.Date getCreationDate(String space_Id) throws PersistenceException {
    	  java.util.Date creationDate = null;
    	  StringBuffer query;
    	  int index;
    	  DBBean db = new DBBean(); 
    	  try {
    		  query = new StringBuffer();
              query.append("select DATE_CREATED from pn_space_has_space ");
              query.append(" where child_space_id = ? ");
              db.prepareStatement(query.toString());
              index = 0;
              db.pstmt.setString(++index, space_Id);

              db.executePrepared();

              if (db.result.next()) {
            	  Date dt = db.result.getDate(1);
            	  if(dt!=null)
            	  creationDate = new java.util.Date(dt.getTime());
              }

          } catch (SQLException sqle) {
        	  Logger.getLogger(Space.class).error("Space.getCreationDate threw an SQLException: " + sqle);
              throw new PersistenceException("Space GetCreation Date operation failed: " + sqle, sqle);

          } finally {
              db.release();
          }
    	  return creationDate;
    }
}
