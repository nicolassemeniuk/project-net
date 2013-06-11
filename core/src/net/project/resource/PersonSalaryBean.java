package net.project.resource;

import net.project.security.User;

public class PersonSalaryBean extends PersonSalary {
	
	protected User user = null;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
	

}
