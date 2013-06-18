package net.project.resource;

import java.util.ArrayList;
import java.util.Collection;

import net.project.hibernate.model.PnPersonSalary;

public class PnPersonSalaryList extends ArrayList<PnPersonSalary> {
	
	public PnPersonSalaryList(){
		
	}

	public PnPersonSalaryList(Collection<PnPersonSalary> pnPersonSalary){
		super(pnPersonSalary);
	}

}
