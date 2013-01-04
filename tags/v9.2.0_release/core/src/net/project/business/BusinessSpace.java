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

package net.project.business;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;

import javax.servlet.ServletContext;

import net.project.database.DBBean;
import net.project.form.FormManager;
import net.project.methodology.MethodologyProvider;
import net.project.methodology.MethodologySpace;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.project.ProjectSpace;
import net.project.resource.Address;
import net.project.resource.Person;
import net.project.resource.Roster;
import net.project.security.SessionManager;
import net.project.security.group.Group;
import net.project.security.group.GroupException;
import net.project.security.group.GroupProvider;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceManager;
import net.project.space.SpaceRelationship;
import net.project.space.SpaceTypes;
import net.project.util.TemplateFormatter;
import net.project.util.TextFormatter;
import net.project.util.Validator;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.log4j.Logger;

/**
 * A business workspace.
 * 
 * @author Carlos Montemuiño
 * @author BrianConneen 05/00
 * @since 1.0
 */
public class BusinessSpace extends Space implements IPortfolioEntry, IJDBCPersistence, IXMLPersistence, Serializable {

    /**
     * This business's address.
     */
    protected Address m_address = new Address();

    /**
     * The id of this business's portfolio which maintains the collection of all
     * projects owned by this business.
     */
    protected String m_complete_portfolio_id = null;

    /**
     * The id of this business's logo.
     */
    protected String logoID = null;

    /**
     * Number of projects owned by this business.
     */
    private String numProjects = null;

    /**
     * Number of persons who are members of this business.
     */
    private String numMembers = null;

    /**
     * The id of a methodology used to create this business.
     */
    private String methodologyID = null;

    // for master business
    private boolean isMaster = false;

    private String businessCategoryID = null;

    private String brandID = null;

    private String billingAccountID = null;

    private boolean parentChanged;
    
    /**
     * Construct an empty BusinessSpace.
     */
    public BusinessSpace() {
        setType(ISpaceTypes.BUSINESS_SPACE);
        this.spaceType = SpaceTypes.BUSINESS;
    }

    /**
     * Construct a BusinessSpace to be restored from persistence. No load is
     * performed.
     * 
     * @param businessID
     *            the id of the business
     */
    public BusinessSpace(String businessID) {
        super(businessID);
        setType(ISpaceTypes.BUSINESS_SPACE);
        this.spaceType = SpaceTypes.BUSINESS;
    }

    /**
     * Returns the total number of projects owned by this BusinessSpace.
     * 
     * @return the total number of projects owned
     * @see #setNumProjects
     */
    public String getNumProjects() {
        return this.numProjects;
    }

    /**
     * Sets the total number of projects owned by this BusinessSpace.
     * 
     * @param num
     *            the number of projects owned
     * @see #getNumProjects
     */
    public void setNumProjects(String num) {
        this.numProjects = num;
    }

    /**
     * Returns the total number of people that are members of this
     * BusinessSpace.
     * 
     * @return the total number of members
     * @see #setNumMembers
     */
    public String getNumMembers() {
        return this.numMembers;
    }

    /**
     * Sets the total number of members of this BusinessSpace.
     * 
     * @param num
     *            the number of members
     * @see #getNumMembers
     */
    public void setNumMembers(String num) {
        this.numMembers = num;
    }

    /**
     * Returns the address for the business that owns this business space.
     * 
     * @return the address
     */
    public Address getAddress() {
        return this.m_address;
    }

    /**
     * Specifies whether this is a a master business (top-level business).
     * 
     * @param value
     *            true if this business is a master business, false if not
     * @see #getIsMaster
     */
    public void setIsMaster(boolean value) {
        this.isMaster = value;
    }

    /**
     * Indicates whether this is a master business (top-level business).
     * 
     * @return true if this is a master business; false otherwise
     * @see #setIsMaster
     */
    public boolean getIsMaster() {
        return this.isMaster;
    }

    /**
     * Sets the business category ID (aka Displine).
     * 
     * @param value
     *            the business category id
     * @see #getBusinessCategoryID
     */
    public void setBusinessCategoryID(String value) {
        this.businessCategoryID = value;
    }

    /**
     * Returns the business category ID (aka Displine).
     * 
     * @return the business category id
     * @see #setBusinessCategoryID
     */
    public String getBusinessCategoryID() {
        return this.businessCategoryID;
    }

    /**
     * Specifies the business brand ID.
     * 
     * @see #getBrandID
     * @deprecated as of 7.4; no replacement This value has no usage in the
     *             application
     */
    public void setBrandID(String value) {
        this.brandID = value;
    }

    /**
     * Returns the business brand id.
     * 
     * @return the brand id
     * @see #setBrandID
     * @deprecated as of 7.4; no replacement This value has no usage in the
     *             application
     */
    public String getBrandID() {
        return this.brandID;
    }

    /**
     * Specifies the ID of the BillingAccount for this business.
     * 
     * @param value
     *            the billing account id
     * @see #getBillingAccountID
     * @deprecated as of 7.4; no replacement This value has no usage in the
     *             application
     */
    public void setBillingAccountID(String value) {
        this.billingAccountID = value;
    }

    /**
     * Returns the ID of the BillingAccount for this business.
     * 
     * @return the billing account id
     * @see #setBillingAccountID
     * @deprecated as of 7.4; no replacement This value has no usage in the
     *             application
     */
    public String getBillingAccountID() {
        return this.billingAccountID;
    }

    /**
     * Sets the Methodology ID.
     * 
     * @param methodologyID
     *            the id of the methodology to be applied when this business is
     *            created
     * @see #getMethodologyID
     */
    public void setMethodologyID(String methodologyID) {
        this.methodologyID = methodologyID;
    }

    /**
     * Returns the Methodology ID of the methodology to apply when creating this
     * business.
     * 
     * @return methodologyID the Methodology ID
     * @see #setMethodologyID
     */
    public String getMethodologyID() {
        return this.methodologyID;
    }

    /**
     * Returns the Methodology Name based on the Methodology ID
     * 
     * @param methodologyID
     *            the Methodology ID
     * @return The Methodology Name
     */

    public String getMethodologyName(String methodologyID) {
        return (MethodologyProvider.getMethodologyName(methodologyID));
    }

    /**
     * Returns the Methodology Name based on the current object's Methodology ID
     * 
     * @return The Methodology Name
     */
    public String getMethodologyName() {
        return getMethodologyName(this.methodologyID);
    }

    public String getAddress1() {
        return this.m_address.getAddress1();
    }

    public void setAddress1(String a1) {
        this.m_address.setAddress1(a1);
    }

    public String getAddress2() {
        return this.m_address.getAddress2();
    }

    public void setAddress2(String a2) {
        this.m_address.setAddress2(a2);
    }

    public String getAddress3() {
        return this.m_address.getAddress3();
    }

    public void setAddress3(String a3) {
        this.m_address.setAddress3(a3);
    }

    public String getCity() {
        return m_address.getCity();
    }

    public void setCity(String city) {
        this.m_address.setCity(city);
    }

    public String getProvinceCode() {
        return m_address.getState();
    }

    public void setProvinceCode(String pc) {
        this.m_address.setState(pc);
    }

    public String getPostalCode() {
        return m_address.getZipcode();
    }

    public void setPostalCode(String pc) {
        this.m_address.setZipcode(pc);
    }

    public String getCountryCode() {
        return m_address.getCountry();
    }

    public void setCountryCode(String cc) {
        this.m_address.setCountry(cc);
    }

    public String getPhone() {
        return m_address.getOfficePhone();
    }

    public void setPhone(String phone) {
        this.m_address.setOfficePhone(phone);
    }

    public String getFax() {
        return m_address.getFaxPhone();
    }

    public void setFax(String fax) {
        this.m_address.setFaxPhone(fax);
    }

    public String getWebsite() {
        return m_address.getWebsiteURL();
    }

    public void setWebsite(String url) {
        this.m_address.setWebsiteURL(url);
    }

    /**
     * Indicates whether this business space is a sub-business of another
     * BusinessSpace.
     * 
     * @return true if this business space has a parent space; false otherwise
     */
    public boolean isSubbusiness() {
        return (getParentSpaceID() != null);
    }

    
    public boolean isParentChanged() {
		return parentChanged;
	}

	public void setParentChanged(boolean parentChanged) {
		this.parentChanged = parentChanged;
	}

	/**
     * set the document id of the the business logo without persisting the
     * change to the database.
     */
    public void setLogoID(String logoID) {
        this.logoID = logoID;
    }

    /** get the document id of the the business logo */
    public String getLogoID() {
        return this.logoID;
    }

    /**
     * Adds the logo with the specified id to the business and persists to the
     * database. It is assumed the logo is already uploaded and stored in a
     * document vault.
     * 
     * @param logoID
     *            the id of the logo
     * @throws PersistenceException
     *             if there is a problem storing
     */
    public void addLogoToBusiness(String logoID) throws PersistenceException {
        setLogoID(logoID);
        DBBean db = new DBBean();

        try {
            db.prepareCall("{call BUSINESS.ADD_LOGO(?,?)}");
            db.cstmt.setString(1, getID());
            db.cstmt.setString(2, getLogoID());
            db.executeCallable();

            // ENW contrib: add view security permission to Team Member
            // group for logo document.
            // TODO: do this with security API.
            StringBuffer query = new StringBuffer();
            String objectID = logoID;
            // get current permissions
            int actions = 0;
            query.append("select op.actions ").append("from pn_object_permission op, pn_group g, pn_space_has_group shg ").append("where op.object_id = ? and shg.space_id = ? ").append("and g.group_id = op.group_id and shg.group_id = g.group_id ").append("and g.group_type_id = 300 ");
            db.prepareStatement(query.toString());
            db.pstmt.setString(1, objectID);
            db.pstmt.setString(2, this.getID());
            db.executePrepared();
            // update permissions
            if (db.result.next()) {
                actions = (Integer.parseInt(db.result.getString("actions")));
                if ((actions & net.project.security.Action.VIEW) == 0) {
                    actions |= net.project.security.Action.VIEW;
                    query = new StringBuffer();
                    query.append("update pn_object_permission set actions = ? ").append("where object_id = ? and group_id in (").append("select g.group_id from pn_group g, pn_space_has_group shg ").append("where shg.space_id = ? and shg.group_id = g.group_id ").append("and g.group_type_id = 300) ");
                    db.prepareStatement(query.toString());
                    int i = 0;
                    db.pstmt.setString(++i, "" + actions);
                    db.pstmt.setString(++i, objectID);
                    db.pstmt.setString(++i, this.getID());
                    db.executePrepared();
                }
            }
        } catch (SQLException sqle) {
            Logger.getLogger(BusinessSpace.class).error("BusinessSpace.addLogo() threw an SQL exception: " + sqle);
            throw new PersistenceException("Business add logo operation failed: " + sqle, sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Remove the logo associated with the business from the database.
     */
    public void removeLogo() throws PersistenceException {
        String qstrRemoveLogo = "update pn_business set logo_image_id = NULL where business_id = " + getID();
        DBBean db = new DBBean();

        try {
            db.executeQuery(qstrRemoveLogo);

        } catch (SQLException sqle) {
            Logger.getLogger(BusinessSpace.class).error("BusinessSpace.removeLogo () threw an " + "SQL exception: " + sqle);
            throw new PersistenceException("BusinessSpace.removeLogo () threw " + "a SQL exception: " + sqle, sqle);
        } finally {
            db.release();
        }
    }

    /**
     * Get the business' "owner" project portfolio id. These are the projects
     * that the Business Space owns.
     */
    public String getProjectPortfolioID(String portfolioName) {
        String portfolioID = null;
        String query = "select p.portfolio_id  from pn_space_has_portfolio shp, pn_portfolio p  " + "where shp.space_id=" + this.getID() + " and shp.portfolio_id=p.portfolio_id and portfolio_name='" + portfolioName + "'";

        DBBean db = new DBBean();

        try {
            db.executeQuery(query);

            if (db.result.next())
                portfolioID = db.result.getString("portfolio_id");

        } catch (SQLException sqle) {
            portfolioID = null;

        } finally {
            db.release();

        }

        return portfolioID;
    }

    /*---------------------------  Implementing Interface IJDBCPersistence ---------------------------------*/

    /**
     * Load the BusinessSpace properties from persistence store.
     */
    public void load() throws PersistenceException {

        if (getID() == null) {
            throw new NullPointerException("id is null. Can't load.");
        }

        new BusinessSpaceFinder().findByID(getID(), this);
        // Finally load the address
        this.m_address.load();
    }

    /**
     * Store the Business Space in the DB
     * 
     * @throws PersistenceException
     *             Thrown to indicate a failure storing in the database, a
     *             system-level error.
     * 
     */
    public void store() throws PersistenceException {

        String qstrUpdateBusiness = "{call BUSINESS.UPDATE_BUSINESS(?,?,?,?,?,?,?,?)}";

        DBBean db = new DBBean();

        try {
            db.prepareCall(qstrUpdateBusiness);
            db.cstmt.setInt(1, Integer.parseInt(getID()));
            db.cstmt.setString(2, getName());
            db.cstmt.setString(3, getDescription());
            db.cstmt.setString(4, getFlavor());
            db.cstmt.setString(5, getBusinessCategoryID());
            db.cstmt.setString(6, getBrandID());
            db.cstmt.setString(7, getBillingAccountID());

            db.cstmt.registerOutParameter(8, java.sql.Types.INTEGER);
            db.executeCallable();
            this.m_address.store();

            // Update relationship to parent business
            SpaceManager.removeSuperBusinessRelationships(this);
            if ((getParentSpaceID() != null) && !getParentSpaceID().equals("")) {
                BusinessSpace parentSpace = new BusinessSpace();
                parentSpace.setID(getParentSpaceID());
                parentSpace.setType(ISpaceTypes.BUSINESS_SPACE);
                SpaceManager.addSuperBusinessRelationship(parentSpace, this);
                if (isParentChanged()){
                	FormManager.updateSharedForms(db, parentSpace , this);
                }
            }else{
                if (isParentChanged()){
                	FormManager.updateSharedForms(db, null , this);
                }            	
            }
        } catch (SQLException sqle) {
            Logger.getLogger(BusinessSpace.class).error("BusinessSpace.store() threw a SQL exception: " + sqle);
            throw new PersistenceException("BusinessSpace.store() thew a SQL exception: " + sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Removes the object that is currently associated with the instance.
     * 
     * @throws PersistenceException
     *             Thrown to indicate a failure storing to the database, a
     *             system-level error.
     */
    public void remove() throws PersistenceException {

        Group spaceAdmin = null;
        GroupProvider groupProvider = new GroupProvider();

        DBBean db = new DBBean();
        //DBBean db1 = new DBBean();
        try {
            db.executeQuery("update pn_business set record_status='D' where business_id=" + getID());
            db.executeQuery("update pn_business_space set record_status='D' where business_space_id=" + getID());

            db.prepareStatement("select child_space_id, child_space_type from pn_space_has_space where parent_space_id = ?");
            db.pstmt.setString(1, getID());
            db.executePrepared();
            while (db.result.next()) {
                int index = 0;
                //db1.prepareCall("{call PROJECT.REMOVE (?,?)}");
                //db1.cstmt.setString(++index, db.result.getString(1));
                //db1.cstmt.setString(++index, SessionManager.getUser().getID());
                //db1.executeCallable();

                // Remove parent relationship
                SpaceManager.removeParentRelationships(this, SpaceRelationship.SUBSPACE, "project");

                // Now remove all child sub-projects relationships
                // This ensures that child projects no longer have a parent
                SpaceManager.removeChildRelationships(this, SpaceRelationship.SUBSPACE, "project");

                SpaceManager.removeSharedRelationships(db.result.getString(1));

                // Update project to remove it's parent business   
                if(db.result.getString(2).equals(ISpaceTypes.PROJECT_SPACE)) {
	                ProjectSpace projectSpace = new ProjectSpace();
	                projectSpace.setID(db.result.getString(1));
	                projectSpace.load();
	                projectSpace.setParentBusinessID(null);
	                projectSpace.store();
                }
            }

            // --------------------------------------------
            // Remove all templates owned by this business.
            // --------------------------------------------
            // 1) Construct a query to get templates.
            StringBuilder templateQuerySearch = new StringBuilder();
            templateQuerySearch.append("select shs.child_space_id from pn_space_has_space shs, pn_methodology_space ms ");
            templateQuerySearch.append("where shs.child_space_id = ms.methodology_id and shs.parent_space_id = ?");
            db.prepareStatement(templateQuerySearch.toString());
            db.pstmt.setString(1, this.spaceID);

            // 2) Execute the query and iterate over resultset in order to
            // get
            // each template and remove it.
            db.executePrepared();
            while (db.result.next()) {
                String templateId = db.result.getString(1);
                if (!Validator.isBlankOrNull(templateId)) {
                    MethodologySpace template = new MethodologySpace(templateId);
                    template.remove();
                    // Remove the relationship the business and the template
                    // has.
                    SpaceManager.removeRelationship(this, template, SpaceRelationship.OWNERSHIP);
                }
            }
            spaceAdmin = groupProvider.getSpaceAdminGroup(getID());
            spaceAdmin.load();

            Roster roster = new Roster();
            roster.setSpace(this);
            roster.load();
            Iterator itr = roster.iterator();
            while (itr.hasNext()) {
                Person person = (Person) itr.next();
                if (!spaceAdmin.isMember(person.getID())) {
                    roster.removePerson(person.getID());
                }
            }
            
            FormManager.removeAllSharedForms(db, getID());

        } catch (SQLException sqle) {
            Logger.getLogger(BusinessSpace.class).error("BusinessSpace.remove() threw an SQL exception: " + sqle);
            throw new PersistenceException("BusinessSpace.remove() threw an SQL exception:", sqle);

        } catch (GroupException ge) {
            // Problem locating space admin group
            Logger.getLogger(BusinessSpace.class).error("BusinessSpace.remove() threw an Group exception: " + ge);
            throw new PersistenceException("BusinessSpace.remove() threw an Group exception:", ge);

        } finally {
            db.release();

        }

    }

    /**
     * Activates the object that is currently associated with the instance.
     * 
     * @throws PersistenceException
     *             Thrown to indicate a failure storing to the database, a
     *             system-level error.
     */
    public void activate() throws PersistenceException {

        DBBean db = new DBBean();

        try {
            db.executeQuery("update pn_business set record_status='A' where business_id=" + getID());
            db.executeQuery("update pn_business_space set record_status='A' where business_space_id=" + getID());

        } catch (SQLException sqle) {
            Logger.getLogger(BusinessSpace.class).error("BusinessSpace.activate() threw an SQL exception: " + sqle);
            throw new PersistenceException("BusinessSpace.activate() threw an SQL exception: " + sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Clear the properties of this object. For reusing the object for another
     * project space.
     */
    public void clear() {
        super.clear();
        m_address.clear();
        m_complete_portfolio_id = null;
        logoID = null;
        numProjects = null;
        numMembers = null;
    }

    /*---------------------------- Implementing INTERFACE IXMLPersistence -----------------------------------*/

    /**
     * Converts the object to XML representation This method returns the object
     * as XML text including the XML Vesion tag.
     * 
     * @return XML representation of the object
     */
    public String getXML() {
        return getXMLDocument().getXMLString();
    }

    /**
     * Converts the object to XML node representation without the xml header
     * tag. This method returns the object as XML text.
     * 
     * @return XML node representation
     */
    public String getXMLBody() {
        return getXMLDocument().getXMLBodyString();
    }

    /**
     * Returns the XML for this BusinessSpace.
     * 
     * @return the XML
     */
    protected XMLDocument getXMLDocument() {

        XMLDocument doc = new XMLDocument();

        try {

            doc.startElement("BusinessSpace");
            doc.addElement("businessID", getID());
            doc.addElement("name", getName());
            doc.addElement("description", getDescription());
            doc.addElement("businessType", getFlavor());
            doc.addElement("parentSpaceID", getParentSpaceID());
            doc.addElement("ownerSpaceID", getOwnerSpaceID());
            doc.addElement("numProjects", getNumProjects());
            doc.addElement("numMembers", getNumMembers());
            doc.addElement("completePortfolioID", this.m_complete_portfolio_id);
            doc.addElement("logoID", getLogoID());
            doc.addElement("isMaster", Boolean.valueOf(getIsMaster()));
            doc.addElement("businessCategoryID", getBusinessCategoryID());
            doc.addElement("brandID", getBrandID());
            doc.addElement("billingAccountID", getBillingAccountID());
            if (m_address != null && m_address.isLoaded()) {
                doc.addXMLString(m_address.getXMLBody());
            }
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Do nothing
            // Return partially constructed document
        }

        return doc;
    }

    /**
     * Returns the SearchableDirectory for this business space.
     * 
     * @return the SearchableDirectory
     */
    public net.project.base.directory.search.ISearchableDirectory getSearchableDirectory() {
        return new BusinessSearchableDirectory();
    }
    
    /**
     * Truncates bussiness name.
     * @return
     */
    public String getTruncatedBussinessName(){
    	
    	return TextFormatter.truncateString(this.getName(), 40).replaceAll("'", "`");
    }
    
    //
    // Inner classes
    //

    /**
     * The ISearchableDirectory for this business space.
     */
    private class BusinessSearchableDirectory implements net.project.base.directory.search.ISearchableDirectory {

        /**
         * Returns the name of this directory.
         * 
         * @return the name
         */
        public String getSearchableDirectoryName() {
            return BusinessSpace.this.getName();
        }

        /**
         * Returns the description of this directory.
         * 
         * @return the description
         */
        public String getSearchableDirectoryDescription() {
            return BusinessSpace.this.getDescription();
        }

        /**
         * Returns the directory context for this directory. The context
         * actually provides the search facilities.
         * 
         * @return the directory context
         */
        public net.project.base.directory.search.IDirectoryContext getDirectoryContext() {
            return new BusinessDirectoryContext(BusinessSpace.this);
        }

    }

    @Override
    public boolean equals(Object other) {
        if ((this == other))
            return true;
        if (!(other instanceof BusinessSpace))
            return false;
        BusinessSpace castOther = (BusinessSpace) other;
        return new EqualsBuilder().append(this.spaceID, castOther.spaceID).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(getID()).toHashCode();
    }
    
    /**
	 * details of this business object in html format.
	 * By usnig a template business-detail.tml
	 * @param servletContext
	 * @return <code>String</code>HTML of detaill for this object. 
	 */
	public String getDetails(ServletContext servletContext){
		return new TemplateFormatter(servletContext, "/details/template/business-detail.tml").transForm(this);
	}

}
