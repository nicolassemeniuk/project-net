package net.project.financial;

import java.sql.SQLException;

import net.project.base.PnetException;
import net.project.database.DBBean;
import net.project.database.DBExceptionFactory;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.space.Space;
import net.project.space.SpaceManager;
import net.project.space.SpaceRelationship;

public class FinancialCreateWizard extends FinancialSpace {

	/**
	 * Related business space
	 */
	private Space relatedSpace;
	
	/**
	 * Related business relationship
	 */
	private SpaceRelationship relationship;
	
	/**
	 * Related financial super-space
	 */
	private Space relatedOwnerSpace;
	
	/**
	 * Financial super-space relationship
	 */
	private SpaceRelationship ownerRelationship;
	
	/**
	 * The user creator of this space.
	 */
	protected User user = null;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Space getRelatedSpace() {
		return relatedSpace;
	}

	public void setRelatedSpace(Space relatedSpace) {
		this.relatedSpace = relatedSpace;
	}

	public SpaceRelationship getRelationship() {
		return relationship;
	}

	public void setRelationship(SpaceRelationship relationship) {
		this.relationship = relationship;
	}
		
	public Space getRelatedOwnerSpace() {
		return relatedOwnerSpace;
	}

	public void setRelatedOwnerSpace(Space relatedOwnerSpace) {
		this.relatedOwnerSpace = relatedOwnerSpace;
	}

	public SpaceRelationship getOwnerRelationship() {
		return ownerRelationship;
	}

	public void setOwnerRelationship(SpaceRelationship ownerRelationship) {
		this.ownerRelationship = ownerRelationship;
	}

	public void store() throws PersistenceException {

		if (this.user == null) {
			throw new NullPointerException("FinancialCreateWizard.store() must have a USER set to execute: ");
		}

		// Create the financial space
		Integer financialSpaceId = ServiceFactory.getInstance().getPnFinancialSpaceService().saveFinancialSpace(this);
		this.setID(String.valueOf(financialSpaceId));
		


		// Create the relationship between the business and the financial space
		DBBean db = new DBBean();
		try {
			db.setAutoCommit(false);
			
			int index = 0;
	        db.prepareCall("{call FINANCIAL.CREATE_FINANCIAL(?,?)}");
	        db.cstmt.setString(++index, this.user.getID());
	        db.cstmt.setString(++index, String.valueOf(financialSpaceId));
	        db.executeCallable();
			
			inviteOwner(db);
			
	        // Update relationship to parent financial space. We have two ways of doing this, either
			// with a parentSpaceId or through the relatedOwnerSpace and ownerRelationship attributes.
	        if ((getParentSpaceID() != null) && !getParentSpaceID().equals("")) {
	        	FinancialSpace parentSpace = new FinancialSpace();
	            parentSpace.setID(getParentSpaceID());
	            SpaceManager.addSuperFinancialRelationship(db, parentSpace, this);   

	        } else if ((relatedOwnerSpace != null) && (ownerRelationship != null))	{
	        	SpaceManager.addRelationship(db, this.getRelatedOwnerSpace(), this, this.getOwnerRelationship());
	        }
	        	
	        //Update relationship to related business
        	if ((relatedSpace != null) && (relationship != null)){
	        	SpaceManager.addRelationship(db, this.getRelatedSpace(), this, this.getRelationship());
	        }

			
			db.commit();

		} catch (SQLException e) {
			try {
				db.rollback();
			} catch (SQLException ignored) {
				// Fail with original error
			}
			throw new PersistenceException("Error storing financial space: " + e, e);

		} finally {
			db.release();
		}

	}

	/**
	 * Add the person who just created the financial space to the directory. Does not
	 * commit or rollback.
	 * 
	 * @param db
	 *            the DBBean in which to perform the transaction
	 * @throws SQLException
	 *             if there is a problem storing
	 */
	private void inviteOwner(DBBean db) throws SQLException {
		int statusId;
		int invitationCode;

		db.prepareCall("begin FINANCIAL.INVITE_PERSON_TO_FINANCIAL(?,?,?,?,?,?,?,?,?); end;");
		// i_business_id IN NUMBER
		db.cstmt.setInt(1, new Integer(getID()).intValue());
		// i_person_id IN NUMBER
		db.cstmt.setInt(2, new Integer(this.user.getID()).intValue());
		// i_email IN VARCHAR2
		db.cstmt.setString(3, this.user.getEmail().trim());
		// i_firstname IN VARCHAR2
		db.cstmt.setString(4, this.user.getFirstName());
		// i_lastname IN VARCHAR2
		db.cstmt.setString(5, this.user.getLastName());
		// i_resonsibilties IN VARCHAR2
		db.cstmt.setString(6, "");
		// i_invitor_id IN NUMBER
		db.cstmt.setInt(7, new Integer(this.user.getID()).intValue());
		// o_invitation_code OUT NUMBER
		db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);
		// o_status OUT NUMBER
		db.cstmt.registerOutParameter(9, java.sql.Types.INTEGER);
		db.executeCallable();

		// get out parameters
		invitationCode = db.cstmt.getInt(8);
		statusId = db.cstmt.getInt(9);

		try {
			DBExceptionFactory.getException("FinancialCreateWizard.inviteOwner() financial.INVITE_PERSON_TO_FINANCIAL", statusId);
		} catch (PnetException e) {
			throw (SQLException) new SQLException("Error in stored procedure INVITE_PERSON_TO_FINANCIAL: " + e).initCause(e);
		}

		db.prepareStatement("Update pn_invited_users set invited_status = 'Accepted' where invitation_code = ?");
		db.pstmt.setInt(1, invitationCode);
		db.pstmt.executeQuery();

	}

}
