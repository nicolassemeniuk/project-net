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

import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.service.ServiceFactory;
import net.project.persistence.IXMLPersistence;
import net.project.persistence.PersistenceException;
import net.project.portfolio.IPortfolioEntry;
import net.project.space.ISpaceTypes;
import net.project.space.Space;
import net.project.space.SpaceTypes;
import net.project.xml.document.XMLDocument;
import net.project.xml.document.XMLDocumentException;

/**
 * A Financial Workspace.
 */
public class FinancialSpace extends Space implements Serializable, IXMLPersistence, IPortfolioEntry {


	/**
	 * Creates a new FinancialSpace.
	 */
	public FinancialSpace() {
		setType(ISpaceTypes.FINANCIAL_SPACE);
		this.spaceType = SpaceTypes.FINANCIAL;
	}
	
	/**
	 * Creates a financial space based on an existing PnFinancialSpace from the database.
	 * @param pnFinancialSpace obtained from the database.
	 */
	
	public FinancialSpace(PnFinancialSpace pnFinancialSpace){
		this.name = pnFinancialSpace.getFinancialSpaceName();
		this.spaceID = String.valueOf(pnFinancialSpace.getFinancialSpaceId());
		setType(ISpaceTypes.FINANCIAL_SPACE);
		this.spaceType = SpaceTypes.FINANCIAL;
		this.parentSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getParentSpaceID(Integer.valueOf(spaceID)));
		this.relatedSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getBusinessRelatedSpace(Integer.valueOf(spaceID)));
	}

	/**
	 * Creates a new FinancialSpace for the specified id.
	 * The space is not loaded.
	 */
	public FinancialSpace(String financialSpaceId) {
		super(financialSpaceId);
		setType(ISpaceTypes.FINANCIAL_SPACE);
		this.spaceType = SpaceTypes.FINANCIAL;
	}


	/**************************************************************************************************
	 * Implementing IJDBCPersistence
	 **************************************************************************************************/
	
	@Override
	public void load() throws PersistenceException
	{
		if(spaceID!=null){
			PnFinancialSpace pnFinancialSpace = ServiceFactory.getInstance().getPnFinancialSpaceService().getFinancialSpace(Integer.valueOf(spaceID));
			this.name = pnFinancialSpace.getFinancialSpaceName();
			this.spaceID = String.valueOf(pnFinancialSpace.getFinancialSpaceId());
			setType(ISpaceTypes.FINANCIAL_SPACE);
			this.spaceType = SpaceTypes.FINANCIAL;
			this.parentSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getParentSpaceID(Integer.valueOf(spaceID)));
			this.relatedSpaceID = String.valueOf(ServiceFactory.getInstance().getPnSpaceHasSpaceService().getBusinessRelatedSpace(Integer.valueOf(spaceID)));
		}
		
	}

	@Override
	public void store() throws PersistenceException
	{
        
		
	}

	@Override
	public void remove() throws PersistenceException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getXML()
	{
        return getXMLDocument().getXMLString();
	}

	@Override
	public String getXMLBody()
	{
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
            doc.addElement("numProjects", "0");
            doc.addElement("numMembers", "0");
            doc.endElement();

        } catch (XMLDocumentException e) {
            // Do nothing
            // Return partially constructed document
        }

        return doc;
    }

}
