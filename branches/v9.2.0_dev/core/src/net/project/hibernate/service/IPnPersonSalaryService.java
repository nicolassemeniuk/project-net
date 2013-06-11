package net.project.hibernate.service;

import net.project.hibernate.model.PnPersonSalary;
import net.project.resource.PersonSalaryBean;

public interface IPnPersonSalaryService {
	
	public Integer savePersonSalary(PersonSalaryBean personSalary);
	
	/**
	 * Obtain the actual salary for a person.
	 * @param personID the id of the person.
	 * @return a person actual salary.
	 */
	public PnPersonSalary getPersonSalaryByPersonId(Integer personID);

	/**
	 * Obtain a person Salary by object Id.
	 * @param personSalaryId the id of the person salary object.
	 * @returns a person salary.
	 */
	public PnPersonSalary getPersonSalary(String personSalaryId);

}
