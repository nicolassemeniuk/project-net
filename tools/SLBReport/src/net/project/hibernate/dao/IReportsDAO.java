package net.project.hibernate.dao;

import java.math.BigDecimal;
import java.util.List;

import net.project.hibernate.model.PnPerson;
import net.project.hibernate.model.reports.PnProjectActivity;
import net.project.hibernate.model.reports.PnUserActivity;

public interface IReportsDAO {
	
	public List<PnUserActivity> getUsersActivity();

	public List<PnProjectActivity> getProjectActivity();
	
	public List getUsersActivityNulls();
	
	public PnPerson getPerson(BigDecimal personId);	
}
