package net.project.hibernate.service.impl;

import java.util.Date;

import net.project.hibernate.dao.IPnPersonSalaryDAO;
import net.project.hibernate.model.PnObject;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;
import net.project.hibernate.service.IPnObjectService;
import net.project.hibernate.service.IPnPersonSalaryService;
import net.project.resource.PersonSalaryBean;
import net.project.resource.PnPersonSalaryList;

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
	public PnPersonSalary getCurrentPersonSalaryByPersonId(Integer personID) {		
		return this.pnPersonSalaryDAO.getCurrentPersonSalaryByPersonId(personID);
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

	@Override
	public PnPersonSalary getPersonSalaryForDate(Integer personId, Date date) {
		return this.pnPersonSalaryDAO.getPersonSalaryForDate(personId, date);
	}

	@Override
	public PnPersonSalaryList getPersonSalaries(String personId) {
		return this.pnPersonSalaryDAO.getPersonSalaries(Integer.valueOf(personId));
	}



}
