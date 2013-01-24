package net.project.dto;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

public class Version implements Serializable {

	/** 
	 * default constructor 
	 */
	public Version() {
	}
	
	public Version(String version) {
		this.version = version;
	}

	/**
	 * generated id
	 */
	private static final long serialVersionUID = -471166234389498812L;

	private String version;

	private Date versionTime;

	private String userUsername;

	private String userMessage;

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getVersionTime() {
		return versionTime;
	}

	public void setVersionTime(Date versionTime) {
		this.versionTime = versionTime;
	}

	public String getUserUsername() {
		return userUsername;
	}

	public void setUserUsername(String userUsername) {
		this.userUsername = userUsername;
	}

	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

}
