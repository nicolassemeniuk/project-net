package net.project.hibernate.service.impl;

import java.sql.Date;
import java.util.ArrayList;

import net.project.financial.FinancialCreateWizard;
import net.project.financial.PnFinancialSpaceList;
import net.project.hibernate.dao.IPnFinancialSpaceDAO;
import net.project.hibernate.model.PnFinancialSpace;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.service.IPnFinancialSpaceService;
import net.project.hibernate.service.IPnObjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value = "pnFinancialSpaceService")
public class PnFinancialSpaceService implements IPnFinancialSpaceService {
	
	@Autowired
	private IPnFinancialSpaceDAO pnFinancialSpaceDAO;
	
	@Autowired
	private IPnObjectService objectService;

	@Override
	public PnFinancialSpace getFinancialSpace(Integer financialSpaceId) {
		return this.pnFinancialSpaceDAO.getFinancialSpaceById(financialSpaceId);
	}
	
	public Integer saveFinancialSpace(FinancialCreateWizard financialSpace){
		PnObject pn = new PnObject(PnFinancialSpace.OBJECT_TYPE, new Integer(financialSpace.getUser().getID()), new Date(System
				.currentTimeMillis()), "A");
		
		Integer materialObjectId = objectService.saveObject(pn);

		PnFinancialSpace pnFinancialSpace = new PnFinancialSpace();
		pnFinancialSpace.setFinancialSpaceName(financialSpace.getName());
		pnFinancialSpace.setFinancialSpaceDescription(financialSpace.getDescription());
		pnFinancialSpace.setRecordStatus("A");
		pnFinancialSpace.setFinancialSpaceId(materialObjectId);
		return this.pnFinancialSpaceDAO.create(pnFinancialSpace);
	}

	@Override
	public PnFinancialSpaceList getFinancialSpacesByIds(ArrayList<Integer> additionalSpaceIDCollection) {
		return this.pnFinancialSpaceDAO.getFinancialSpacesByIds(additionalSpaceIDCollection);
	}
	
	

}
