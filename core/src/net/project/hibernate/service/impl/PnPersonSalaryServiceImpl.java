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
import net.project.util.DateUtils;

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
	public Integer saveFirstPersonSalary(PersonSalaryBean personSalary) {
		//Creates the new person salary
		Integer personSalaryObjectId = objectService.saveObject(new PnObject(PnPersonSalary.OBJECT_TYPE, new Integer(personSalary.getUser().getID()), new Date(System
				.currentTimeMillis()), "A"));
		PnPersonSalaryPK personSalaryPk = new PnPersonSalaryPK(personSalaryObjectId);
		
		PnPersonSalary pnPersonSalary = new PnPersonSalary();
		pnPersonSalary.setComp_id(personSalaryPk);
		pnPersonSalary.setPersonId(Integer.valueOf(personSalary.getPersonId()));
		pnPersonSalary.setStartDate(personSalary.getStartDate());
		pnPersonSalary.setEndDate(null);
		pnPersonSalary.setCostByHour(new Float(personSalary.getCostByHour()));
		pnPersonSalary.setRecordStatus("A");
		
		PnPersonSalaryPK pk = this.pnPersonSalaryDAO.create(pnPersonSalary);
		
		return pk.getPersonSalaryId();
	}

	@Override
	public Integer savePersonSalary(PersonSalaryBean personSalary) {
		//Obtain the last person salary to the date
		PnPersonSalary lastPersonSalary = pnPersonSalaryDAO.getLastPersonSalaryByPersonId(Integer.valueOf(personSalary.getPersonId()));
		
		//Set the end date as 1 day before the new salary start date.
		Date oldSalaryEndDate = personSalary.getStartDate();
		DateUtils.addDay(oldSalaryEndDate, -1);
		lastPersonSalary.setEndDate(oldSalaryEndDate);

		this.pnPersonSalaryDAO.update(lastPersonSalary);
		
		//Creates the new person salary
		Integer personSalaryObjectId = objectService.saveObject(new PnObject(PnPersonSalary.OBJECT_TYPE, new Integer(personSalary.getUser().getID()), new Date(System
				.currentTimeMillis()), "A"));
		PnPersonSalaryPK personSalaryPk = new PnPersonSalaryPK(personSalaryObjectId);
		
		PnPersonSalary pnPersonSalary = new PnPersonSalary();
		pnPersonSalary.setComp_id(personSalaryPk);
		pnPersonSalary.setPersonId(Integer.valueOf(personSalary.getPersonId()));
		pnPersonSalary.setStartDate(personSalary.getStartDate());
		pnPersonSalary.setEndDate(null);
		pnPersonSalary.setCostByHour(new Float(personSalary.getCostByHour()));
		pnPersonSalary.setRecordStatus("A");
		
		PnPersonSalaryPK pk = this.pnPersonSalaryDAO.create(pnPersonSalary);
		
		return pk.getPersonSalaryId();
	}
	
	@Override
	public void updatePersonSalary(PersonSalaryBean personSalary) {
		PnPersonSalary pnPersonSalary = this.pnPersonSalaryDAO.getPersonSalaryById(Integer.valueOf(personSalary.getPersonSalaryId()));
		
//		pnPersonSalary.setStartDate(personSalary.getStartDate());
//		pnPersonSalary.setEndDate(personSalary.getEndDate());
		pnPersonSalary.setCostByHour(Float.valueOf(personSalary.getCostByHour()));

		this.pnPersonSalaryDAO.update(pnPersonSalary);
	}

	@Override
	public PnPersonSalary getPersonSalaryForDate(Integer personId, Date date) {
		return this.pnPersonSalaryDAO.getPersonSalaryForDate(personId, date);
	}

	@Override
	public PnPersonSalaryList getPersonSalaries(String personId) {
		return this.pnPersonSalaryDAO.getPersonSalaries(Integer.valueOf(personId));
	}

	@Override
	public PnPersonSalary getPersonSalaryById(String personSalaryId) {
		return this.pnPersonSalaryDAO.getPersonSalaryById(Integer.valueOf(personSalaryId));
	}

	@Override
	public PnPersonSalary getLastPersonSalaryByPersonId(Integer personId) {
		return this.pnPersonSalaryDAO.getLastPersonSalaryByPersonId(personId);
	}

	@Override
	public PnPersonSalaryList getPersonSalaries(String personId, Date startDate, Date endDate) {
		return this.pnPersonSalaryDAO.getPersonSalaries(Integer.valueOf(personId), startDate, endDate);
	}





}
