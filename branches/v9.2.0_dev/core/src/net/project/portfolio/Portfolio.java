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
package net.project.portfolio;

import java.sql.SQLException;
import java.util.Iterator;

import net.project.database.DBBean;
import net.project.persistence.IJDBCPersistence;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.security.User;
import net.project.xml.XMLUtils;

import org.apache.log4j.Logger;


/**
 * An abstract portfolio (collection) of workspaces.
 * Each element is an {@link IPortfolioEntry}.
 */
public abstract class Portfolio extends java.util.ArrayList implements IJDBCPersistence, IXMLPersistence {

    /** The ID of the portfolio. */
    private String portfolioID = null;

    /** The display name of the portfolio. */
    private String name = null;

    /** The description of the portfolio. */
    private String description = null;

    /**
     * The type of portfolio.
     * @see IPortfolioTypes
     */
    private String type = null;

    /**
     * The type of portfolio entries.
     * Unknown where set of values are defined.
     */
    private String contentType = null;


    /**
     * Unknown usage, not stored in database.
     * Appears to be used by PortfolioManager.
     */
    private boolean isDefault = false;


    /**
     * Parent space of Portfolio Entries.
     * Appears to be exclusively used by MethodologyPortfolio.
     * This value is never stored in the database.  Instead, it is set on demand
     * and may be used by sub-classes to limit loading of portfolio entries
     * to be owned by a particular parentSpaceID.
     */
    private String parentSpaceID = null;

    /**
     * The User context for this portfolio.
     * Typically the person who created this Portfolio.
     */
    private User user = null;

    /**
     * Indicates whether this Portfolio has been loaded from the database.
     */
    private boolean isLoaded = false;


    /**
     * Creates an empty Portfolio.
     */
    public Portfolio() {
        // Do nothing
    }

    /**
     * Creates a Portfolio for the specified ID.
     * The portfolio is not loaded.
     * @param portfolioID the id of the portfolio
     */
    public Portfolio(String portfolioID) {
        setID(portfolioID);
    }

    /**
     * Creates a Portfolio based on the specified parent space and type.
     * The portfolio is not loaded.
     * @param parentSpaceID the ID of the parent space
     * @param type the type of portfolio
     */
    public Portfolio(String parentSpaceID, String type) {
        setParentSpaceID(parentSpaceID);
        setType(type);
    }


    /**
     * Sets the portfolio id.
     * The portfolio is not loaded.
     * @param portfolioID the id of the portfolio
     * @see #getID
     */
    public void setID(String portfolioID) {
        this.portfolioID = portfolioID;
    }

    /**
     * Returns the portfolio id.
     * @return the id of the portfolio
     * @see #setID
     */
    public String getID() {
        return this.portfolioID;
    }

    /**
     * Sets the display name of this portfolio.
     * @param name the display name
     * @see #getName
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the display name of this portfolio.
     * @return the display name
     * @see #setName
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the description of this portfolio.
     * @param description the description
     * @see #getDescription
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the description of this portfolio.
     * @return the description
     * @see #setDescription
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Sets the type of the portfolio.
     * @param type one of the space type defined by IPortfolioTypes.
     * @see IPortfolioTypes
     * @see #getType
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the type of Space this portfolio contains.
     * @return one of the space types defined by IPortfolioTypes.
     * @see IPortfolioTypes
     * @see #setType
     */
    public String getType() {
        return this.type;
    }


    /**
     * Sets the type of portfolio entries that this portfolio contains.
     * @param contentType one of the space type defined by ISpaceTypes.
     * @see net.project.space.ISpaceTypes
     * @see #getContentType
     */
    public void setContentType(String contentType) {
        this.contentType = type;
    }


    /**
     * Returns the type of portfolio entries this portfolio contains.
     * @return one of the space types defined by ISpaceTypes.
     * @see net.project.space.ISpaceTypes
     * @see #setContentType
     */
    public String getContentType() {
        return this.contentType;
    }


    /**
     * Sets this portfolio as the default within a PortfolioMenu.
     * This value is not stored in Portfolio.
     * @param value true if this is the default portfolio; false otherwise
     * @deprecated as of 7.4; no replacement
     * PortfolioMenu is obsolete.
     */
    public void setIsDefault(boolean value) {
        this.isDefault = value;
    }

    /**
     * Indicates whether this portfolio is default.
     * This value only makes sense in the context of a PortfolioMenu.
     * @return true if this is the default portfolio; false otherwise
     * @deprecated as of 7.4; no replacement
     * PortfolioMenu is obsolete.
     */
    public boolean isDefault() {
        return this.isDefault;
    }


    /**
     * Sets the parent space ID for all portfolio entries.
     * @param parentSpaceID the id of the parent space to which to limit portfolio entries.
     * @see #getParentSpaceID
     */
    public void setParentSpaceID(String parentSpaceID) {
        this.parentSpaceID = parentSpaceID;
    }

    /**
     * Returns the parent space ID.
     * @return the parent space id to which all portfolio entries are limited.
     * @see #setParentSpaceID
     */
    public String getParentSpaceID() {
        return this.parentSpaceID;
    }


    /**
     * Sets the User context.
     * Not all portfolios need a user context.
     * Must set a user context before calling loadUserInfo().
     * @param user the current user
     * @see #getUser
     */
    public void setUser(User user) {
        this.user = user;
    }


    /**
     * Returns the user context for this portfolio.
     * @return the current user
     * @see #setUser
     */
    public User getUser() {
        return this.user;
    }

    /**
     * Returns a tree-style portfolio view.
     * @return a tree view
     * @throws UnsupportedOperationException if sub-classes of Portfolio
     * have not overridden this method
     */
    public IPortfolioView getTreeView() {
        throw new UnsupportedOperationException("Tree View not supported.");
    }


    //
    // Implementing IJDBCPersistence
    //

    /**
     * Load the properties of the portfolio from the database.
     * Loads only the properties of a Portfolio, subclasses must extend
     * to load portfolio entries or views.
     * After calling, {@link #isLoaded} return <code>true</code>.
     * @throws net.project.persistence.PersistenceException if there is a problem loading
     * @throws NullPointerException if ID is null
     */
    public void load() throws PersistenceException {

        DBBean db = new DBBean();

        if (getID() == null) {
            throw new NullPointerException("portfolioID is null. Must be set before calling load()");
        }

        try {
            String qstrGetPortfolioInfo = "select portfolio_id, portfolio_name, portfolio_desc, portfolio_type, content_type from pn_portfolio where portfolio_id=?";
            db.prepareStatement(qstrGetPortfolioInfo);
            db.pstmt.setString(1, this.portfolioID);
            db.executePrepared();

            if (db.result.next()) {
                setID(db.result.getString("portfolio_id"));
                setName(db.result.getString("portfolio_name"));
                setDescription(db.result.getString("portfolio_desc"));
                setType(db.result.getString("portfolio_type"));
                setContentType(db.result.getString("content_type"));
                setLoaded(true);
            }

        } catch (SQLException sqle) {
        	Logger.getLogger(Portfolio.class).error("Portfolio.load() threw a PersistenceException: " + sqle);
            throw new PersistenceException("Portfolio.load() threw an SQL exception: " + sqle, sqle);

        } finally {
            db.release();
        }

    }


    /**
     * Not supported.
     * @throws PersistenceException Thrown to indicate a failure storing to the
     * database, a system-level error.
     * @throws UnsupportedOperationException always
     */
    public void store() throws PersistenceException {
        throw new UnsupportedOperationException("Business portfolio store operation not supported");
    }

    /**
     * Associates the current portfolio entries with this portfolio.
     * No commit or rollback is performed.
     * @param db the DBBean in which to perform the transaction
     * @throws SQLException if there is a problem storing
     */
    private void storeEntries(DBBean db) throws SQLException {

        // delete old portolio entries first.
        db.executeQuery("delete from pn_portfolio_has_space where portfolio_id=" + getID());

        if (size() > 0) {

            String spaceID = null;
            int index = 0;

            // store the portfolio entries using a batch insert.
            db.prepareStatement("insert into pn_portfolio_has_space (portfolio_id, space_id, is_private) values (?, ?, 0)");

            for (Iterator it = iterator(); it.hasNext();) {
                spaceID = ((IPortfolioEntry) it.next()).getID();
                index = 0;
                db.pstmt.setString(++index, getID());
                db.pstmt.setString(++index, spaceID);
                db.pstmt.addBatch();
            }

            db.executePreparedBatch();
        }

    }

    /**
     * Soft delete the portfolio from the database.
     * @throws net.project.persistence.PersistenceException if there is a problem removing the portfolio
     */
    public void remove() throws net.project.persistence.PersistenceException {

        DBBean db = new DBBean();

        try {
            db.executeQuery("update pn_portfolio set record_status='D' where portfolio_id=" + getID());

        } catch (SQLException sqle) {
        	Logger.getLogger(Portfolio.class).error("BusinessSpace.remove() threw an SQL exception: " + sqle);
            throw new PersistenceException("BusinessSpace.remove() threw an SQL exception: " + sqle, sqle);

        } finally {
            db.release();

        }

    }


    /**
     * Indicates whether the portfolio is loaded.
     * Does not indicate whether portfolio entries are loaded.
     * @return true if this portfolio has been loaded; false otherwise
     * @see #load
     * @see #setLoaded
     */
    public boolean isLoaded() {
        return this.isLoaded;
    }

    /**
     * Specifies whether the portfolio properties are loaded.
     * Does not set whether portfolio entries are loaded.
     * @param value true if this is loaded; false otherwise
     */
    protected void setLoaded(boolean value) {
        this.isLoaded = value;
    }


    //
    // IXMLPersistence
    //

    /**
     * Get an XML representation of the PortfolioMenu.
     * XML for portfolio entries is not included.
     * @return a complete XML document representation of this PortfolioMenu.
     */
    public String getXML() {
        return net.project.persistence.IXMLPersistence.XML_VERSION + getXMLBody();
    }


    /**
     * Get an XML node representation of this PortfolioMenu.
     * XML for portfolio entries is not included.
     * @return an XML node representation of this PortfolioMenu.
     */
    public String getXMLBody() {
        StringBuffer xml = new StringBuffer();
        xml.append("<Portfolio>\n");
        xml.append("<portfolioID>" + getID() + "</portfolioID>\n");
        xml.append("<type>" + XMLUtils.escape(getType()) + "</type>\n");
        xml.append("<contentType>" + XMLUtils.escape(getContentType()) + "</contentType>\n");
        xml.append("<parentSpaceID>" + getParentSpaceID() + "</parentSpaceID>\n");
        xml.append("<name>" + XMLUtils.escape(getName()) + "</name>\n");
        xml.append("<description>" + XMLUtils.escape(getDescription()) + "</description>\n");

        if ((user != null) && (user.getID() != null))
            xml.append("<userID>" + user.getID() + "</userID>\n");
        else
            xml.append("<userID>null</userID>\n");

        xml.append("</Portfolio>\n");
        return xml.toString();
    }



    /* -------------------------------  Implementing utility methods  ------------------------------- */

    /**
     * Indicates whether the specified portfolio type is the same as this portfolio's type.
     * @param portfolioType the portfolio type
     * @return true if the specified portfolio type is equal to this portfolio's type; false otherwise
     */
    public boolean isTypeOf(String portfolioType) {
        if (portfolioType.equals(getType()))
            return true;
        else
            return false;
    }


    /**
     *  Clear the portfolio properties and entries.
     */
    public void clear() {
        super.clear();
        setID(null);
        setName(null);
        setDescription(null);
        setType(null);
        setContentType(null);
        setIsDefault(false);
        setParentSpaceID(null);
        setLoaded(false);
    }

}


