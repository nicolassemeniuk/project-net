package net.project.hibernate.service.impl;

import net.project.hibernate.dao.IPnPersonSalaryDAO;
import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.service.IPnPersonSalaryService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="pnPersonSalaryService")
public class PnPersonSalaryServiceImpl implements IPnPersonSalaryService {
	
	/**
	 * PnPersonSalary data access object
	 */
	@Autowired
	private IPnPersonSalaryDAO pnPersonSalaryDAO;

	@Override
	public PnPersonSalary getPersonSalary(Integer personID) {		
		return this.pnPersonSalaryDAO.getPersonSalary(personID);
	}

}
