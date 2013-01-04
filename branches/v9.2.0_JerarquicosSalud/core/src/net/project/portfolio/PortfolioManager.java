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
|   $Revision: 18397 $
|       $Date: 2008-11-21 10:47:28 -0200 (vie, 21 nov 2008) $
|     $Author: umesha $
|
|
+----------------------------------------------------------------------*/
package net.project.portfolio;

import java.sql.SQLException;

import net.project.database.DBBean;
import net.project.methodology.MethodologyPortfolio;
import net.project.space.ISpaceListLoadable;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceList;
import net.project.space.SpaceListLoadableException;

import org.apache.log4j.Logger;


/**
 * Provides static methods to manage portfolios of Spaces.
 */
public class PortfolioManager implements ISpaceTypes, java.io.Serializable {

    /**
     * Creates an empty PortfolioManager.
     */
    private PortfolioManager() {
        // Do nothing
    }


    /**
     * Factory method that constructs a subclass of Porfolio of the specified type.
     * @param spaceType the type of the space from which to create the Portfolio.
     * @return an empty portfolio
     * @see net.project.space.ISpaceTypes
     */
    public static Portfolio makePortfolioFromType(String spaceType) {
        Portfolio portfolio = null;

        if (PROJECT_SPACE.equals(spaceType)) {
            portfolio = new ProjectPortfolio();

        } else if (BUSINESS_SPACE.equals(spaceType)) {
            portfolio = new BusinessPortfolio();

        } else if (METHODOLOGY_SPACE.equals(spaceType)) {
            portfolio = new MethodologyPortfolio();

        } else if (GENERIC_SPACE.equals(spaceType)) {
            portfolio = new GenericPortfolio();

        } else {
            portfolio = new GenericPortfolio();

        }

        return portfolio;
    }


    /**
     * Factory method which constructs a subclass of Porfolio from a SpaceList.
     * The type of the portfolio is determined by the type of the SpaceList.
     * If the SpaceList contains mixed or unknown space types, a GenericPortfolio will be returned.
     * @param spaceList list of spaces to include in the portfolio
     * @return a subclass of Portfolio, with the spaceList context set, NOT loaded from the database.
     * @throws net.project.space.SpaceListLoadableException if the constructed Portfolio subclass does not support this ISpaceListLoadable interface to load from SpaceLists.
     */
    public static Portfolio makePortfolioFromSpaceList(SpaceList spaceList) throws SpaceListLoadableException {

        Portfolio portfolio = null;

        // Construct the proper type of portfolio
        portfolio = makePortfolioFromType(spaceList.getType());

        // Set the portfolio type based on the SpaceList type.
        portfolio.setContentType(null);

        // Set the spaceList context for the portfolio, if supported
        if (portfolio instanceof ISpaceListLoadable) {
            ((ISpaceListLoadable) portfolio).setSpaceList(spaceList);

        } else {
        	Logger.getLogger(PortfolioManager.class).error("PortfolioManager.makePortfolioFromSpaceList failed.  Portfolio type: " + portfolio.getContentType() + " does not implement ISpaceListLoadable. Can't make portfolio from SpaceList. ");
            throw new SpaceListLoadableException("Portfolio type: " + portfolio.getContentType() + " does not implement ISpaceListLoadable. Can't make portfolio from SpaceList.");

        }

        return portfolio;
    }


    /**
     * Removes a portfolio from a space's portolio list.
     * @param space the space from which to remove the portfolio
     * @param portfolio the portfolio to remove
     * @throws NullPointerException if the space or portfolio is null
     */
    public static void removeSpaceHasPortfolio(Space space, Portfolio portfolio) {
        removeSpaceHasPortfolio(space.getID(), portfolio.getID());
    }


    /**
     * Removes a portfolio from a space's portolio list.
     * @param spaceID the id of the space from which to remove the portfolio
     * @param portfolioID the id of the portfolio to remove
     * @throws NullPointerException if the spaceID or portfolioID is null
     */
    public static void removeSpaceHasPortfolio(String spaceID, String portfolioID) {

        DBBean db = new DBBean();

        if ((spaceID == null) || (portfolioID == null)) {
            throw new NullPointerException("spaceID=" + spaceID + "and portfolioID=" + portfolioID + " can't be null.");
        }

        try {
            String query = "delete from pn_space_has_portfolio where space_id=" + spaceID + " and portfolio_id=" + portfolioID;
            db.executeQuery(query);

        } catch (SQLException sqle) {
        	Logger.getLogger(PortfolioManager.class).error("PortfolioManager.removeSpaceHasPortfolio threw " + sqle);

        } finally {
            db.release();

        }

    }

    /**
     * Adds a portfolio to a space's portolio list.
     * @param space the space to which to add the portfolio
     * @param portfolio the portfolio to add to the space
     * @param isDefault true if the portfolio is the default portfolio for the space; false otherwise
     * @throws NullPointerException if the space or portfolio is null
     */
    public static void setSpaceHasPortfolio(Space space, Portfolio portfolio, boolean isDefault) {
        setSpaceHasPortfolio(space.getID(), portfolio.getID(), isDefault);
    }


    /**
     * Adds a portfolio to a space's portolio list.
     * @param spaceID the id of the space to which to add the portfolio
     * @param portfolioID the id of the portfolio to add to the space
     * @param isDefault true if the portfolio is the default portfolio for the space; false otherwise
     * @throws NullPointerException if the spaceID or portfolioID is null
     */
    public static void setSpaceHasPortfolio(String spaceID, String portfolioID, boolean isDefault) {

        DBBean db = new DBBean();
        String isDefaultString = null;

        if ((spaceID == null) || (portfolioID == null)) {
            throw new NullPointerException("spaceID=" + spaceID + "and portfolioID=" + portfolioID + " can't be null.");
        }

        if (isDefault == true) {
            isDefaultString = "1";
        } else {
            isDefaultString = "0";
        }

        try {
            String query = "insert into pn_space_has_portfolio (space_id, portfolio_id, isDefault) values (" + spaceID + "," + portfolioID + "," + isDefaultString + ")";
            db.executeQuery(query);
        } catch (SQLException sqle) {
        	Logger.getLogger(PortfolioManager.class).error("PortfolioManager.setSpaceHasPortfolio threw " + sqle);

        } finally {
            db.release();

        }

    }

}
