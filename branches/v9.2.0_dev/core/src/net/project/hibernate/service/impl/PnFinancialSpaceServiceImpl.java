package net.project.hibernate.service.impl;

import java.sql.Date;
import java.util.ArrayList;

import net.project.financial.FinancialCreateWizard;
import net.project.financial.FinancialSpace;
import net.project.financial.PnFinancialSpaceList;
import net.project.hibernate.dao.IPnFinancialSpaceDAO;
import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnSpaceHasSpace;
import net.project.hibernate.service.IPnFinancialSpaceService;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnSpaceHasSpaceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "pnFinancialSpaceService")
public class PnFinancialSpaceServiceImpl implements IPnFinancialSpaceService {

	@Autowired
	private IPnFinancialSpaceDAO pnFinancialSpaceDAO;

	@Autowired
	private IPnSpaceHasSpaceService spaceHasSpaceService;

	@Autowired
	private IPnObjectService objectService;

	@Override
	public PnFinancialSpace getFinancialSpace(String financialSpaceId) {
		return this.pnFinancialSpaceDAO.getFinancialSpaceById(Integer.valueOf(financialSpaceId));
	}

	public Integer saveFinancialSpace(FinancialCreateWizard financialSpace) {
		PnObject pn = new PnObject(PnFinancialSpace.OBJECT_TYPE, new Integer(financialSpace.getUser().getID()), new Date(System.currentTimeMillis()), "A");

		Integer materialObjectId = objectService.saveObject(pn);

		PnFinancialSpace pnFinancialSpace = new PnFinancialSpace();
		pnFinancialSpace.setFinancialSpaceName(financialSpace.getName());
		pnFinancialSpace.setFinancialSpaceDescription(financialSpace.getDescription());
		pnFinancialSpace.setRecordStatus("A");
		pnFinancialSpace.setFinancialSpaceId(materialObjectId);
		return this.pnFinancialSpaceDAO.create(pnFinancialSpace);
	}
	
	@Override
	public void saveFinancialSpace(FinancialSpace financialSpace) {		
		PnFinancialSpace pnFinancialSpace = pnFinancialSpaceDAO.getFinancialSpaceById(Integer.valueOf(financialSpace.getID()));		
		pnFinancialSpace.setFinancialSpaceName(financialSpace.getName());
		pnFinancialSpace.setFinancialSpaceDescription(financialSpace.getDescription());
		pnFinancialSpace.setRecordStatus(financialSpace.getRecordStatus());
		this.pnFinancialSpaceDAO.update(pnFinancialSpace);
		
	}

	@Override
	public PnFinancialSpaceList getFinancialSpacesByIds(ArrayList<Integer> additionalSpaceIDCollection) {
		return this.pnFinancialSpaceDAO.getFinancialSpacesByIds(additionalSpaceIDCollection);
	}

	@Override
	public void disableFinancialSpace(String financialSpaceId) {
		PnFinancialSpace financialSpace = this.pnFinancialSpaceDAO.getFinancialSpaceByIdAnyStatus(Integer.valueOf(financialSpaceId));
		financialSpace.setRecordStatus("D");
		this.pnFinancialSpaceDAO.update(financialSpace);

//		// Disable the parent relationship (if exists)
//		PnSpaceHasSpace financialParentSpaceHasSpace = this.spaceHasSpaceService.getFinancialParentSpaceRelationship(financialSpaceId);
//		if (financialParentSpaceHasSpace.getComp_id() != null) {
//			financialParentSpaceHasSpace.setRecordStatus("D");
//			this.spaceHasSpaceService.update(financialParentSpaceHasSpace);
//		}
//
//		// Disable the child's relationships (if exists)
//		ArrayList<PnSpaceHasSpace> financialChildSpaceHasSpaceList = this.spaceHasSpaceService.getFinancialChildsSpaceRelationships(financialSpaceId);
//		if (financialChildSpaceHasSpaceList.size() > 0) {
//			for (PnSpaceHasSpace spaceRelationship : financialChildSpaceHasSpaceList) {
//
//				spaceRelationship.setRecordStatus("D");
//				this.spaceHasSpaceService.update(spaceRelationship);
//			}
//
//		}

	}

	@Override
	public void activateFinancialSpace(String financialSpaceId) {
		PnFinancialSpace financialSpace = this.pnFinancialSpaceDAO.getFinancialSpaceByIdAnyStatus(Integer.valueOf(financialSpaceId));
		financialSpace.setRecordStatus("A");
		this.pnFinancialSpaceDAO.update(financialSpace);

//		// Disable the parent relationship (if exists)
//		PnSpaceHasSpace financialParentSpaceHasSpace = this.spaceHasSpaceService.getFinancialParentSpaceRelationship(financialSpaceId);
//		if (financialParentSpaceHasSpace.getComp_id() != null) {
//			financialParentSpaceHasSpace.setRecordStatus("A");
//			this.spaceHasSpaceService.update(financialParentSpaceHasSpace);
//		}
//
//		// Disable the child's relationships (if exists)
//		ArrayList<PnSpaceHasSpace> financialChildSpaceHasSpaceList = this.spaceHasSpaceService.getFinancialChildsSpaceRelationships(financialSpaceId);
//		if (financialChildSpaceHasSpaceList.size() > 0) {
//			for (PnSpaceHasSpace spaceRelationship : financialChildSpaceHasSpaceList) {
//
//				spaceRelationship.setRecordStatus("A");
//				this.spaceHasSpaceService.update(spaceRelationship);
//			}
//
//		}
		
	}



}
