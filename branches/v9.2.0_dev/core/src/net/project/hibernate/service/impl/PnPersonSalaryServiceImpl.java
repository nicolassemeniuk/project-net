package net.project.hibernate.service.impl;

import java.sql.Date;

import net.project.hibernate.dao.IPnPersonSalaryDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnPersonSalaryService;
import net.project.resource.PersonSalaryBean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnPersonSalaryService")
public class PnPersonSalaryServiceImpl implements IPnPersonSalaryService {
	
	@Autowired
	private IPnObjectService objectService;
	
	/**
	 * PnPersonSalary data access object
	 */
	@Autowired
	private IPnPersonSalaryDAO pnPersonSalaryDAO;

	@Override
	public PnPersonSalary getPersonSalaryByPersonId(Integer personID) {		
		return this.pnPersonSalaryDAO.getPersonSalaryByPersonId(personID);
	}
	
	@Override
	public PnPersonSalary getPersonSalary(String personSalaryId) {
		return this.pnPersonSalaryDAO.getPersonSalaryById(Integer.valueOf(personSalaryId));
	}

	@Override
	public Integer savePersonSalary(PersonSalaryBean personSalary) {
		Integer personSalaryObjectId = objectService.saveObject(new PnObject(PnPersonSalary.OBJECT_TYPE, new Integer(personSalary.getUser().getID()), new Date(System
				.currentTimeMillis()), "A"));
		PnPersonSalaryPK personSalaryPk = new PnPersonSalaryPK(personSalaryObjectId);
		
		PnPersonSalary pnPersonSalary = new PnPersonSalary();
		pnPersonSalary.setComp_id(personSalaryPk);
		pnPersonSalary.setPersonId(Integer.valueOf(personSalary.getPersonId()));
		pnPersonSalary.setStartDate(personSalary.getStartDate());
		pnPersonSalary.setEndDate(personSalary.getEndDate());
		pnPersonSalary.setCostByHour(new Float(personSalary.getCostByHour()));
		pnPersonSalary.setRecordStatus("A");
		
		PnPersonSalaryPK pk = this.pnPersonSalaryDAO.create(pnPersonSalary);
		
		return pk.getPersonSalaryId();
	}



}
