package net.project.hibernate.dao;

import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.model.PnPersonSalaryPK;

public interface IPnPersonSalaryDAO extends IDAO<PnPersonSalary, PnPersonSalaryPK> {
	
	public PnPersonSalary getPersonSalary(Integer personID);

}
