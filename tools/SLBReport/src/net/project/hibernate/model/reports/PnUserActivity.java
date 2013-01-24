/**
 * 
 */
package net.project.hibernate.model.reports;

import java.util.Date;


public class PnUserActivity {
	
	private String firstName;
	
	private String lastName;
	
	private Date lastLogin;

	private String email;
	
	private String phoneNumber;
	
	private String userStatus;

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
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

	/** default constructor */
	public PnUserActivity() {
		super();
	}

	/** full constructor */
	public PnUserActivity(String firstName, String lastName, Date lastLogin, String email,
			String phoneNumber, String userStatus) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.lastLogin = lastLogin;
		this.email = email;
		this.phoneNumber = phoneNumber;
		this.userStatus = userStatus;
	}
	
	
	
}
