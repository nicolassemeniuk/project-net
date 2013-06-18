package net.project.hibernate.service;

import java.util.Date;

import net.project.hibernate.model.PnPersonSalary;
import net.project.resource.PersonSalaryBean;

public interface IPnPersonSalaryService {
	
	public Integer savePersonSalary(PersonSalaryBean personSalary);
	
	/**
	 * Obtain the current salary for a person.
	 * @param personID the id of the person.
	 * @return a person current salary.
	 */
	public PnPersonSalary getCurrentPersonSalaryByPersonId(Integer personID);

	public PnPersonSalary getPersonSalaryForDate(Integer personId, Date date);
	

}
