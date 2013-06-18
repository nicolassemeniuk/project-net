package net.project.hibernate.service;

import java.util.Date;

import net.project.hibernate.model.PnPersonSalary;
import net.project.resource.PersonSalaryBean;
import net.project.resource.PnPersonSalaryList;

public interface IPnPersonSalaryService {

	public Integer savePersonSalary(PersonSalaryBean personSalary);

	/**
	 * Obtain the current salary for a person. This means that the salary
	 * returned is the one valid for the current date. The future salaries in
	 * the salary history are not taking in count.
	 * 
	 * @param personID
	 *            the id of the person.
	 * @return a person current salary.
	 */
	public PnPersonSalary getCurrentPersonSalaryByPersonId(Integer personId);

	/**
	 * Obtain a person salary for a certain date.
	 * 
	 * @param personId
	 *            the id of the person.
	 * @param date
	 *            the date from which we want to obtain the salary.
	 * @return the person salary for the period the date is in.
	 */
	public PnPersonSalary getPersonSalaryForDate(Integer personId, Date date);
	
	public PnPersonSalaryList getPersonSalaries(String personId);

}
