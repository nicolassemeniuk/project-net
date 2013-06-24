package net.project.resource;

import java.io.Serializable;
import java.sql.SQLException;

import net.project.hibernate.model.PnPersonSalary;
import net.project.persistence.IXMLPersistence;
import net.project.security.SessionManager;
import net.project.security.User;
import net.project.util.DateFormat;

public class PersonSalaryBean extends PersonSalary implements Serializable, IXMLPersistence {
	
	protected User user = null;
	
	public PersonSalaryBean(PnPersonSalary personSalary) {
		super(personSalary);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	@Override
	public String getXML() throws SQLException {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append(getXMLBody());
		return xml.toString();
	}

	@Override
	public String getXMLBody() throws SQLException
	{
    	DateFormat dateFormat = SessionManager.getUser().getDateFormatter();		
		
		StringBuffer xml = new StringBuffer();
		
    	xml.append("<personSalary>"); 
    	xml.append("<id>" + this.getPersonSalaryId() + "</id>");
    	xml.append("<person>" + this.getPersonId() + "</person>");
    	xml.append("<startDate>" + dateFormat.formatDate(this.getStartDate()) + "</startDate>");
    	xml.append("<endDate>" + (this.getEndDate() != null ? dateFormat.formatDate(this.getEndDate()) : "" ) + "</endDate>");
    	xml.append("<costByHour>" + this.getCostByHour() + "</costByHour>");
    	xml.append("</personSalary>");
    	
    	return xml.toString();
	}
	
	

}
