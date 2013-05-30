package net.project.financial;

import java.util.ArrayList;
import java.util.List;

import net.project.hibernate.model.PnFinancialSpace;

public class PnFinancialSpaceList extends ArrayList<PnFinancialSpace> {
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PnFinancialSpaceList(List financialSpaces){
		super(financialSpaces);
	}
	
	public PnFinancialSpaceList(){
		super();
	}

}
