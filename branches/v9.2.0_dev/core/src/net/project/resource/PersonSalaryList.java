package net.project.resource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import net.project.hibernate.model.PnPersonSalary;
import net.project.hibernate.service.ServiceFactory;
import net.project.security.User;

public class PersonSalaryList extends ArrayList<PersonSalaryBean> {
	
	private User user = null;    
    private Boolean isLoaded=false;
	
	public Boolean getIsLoaded() {
		return isLoaded;
	}

	public void setIsLoaded(Boolean isLoaded) {
		this.isLoaded = isLoaded;
	}
	
	public void load()
	{
		PnPersonSalaryList personSalaryList = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaries(this.user.getID());
		for(PnPersonSalary personSalary : personSalaryList)
		{
			PersonSalaryBean personSalaryBean = new PersonSalaryBean(personSalary);
			this.add(personSalaryBean);
		}
		
		this.isLoaded = true;
	}
	
	public void load(Date startDate, Date endDate)
	{
		PnPersonSalaryList personSalaryList = ServiceFactory.getInstance().getPnPersonSalaryService().getPersonSalaries(this.user.getID(), startDate, endDate);
		
		for(PnPersonSalary personSalary : personSalaryList)
		{
			PersonSalaryBean personSalaryBean = new PersonSalaryBean(personSalary);
			this.add(personSalaryBean);
		}
		
		this.isLoaded = true;		
	}
	
	public void setUser(User user)
	{
		this.user = user;
	}

	/**
	 * Converts the object to XML representation This method returns the object
	 * as XML text.
	 * 
	 * @return XML representation of this object
	 */
	public String getXML()
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" ?>\n\n");
		xml.append("<personSalaryList>\n");
		
   		for(Iterator<PersonSalaryBean> iterator = this.iterator(); iterator.hasNext(); )
		{		
			try
			{
				PersonSalaryBean personSalary = iterator.next();
				xml.append(personSalary.getXMLBody());
			}
			catch(SQLException exception)
			{
		    	xml.append("</personSalary>");				
			}
		}
		
		xml.append("</personSalaryList>\n");		
		return xml.toString();
	}
}
