package net.project.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "version")
public class Version implements Serializable {

	/** 
	 * default constructor 
	 */
	public Version() {
	}
	
	public Version(String version, Date versionTime, String userUsername, String userMessage) {
		this.version = version;
		this.versionTime = versionTime;
		this.userUsername = userUsername;
		this.userMessage = userMessage;
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

	@Id
	@Column(name = "VERSION", unique = true, nullable = false, length = 10)
	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	@Column(name = "VERSION_TIME", nullable = false)
	public Date getVersionTime() {
		return versionTime;
	}

	public void setVersionTime(Date versionTime) {
		this.versionTime = versionTime;
	}

	@Column(name = "CURRENT_USER", nullable = false, length=20)
	public String getUserUsername() {
		return userUsername;
	}

	public void setUserUsername(String userUsername) {
		this.userUsername = userUsername;
	}

	@Column(name = "USER_MESSAGE", nullable = false, length=400)
	public String getUserMessage() {
		return userMessage;
	}

	public void setUserMessage(String userMessage) {
		this.userMessage = userMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((userMessage == null) ? 0 : userMessage.hashCode());
		result = prime * result + ((userUsername == null) ? 0 : userUsername.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		result = prime * result + ((versionTime == null) ? 0 : versionTime.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Version other = (Version) obj;
		if (userMessage == null) {
			if (other.userMessage != null)
				return false;
		} else if (!userMessage.equals(other.userMessage))
			return false;
		if (userUsername == null) {
			if (other.userUsername != null)
				return false;
		} else if (!userUsername.equals(other.userUsername))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		if (versionTime == null) {
			if (other.versionTime != null)
				return false;
		} else if (!versionTime.equals(other.versionTime))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Version[\n");
		sb.append("version:").append(version).append("\n");
		sb.append("userUsername:").append(userUsername).append("\n");
		sb.append("versionTime:").append(versionTime).append("\n");
		sb.append("userMessage:").append(userMessage).append("\n");
		sb.append("]");
		System.out.println(sb.toString());
		return sb.toString();
	}

	
	
}
