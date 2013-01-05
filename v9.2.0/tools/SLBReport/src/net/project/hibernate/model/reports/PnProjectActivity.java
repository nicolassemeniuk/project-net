package net.project.hibernate.model.reports;

import java.math.BigDecimal;
import java.util.Date;

public class PnProjectActivity {

	private BigDecimal projectId;
	
	private String projectName;
	
	private Date lastAccess;
	
	private String firstName;
	
	private String lastName;
	
	private String phoneNumber;
	
	private String email;

	public BigDecimal getProjectId() {
		return projectId;
	}

	public void setProjectId(BigDecimal projectId) {
		this.projectId = projectId;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public Date getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(Date lastAccess) {
		this.lastAccess = lastAccess;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public PnProjectActivity() {
		super();
	}

	public PnProjectActivity(BigDecimal projectId, String projectName,
			Date lastAccess, String firstName, String lastName, String phoneNumber,
			String email) {
		super();
		this.projectId = projectId;
		this.projectName = projectName;
		this.lastAccess = lastAccess;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	
	
}
