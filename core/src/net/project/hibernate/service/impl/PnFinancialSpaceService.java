package net.project.hibernate.service.impl;

import java.sql.Date;

import net.project.financial.FinancialSpaceBean;
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
	
	public Integer saveFinancialSpace(FinancialSpaceBean financialSpaceBean){
		Integer materialObjectId = objectService.saveObject(new PnObject(PnFinancialSpace.OBJECT_TYPE, new Integer(financialSpaceBean.getUser().getID()), new Date(System
				.currentTimeMillis()), "A"));

		PnFinancialSpace pnFinancialSpace = new PnFinancialSpace();
		pnFinancialSpace.setFinancialSpaceName(financialSpaceBean.getName());
		pnFinancialSpace.setRecordStatus("A");
		pnFinancialSpace.setFinancialSpaceId(materialObjectId);
		return this.pnFinancialSpaceDAO.create(pnFinancialSpace);
	}

}
