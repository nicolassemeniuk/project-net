package net.project.hibernate.dao;

import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;

public interface IPnPersonSalaryDAO extends IDAO<PnPersonSalary, PnPersonSalaryPK> {
	
	/**
	 * Obtain a person current salary by the person id.
	 * @param personID the id of the person we want to obtain the current salary.
	 * @return a person salary.
	 */
	public PnPersonSalary getPersonSalaryByPersonId(Integer personID);
	
	/**
	 * Obtain the person salary by person salary object id.
	 * @param personSalaryID the id of the salary object.
	 * @return a person salary.
	 */
	public PnPersonSalary getPersonSalaryById(Integer personSalaryID);

}
