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

package net.project.financial;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.business.BusinessSpace;
import net.project.database.DBBean;
import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.portfolio.ProjectPortfolio;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceManager;
import net.project.space.SpaceTypes;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * A Financial Workspace.
 */
public class FinancialSpace extends Space implements Serializable, IXMLPersistence, IPortfolioEntry {

	private String totalActualCostToDate;

	private String totalEstimatedCurrentCost;

	private String totalBudgetedCost;

	private boolean parentChanged;

	/**
	 * Creates a new FinancialSpace.
	 */
	public FinancialSpace() {
		setType(ISpaceTypes.FINANCIAL_SPACE);
		this.spaceType = SpaceTypes.FINANCIAL;
	}

	/**
	 * Creates a financial space based on an existing PnFinancialSpace from the
	 * database.
	 * 
	 * @param pnFinancialSpace
	 *            obtained from the database.
	 */

	public FinancialSpace(PnFinancialSpace pnFinancialSpace) {
		this.spaceID = String.valueOf(pnFinancialSpace.getFinancialSpaceId());
		this.name = pnFinancialSpace.getFinancialSpaceName();
		this.description = pnFinancialSpace.getFinancialSpaceDescription();
		setType(ISpaceTypes.FINANCIAL_SPACE);
		this.parentSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getParentSpaceID(spaceID));
		this.relatedSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getBusinessRelatedSpace(spaceID).getComp_id()
				.getParentSpaceId());
		loadCosts();
	}

	/**
	 * Creates a new FinancialSpace for the specified id. The space is not
	 * loaded.
	 */
	public FinancialSpace(String financialSpaceId) {
		super(financialSpaceId);
		setType(ISpaceTypes.FINANCIAL_SPACE);
		this.spaceType = SpaceTypes.FINANCIAL;
	}

	public String getTotalActualCostToDate() {
		return totalActualCostToDate;
	}

	public void setTotalActualCostToDate(String totalActualCostToDate) {
		this.totalActualCostToDate = totalActualCostToDate;
	}

	public String getTotalEstimatedCurrentCost() {
		return totalEstimatedCurrentCost;
	}

	public void setTotalEstimatedCurrentCost(String totalEstimatedCurrentCost) {
		this.totalEstimatedCurrentCost = totalEstimatedCurrentCost;
	}

	public String getTotalBudgetedCost() {
		return totalBudgetedCost;
	}

	public void setTotalBudgetedCost(String totalBudgetedCost) {
		this.totalBudgetedCost = totalBudgetedCost;
	}

	public boolean isParentChanged() {
		return parentChanged;
	}

	public void setParentChanged(boolean parentChanged) {
		this.parentChanged = parentChanged;
	}

	/**************************************************************************************************
	 * Implementing IJDBCPersistence
	 **************************************************************************************************/

	@Override
	public void load() throws PersistenceException {
		if (spaceID != null) {
			PnFinancialSpace pnFinancialSpace = ServiceFactory.getInstance().getPnFinancialSpaceService().getFinancialSpace(spaceID);
			this.spaceID = String.valueOf(pnFinancialSpace.getFinancialSpaceId());
			this.name = pnFinancialSpace.getFinancialSpaceName();
			this.description = pnFinancialSpace.getFinancialSpaceDescription();
			setType(ISpaceTypes.FINANCIAL_SPACE);
			this.parentSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getParentSpaceID(spaceID));
			this.relatedSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getBusinessRelatedSpace(spaceID).getComp_id()
					.getParentSpaceId());
			loadCosts();
		}

	}

	@Override
	public void store() throws PersistenceException {
		DBBean db = new DBBean();
		try {
			ServiceFactory.getInstance().getPnFinancialSpaceService().saveFinancialSpace(this);

			// Create the relationship between the business and the financial
			// space.
			if (parentChanged) {
				db.setAutoCommit(false);

				if (getParentSpaceID() != null) {
					//Remove the previous super space / subspace relationship
					SpaceManager.removeSuperFinancialRelationships(this);
					
					//Save the new super space / subspace relationship
					FinancialSpace parentSpace = new FinancialSpace();
					parentSpace.setID(getParentSpaceID());
					SpaceManager.addSuperFinancialRelationship(db, parentSpace, this);
					
					db.commit();
				} else {
					//The new parent is null, remove the old super space / subspace relationship					
					SpaceManager.removeSuperFinancialRelationships(this);
				}
				
			}
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
	 * This method disables the space and they relationships with other
	 * financial spaces.
	 */
	@Override
	public void remove() throws PersistenceException {
		ServiceFactory.getInstance().getPnFinancialSpaceService().disableFinancialSpace(this.getID());
	}

	public void activate() {
		ServiceFactory.getInstance().getPnFinancialSpaceService().activateFinancialSpace(this.getID());
	}

	/**
	 * This method loads the total cost values for the financial project
	 * portfolio. The portfolio is obtained through the related business of the
	 * financial space.
	 */
	public void loadCosts() {
		Float totalActualCostToDateFloat = new Float(0.0);
		Float totalEstimatedCurrentCostFloat = new Float(0.0);
		Float totalBudgetedCostFloat = new Float(0.0);

		try {
			BusinessSpace businessSpace = new BusinessSpace(getRelatedSpaceID());
			businessSpace.load();
			ProjectPortfolio projectPortfolio = new ProjectPortfolio();
			projectPortfolio.clear();
			projectPortfolio.setID(businessSpace.getProjectPortfolioID("owner"));
			projectPortfolio.setProjectMembersOnly(true);
			projectPortfolio.load();

			for (Object entry : projectPortfolio) {
				IPortfolioEntry portfolioEntry = (IPortfolioEntry) entry;
				totalActualCostToDateFloat += ServiceFactory.getInstance().getProjectFinancialService().calculateActualCostToDate(portfolioEntry.getID());
				totalEstimatedCurrentCostFloat += ServiceFactory.getInstance().getProjectFinancialService().calculateEstimatedTotalCost(portfolioEntry.getID());
				totalBudgetedCostFloat += ServiceFactory.getInstance().getProjectFinancialService().getBudgetedCost(portfolioEntry.getID());
			}
		} catch (PersistenceException e) {
			// TODO handle errors
		}

		totalActualCostToDate = String.valueOf(totalActualCostToDateFloat);
		totalEstimatedCurrentCost = String.valueOf(totalEstimatedCurrentCostFloat);
		totalBudgetedCost = String.valueOf(totalBudgetedCostFloat);

	}

	/******************************************************************************
	 * XML
	 ******************************************************************************/

	@Override
	public String getXML() {
		return getXMLDocument().getXMLString();
	}

	@Override
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

			doc.startElement("FinancialSpace");
			doc.addElement("financialID", getID());
			doc.addElement("name", getName());
			doc.addElement("description", getDescription());
			doc.addElement("parentSpaceID", getParentSpaceID());
			doc.addElement("ownerSpaceID", getOwnerSpaceID());
			doc.addElement("totalActualCostToDate", getTotalActualCostToDate());
			doc.addElement("totalEstimatedCurrentCost", getTotalEstimatedCurrentCost());
			doc.addElement("totalBudgetedCost", getTotalBudgetedCost());
			doc.endElement();

		} catch (XMLDocumentException e) {
			// Do nothing
			// Return partially constructed document
		}

		return doc;
	}

}
